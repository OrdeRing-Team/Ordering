package com.example.orderingproject.review;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.Dto.response.ReviewPreviewDto;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.OrderlistAdapter;
import com.example.orderingproject.ReviewListAdapter;
import com.example.orderingproject.databinding.FragmentStoreReviewBinding;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreReviewFragment extends Fragment {

    private View view;
    private FragmentStoreReviewBinding binding;

    Long restaurantId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStoreReviewBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();

        return view;
    }

    private void initData(){
        restaurantId = Long.parseLong(MenuActivity.store);

        initReviewRecyclerView();
    }

    private void initReviewRecyclerView(){
        try{
            new Thread(){
                @SneakyThrows
                public void run(){
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + restaurantId + "/reviews/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<ReviewPreviewDto>>> call = service.getReviewList(restaurantId);

                    call.enqueue(new Callback<ResultDto<List<ReviewPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<ReviewPreviewDto>>> call, Response<ResultDto<List<ReviewPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<ReviewPreviewDto>> result;
                                result = response.body();
                                if (!result.getData().isEmpty()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            RecyclerView recyclerView = binding.rvReview;
                                            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(result.getData(), getContext());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                            recyclerView.setAdapter(reviewListAdapter);
                                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
                                        }
                                    });
                                }else{
                                    // 리뷰가 한 개도 존재하지 않을 때
                                    binding.rvReview.setVisibility(View.GONE);
                                    binding.clEmptyReview.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<ReviewPreviewDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

}