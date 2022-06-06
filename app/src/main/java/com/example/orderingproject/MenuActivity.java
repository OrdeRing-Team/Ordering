package com.example.orderingproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewDto;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.Dto.response.RestaurantInfoDto;
import com.example.orderingproject.Dto.response.ReviewPreviewDto;
import com.example.orderingproject.ENUM_CLASS.FoodCategory;
import com.example.orderingproject.ENUM_CLASS.RestaurantType;
import com.example.orderingproject.databinding.ActivityMenuBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
    public static String notice = null;
    public static double storeLatitude;
    public static double storeLongitude;
    public static String ownerName;
    public static String restaurantNameForInfo;
    public static String address;
    public static RestaurantType restaurantType;
    public static FoodCategory foodCategory;
    public static Integer tableCount;
    public static Integer orderWaitingTime;

    public int basketCount = UserInfo.getBasketCount();

    private Long favStoreId;

    public static int totalStars, oneStar, twoStars, threeStars, fourStars, fiveStars;
    public static List<ReviewPreviewDto> reviewList;
    public static float reviewTotalRating;

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
        getStoreInfo();

    }

    private void initButtonListener() {
        binding.btnBackToManageFrag.setOnClickListener(view -> finish());

        binding.btnBasket.setOnClickListener(view -> {
            if (UserInfo.getBasketCount() != 0) {
                Intent intent = new Intent(MenuActivity.this, BasketActivity.class);
                intent.putExtra("store", store);
                intent.putExtra("service", service);
                intent.putExtra("restaurantName", restaurantName);
                startActivity(intent);
            } else {
                Toast.makeText(MenuActivity.this, "메뉴를 선택해주세요.", Toast.LENGTH_SHORT).show();
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


    private void initData() {
        notice = null;
        totalStars = 0;
        oneStar = 0;
        twoStars = 0;
        threeStars = 0;
        fourStars = 0;
        fiveStars = 0;
        reviewList = null;
        reviewTotalRating = 0;
        storeLatitude = 0;
        storeLongitude = 0;

        if (getIntent() != null) {
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
                    if (profileImageUrl != null) {
                        Log.e("profileImageUrl", profileImageUrl);
                    }
                    if (backgroundImageUrl != null) {
                        Log.e("backgroundImageUrl", backgroundImageUrl);
                    }

                    Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
                    Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
                    binding.tvStoreName.setText(restaurantName);
                    if (profileImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if (backgroundImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();

                    updateBasket();

                    if (basketCount > 0) {
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
                    if (profileImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if (backgroundImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();
                    updateBasket();

                    if (basketCount > 0) {
                        binding.tvBasketcount.setVisibility(View.VISIBLE);
                        binding.tvBasketcount.setText(Integer.toString(basketCount));
                    }
                    break;

                case "orderList":
                    Log.e("this Intent", "came from orderList");
                    fromTo = "orderList";
                    store = getIntent().getStringExtra("storeId");
                    restaurantName = getIntent().getStringExtra("restaurantName");
                    profileImageUrl = getIntent().getStringExtra("profileImageUrl");
                    service = "takeout";
                    setStoreBackgroundImage(store, this);
                    Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
                    binding.tvStoreName.setText(restaurantName);
                    if (profileImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if (backgroundImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();
                    updateBasket();
                    break;


                default:
                    Log.e("this Intent", "came from waitingFrag");
                    fromTo = "waitingFrag";
                    store = getIntent().getStringExtra("storeId");
                    restaurantName = getIntent().getStringExtra("storeName");
                    profileImageUrl = getIntent().getStringExtra("profileImageUrlfromFav");
                    backgroundImageUrl = getIntent().getStringExtra("backgroundImageUrlfromFav");
                    service = "waiting";

                    Glide.with(this).load(profileImageUrl).into(binding.ivStoreIcon);
                    Glide.with(this).load(backgroundImageUrl).into(binding.ivSigmenu);
                    binding.tvStoreName.setText(restaurantName);
                    if (profileImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivStoreIcon);
                    if (backgroundImageUrl == null)
                        Glide.with(this).load(R.drawable.icon).into(binding.ivSigmenu);
                    stopProgress();

                    binding.btnBasket.setVisibility(View.GONE);
                    break;
            }

        }

    }

    private void initView() {

        // 툴바 타이틀 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(restaurantName);

        // 뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout_menu);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage_menu);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 1, 3);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        if (position == 0) {
                            tab.setText("메뉴");
                        } else if (position == 1) {
                            tab.setText("정보");
                        } else {
                            tab.setText("리뷰");
                        }
                    }
                }).attach();

    }

    public static void updateBasket() {
        int basketCounts = UserInfo.getBasketCount();
        if (basketCounts != 0) {
            if (BasketCountTextView.getVisibility() == View.GONE) {
                BasketCountTextView.setVisibility(View.VISIBLE);
            }
            BasketCountTextView.setText(Integer.toString(basketCounts));
        } else {
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
                Log.e("e = ", t.getMessage());
            }
        });
    }


    // 매장 찜 상태 불러오기
    public void getFavStoreIdFromServer() {
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
                                result.getData().forEach(bookmarkPreviewDto -> {
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

    private void setStoreBackgroundImage(String storeId, Activity activity) {
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + storeId + "/preview/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantPreviewDto>> call = service.storePreview(Long.parseLong(storeId));

                    call.enqueue(new Callback<ResultDto<RestaurantPreviewDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantPreviewDto>> call, Response<ResultDto<RestaurantPreviewDto>> response) {

                            ResultDto<RestaurantPreviewDto> result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            backgroundImageUrl = result.getData().getBackgroundImageUrl();

                                            Glide.with(activity).load(backgroundImageUrl).into(binding.ivSigmenu);
                                        }
                                    });
                                } else {
                                    Toast.makeText(activity, "매장 이미지 로드에 실패하였습니다.\n다시 시도해 주세요", Toast.LENGTH_LONG).show();

                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantPreviewDto>> call, Throwable t) {

                            Toast.makeText(activity, "매장 이미지 로드에 실패하였습니다.\n다시 시도해 주세요", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());

                        }
                    });
                }
            }.start();

        } catch (Exception e) {

            Toast.makeText(activity, "일시적인 오류가 발생하였습니다\n다시 시도해 주세요", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("activity").equals("fromQR")) {
            updateBasket();
        }
    }

    public void getStoreInfo() {
        try {
            startProgress(this);
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + store + "/info/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantInfoDto>> call = service.getStoreNoticeAndCoordinate(Long.valueOf(store));

                    call.enqueue(new Callback<ResultDto<RestaurantInfoDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantInfoDto>> call, Response<ResultDto<RestaurantInfoDto>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<RestaurantInfoDto> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notice = result.getData().getNotice();
                                            if (notice != null) {
                                                Log.e("매장 공지사항 ", notice);
                                            }
                                            storeLatitude = result.getData().getLatitude();
                                            storeLongitude = result.getData().getLongitude();
                                            ownerName = result.getData().getOwnerName();
                                            restaurantNameForInfo = result.getData().getRestaurantName();
                                            address = result.getData().getAddress();
                                            restaurantType = result.getData().getRestaurantType();
                                            foodCategory = result.getData().getFoodCategory();
                                            tableCount = result.getData().getTableCount();
                                            orderWaitingTime = result.getData().getOrderingWaitingTime();

                                            initReviewRecyclerView();

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantInfoDto>> call, Throwable t) {
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Log.e("e = ", e.getMessage());
        }
    }

    public void setRatings(float rating, String ratingString) {
        binding.ratingBar.setRating(rating);
        binding.tvScore.setText(ratingString);
    }

    private void initReviewRecyclerView() {
        try {
            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + store + "/reviews/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<ReviewPreviewDto>>> call = service.getReviewList(Long.parseLong(store));

                    call.enqueue(new Callback<ResultDto<List<ReviewPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<ReviewPreviewDto>>> call, Response<ResultDto<List<ReviewPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<ReviewPreviewDto>> result;
                                result = response.body();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (ReviewPreviewDto i : result.getData()) {
                                            reviewTotalRating += i.getRating();
                                            switch ((int) i.getRating()) {
                                                case 1:
                                                    oneStar++;
                                                    break;
                                                case 2:
                                                    twoStars++;
                                                    break;
                                                case 3:
                                                    threeStars++;
                                                    break;
                                                case 4:
                                                    fourStars++;
                                                    break;
                                                case 5:
                                                    fiveStars++;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        totalStars = oneStar + twoStars + threeStars + fourStars + fiveStars;
                                        if (result.getData().size() != 0) {
                                            reviewTotalRating /= result.getData().size();
                                        } else {
                                            reviewTotalRating = 0;
                                        }
                                        reviewList = result.getData();

                                        setRatings(reviewTotalRating, Float.toString(reviewTotalRating));

                                        Log.e("reviewList", "########################");
                                        stopProgress();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<ReviewPreviewDto>>> call, Throwable t) {
                            Toast.makeText(MenuActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            stopProgress();

                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(MenuActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            stopProgress();

        }
    }
}