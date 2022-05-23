package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dialog.CustomMenuOptionDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.BasketResponseDto;
import com.example.orderingproject.databinding.ActivityPaymentBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends BasicActivity {
    private ActivityPaymentBinding binding;
    String store, service, restaurantName;
    ArrayList<BasketData> basketList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initView();
        initButtonListener();

    }

    private void initButtonListener() {
        binding.btnBackPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });

        binding.clPay1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                activatePayCreditCard();
            }
        });

        binding.clPay2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                activatePayKakaoPay();
            }
        });

        binding.clPay3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                activatePayCash();
            }
        });
    }

    private void activatePayCreditCard(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_selected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.bottomLayoutPayment.setText("지금은 쿠폰 또는 직접 결제만 가능해요");
        ButtonLock();
    }

    private void activatePayKakaoPay(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_selected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.bottomLayoutPayment.setText("지금은 쿠폰 또는 직접 결제만 가능해요");
        ButtonLock();
    }

    private void activatePayCash(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_selected);
        binding.bottomLayoutPayment.setText("결제하기");
        ButtonRelease();
    }

    private void initData() {
        store = getIntent().getStringExtra("store");
        service = getIntent().getStringExtra("service");
        restaurantName = getIntent().getStringExtra("restaurantName");

        getBasketListFromServer();
    }

    @SuppressLint("SetTextI18n")
    private void initView(){
        ButtonLock();

        binding.tvSubtitleStoreName.setText(restaurantName);

        String[] serviceSplitArr = service.split("e");
        if(serviceSplitArr[0].equals("tabl")){
            binding.tvSubtitleTableNum.setText(serviceSplitArr[1] + "번");
        }else{
            binding.tvSubtitleTableNum.setVisibility(View.GONE);
            binding.tvSubtitleTwo.setVisibility(View.GONE);
        }
        if(service.equals("takeout")){
            binding.tvService.setText("포장");
        }
    }

    private void getBasketListFromServer(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/baskets/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<BasketResponseDto>>> call = service.getBasketList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<BasketResponseDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<BasketResponseDto>>> call, Response<ResultDto<List<BasketResponseDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<BasketResponseDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            int totalPrice = 0;
                                            for(BasketResponseDto i : result.getData()){
                                                basketList.add(new BasketData(i.getBasketId(),
                                                        i.getFoodId(),
                                                        i.getFoodName(),
                                                        i.getImageUrl(),
                                                        i.getPrice(),
                                                        i.getCount()));
                                                Log.e("결제 정보", "BasketId = " + i.getBasketId() + ", " +
                                                        "FoodId = " + i.getFoodId() +
                                                        "FoodName = " + i.getFoodName() +
                                                        ", image url = " + i.getImageUrl() +
                                                        ", Price = " + i.getPrice() +
                                                        ", count = " + i.getCount());
                                                totalPrice += i.getPrice() * i.getCount();
                                            }

                                            RecyclerView recyclerView = binding.rvMenuPayment;
                                            PaymentAdapter paymentAdapter = new PaymentAdapter(basketList, PaymentActivity.this);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this));
                                            recyclerView.setAdapter(paymentAdapter);
                                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),1));

                                            binding.tvTotalPrice.setText( CustomMenuOptionDialog.computePrice(totalPrice) + "원");

                                            binding.progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<BasketResponseDto>>> call, Throwable t) {
                            Toast.makeText(PaymentActivity.this, "주문 메뉴를 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(PaymentActivity.this, "주문 메뉴를 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    /* 버튼 Lock거는 함수 */
    private void ButtonLock() {
        binding.bottomLayoutPayment.setBackgroundColor(Color.parseColor("#5E5E5E"));
        binding.bottomLayoutPayment.setEnabled(false);
    }


    /* 버튼 Lock푸는 함수 */
    private void ButtonRelease() {
        binding.bottomLayoutPayment.setBackgroundColor(Color.parseColor("#E35555"));
        binding.bottomLayoutPayment.setEnabled(true);
    }
}