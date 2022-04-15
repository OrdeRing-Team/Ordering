package com.example.orderingproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.orderingproject.Dto.EventsDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends Activity {

    public static EventPagerAdapter adapter;
    public static int listSize;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startSplash2();
        startLoading();
        initData();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent= new Intent(getApplicationContext(), StartActivity.class);
                startActivity(intent);
                finish();   //현재 액티비티 종료
            }
        }, 3000);

    }

    private void startSplash2() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView splash1 = findViewById(R.id.splash1img);
                splash1.setVisibility(View.GONE);
                ImageView splash2 = findViewById(R.id.splash2img);
                splash2.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }
    private void initData(){
        // 이벤트,쿠폰 배너에 사용되는 remoteConfig
        // remoteConfig를 통해 이미지 url(파베 스토리지에 업로드된 사진)을 실시간 가져온다.
        // 기본적으로 Fetch의 시간 간격은 12시간이다.
        // 앱 사용자가 remoteConfig를 한번 받아오면 12시간 동안은 최신화 할 수 없다는 말이다
        // 지금은 Test단계이기 때문에 0으로 설정 <- 바로바로 최신화가 반영됨
        // 너무 많은 요청 시 파베 자체에서 앱을 블락 시킬 수 있기 때문에 개발을 마친 상황에선 시간을 조정해야함
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            // fetchAndActivate가 성공 했을 때
                            try {
                                List<EventsDto> eventDto = parseEventsJson(remoteConfig.getString("events"));
                                adapter = new EventPagerAdapter((ArrayList<EventsDto>) eventDto,getApplicationContext());
                                listSize = eventDto.size();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Log.e("fetch&Activate","Completed");
    }

    private List<EventsDto> parseEventsJson(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        List<EventsDto> urls = new ArrayList<>();
        for(int index = 0; index < jsonArray.length(); index++){
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if(jsonObject != null){
                EventsDto eventsDto = new EventsDto();
                JSONObject object = (JSONObject) jsonArray.get(index);
                String imageUrl = (String)object.get("imageUrl");
                String loadUrl = (String)object.get("loadUrl");
                eventsDto.setUrls(imageUrl,loadUrl);
                urls.add(eventsDto);
            }
        }
        return urls;
    }
}
