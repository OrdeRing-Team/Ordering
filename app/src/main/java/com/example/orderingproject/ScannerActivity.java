package com.example.orderingproject;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.orderingproject.databinding.ActivityQrscannerBinding;
import com.google.zxing.integration.android.IntentIntegrator;

public class ScannerActivity extends BasicActivity {

    private static final String TAG = "ScannerActivity_TAG";

    IntentIntegrator integrator;

    private ActivityQrscannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQrscannerBinding.inflate(getLayoutInflater());

        initData();
    }

    private void initData(){
        integrator = new IntentIntegrator(this);

        // QR코드 포맷만 스캔하도록 설정
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);

        // 바코드 인식시 소리 여부
        integrator.setBeepEnabled(false);

        // 0 = 후면카메라, 1 = 전면카메라
        integrator.setCameraId(0);

        // true일때는 onActivityResult에서 QR코드 스캔한 결과값만 받는것이 아닌
        // QR코드 이미지도 비트맵 형식으로 전달 받을 수 있다.
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(ScannerBackgroundActivity.class); //바코드 스캐너 시작
        integrator.setOrientationLocked(true);

        integrator.initiateScan();

    }

}