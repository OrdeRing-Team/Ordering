package com.example.orderingproject.stores;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewListReqDto;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.Dto.response.RestaurantPreviewWithDistanceDto;
import com.example.orderingproject.ENUM_CLASS.FoodCategory;
import com.example.orderingproject.HomeFragment;
import com.example.orderingproject.MenuActivity;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.ActivityFavStoreListBinding;
import com.example.orderingproject.databinding.FragmentKoreanFoodBinding;
import com.example.orderingproject.favoriteStores.FavStoreAdapter;
import com.example.orderingproject.favoriteStores.FavStoreData;
import com.example.orderingproject.favoriteStores.FavStoreListActivity;
import com.example.orderingproject.stores.StoreRecyclerAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class KoreanFoodFragment extends Fragment {

    private static final FoodCategory KOREAN_FOOD = FoodCategory.KOREAN_FOOD;
    private FragmentKoreanFoodBinding binding;
    private View v;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentKoreanFoodBinding.inflate(inflater, container, false);
        v = binding.getRoot();

        delayFunction();
        refreshStoreList();

        return v;
    }


    // 사용자 위치 기반 매장 리스트 가져오기
    public void getStoreListFromServer(FoodCategory foodCategory) {

        Log.e("사용자 위도 from HomeFrag", String.valueOf(HomeFragment.longitude));
        Log.e("사용자 경도 from HomeFrag", String.valueOf(HomeFragment.latitude));

        ArrayList<StoreData> storeList = new ArrayList<>();
        RestaurantPreviewListReqDto restaurantPreviewListReqDto = new RestaurantPreviewListReqDto(HomeFragment.latitude, HomeFragment.longitude, foodCategory);

        try {
            Log.e("foodcategory", String.valueOf(foodCategory));

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurants/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<RestaurantPreviewWithDistanceDto>>> call = service.getStoreList(restaurantPreviewListReqDto);

                    call.enqueue(new Callback<ResultDto<List<RestaurantPreviewWithDistanceDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<RestaurantPreviewWithDistanceDto>>> call, Response<ResultDto<List<RestaurantPreviewWithDistanceDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<RestaurantPreviewWithDistanceDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            result.getData().forEach(restaurantPreviewWithDistanceDto ->{
                                                //restaurantPreviewWithDistanceDto.getDistanceMeter();
                                                storeList.add(new StoreData(restaurantPreviewWithDistanceDto.getProfileImageUrl(), restaurantPreviewWithDistanceDto.getRestaurantName(), restaurantPreviewWithDistanceDto.getRepresentativeMenus()));
                                                Log.e("매장명", restaurantPreviewWithDistanceDto.getRestaurantName());
                                            });

                                            // 주변 매장이 없을 경우 예외 처리
                                            Log.e("storeList's size", String.valueOf(storeList.size()));
                                            if (storeList.size() == 0) { binding.tvEmptyStores.setVisibility(View.VISIBLE); }
                                            else { binding.tvEmptyStores.setVisibility(View.GONE); }

                                            // 리사이클러뷰 연결
                                            RecyclerView recyclerView = binding.koreanFoodList;
                                            StoreRecyclerAdapter StoreAdapter = new StoreRecyclerAdapter(storeList);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(StoreAdapter);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<RestaurantPreviewWithDistanceDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });

                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }


    // 데이터 업로드 지연 처리 함수
    private void delayFunction() {

        binding.progressBar.setVisibility(View.VISIBLE);

        // 사용자 위치를 불러오는데 일정 시간이 소요되므로 지연 처리를 꼭 해주어야 함. ( -> 좀 오래 걸리긴 하는데... )
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getStoreListFromServer(KOREAN_FOOD);
                binding.progressBar.setVisibility(View.GONE);
            }

        },4000);

    }


    // 스크롤 새로고침
    private void refreshStoreList() {
        SwipeRefreshLayout mSwipeRefreshLayout = v.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //ft.commit();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStoreListFromServer(KOREAN_FOOD);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });
    }


}
