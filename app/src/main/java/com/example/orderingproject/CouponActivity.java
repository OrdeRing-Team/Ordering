package com.example.orderingproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.CouponDto;
import com.example.orderingproject.databinding.ActivityCouponBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CouponActivity extends BasicActivity {
    private ActivityCouponBinding binding;
    static Boolean fromPayment = false;

    ArrayList<CouponData> couponList = new ArrayList<>();
    TextView emptyCouponText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCouponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();

        initButtonListener();

        if (getIntent().getStringExtra("from").equals("PaymentActivity")) {
            Log.e("CouponActivity", "From PaymentActivity is true");
            fromPayment = true;
        }

    }

    private void initButtonListener() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });

    }

    private void initData() {
        startProgress(this);

        emptyCouponText = binding.tvEmpty;
        fromPayment = false;
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/my_coupons/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<CouponDto>>> call = service.getCouponList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<CouponDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<CouponDto>>> call, Response<ResultDto<List<CouponDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<CouponDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (result.getData().size() != 0) {
                                                for (CouponDto i : result.getData()) {
                                                    couponList.add(new CouponData(i.getSerialNumber(),
                                                            i.getValue()));

                                                    Log.e("쿠폰 ", "SerialNumber = " + i.getSerialNumber() + ", " +
                                                            "Value = " + i.getValue());
                                                }

                                                RecyclerView recyclerView = binding.rvCoupon;
                                                CouponAdapter couponAdapter = new CouponAdapter(couponList, CouponActivity.this);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(CouponActivity.this));
                                                recyclerView.setAdapter(couponAdapter);
                                                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));

                                            } else {
                                                setEmptyView();
                                            }

                                            stopProgress();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<CouponDto>>> call, Throwable t) {
                            Toast.makeText(CouponActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            stopProgress();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(CouponActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            stopProgress();
        }
    }

    public void setEmptyView() {
        emptyCouponText.setVisibility(View.VISIBLE);
    }
}