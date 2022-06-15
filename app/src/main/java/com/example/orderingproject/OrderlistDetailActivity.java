package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.response.OrderDetailDto;
import com.example.orderingproject.Dto.response.OrderFoodDto;
import com.example.orderingproject.Dto.response.OrderPreviewWithRestSimpleDto;
import com.example.orderingproject.ENUM_CLASS.OrderStatus;
import com.example.orderingproject.ENUM_CLASS.OrderType;
import com.example.orderingproject.databinding.ActivityOrderlistDetailBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderlistDetailActivity extends BasicActivity {
    private ActivityOrderlistDetailBinding binding;

    private Integer myOrderNumber;
    private String orderSummary;
    private String receivedTime;
    private String receivedTimeOriginal;
    private String checkTime;
    private String checkTimeOriginal;
    private String cancelOrCompletedTime;
    private String cancelOrCompletedTimeOriginal;
    private int totalPrice;
    private OrderType orderType;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private Long restaurantId;
    private String restaurantName;
    private int orderWaitingTime;
    private List<OrderFoodDto> orderFoods;

    int progressMax, progress;
    Long orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityOrderlistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initButtonListener();
    }

    private void initButtonListener(){
        binding.btnBackOrderlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });
    }

    private void initData(){
        progress = 1;

        getOrderData();
    }


    private void setOrderProgress(boolean delayMore){
        Log.e("setOrderProgress",Integer.toString(progress));
        binding.customProgressbarOrder.setProgress(progress);
        int delay = delayMore ? 20 : 10;
        if(progress < progressMax-1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress++;
                    setOrderProgress(delayMore);
                }
            },delay);
        }
    }

    private void getOrderData(){
        startProgress(OrderlistDetailActivity.this);
        orderId = getIntent().getLongExtra("orderId",0);
        if(orderId == 0){
            stopProgress();
            Toast.makeText(this, "상세정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            FinishWithAnim();
        }
        else {
            try {
                new Thread() {
                    @SneakyThrows
                    public void run() {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/customer/order/"+orderId+"/detail/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<OrderDetailDto>> call = service.getOrderDetail(orderId);

                        call.enqueue(new Callback<ResultDto<OrderDetailDto>>() {
                            @Override
                            public void onResponse(Call<ResultDto<OrderDetailDto>> call, Response<ResultDto<OrderDetailDto>> response) {

                                if (response.isSuccessful()) {
                                    ResultDto<OrderDetailDto> result;
                                    result = response.body();
                                    if (result.getData() != null) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                myOrderNumber = result.getData().getMyOrderNumber();
                                                orderSummary = result.getData().getOrderSummary();
                                                receivedTimeOriginal = result.getData().getReceivedTime();
                                                receivedTime = convertTimeToString(receivedTimeOriginal);

                                                if(result.getData().getCheckTime() != null) {
                                                    checkTimeOriginal = result.getData().getCheckTime();
                                                    checkTime = convertTimeToString(checkTimeOriginal);
                                                }
                                                if(result.getData().getCancelOrCompletedTime() != null) {
                                                    Log.e("result.getData().getCancelOrCompletedTime", result.getData().getCancelOrCompletedTime());
                                                    cancelOrCompletedTimeOriginal = result.getData().getCancelOrCompletedTime();
                                                    cancelOrCompletedTime = convertTimeToString(cancelOrCompletedTimeOriginal);
                                                }
                                                totalPrice = result.getData().getTotalPrice();
                                                orderType = result.getData().getOrderType();
                                                tableNumber = result.getData().getTableNumber();
                                                orderStatus = result.getData().getOrderStatus();
                                                restaurantId = result.getData().getRestaurantId();
                                                restaurantName = result.getData().getRestaurantName();
                                                orderWaitingTime = result.getData().getOrderingWaitingTime();
                                                Log.e("orderWaitingTime############", Integer.toString(orderWaitingTime));
                                                orderFoods = result.getData().getOrderFoods();

                                                setOrderData();
                                                stopProgress();
                                            }
                                        });
                                    } else {
                                        stopProgress();

                                        Toast.makeText(OrderlistDetailActivity.this, "상세정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.e("response failed", Long.toString(orderId));
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<OrderDetailDto>> call, Throwable t) {
                                stopProgress();

                                Toast.makeText(OrderlistDetailActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                Log.e("e = ", t.getMessage());

                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                stopProgress();

                Toast.makeText(OrderlistDetailActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", e.getMessage());

            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "UseCompatLoadingForDrawables"})
    private void setOrderData(){
        switch (orderStatus){
            case ORDERED:
                progressMax = 1;
                setOrderProgress(true);
                binding.tvOrderDetailStatus.setText("결제를 완료했어요");
                binding.tvReceived.setTextColor(getColor(R.color.blue));
                break;
            case CHECKED:
                progressMax = 50;
                setOrderProgress(true);
                binding.tvCheck.setTextColor(getColor(R.color.blue));
                binding.tvCheckTime.setText(checkTime);
                binding.tvCheckTime.setVisibility(View.VISIBLE);
                binding.tvOrderDetailStatus.setText("주문이 접수됐어요");
                binding.tvOrderDetailStatus.setTextColor(getColor(R.color.blue));
                binding.tvReceived.setTextColor(getColor(R.color.gray));
                setRemainTime(checkTimeOriginal);
                break;
            case CANCELED:
                binding.cvOrderProgress.setVisibility(View.GONE);
                binding.tvOrderDetailStatus.setText("취소된 주문입니다");
                binding.tvOrderDetailStatus.setTextColor(getColor(R.color.error));
                binding.tvCanceledTime.setVisibility(View.VISIBLE);
                binding.tvCanceledTime.setText(convertDateToMyFormat(cancelOrCompletedTimeOriginal));
                break;
            case COMPLETED:
                progressMax = 100;
                setOrderProgress(false);
                binding.tvCheckTime.setText(checkTime);
                binding.tvCheckTime.setVisibility(View.VISIBLE);
                binding.tvOrderDetailStatus.setText("주문이 완료됐어요");
                binding.tvOrderDetailCompleteTimeHeader.setVisibility(View.VISIBLE);
                binding.tvOrderDetailCompleteTime.setVisibility(View.VISIBLE);
                binding.tvOrderDetailCompleteTime.setText(convertDateToMyFormat(cancelOrCompletedTimeOriginal));
                binding.clRemainTime.setBackground(getDrawable(R.drawable.background_custom_waiting_grayline));
                binding.tvRemainTimeHeader.setText("주문완료");
                binding.tvComplete.setTextColor(getColor(R.color.blue));
                binding.tvCheck.setTextColor(getColor(R.color.gray));
//                setRemainTime(cancelOrCompletedTimeOriginal);
                binding.tvRemainTime.setText(convertTimeToString(cancelOrCompletedTimeOriginal));
                break;
        }

        binding.tvReceivedTime.setText(receivedTime);
        binding.tvOrderDetailStoreName.setText(restaurantName);
        binding.tvOrderDetailOrderId.setText(myOrderNumber + "번");
        binding.tvOrderDetailOrderTime.setText(convertDateToMyFormat(receivedTimeOriginal));
        if(orderType == OrderType.PACKING){
            binding.tvOrderDetailOrderType.setText("포장");
        }else{
            binding.tvOrderDetailOrderType.setText(String.format("매장 식사(%d번 테이블)",tableNumber));
        }

        String[] orderSummarySplitArr = orderSummary.split(",");
        String[] orderSummaryFirstMenuSplitArr = orderSummarySplitArr[0].split(":");

        int orderSummaryOtherMenuCount = orderSummarySplitArr.length-2;
        if(orderSummarySplitArr.length == 2) {
            binding.tvOrderDetailMenu.setText(orderSummarySplitArr[0]);
        }else{
            binding.tvOrderDetailMenu.setText(String.format("%s 외 %d개", orderSummaryFirstMenuSplitArr[0],orderSummaryOtherMenuCount));
        }

        int sumCount = 0;
        int sumPrice = 0;

        for(OrderFoodDto food : orderFoods){
            sumPrice += (food.getCount()*food.getPrice());
        }
        binding.tvTotalPrice.setText(Utillity.computePrice(sumPrice) + "원");

        initOrderListRecyclerView();
    }

    @SuppressLint("DefaultLocale")
    private String convertTimeToString(String input){
        String result = "";
        Log.e("input123123",input);
        String[] splitData = input.split(" - ");
        Log.e("splitData[0], splitData[1]",splitData[0] + "   " + splitData[1]);

        String[] splitDataFinal = splitData[1].split(":");
        if(Integer.parseInt(splitDataFinal[0]) == 12){
            result = String.format("오후 %s시 %s분 %s초",splitDataFinal[0],splitDataFinal[1],splitDataFinal[2]);
        }
        else if(Integer.parseInt(splitDataFinal[0]) > 12 && Integer.parseInt(splitDataFinal[0]) < 24){
            result = String.format("오후 %d시 %s분 %s초",
                    Integer.parseInt(splitDataFinal[0])%12,
                    splitDataFinal[1],
                    splitDataFinal[2]);
        }
        else if(Integer.parseInt(splitDataFinal[0]) < 12){
            result = String.format("오전 %s시 %s분 %s초",
                    splitDataFinal[0],
                    splitDataFinal[1],
                    splitDataFinal[2]);
        }

        return result;
    }

    private String convertDateToMyFormat(String input){
        // input format -> "2022/06/03 - 23:10:00"

        // splitData[0] = 2022/06/03, splitData[1] = 23:10:00
        String[] splitData = input.split(" - ");
        Log.e("input",input);
        // 오후 11시 10분
        String timeFormat = convertTimeToString(input).substring(0, 10);

        String[] dateFormatArray = splitData[0].split("/");
        String dateFormat = String.format(
                "%s년 %s월 %s일 ",
                dateFormatArray[0],
                dateFormatArray[1],
                dateFormatArray[2]);

        return dateFormat + timeFormat;
    }

    private void setRemainTime(String original){
        Log.e("estimatedWaitingTime", Integer.toString(orderWaitingTime));
        String[] tmpDate = original.split(" - ");
//        String convertDate = tmpDate[0] + tmpDate[1];
//        Log.e("condate",convertDate);
        @SuppressLint("SimpleDateFormat") DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date regTime = null;
        try {
            regTime = hourFormat.parse(tmpDate[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(regTime);

        // TODO : 매장 주문 대기시간 설정 기능 구현할 것
        cal.add(Calendar.MINUTE,orderWaitingTime);

        String result = convertTimeToString(" - " + hourFormat.format(cal.getTime()));

        binding.clRemainTime.setVisibility(View.VISIBLE);
        binding.tvRemainTime.setText(result.substring(0,10));
    }

    private void initOrderListRecyclerView(){
        RecyclerView recyclerView = binding.rvOrderDetailFoodList;
        OrderlistDetailFoodListAdapter orderlistDetailFoodListAdapter = new OrderlistDetailFoodListAdapter(orderFoods, OrderlistDetailActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderlistDetailActivity.this));
        recyclerView.setAdapter(orderlistDetailFoodListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),1));

        binding.progressBarOrderdetailList.setVisibility(View.GONE);
    }


    @Override
    public void onResume(){
        super.onResume();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter("testData"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getExtras().getString("data");
            Log.e("BroadcastReceiver@@@@@@@@@@@@@@@",data);

            if(data != OrderType.WAITING.toString()){
                getOrderData();
            }
        }
    };
}
