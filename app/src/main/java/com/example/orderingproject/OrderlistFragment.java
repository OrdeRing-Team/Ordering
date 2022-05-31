package com.example.orderingproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.BasketFoodDto;
import com.example.orderingproject.Dto.response.PreviousHistoryDto;
import com.example.orderingproject.databinding.FragmentOrderlistBinding;
import com.loopeer.cardstack.CardStackView;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderlistFragment extends Fragment implements CardStackView.ItemExpendListener {

    private FragmentOrderlistBinding binding;
    private CardStackView mStackView;
    private LinearLayout mActionButtonContainer;
//    private TestStackAdapter mTestStackAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderlistBinding.inflate(inflater, container, false);

//        mStackView = binding.stackviewMain;
//        mStackView.setItemExpendListener(this);
//        mTestStackAdapter = new TestStackAdapter(this);
//        mStackView.setAdapter(mTestStackAdapter);

        initData();

//        new Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));
//                    }
//                }
//                , 200
//        );

        return binding.getRoot();
    }

    private void initData() {
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/orders/ongoing/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<PreviousHistoryDto>>> call = service.getOrderInList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<PreviousHistoryDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<PreviousHistoryDto>>> call, Response<ResultDto<List<PreviousHistoryDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<PreviousHistoryDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (result.getData().size() == 0) {
                                                binding.clEmptyOrderInProgress.setVisibility(View.VISIBLE);
                                                binding.rvOrderInProgress.setVisibility(View.GONE);
                                            } else {
                                                binding.clEmptyOrderInProgress.setVisibility(View.GONE);

                                                RecyclerView recyclerView = binding.rvOrderInProgress;
                                                OrderlistAdapter orderlistAdapter = new OrderlistAdapter(result.getData(), getContext(), binding.clEmptyOrderInProgress);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(orderlistAdapter);
                                                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<PreviousHistoryDto>>> call, Throwable t) {
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

    public void onPreClick(View view) {
        mStackView.pre();
    }

    public void onNextClick(View view) {
        mStackView.next();
    }

    @Override
    public void onItemExpend(boolean expend) {

    }
}