package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.request.WaitingRegisterDto;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.databinding.ActivityMenuBinding;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends BasicActivity {
    private ActivityMenuBinding binding;

    public static String store, service, restaurantName,
                         profileImageUrl, backgroundImageUrl, fromTo;

    public static TextView BasketCountTextView;

    public int basketCount = UserInfo.getBasketCount();

    private Long favStoreId;

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
        getFavStoreIdFromServer();

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

        // 찜 버튼 이벤트
        binding.btnLikeEmpty.setOnClickListener(view -> {
            binding.btnLikeEmpty.setVisibility(View.GONE);
            binding.btnLikeFull.setVisibility(View.VISIBLE);
            setFavStore();
        });

        binding.btnLikeFull.setOnClickListener(view -> {
            binding.btnLikeEmpty.setVisibility(View.VISIBLE);
            binding.btnLikeFull.setVisibility(View.GONE);
            deleteFavStore();
        });
    }


    private void initData(){
        if(getIntent() != null) {
            switch (getIntent().getStringExtra("activity")) {
                case "fromQR":
                    fromTo = "QR";
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

                    updateBasket();

                    if(basketCount > 0){
                        binding.tvBasketcount.setVisibility(View.VISIBLE);
                        binding.tvBasketcount.setText(Integer.toString(basketCount));
                    }
                    break;

                case "favActivity":
                    Log.e("this Intent", "came from favActivity");
                    fromTo = "favActivity";
                    store = getIntent().getStringExtra("storeId");
                    restaurantName = getIntent().getStringExtra("storeName");
                    profileImageUrl = getIntent().getStringExtra("profileImageUrlfromFav");
                    backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrlfromFav");
                    service = "takeout";

                    Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
                    Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
                    binding.tvStoreName.setText(restaurantName);
                    if(profileImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if(backgroundImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();

                    binding.btnBasket.setVisibility(View.GONE);

                    break;
                    
                default:
                    Log.e("this Intent", "came from waitingFrag");
                    fromTo = "waitingFrag";
                    store = getIntent().getStringExtra("storeId");
                    restaurantName = getIntent().getStringExtra("storeName");
                    profileImageUrl = getIntent().getStringExtra("profileImageUrlfromFav");
                    backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrlfromFav");
                    service = "takeout";

                    Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
                    Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
                    binding.tvStoreName.setText(restaurantName);
                    if(profileImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if(backgroundImageUrl == null) Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();

                    binding.btnBasket.setVisibility(View.GONE);
            }

        }

    }


    private void initView(){

        // 툴바 타이틀 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(restaurantName);

        // 뷰페이저 세팅
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

    // 매장 찜하기 -> 찜한 매장으로 서버 업로드
    private void setFavStore() {
        String url = "http://www.ordering.ml/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ResultDto<Long>> call = service.setFavStore(UserInfo.getCustomerId(), Long.valueOf(store));

        call.enqueue(new Callback<ResultDto<Long>>() {
            @Override
            public void onResponse(Call<ResultDto<Long>> call, Response<ResultDto<Long>> response) {

                if (response.isSuccessful()) {
                    ResultDto<Long> result;
                    result = response.body();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("setFavStoreId", String.valueOf(result.getData()));
                            Toast.makeText(MenuActivity.this, "찜 등록", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResultDto<Long>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }


    // 매장 찜 취소
    private void deleteFavStore() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/customer/bookmark/" + favStoreId + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        Call<ResultDto<Boolean>> call;
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        call = retrofitService.deleteFavStore(favStoreId);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MenuActivity.this, "찜 해제", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }


    // 매장 찜 상태 불러오기
    public void getFavStoreIdFromServer(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/bookmarks/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService service = retrofit.create(RetrofitService.class);
        Call<ResultDto<List<BookmarkPreviewDto>>> call = service.getFavStoreList(UserInfo.getCustomerId());

        call.enqueue(new Callback<ResultDto<List<BookmarkPreviewDto>>>() {
            @Override
            public void onResponse(Call<ResultDto<List<BookmarkPreviewDto>>> call, Response<ResultDto<List<BookmarkPreviewDto>>> response) {

                if (response.isSuccessful()) {
                    ResultDto<List<BookmarkPreviewDto>> result;
                    result = response.body();
                    if (result.getData() != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                result.getData().forEach(bookmarkPreviewDto ->{
                                    if (store.equals(String.valueOf(bookmarkPreviewDto.getRestaurantId()))) {
                                        // 찜 매장 리스트에서 매장아이디가 현재 메뉴엑티비티의 매장아이디와 같은 경우에 찜 버튼을 채워진 하트로 변경.
                                        // 즉, 찜 매장 리스트에 해당 매장아이디가 존재한다면 채워진 하트 VISIBLE.
                                        favStoreId = bookmarkPreviewDto.getBookmarkId();
                                        binding.btnLikeEmpty.setVisibility(View.GONE);
                                        binding.btnLikeFull.setVisibility(View.VISIBLE);
                                    }
                                });
                                Log.e("favStoreId from Server", String.valueOf(favStoreId));
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultDto<List<BookmarkPreviewDto>>> call, Throwable t) {
                Toast.makeText(MenuActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("activity").equals("fromQR")) {
            updateBasket();
        }
    }
}