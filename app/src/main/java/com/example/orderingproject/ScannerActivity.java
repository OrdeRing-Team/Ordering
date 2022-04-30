package com.example.orderingproject;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.orderingproject.databinding.ActivityQrscannerBinding;
import com.journeyapps.barcodescanner.CaptureManager;

import java.security.Permission;

public class ScannerActivity extends BasicActivity{

    private ActivityQrscannerBinding binding;

    CaptureManager captureManager;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        initData(savedInstanceState);
    }

    private void initData(Bundle savedInstanceState){
        captureManager = new CaptureManager(this,binding.scanBox);
        captureManager.initializeFromIntent(this.getIntent(), savedInstanceState);
        captureManager.decode();
    }

    private void initViews(){
        Glide.with(this).load(R.drawable.qr_scan_animation_white).into(binding.ivScanAnim);
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
