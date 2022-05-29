package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.orderingproject.databinding.ActivityWriteReviewBinding;

public class WriteReviewActivity extends AppCompatActivity {

    private ActivityWriteReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}