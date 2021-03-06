package com.example.orderingproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.orderingproject.Dto.EventsDto;
import com.example.orderingproject.Dto.NoticeDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SplashActivity extends Activity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    public static EventPagerAdapter adapter;
    public static int listSize;

    SharedPreferences loginSP;
    SharedPreferences noticeSP;
    String memberId, password, notice;
    Bundle extras;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initData();

        extras = getIntent().getExtras();

        startSplash2();
    }

    private boolean getLocalData(){
        loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        memberId = loginSP.getString("signInId", null);
        password = loginSP.getString("password", null);

        return (memberId != null && password != null);
    }

    private void getNoticeSP(){
        // 1. SP?????? ????????? ?????????
        noticeSP = getSharedPreferences("notice", Activity.MODE_PRIVATE);

        // 2. ????????? ????????? ??????("show")??? ????????????. ????????? default?????? 0??????.
        notice = noticeSP.getString("show", "0");

        // ?????? ?????? 'dd'????????? String????????? ??????
        String today = NoticeInfo.getDate(System.currentTimeMillis());

        // ?????? ?????? - SP??? ????????? ??????
        // ?????? 0??? ???????????? "?????? ?????? ????????????"??? ?????? ?????? ????????? ???????????? ??????
        int noticeData = Integer.parseInt(today) - Integer.parseInt(notice);

        // noticeData ?????? 0??? ????????? setShow(true), ????????? setShow(false)??? ??????
        NoticeInfo.setShow(noticeData != 0);
        // ??? ?????? ????????? MainActivity?????? ???????????? ????????????.
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // preferences??? ????????? ????????? ????????? ????????? ??????
                if(getLocalData()){
                    autoLogin();
                }
                else{
                    // ????????? ????????? ????????? ????????? ????????????
                    clearSharedPreferences();
                    startLoginActivity();
                }
            }
        }, 3000);

    }

    private void autoLogin() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.e("?????? ??????", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            Log.e("token ", token);
                            SignInDto signInDto = new SignInDto(memberId, password, token);
                            new Thread() {
                                @SneakyThrows
                                public void run() {
                                    // login

                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl("http://www.ordering.ml/api/customer/signin/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build();

                                    RetrofitService service = retrofit.create(RetrofitService.class);
                                    Call<ResultDto<CustomerSignInResultDto>> call = service.customerSignIn(signInDto);

                                    call.enqueue(new Callback<ResultDto<CustomerSignInResultDto>>() {
                                        @Override
                                        public void onResponse(Call<ResultDto<CustomerSignInResultDto>> call, Response<ResultDto<CustomerSignInResultDto>> response) {

                                            ResultDto<CustomerSignInResultDto> result = response.body();
                                            if(result.getData() != null){
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        // fcm ?????? ???????????? ???????????? ??? ??????
                                                        if (extras != null) {
                                                            Log.e("bundle ?????? :: LoginActivity", getIntent().getStringExtra("fromFCM_Channel"));
                                                            intent.putExtra("fromFCM_Channel", getIntent().getStringExtra("fromFCM_Channel"));
                                                        }
                                                        UserInfo.setUserInfo(result.getData(), memberId);
                                                        startActivity(intent);

                                                        finish();
                                                    }
                                                });
                                            }
                                            else{
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.e("????????? ?????? ! ", "????????? ?????? ???????????? ???????????? ??????");
                                                        Toast.makeText(getApplicationContext(), "????????? ????????? ???????????? ?????????????????? ?????? ???????????????.",Toast.LENGTH_SHORT).show();
                                                        startLoginActivity();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResultDto<CustomerSignInResultDto>> call, Throwable t) {
                                            Log.e("e = " , t.getMessage());
                                            clearSharedPreferences();
                                            startLoginActivity();
                                        }
                                    });
                                }
                            }.start();

                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.e("token Log", msg);
                        }
                    });


        } catch (Exception e) {
            Log.e("e = " , e.getMessage());
            clearSharedPreferences();
            startLoginActivity();
        }

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
        // ?????????,?????? ????????? ???????????? remoteConfig
        // remoteConfig??? ?????? ????????? url(?????? ??????????????? ???????????? ??????)??? ????????? ????????????.
        // ??????????????? Fetch??? ?????? ????????? 12????????????.
        // ??? ???????????? remoteConfig??? ?????? ???????????? ??? ???????????? 12?????? ????????? ????????? ??? ??? ????????? ?????????
        // ????????? Test???????????? ????????? 0?????? ?????? <- ???????????? ???????????? ?????????
        // ?????? ?????? ?????? ??? ?????? ???????????? ?????? ?????? ?????? ??? ?????? ????????? ????????? ?????? ???????????? ????????? ???????????????
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
                            // fetchAndActivate??? ?????? ?????? ???
                            try {
                                // ?????? remoteConfig
                                List<EventsDto> eventDto = parseEventsJson(remoteConfig.getString("events"));
                                adapter = new EventPagerAdapter((ArrayList<EventsDto>) eventDto,getApplicationContext());
                                listSize = eventDto.size();

                                // ?????? remoteConfig
                                // ?????? ???????????? ?????? setting
                                parseNoticeJson(remoteConfig.getString("notice"));
                                startLoading();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Log.e("fetch&Activate","Completed");

        // SharedPrefereces??? ???????????? ?????? "?????? ?????? ????????????" ??? ????????????
        getNoticeSP();
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
                String title = (String)object.get("title");
                // ???????????? ???????????? gif??? ????????? ??? ?????? ??????????????? ???????????? JsonException??? ???
                // (??????) ??? ?????? ??? ?????? ????????? ???;;
                // optString??? name??? "gif"??? ?????? ????????????, null????????? 2?????? ??????????????? ???????????? ?????????
                String gif = object.optString("isGif", "false");
//                String gif = (String)object.get("isGif");

                eventsDto.setUrls(imageUrl,loadUrl, title, gif);
                urls.add(eventsDto);
            }
        }
        return urls;
    }

    private void parseNoticeJson(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        for(int index = 0; index < jsonArray.length(); index++){
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            if(jsonObject != null){
                JSONObject object = (JSONObject) jsonArray.get(index);
                String imageUrl = (String)object.get("noticeImage");
                String loadUrl = (String)object.get("loadUrl");
                String title = (String)object.get("title");

                // NoticeInfo ????????? & NoticeDto Setting
                NoticeInfo.setNoticeInfo(imageUrl,loadUrl, title);
            }
        }
    }
    private void startLoginActivity(){
        Intent intent= new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearSharedPreferences(){
        SharedPreferences loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = loginSP.edit();
        spEdit.clear();
        spEdit.commit();
    }
}
