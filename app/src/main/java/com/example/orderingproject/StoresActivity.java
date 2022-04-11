package com.example.orderingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.orderingproject.databinding.ActivitySignupBinding;
import com.example.orderingproject.databinding.ActivityStoresBinding;
import com.example.orderingproject.databinding.FragmentHomeBinding;

public class StoresActivity extends AppCompatActivity {

    private ActivityStoresBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        binding = ActivityStoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 메뉴 선택하러 가는 버튼 (임시 !!!!!!) -> 리사이클러뷰로 바뀔 예정
        binding.btnGoSelectMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                finish();   //현재 액티비티 종료
            }
        });

    }
}