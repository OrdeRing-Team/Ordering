package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.example.orderingproject.databinding.ActivityQrscannerBinding;
import com.journeyapps.barcodescanner.CaptureManager;

import java.security.Permission;

public class ScannerActivity extends BasicActivity{

    private ActivityQrscannerBinding binding;

    CaptureManager captureManager;
    boolean flashStatus = false;
    Drawable lightOn;
    Drawable lightOff;
    Drawable lightOnButton;
    Drawable lightOffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        initData(savedInstanceState);
        initClickListeners();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initData(Bundle savedInstanceState){
        captureManager = new CaptureManager(this,binding.scanBox);
        captureManager.initializeFromIntent(this.getIntent(), savedInstanceState);
        captureManager.decode();

        lightOn = getDrawable(R.drawable.light_on);
        lightOff = getDrawable(R.drawable.light_off);
        lightOnButton = getDrawable(R.drawable.circle_button_light_on);
        lightOffButton = getDrawable(R.drawable.circle_button);
    }

    private void initViews(){
        Glide.with(this).load(R.drawable.qr_scan_animation_white).into(binding.ivScanAnim);
    }

    private void initClickListeners(){
        binding.btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!flashStatus){
                    binding.scanBox.setTorchOn();
                    binding.ivFlash.setImageDrawable(lightOn);
                    binding.btnFlash.setBackground(lightOnButton);
                    MainActivity.showToast(ScannerActivity.this, "라이트 On");
                    flashStatus = true;
                }
                else{
                    binding.scanBox.setTorchOff();
                    binding.ivFlash.setImageDrawable(lightOff);
                    binding.btnFlash.setBackground(lightOffButton);
                    binding.btnFlash.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.circle_button));
                    MainActivity.showToast(ScannerActivity.this, "라이트 Off");
                    flashStatus = false;
                }
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        captureManager.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();

        captureManager.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
