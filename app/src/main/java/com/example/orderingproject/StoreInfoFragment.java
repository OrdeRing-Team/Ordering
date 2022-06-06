package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingproject.ENUM_CLASS.RestaurantType;
import com.example.orderingproject.databinding.FragmentStoreInfoBinding;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;


public class StoreInfoFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private MapView mapView;
    private NaverMap naverMap;
    private FragmentStoreInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_store_info, container, false);
        binding = FragmentStoreInfoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initView();

        return view;
    }


    @SuppressLint("SetTextI18n")
    public void initView(){
        binding.tvStoreInfoStoreName.setText(MenuActivity.restaurantNameForInfo);
        binding.tvStoreInfoLocation.setText(MenuActivity.address);
        binding.tvStoreInfoOwnerName.setText(MenuActivity.ownerName);
        binding.tvStoreInfoOrderWaitingTime.setText(Integer.toString(MenuActivity.orderWaitingTime)+"분");
        if(MenuActivity.restaurantType == RestaurantType.FOR_HERE_TO_GO) {
            binding.tvStoreInfoRestauranttype.setText("매장식사, 포장");
        }else binding.tvStoreInfoRestauranttype.setText("포장");
        binding.tvStoreInfoTableCount.setText(Integer.toString(MenuActivity.tableCount)+"개");
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap){

        this.naverMap = naverMap;

        //배경 지도 선택
        naverMap.setMapType(NaverMap.MapType.Basic);

        //위치 및 각도 조정
        CameraPosition cameraPosition = new CameraPosition(
                new LatLng(MenuActivity.storeLatitude, MenuActivity.storeLongitude),   // 위치 지정
                17); // 줌 레벨
        naverMap.setCameraPosition(cameraPosition);

        Marker marker = new Marker();
        marker.setPosition(new LatLng(MenuActivity.storeLatitude, MenuActivity.storeLongitude));
        marker.setCaptionText(MenuActivity.restaurantNameForInfo);
        marker.setMap(naverMap);

    }

    @Override
    public void onStart()
    {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}