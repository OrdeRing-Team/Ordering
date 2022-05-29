package com.example.orderingproject.stores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.orderingproject.R;
import com.google.android.material.tabs.TabLayout;

public class StoresActivity extends AppCompatActivity {

    //private ActivityStoresBinding binding;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        ViewPager vp = findViewById(R.id.viewpager);
        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);

        TabLayout tab = findViewById(R.id.tab);
        tab.setupWithViewPager(vp);

        ImageButton btn_back = findViewById(R.id.btn_back) ;
        //뒤로가기 버튼 클릭 이벤트
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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