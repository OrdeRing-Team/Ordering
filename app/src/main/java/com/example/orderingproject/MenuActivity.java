package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.example.orderingproject.databinding.ActivityMenuBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MenuActivity extends AppCompatActivity {
    private ActivityMenuBinding binding;

    public static String store, service, restaurantName,
                         profileImageUrl, backgroundImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initView();
        initButtonListener();
        //뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout_menu);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage_menu);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 1,3);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        if (position == 0) {
                            tab.setText("메뉴");
                        }
                        else if (position == 1) {
                            tab.setText("정보");
                        }
                        else {
                            tab.setText("리뷰");
                        }
                    }
                }).attach();
    }
    private void initButtonListener(){
        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuActivity.this,"장바구니 버튼 클릭",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initData(){
        store = getIntent().getStringExtra("store");
        service = getIntent().getStringExtra("service");
        restaurantName = getIntent().getStringExtra("restaurantName");
        profileImageUrl = getIntent().getStringExtra("profileImageUrl");
        backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrl");

        Log.e("store", store);
        Log.e("service", service);
        Log.e("restaurantName", restaurantName);
        if(profileImageUrl != null) {
            Log.e("profileImageUrl", profileImageUrl);
        }
        if(backgroundImageUrl != null) {
            Log.e("backgroundImageUrl", backgroundImageUrl);
        }
    }

    private void initView(){
        Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
        Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
        binding.tvStoreName.setText(restaurantName);
        if(profileImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
        if(backgroundImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
    }
}