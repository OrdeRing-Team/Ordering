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
                                            float reviewTotalRating = 0;
                                            int fiveStars = 0, fourStars = 0, threeStars = 0, twoStars = 0, oneStar = 0;
                                            for(ReviewPreviewDto i : result.getData()){
                                                reviewTotalRating += i.getRating();
                                                switch ((int)i.getRating()){
                                                    case 1:
                                                        oneStar++;
                                                        break;
                                                    case 2:
                                                        twoStars++;
                                                        break;
                                                    case 3:
                                                        threeStars++;
                                                        break;
                                                    case 4:
                                                        fourStars++;
                                                        break;
                                                    case 5:
                                                        fiveStars++;
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }

                                            int totalStars = oneStar + twoStars + threeStars + fourStars + fiveStars;
                                            Log.e("totalStars###############",Integer.toString(totalStars));
                                            binding.progressbar1.setMax(totalStars);
                                            binding.progressbar2.setMax(totalStars);
                                            binding.progressbar3.setMax(totalStars);
                                            binding.progressbar4.setMax(totalStars);
                                            binding.progressbar5.setMax(totalStars);

                                            binding.progressbar1.setProgress(oneStar);
                                            binding.progressbar2.setProgress(twoStars);
                                            binding.progressbar3.setProgress(threeStars);
                                            binding.progressbar4.setProgress(fourStars);
                                            binding.progressbar5.setProgress(fiveStars);

                                            Log.e("one two three four five",Integer.toString(oneStar) + Integer.toString(twoStars) +
                                                    Integer.toString(threeStars) + Integer.toString(fourStars) + Integer.toString(fiveStars));
                                            reviewTotalRating /= result.getData().size();

                                            binding.ratingBar.setRating(reviewTotalRating);
                                            binding.tvRating.setText(Float.toString(reviewTotalRating));

                                            RecyclerView recyclerView = binding.rvReview;
                                            ReviewListAdapter reviewListAdapter = new ReviewListAdapter(result.getData(), getContext(), binding.clEmptyReview);
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