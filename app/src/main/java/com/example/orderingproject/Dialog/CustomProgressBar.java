package com.example.orderingproject.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;

import com.example.orderingproject.R;

public class CustomProgressBar extends Dialog {
    Context mContext;
    ImageView imageView;
    AnimationDrawable animationDrawable;

    public CustomProgressBar(Context activity) {
        super(activity);
        mContext = activity;
        InitProgress();
    }

    public void InitProgress() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.progress_custom_loading);
        imageView = findViewById(R.id.iv_frame_loading);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
}
