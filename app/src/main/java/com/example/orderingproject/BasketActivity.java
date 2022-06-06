package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.BasketPutDto;
import com.example.orderingproject.Dto.response.BasketFoodDto;
import com.example.orderingproject.Dto.response.BasketListResultDto;
import com.example.orderingproject.Dto.response.OrderDetailDto;
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
    static ImageView emptyImage;
    static TextView emptyText;
    static Button orderButton;
    static int orderCount;
    static int totalCount = 0;
    private ActivityBasketBinding binding;

    public static void setEmptyView() {
        emptyImage.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.VISIBLE);
        orderButton.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    public static void setOrderCount() {
        orderButton.setText(UserInfo.getBasketCount() + "개 메뉴 주문하기");
    }

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
                if (!BasketAdapter.hm.isEmpty()) {
                    modifyBasketCount(null);
                } else {
                    FinishWithAnim();
                }
            }
        });
        binding.bottomLayout.setOnClickListener(new View.OnClickListener() {
            // 주문하기 버튼
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BasketActivity.this, PaymentActivity.class);
                intent.putExtra("store", getIntent().getStringExtra("store"));
                intent.putExtra("service", getIntent().getStringExtra("service"));
                intent.putExtra("restaurantName", getIntent().getStringExtra("restaurantName"));

                if (!BasketAdapter.hm.isEmpty()) {
                    modifyBasketCount(intent);
                } else {
                    startActivityResult.launch(intent);
                }
            }
        });
    }

    private void modifyBasketCount(Intent intent) {
        // HashMap에 담긴 음식 수량 변경 내용을 ArrayList로 변환
        Map<Long, Integer> countChangedMap = BasketAdapter.getCountChangedHashMap();
        List<BasketPutDto> countCahngedList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entrySet : countChangedMap.entrySet()) {
            countCahngedList.add(new BasketPutDto(entrySet.getKey(), entrySet.getValue()));
        }
        for (BasketPutDto b : countCahngedList) {
            Log.e("list###", "foodId : " + b.getBasketId() + "\tcount : " + b.getCount());
        }
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    String url = "http://www.ordering.ml/";

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.modifyBasketCount(UserInfo.getCustomerId(), countCahngedList);

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
//                                            UserInfo.setBasketCount(totalCount);
                                            if (intent == null) {
                                                FinishWithAnim();
                                            } else {
                                                startActivityResult.launch(intent);
                                            }
                                        }
                                    });
                                    Log.e("result.getData() ", Boolean.toString(result.getData()));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showToast(BasketActivity.this, "변경된 수량을 저장하는데 실패했습니다.");
                            FinishWithAnim();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            MainActivity.showToast(BasketActivity.this, "변경된 수량을 저장하는데 실패했습니다.");
            FinishWithAnim();
        }
    }

    private void initData() {
        startProgress(this);

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
                    Call<ResultDto<BasketListResultDto>> call = service.getBasketList(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<BasketListResultDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<BasketListResultDto>> call, Response<ResultDto<BasketListResultDto>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<BasketListResultDto> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            totalCount = 0;

                                             for(BasketFoodDto i : result.getData().getBasketFoods()){
                                                 totalCount += i.getCount();
                                            }

                                            if(totalCount == 0){
                                                emptyImage.setVisibility(View.VISIBLE);
                                                emptyText.setVisibility(View.VISIBLE);
                                                orderButton.setVisibility(View.GONE);
                                                binding.scrollViewBasket.setVisibility(View.GONE);
                                                binding.tvBasketStoreName.setVisibility(View.GONE);
                                            }else{
                                                binding.tvBasketStoreName.setText(result.getData().getRestaurantName());
                                            }
                                            UserInfo.setBasketCount(totalCount);

                                            RecyclerView recyclerView = binding.rvBasket;
                                            BasketAdapter basketAdapter = new BasketAdapter(result.getData().getBasketFoods(), BasketActivity.this);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(BasketActivity.this));
                                            recyclerView.setAdapter(basketAdapter);
                                            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),1));

                                            stopProgress();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<BasketListResultDto>> call, Throwable t) {
                            Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            stopProgress();
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(BasketActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            stopProgress();
        }
    }
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentResult = result.getData();

                        if(intentResult != null) {
                            boolean resultValue = intentResult.getBooleanExtra("orderCompleted", false);
                            Long orderId = intentResult.getLongExtra("orderId",0);
                            Log.e("resultValue", Boolean.toString(resultValue));
                            Log.e("orderId", Long.toString(orderId));

                            if(resultValue) {
                                initData();
                                Intent intent = new Intent(BasketActivity.this, OrderlistDetailActivity.class);
                                intent.putExtra("orderId",orderId);
                                Log.e("toOrderListDetailActivity ### orderId : ",Long.toString(orderId));
                                startActivity(intent);
                                finish();
                            }
                            else{
                                MainActivity.showToast(BasketActivity.this,"주문 접수에 실패하였습니다.");
                            }
                        }
                    }
                }
            });
}