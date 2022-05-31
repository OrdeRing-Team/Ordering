package com.example.orderingproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.orderingproject.Dialog.CustomStoreDialog;
import com.example.orderingproject.databinding.ActivityMainBinding;
import com.example.orderingproject.waiting.WaitingFragment;
import com.example.orderingproject.waiting.WaitingInfoDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends BasicActivity {

    BottomNavigationView bottomNavigationView; //바텀네비뷰
    private ActivityMainBinding binding;

    static ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        initData();
    }

    public void initData(){
        Log.e("NoticeInfo",Boolean.toString(NoticeInfo.getShow()));

        // NoticeDialog 띄울지 여부
        if(NoticeInfo.getShow()) initNoticeDialog();
    }

    public void initNoticeDialog(){
        // "닫기"만 눌렀을 때는 앱을 재실행하면 다시 뜨도록 설정해야 하니까 true로 초기화
        NoticeInfo.setShow(true);

        if(NoticeInfo.getNoticeImageUrl() != null && NoticeInfo.getShow()){
            NoticeBottomSheet noticeBottomSheet = new NoticeBottomSheet();
            noticeBottomSheet.show(getSupportFragmentManager(), "bottomSheet");
        }
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

    // ZXING 스캔처리 후 호출됨
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(
                requestCode,
                resultCode,
                intent);
        Log.e("onActivityResult","실행");
        if (result != null) {
            if (result.getContents() == null) {
                // 스캔 취소시
                Log.e("ZXING", "스캔 취소됨");
            } else {
                showLongToast(this, result.getContents());
                String url[] = result.getContents().split("/");
                StringBuilder sb = new StringBuilder();
                int a = 0;
                for(String i : url){
                    sb.append("url["+a+"] = "+ i +"\n");
                    a++;
                }
                Log.e("asdasd", sb.toString());
                // url[]: url[0] : http:  url[1] :   url[2] : ordering.ml  url[3] : 6  url[4] : table36
                // url[3] = restaurantId, url[4] 포장/웨이팅/테이블

                if(!url[2].equals("www.ordering.ml")){
                    showLongToast(this, "오더링 매장의 QR코드가 아닙니다. 다시 확인해 주세요.");
                }
                else {

                    if (url[4].equals("waiting")) {

                        Log.e("Customer Id", String.valueOf(UserInfo.getCustomerId()));
                        Log.e("Restaurant Id", url[3]);

                        // Bundle에 담아서 WaitingInfoDialog로 보낸다.
                        Bundle waitingData = new Bundle();
                        waitingData.putString("storeId", url[3]);

                        WaitingInfoDialog waitingInfoDialog = new WaitingInfoDialog();
                        waitingInfoDialog.show(this.getSupportFragmentManager(),"waitingInfoDialog");
                        waitingInfoDialog.setArguments(waitingData);
                    }

                    else {
                        CustomStoreDialog dialog = new CustomStoreDialog(MainActivity.this, url[3], url[4]);
                        dialog.show();
                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    }
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

}