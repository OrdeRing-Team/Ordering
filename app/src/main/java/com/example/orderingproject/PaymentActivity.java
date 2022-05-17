package com.example.orderingproject;

import android.os.Bundle;
import android.view.View;

import com.example.orderingproject.databinding.ActivityPaymentBinding;

public class PaymentActivity extends BasicActivity {
    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initButtonListener();

    }

    private void initButtonListener() {
        binding.btnBackPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });
    }

    private void initData() {

    }
}