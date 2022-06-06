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
        startLoading();
    }

    private boolean getLocalData(){
        loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        memberId = loginSP.getString("signInId", null);
        password = loginSP.getString("password", null);

        return (memberId != null && password != null);
    }

    private void getNoticeSP(){
        // 1. SP에서 날짜를 가져옴
        noticeSP = getSharedPreferences("notice", Activity.MODE_PRIVATE);

        // 2. 최근에 저장한 날짜("show")를 가져온다. 없다면 default값은 0이다.
        notice = noticeSP.getString("show", "0");

        // 현재 날짜 'dd'형태의 String값으로 저장
        String today = NoticeInfo.getDate(System.currentTimeMillis());

        // 현재 날짜 - SP에 저장된 날짜
        // 만약 0이 아니라면 "오늘 하루 그만보기"를 누른 날과 오늘이 다르다는 것임
        int noticeData = Integer.parseInt(today) - Integer.parseInt(notice);

        // noticeData 값이 0이 아니면 setShow(true), 맞으면 setShow(false)로 설정
        NoticeInfo.setShow(noticeData != 0);
        // 이 값을 가지고 MainActivity에서 조건문을 실행한다.
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // preferences에 로그인 정보가 있으면 로그인 처리
                if(getLocalData()){
                    autoLogin();
                }
                else{
                    // 로그인 정보가 없으면 로그인 화면으로
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
                                Log.e("토큰 조회", "Fetching FCM registration token failed", task.getException());
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
                                                        // fcm 푸시 클릭으로 들어왔을 때 처리
                                                        if (extras != null) {
                                                            Log.e("bundle 유효 :: LoginActivity", getIntent().getStringExtra("fromFCM_Channel"));
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
                                                        Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
                                                        Toast.makeText(getApplicationContext(), "로그인 정보가 바뀌어서 자동로그인이 해제 되었습니다.",Toast.LENGTH_SHORT).show();
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
        // 이벤트,쿠폰 배너에 사용되는 remoteConfig
        // remoteConfig를 통해 이미지 url(파베 스토리지에 업로드된 사진)을 실시간 가져온다.
        // 기본적으로 Fetch의 시간 간격은 12시간이다.
        // 앱 사용자가 remoteConfig를 한번 받아오면 그 사용자는 12시간 동안은 최신화 할 수 없다는 말이다
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
                                // 배너 remoteConfig
                                List<EventsDto> eventDto = parseEventsJson(remoteConfig.getString("events"));
                                adapter = new EventPagerAdapter((ArrayList<EventsDto>) eventDto,getApplicationContext());
                                listSize = eventDto.size();

                                // 공지 remoteConfig
                                // 아래 함수에서 바로 setting
                                parseNoticeJson(remoteConfig.getString("notice"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Log.e("fetch&Activate","Completed");

        // SharedPrefereces에 저장되어 있는 "오늘 하루 그만보기" 값 가져오기
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
                // 폰에서는 에러없이 gif가 실행이 됨 근데 애뮬에서는 이상하게 JsonException이 뜸
                // (해결) 앱 삭제 후 다시 까니까 됨;;
                // optString은 name이 "gif"인 값을 불러오되, null값이면 2번째 파라미터를 디폴트로 할당함
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

                // NoticeInfo 초기화 & NoticeDto Setting
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
