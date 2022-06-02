package com.example.orderingproject;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.orderingproject.ENUM_CLASS.OrderStatus;
import com.example.orderingproject.databinding.ActivityOrderlistDetailBinding;

public class OrderlistDetailActivity extends BasicActivity {
    private ActivityOrderlistDetailBinding binding;

    OrderStatus orderStatus;
    String orderedTime, checkTime, storeName, orderMenu, orderId, orderType;
    int progressMax, progress;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityOrderlistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initView();
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
        progress = 0;
        orderStatus = OrderStatus.CHECKED;
        orderedTime = "12:00:08";
        storeName = "반반스프링스 경상대점";
        orderMenu = "아메리카노(ICE) 외 1개";
        orderId = "15번";
        orderType = "매장 식사(31번 테이블)";
        checkTime = "12:01:00";
    }

    private void initView(){
        switch (orderStatus){
            case ORDERED:
                progressMax = 1;
                setOrderProgress();
                binding.tvReceived.setTextColor(getColor(R.color.blue));
                binding.tvReceivedTime.setText(orderedTime);
                binding.tvOrderDetailStatus.setText("결제를 완료했어요");
                break;
            case CHECKED:
                Log.e("orderStatus", "Checkd");
                progressMax = 50;
                setOrderProgress();
                binding.tvCheck.setTextColor(getColor(R.color.blue));
                binding.tvCheckTime.setText(checkTime);
                binding.tvOrderDetailStatus.setText("조리가 시작됐어요");
                break;
        }
    }

    private void setOrderProgress(){
        progress++;
        binding.customProgressbarOrder.setProgress(progress);
        if(progress < progressMax){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setOrderProgress();
                }
            },10);
        }
    }
}
