package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    private boolean likeState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BasketCountTextView = binding.tvBasketcount;

        startProgress(this);

        initData();
        initView();
        initButtonListener();



    }
    private void initButtonListener(){
        binding.btnBackToManageFrag.setOnClickListener(view -> finish());

        binding.btnBasket.setOnClickListener(view -> {
            if(UserInfo.getBasketCount() != 0) {
                Intent intent = new Intent(MenuActivity.this, BasketActivity.class);
                intent.putExtra("store", store);
                intent.putExtra("service", service);
                intent.putExtra("restaurantName", restaurantName);
                startActivity(intent);
            }
            else{
                Toast.makeText(MenuActivity.this,"메뉴를 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
        });

        // 좋아요 버튼 이벤트
        binding.btnLikeEmpty.setOnClickListener(view -> {
            likeState = !likeState;
            binding.btnLikeEmpty.setVisibility(View.GONE);
            binding.btnLikeFull.setVisibility(View.VISIBLE);
        });

        binding.btnLikeFull.setOnClickListener(view -> {
            likeState = !likeState;
            binding.btnLikeEmpty.setVisibility(View.VISIBLE);
            binding.btnLikeFull.setVisibility(View.GONE);
        });
    }


    private void initData(){
        if(getIntent() != null) {
            store = getIntent().getStringExtra("store");
            service = getIntent().getStringExtra("service");
            restaurantName = getIntent().getStringExtra("restaurantName");
            profileImageUrl = getIntent().getStringExtra("profileImageUrl");
            backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrl");
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

            Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
            Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
            binding.tvStoreName.setText(restaurantName);
            if(profileImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
            if(backgroundImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
            stopProgress();

        }
        updateBasket();

        if(basketCount > 0){
            binding.tvBasketcount.setVisibility(View.VISIBLE);
            binding.tvBasketcount.setText(Integer.toString(basketCount));
        }
    }


    private void initView(){

        //툴바 타이틀 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(restaurantName);

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