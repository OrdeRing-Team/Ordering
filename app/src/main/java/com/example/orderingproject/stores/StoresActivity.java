package com.example.orderingproject.stores;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.LocationListener;
import android.location.LocationManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.orderingproject.BasicActivity;
import com.example.orderingproject.HomeFragment;
import com.example.orderingproject.MainActivity;
import com.example.orderingproject.R;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class StoresActivity extends BasicActivity implements HomeFragment.OnApplySelectedListener {

    //private ActivityStoresBinding binding;

    // 사용자 위치 좌표값 전역 변수
    public static double longitude;
    public static double latitude;
    //ExecutorService executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stores);
        // 위치 관리자 객체 참조하기
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            // 가장최근 위치정보 가져오기
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            // 위치정보를 원하는 시간, 거리마다 갱신해준다.
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    });
        }

        //startProgress(this);

        ImageButton btn_back = findViewById(R.id.btn_back);
        //뒤로가기 버튼 클릭 이벤트
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getFragment();
    }

    private void getFragment() {
        ViewPager vp = findViewById(R.id.viewpager);

        TabLayout tab = findViewById(R.id.tab);
        tab.setupWithViewPager(vp);

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length > 0) {

            // 권한 허가
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }

                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                getFragment();

            } else {
                Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "오류", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCatagoryApplySelected(int longitude) {

    }
}