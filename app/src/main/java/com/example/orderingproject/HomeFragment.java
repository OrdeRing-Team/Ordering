package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.orderingproject.Dto.EventsDto;
import com.example.orderingproject.databinding.BottomSheetDialogNoticeBinding;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private View view;

    private FragmentHomeBinding binding;

    int currentPage = SplashActivity.adapter.getItemCount() / 2;

    String eventPageDescript;
    int listSize;

    Timer timer;
    final long DELAY_MS = 5000;  // (초기 웨이팅 타임) ex) 앱 로딩 후 5초 뒤 플립됨.
    final long PERIOD_MS = 5000; // 5초 주기로 배너 이동

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();
        initViews();
        initButtonListener();
        return view;
    }

    private void initButtonListener() {
        binding.btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StoresActivity.class);
                startActivity(intent);
                //getActivity().finish();   //현재 액티비티 종료
            }
        });

        binding.btnQrCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        // 배너 데이터
        if (SplashActivity.adapter != null) {
            this.listSize = SplashActivity.listSize;

            binding.vpEvent.setAdapter(SplashActivity.adapter);
            Log.e("adapterItemCount", Integer.toString(SplashActivity.adapter.getItemCount()));

            binding.vpEvent.setCurrentItem(currentPage, false);

            binding.vpEvent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // 스크롤 했을 때 현재 페이지를 설정해준다.
                    // 자동 스크롤의 경우 currentPage의 변수 값을 현재 position으로 잡는데
                    // 2페이지에서 사용자가 1페이지로 슬라이드 했을 때
                    // 자동 스크롤 시 다음 페이지를 3번이 아닌 2번으로 다시 바꿔줌
                    currentPage = position;
                    if(timer != null){
                        // 타이머 종료 후 재실
                        timer.cancel();
                        timer = null;

                        timerStart();
                    }

                    // 페이지가 스크롤(자동스크롤, 사용자 직접 스크롤 모두 포함)될 때마다 페이지 번호를 갱신
                    eventPageDescript = getString(R.string.EventViewPagerDescript, position % listSize+1, listSize);

                    binding.tvPagenum.setText(eventPageDescript);
                }
            });
        } else {
            Toast.makeText(getActivity(), "배너 로딩 중 오류가 발생했습니다.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews(){
        // 초기 실행 시 페이지는 1번으로 설정하고, 그 이후는 initData()의 onPageSelected를 따름
        eventPageDescript = getString(R.string.EventViewPagerDescript,1, listSize);
        binding.tvPagenum.setText(eventPageDescript);
    }

    @Override
    public void onResume() {
        super.onResume();

        timerStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 다른 탭으로 이동 시 타이머 중지시킴
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void timerStart() {
        // Adapter 세팅 후 타이머 실행
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int nextPage = currentPage + 1;
                binding.vpEvent.setCurrentItem(nextPage, true);
                currentPage = nextPage;
            }
        };
        timer = new Timer();
        // thread에 작업용 thread 추가
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }
}