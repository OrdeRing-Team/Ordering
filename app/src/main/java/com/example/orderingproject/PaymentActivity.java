package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.OrderDto;
import com.example.orderingproject.Dto.response.BasketFoodDto;
import com.example.orderingproject.ENUM_CLASS.OrderType;
import com.example.orderingproject.databinding.ActivityPaymentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends BasicActivity {
    private ActivityPaymentBinding binding;

    int totalPrice = 0;
    Long selectedCouponId;

    boolean possibleToOrder = false;
    boolean cashButtonClicked = false;
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

        binding.clCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this ,CouponActivity.class);
                intent.putExtra("from","PaymentActivity");
                startActivityResult.launch(intent);
            }
        });

        binding.bottomLayoutPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(possibleToOrder || cashButtonClicked) {
                    startOrderProcess();
                }
            }
        });
    }

    private void startOrderProcess(){
        //주문 요청
        startProgress(this);
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    /** 주문 방식 (PACKING_ORDER) 새롭게 처리할 것 **/

                    OrderDto orderDto = new OrderDto(null, OrderType.PACKING_ORDER);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Long>> call = service.orderRequest(UserInfo.getCustomerId(), orderDto);

                    call.enqueue(new Callback<ResultDto<Long>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Long>> call, Response<ResultDto<Long>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Long> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            stopProgress();
                                            Toast.makeText(PaymentActivity.this, "주문 요청됨",Toast.LENGTH_SHORT).show();
                                            UserInfo.setBasketCount(0);
                                            if(selectedCouponId == null || selectedCouponId == 0){
                                                Intent intent = new Intent(PaymentActivity.this, BasketActivity.class);
                                                intent.putExtra("orderCompleted", true);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }
                                            else{
                                                deleteSelectedCoupon();
                                            }
                                        }
                                    });
                                }else{
                                    Log.e("result","Null");
                                }
                            }
                            else{
                                Log.e("menuOrder","isFailed");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Long>> call, Throwable t) {
                            Toast.makeText(PaymentActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(PaymentActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    private void activatePayCreditCard(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_selected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_unselected);
        cashButtonClicked = false;
        if(!possibleToOrder) {
            binding.bottomLayoutPayment.setText("지금은 쿠폰 또는 직접 결제만 가능해요");
            ButtonLock();
        }
    }

    private void activatePayKakaoPay(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_selected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_unselected);
        cashButtonClicked = false;
        if(!possibleToOrder) {
            binding.bottomLayoutPayment.setText("지금은 쿠폰 또는 직접 결제만 가능해요");
            ButtonLock();
        }
    }

    private void activatePayCash(){
        binding.clPay1.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay2.setBackgroundResource(R.drawable.background_payment_unselected);
        binding.clPay3.setBackgroundResource(R.drawable.background_payment_selected);
        binding.bottomLayoutPayment.setText("결제하기");
        cashButtonClicked = true;
        ButtonRelease();
    }

    private void initData() {
        startProgress(this);
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
                    Call<ResultDto<List<BasketFoodDto>>> call = service.getBasketList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<List<BasketFoodDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<BasketFoodDto>>> call, Response<ResultDto<List<BasketFoodDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<BasketFoodDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            for(BasketFoodDto i : result.getData()){
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

                                            String firstPrice = Utillity.computePrice(totalPrice) + "원";
                                            binding.tvTotalPrice.setText(firstPrice);
                                            binding.tvSubtitleTotalOrderPrice.setText(firstPrice);
                                            binding.tvSubtitleTotalPrice.setText(firstPrice);

                                            stopProgress();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<BasketFoodDto>>> call, Throwable t) {
                            Toast.makeText(PaymentActivity.this, "주문 메뉴를 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            stopProgress();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(PaymentActivity.this, "주문 메뉴를 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            stopProgress();
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

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentResult = result.getData();

                        if(intentResult != null) {
                            int resultValue = intentResult.getIntExtra("couponValue", -1);
                            selectedCouponId = intentResult.getLongExtra("couponId", 0);
                            Log.e("resultCouponId", Long.toString(intentResult.getLongExtra("couponId", -1)));
                            Log.e("resultValue", Integer.toString(resultValue));

                            String resultValueText = Utillity.computePrice(resultValue)+ "원";
                            binding.clTotalCoupon.setVisibility(View.VISIBLE);
                            binding.tvSubtitleTotalCoupon.setText(String.format("-%s",resultValueText));
                            binding.tvCouponValue.setText(resultValueText);
                            binding.tvCouponValue.setTextSize(16);

                            int finalPrice = totalPrice - resultValue;
                            if(finalPrice < 0){
                                binding.tvSubtitleTotalPrice.setText("0원");
                                binding.bottomLayoutPayment.setText("결제하기");
                                ButtonRelease();
                                possibleToOrder = true;
                            }else {
                                binding.tvSubtitleTotalPrice.setText(String.format("%s원",Utillity.computePrice(finalPrice)));
                            }
                        }
                    }
                }
            });

    private void deleteSelectedCoupon(){
        // 선택했던 쿠폰 삭제(사용처리)
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    String url = "http://www.ordering.ml/api/customer/coupon/" + selectedCouponId + "/";

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.useCoupon(selectedCouponId);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            stopProgress();
                                            MainActivity.showToast(PaymentActivity.this,"주문 요청 완료, 쿠폰 삭제 완료");

                                            Intent intent = new Intent(PaymentActivity.this, BasketActivity.class);
                                            intent.putExtra("orderCompleted", true);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                            Log.e("couponId",Long.toString(selectedCouponId) + " // 사용 처리됨");
                                        }
                                    });
                                    Log.e("result.getData() ", Boolean.toString(result.getData()));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            stopProgress();
                            Toast.makeText(PaymentActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            stopProgress();
            Toast.makeText(PaymentActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }
}