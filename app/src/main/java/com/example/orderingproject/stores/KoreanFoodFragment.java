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
    private ArrayList<StoreData> storeList;
    private RecyclerView recyclerView;
    private StoreRecyclerAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FragmentKoreanFoodBinding binding;
    private View v;
    LocationManager locationManager;
    final int REQUEST_CODE_LOCATION = 2;


    public Bundle bundle;
    double longitude;
//    double latitude;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentKoreanFoodBinding.inflate(inflater, container, false);
        v = binding.getRoot();



        // 사용자 위치를 불러오는데 일정 시간이 소요되므로 지연 처리를 해주어야 함.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("* 위도 *", String.valueOf(HomeFragment.longitude));
                Log.e("* 경도 *", String.valueOf(HomeFragment.latitude));

                getStoreListFromServer(KOREAN_FOOD);
            }
        },5000);


        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    public void getStoreListFromServer(FoodCategory foodCategory) {
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
                                            Log.e("latitude", String.valueOf(HomeFragment.latitude));
                                            Log.e("longtitude", String.valueOf(HomeFragment.longitude));

                                            result.getData().forEach(restaurantPreviewWithDistanceDto ->{
                                                //restaurantPreviewWithDistanceDto.getDistanceMeter();
                                                storeList.add(new StoreData(restaurantPreviewWithDistanceDto.getProfileImageUrl(), restaurantPreviewWithDistanceDto.getRestaurantName(), restaurantPreviewWithDistanceDto.getRepresentativeMenus()));
                                                Log.e("매장명", restaurantPreviewWithDistanceDto.getRestaurantName());
                                            });

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

//    // ProgressDialog 생성
//    public void createProgress() {
//        Sprite anim = new ThreeBounce();
//        ProgressBar progressbar = new ProgressBar(this);
//        progressDialog.setMessage("Image Uploading ...");
//
//        anim.setColor(Color.rgb(227, 85, 85));
//        progressDialog.setIndeterminateDrawable(anim);
//    }


}
