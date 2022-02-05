package com.example.orderingproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 켜집니다.
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_none);
    }
    protected void FinishWithAnim() {
        finish();
        if (isFinishing()) {
            // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 사라집니다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }
}
