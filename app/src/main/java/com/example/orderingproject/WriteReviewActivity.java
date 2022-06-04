package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.orderingproject.databinding.ActivityWriteReviewBinding;

public class WriteReviewActivity extends BasicActivity {

    private ActivityWriteReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonFunction();

    }

    private void buttonFunction() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });
    }
}