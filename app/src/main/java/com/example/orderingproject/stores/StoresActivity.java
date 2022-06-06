package com.example.orderingproject.stores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.orderingproject.BasicActivity;
import com.example.orderingproject.HomeFragment;
import com.example.orderingproject.R;
import com.google.android.material.tabs.TabLayout;

public class StoresActivity extends BasicActivity implements HomeFragment.OnApplySelectedListener {

    //private ActivityStoresBinding binding;

    // 사용자 위치 좌표값 전역 변수
    public static double longitude;
    public static double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        startProgress(this);
        initViewPager();

        ImageButton btn_back = findViewById(R.id.btn_back) ;
        //뒤로가기 버튼 클릭 이벤트
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public void onCatagoryApplySelected(int longitude) {

    }

    private void initViewPager() {

        longitude = getIntent().getDoubleExtra("위도",0);
        latitude = getIntent().getDoubleExtra("경도",0);

        Log.e("받아온 위도 from HomeFrag", String.valueOf(longitude));
        Log.e("받아온 경도 from HomeFrag", String.valueOf(latitude));

        ViewPager vp = findViewById(R.id.viewpager);

        TabLayout tab = findViewById(R.id.tab);
        tab.setupWithViewPager(vp);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);

        stopProgress();
    }

}