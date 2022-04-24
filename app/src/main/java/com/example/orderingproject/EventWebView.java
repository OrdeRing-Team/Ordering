package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderingproject.databinding.ActivityEventWebviewBinding;

public class EventWebView extends BasicActivity{
    private ActivityEventWebviewBinding binding;

    WebView mWebView;
    String title;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* 임시 토스트*/
        TextView tvToastMsg = new TextView(this);
        String msg = "임시 페이지입니다..!";
        tvToastMsg.setText(msg);
        tvToastMsg.setPadding(30,10,30,10);
        tvToastMsg.setBackgroundResource(R.drawable.round_background);
        tvToastMsg.setBackgroundColor(Color.parseColor("#E3AA55"));
        tvToastMsg.setTextColor(Color.RED);
        tvToastMsg.setTextSize(16);

        final Toast toastMsg = Toast.makeText(this, "", Toast.LENGTH_LONG);
        toastMsg.setView(tvToastMsg);

        toastMsg.show();
        /*===========*/

        initData();
        initViews();
        initButtonListener();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initData(){
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");

        mWebView = (WebView) findViewById(R.id.wv_event);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);

        // JavaScriptInterface 객체화
        WebViewInterface mWebViewInterface = new WebViewInterface(EventWebView.this,binding.wvEvent);
        // 웹뷰에 자바스크립트 인터페이스 연결
        mWebView.addJavascriptInterface(mWebViewInterface,"Android");
    }

    private void initViews(){
        binding.tvTitle.setText(title);

    }

    private void initButtonListener(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishWithAnim();
            }
        });
    }

}
