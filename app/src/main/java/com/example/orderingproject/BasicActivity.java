package com.example.orderingproject;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

import com.example.orderingproject.Dialog.CustomProgressBar;

public class BasicActivity extends AppCompatActivity {
    static CustomProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 켜집니다.
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_none);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    protected void FinishWithAnim() {
        finish();
        if (isFinishing()) {
            // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 사라집니다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }

    protected void BackWithAnim() {
        finish();
        if (isFinishing()) {
            // 이 화면은 오른쪽에서 왼쪽으로 슬라이딩 하면서 사라집니다.
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }
    }

    public static void showToast(Activity activity, String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }

    public static void startProgress(Activity activity) {

        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new CustomProgressBar(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_custom_loading);
            progressDialog.show();
        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });
    }

    public static void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
