package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.orderingproject.Dto.EventsDto;
import com.example.orderingproject.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView; //바텀네비뷰
    private ActivityMainBinding binding;

    static ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        if(user == null){
//            startActivity(new Intent(MainActivity.this, StartActivity.class));
//            finish();
//        }

        bottomNavigationView = findViewById(R.id.bottomNavi);

        progressBar = binding.progressBar;

        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new HomeFragment()).commit(); //FrameLayout에 QrFragment.xml띄우기

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    //item 클릭 시 id값을 가져와 FrameLayout에 fragment.xml 띄우기
                    case R.id.item_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new HomeFragment()).commit();
                        break;
                    case R.id.item_orderlist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderlistFragment()).commit();
                        break;
                    case R.id.item_waiting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new WaitingFragment()).commit();
                        break;
                    case R.id.item_mypage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MypageFragment()).commit();
                        break;
                }
                return true;
            }
        });

    }


    public static void showProgress(Activity activity){
        progressBar.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void hideProgress(Activity activity){
        progressBar.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg,Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Activity activity, String msg){
        Toast.makeText(activity, msg,Toast.LENGTH_LONG).show();
    }
}