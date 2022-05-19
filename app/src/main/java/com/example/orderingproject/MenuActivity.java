package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.example.orderingproject.databinding.ActivityMenuBinding;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MenuActivity extends BasicActivity {
    private ActivityMenuBinding binding;

    public static String store, service, restaurantName,
                         profileImageUrl, backgroundImageUrl;
    public static TextView BasketCountTextView;
    public int basketCount = UserInfo.getBasketCount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BasketCountTextView = binding.tvBasketcount;

        initData();
        initView();
        initButtonListener();
        //뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout_menu);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage_menu);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 1,3);
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position){
                super.onPageSelected(position);
                // ViewPager 넘길때 마다 수행
                switch (position){
                    case 0 :
                        Log.e("viewpager", "메뉴");
                        Log.e("getHeight", Integer.toString(viewPager2.getChildAt(position).getHeight()));
                        break;
                    case 1 :
                        Log.e("viewpager", "정보");
                        break;
                    case 2 :
                        Log.e("viewpager", "리뷰");
                        break;

                }
//                int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(childView.getWidth(), View.MeasureSpec.EXACTLY);
//                int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(childView.getHeight(), View.MeasureSpec.UNSPECIFIED);
//                childView.measure(wMeasureSpec, hMeasureSpec);
//                if (viewPager2.getLayoutParams().height != childView.getMeasuredHeight()) {
//                    ViewGroup.LayoutParams lp = viewPager2.getLayoutParams();
//                    lp.height = childView.getMeasuredHeight();
//                }


//                View view = viewPager2.getChildAt(position); // this is a method i have in the adapter that returns the fragment's view, by calling fragment.getview()
//                if (view != null) {
//                    view.post(() -> {
//                        int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
//                        int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//                        view.measure(wMeasureSpec, hMeasureSpec);
//                    });
//
//                    if (viewPager2.getLayoutParams().height != view.getMeasuredHeight()) {
//                        ViewGroup.LayoutParams lp = viewPager2.getLayoutParams();
//                        lp.height = view.getMeasuredHeight();
//                        lp.width = view.getWidth();
//                        viewPager2.setLayoutParams(lp);
//
//                        Log.e("asdasd","다름");
//                    }
//                }
            }
        });
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
                if(UserInfo.getBasketCount() != 0) {
                    Intent intent = new Intent(MenuActivity.this, BasketActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MenuActivity.this,"메뉴를 선택해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initData(){
        store = getIntent().getStringExtra("store");
        service = getIntent().getStringExtra("service");
        restaurantName = getIntent().getStringExtra("restaurantName");
        profileImageUrl = getIntent().getStringExtra("profileImageUrl");
        backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrl");

        updateBasket();

        if(basketCount > 0){
            binding.tvBasketcount.setVisibility(View.VISIBLE);
            binding.tvBasketcount.setText(Integer.toString(basketCount));
        }
        Log.e("store", store);
        Log.e("service", service);
        Log.e("restaurantName", restaurantName);
        Log.e("basketCount", Integer.toString(basketCount));
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

    public static void updateBasket(){
        int basketCounts = UserInfo.getBasketCount();
        if(basketCounts != 0){
            if(BasketCountTextView.getVisibility() == View.GONE){
                BasketCountTextView.setVisibility(View.VISIBLE);
            }
            BasketCountTextView.setText(Integer.toString(basketCounts));
        }else{
            BasketCountTextView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBasket();
    }
}