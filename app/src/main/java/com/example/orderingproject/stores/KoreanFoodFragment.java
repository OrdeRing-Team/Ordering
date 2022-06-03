package com.example.orderingproject.stores;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.RestaurantPreviewListReqDto;
import com.example.orderingproject.Dto.response.BookmarkPreviewDto;
import com.example.orderingproject.Dto.response.RestaurantPreviewWithDistanceDto;
import com.example.orderingproject.ENUM_CLASS.FoodCategory;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.ActivityFavStoreListBinding;
import com.example.orderingproject.databinding.FragmentKoreanFoodBinding;
import com.example.orderingproject.favoriteStores.FavStoreAdapter;
import com.example.orderingproject.favoriteStores.FavStoreData;
import com.example.orderingproject.favoriteStores.FavStoreListActivity;
import com.example.orderingproject.stores.StoreRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
    LocationManager locationManager;
    final int REQUEST_CODE_LOCATION = 2;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_korean_food, container, false);
//        //리사이클러뷰
//        recyclerView = (RecyclerView) v.findViewById(R.id.korean_food_list);
//        recyclerView.setHasFixedSize(true);
//
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.scrollToPosition(0);
//        adapter = new StoreRecyclerAdapter(storeList);
//        recyclerView.setAdapter(adapter);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        //사용자의 위치 수신을 위한 세팅
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //사용자의 현재 위치
//        Location userLocation = getMyLocation();
//        if(userLocation!=null){
//            double latitude = userLocation.getLatitude();
//            double longitude = userLocation.getLongitude();
////        userVO.setLat(latitude);
////        userVO.setLon(longitude);
//            System.out.println("////////////현재 내 위치값 : "+latitude+","+longitude);
//            Log.e("위도경도값", String.valueOf(latitude));
//            Log.e("위도경도값", String.valueOf(longitude));
//
//
//            getStoreListFromServer(latitude, longitude, FoodCategory.KOREAN_FOOD);
//
//        }

        return v;
    }

    /*** 사용자의 위치를 수신*/
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한을 요청해야함");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, this.REQUEST_CODE_LOCATION);
            getMyLocation(); //권한 승인하면 즉시 위치값 받는 부분
        }
        else {
            System.out.println("////////////권한요청 안해도됨");
            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


//    private void initDataset() {
//        //for Test
//        storeList = new ArrayList<>();
//        storeList.add(new StoreData(R.drawable.icon, "박씨네 라멘트럭", "4.3","돈코츠 라멘, 탄탄 라멘, 차슈 라멘"));
//        storeList.add(new StoreData(R.drawable.icon, "라화쿵부", "4.9","한그릇 마라탕, 내맘대로 마라탕"));
//        storeList.add(new StoreData(R.drawable.icon, "엽기떡볶이", "5.0","로제떡볶이, 매운떡볶이, 치즈떡볶이"));
//    }


    // 찜 목록 불러오기
    private void getStoreListFromServer(double latitude, double longtitude, FoodCategory foodCategory) {
        ArrayList<StoreData> StoreList = new ArrayList<>();
        RestaurantPreviewListReqDto restaurantPreviewListReqDto = new RestaurantPreviewListReqDto(latitude, longtitude, KOREAN_FOOD);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurants")
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
                                    StoreList.add(new StoreData(restaurantPreviewWithDistanceDto.getProfileImageUrl(), restaurantPreviewWithDistanceDto.getRestaurantName(), restaurantPreviewWithDistanceDto.getRepresentativeMenus()));
                                    Log.e("매장 리스트", String.valueOf(StoreList));
                                });

                                RecyclerView recyclerView = binding.koreanFoodList;
                                StoreRecyclerAdapter StoreAdapter = new StoreRecyclerAdapter(storeList);
                                //FavStoreAdapter favStoreAdapter = new FavStoreAdapter(StoreList, getActivity());
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
}
