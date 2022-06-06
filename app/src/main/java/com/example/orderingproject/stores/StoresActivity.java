package com.example.orderingproject.stores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.orderingproject.BasicActivity;
import com.example.orderingproject.HomeFragment;
import com.example.orderingproject.R;
import com.google.android.material.tabs.TabLayout;

public class StoresActivity extends BasicActivity implements HomeFragment.OnApplySelectedListener {

    //private ActivityStoresBinding binding;
    private TabLayout tabLayout;
    private ViewPager viewPager;

//    public static double longitude;
//    public static double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        startProgress(this);
        KoreanFoodFragment koreanFoodFragment = new KoreanFoodFragment();

//        longitude = getIntent().getDoubleExtra("위도",0);
//        latitude = getIntent().getDoubleExtra("경도",0);
//
//        Log.e("받아온 위도", String.valueOf(longitude));
//        Log.e("받아온 경도", String.valueOf(latitude));
//
//        Bundle bundle = new Bundle();
//        bundle.putDouble("longitude", longitude);
//
//        koreanFoodFragment.setArguments(bundle);

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


    @Override
    public void onCatagoryApplySelected(int longitude) {

    }
}