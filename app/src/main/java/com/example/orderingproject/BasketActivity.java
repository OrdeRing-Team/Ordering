package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.BasketRequestDto;
import com.example.orderingproject.Dto.request.OrderDto;
import com.example.orderingproject.Dto.response.BasketResponseDto;
import com.example.orderingproject.ENUM_CLASS.OrderType;
import com.example.orderingproject.databinding.ActivityBasketBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BasketActivity extends BasicActivity {
    private ActivityBasketBinding binding;

    ArrayList<BasketData> basketList = new ArrayList<>();
    static ImageView emptyImage;
    static TextView emptyText;
    static Button orderButton;
    static int orderCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBasketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initButtonListener();

    }

    private void initButtonListener() {
        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        binding.bottomLayout.setOnClickListener(new View.OnClickListener() {
            // 주문하기 버튼
            @Override
            public void onClick(View view) {

                //주문 요청 로직
//                Map<Long, Integer> hm = BasketAdapter.getHashMap();
//                for (Map.Entry<Long, Integer> entrySet : hm.entrySet()) {
//                    Log.e("수량변경 HashMap ","key : " + entrySet.getKey() + "  value : " + entrySet.getValue());
//                }
//
//                try {
//                    new Thread() {
//                        @SneakyThrows
//                        public void run() {
//                            OrderDto orderDto = new OrderDto(null, OrderType.PACKING_ORDER, hm);
//                            Retrofit retrofit = new Retrofit.Builder()
//                                    .baseUrl("http://www.ordering.ml/")
//                                    .addConverterFactory(GsonConverterFactory.create())
//                                    .build();
//
//                            RetrofitService service = retrofit.create(RetrofitService.class);
//                            Call<ResultDto<Long>> call = service.orderRequest(UserInfo.getCustomerId(), orderDto);
//
//                            call.enqueue(new Callback<ResultDto<Long>>() {
//                                @Override
//                                public void onResponse(Call<ResultDto<Long>> call, Response<ResultDto<Long>> response) {
//
//                                    if (response.isSuccessful()) {
//                                        ResultDto<Long> result;
//                                        result = response.body();
//                                        if (result.getData() != null) {
//                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    Toast.makeText(BasketActivity.this, "주문 요청됨",Toast.LENGTH_SHORT).show();
//                                                    UserInfo.setBasketCount(0);
//
//                                                }
//                                            });
//                                        }else{
//                                            Log.e("result","Null");
//                                        }
//                                    }
//                                    else{
//                                        Log.e("menuOrder","isFailed");
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ResultDto<Long>> call, Throwable t) {
//                                    Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
//                                    Log.e("e = ", t.getMessage());
//                                }
//                            });
//                        }
//                    }.start();
//
//                } catch (Exception e) {
//                    Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
//                    Log.e("e = ", e.getMessage());
//                }


                Intent intent = new Intent(BasketActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {

        emptyImage = binding.ivEmpty;
        emptyText = binding.tvEmpty;
        orderButton = binding.bottomLayout;
        orderCount = 0;

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
                                            int totalCount = 0;
                                            for(BasketResponseDto i : result.getData()){
                                                basketList.add(new BasketData(i.getBasketId(),
                                                        i.getFoodId(),
                                                        i.getFoodName(),
                                                        i.getImageUrl(),
                                                        i.getPrice(),
                                                        i.getCount()));
                                                totalCount += i.getCount();
                                                Log.e("장바구니 정보", "BasketId = " + i.getBasketId() + ", " +
                                                        "FoodId = " + i.getFoodId() +
                                                        "FoodName = " + i.getFoodName() +
                                                        ", image url = " + i.getImageUrl() +
                                                        ", Price = " + i.getPrice() +
                                                        ", count = " + i.getCount());
                                            }

                                            UserInfo.setBasketCount(totalCount);

                                            RecyclerView recyclerView = binding.rvBasket;
                                            BasketAdapter basketAdapter = new BasketAdapter(basketList, BasketActivity.this);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(BasketActivity.this));
                                            recyclerView.setAdapter(basketAdapter);
                                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),1));

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<BasketResponseDto>>> call, Throwable t) {
                            Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }
    public static void setEmptyView(){
        emptyImage.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public static void setOrderCount(){
        orderButton.setText(orderCount + "개 메뉴 주문하기");
    }
}