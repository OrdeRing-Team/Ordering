package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.orderingproject.databinding.ActivitySignupBinding;
import com.example.orderingproject.databinding.ActivityStoresBinding;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class StoresActivity extends AppCompatActivity {

    private ActivityStoresBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        binding = ActivityStoresBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 0,11);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        if (position == 0) {
                            tab.setText("한식");
                        }
                        else if (position ==1){
                            tab.setText("분식");
                        }
                        else if (position ==2){
                            tab.setText("카페·디저트");
                        }
                        else if (position ==3){
                            tab.setText("돈까스·회·초밥");
                        }
                        else if (position ==4){
                            tab.setText("치킨");
                        }
                        else if (position ==5){
                            tab.setText("피자");
                        }
                        else if (position ==6){
                            tab.setText("아시안·양식");
                        }
                        else if (position ==7){
                            tab.setText("중식");
                        }
                        else if (position ==8){
                            tab.setText("족발·보쌈");
                        }
                        else if (position ==9){
                            tab.setText("찜·탕");
                        }
                        else {
                            tab.setText("패스트푸드");
                        }
                    }
                }).attach();


        //뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        // 메뉴 선택하러 가는 버튼 (임시 !!!!!!) -> 리사이클러뷰로 바뀔 예정
//        binding.btnGoSelectMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent= new Intent(getApplicationContext(), MenuActivity.class);
//                startActivity(intent);
//                finish();   //현재 액티비티 종료
//            }
//        });

    }



}