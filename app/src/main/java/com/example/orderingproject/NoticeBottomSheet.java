package com.example.orderingproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;
import com.example.orderingproject.databinding.BottomSheetDialogLoginBinding;
import com.example.orderingproject.databinding.BottomSheetDialogNoticeBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetDialogNoticeBinding binding;

    private View view;

    String todaySDFormat = "0";

    SharedPreferences noticeShow;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding = BottomSheetDialogNoticeBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

        initData();
        bindViews();
        initListener();

        return view;
    }

    public void initData(){

        // 현재 시간을 파라미터로 넘김
        // NoticeInfo의 getDate 메소드는 long 형의 현재시간을 받아 날짜를 계산하여 String형으로 반환
        todaySDFormat = NoticeInfo.getDate(System.currentTimeMillis());
    }

    public void bindViews(){
        Glide.with(this).load(NoticeInfo.getNoticeImageUrl()).into(binding.ivNotice);
    }

    public void initListener(){
        binding.tvClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                NoticeInfo.setShow(false);
                dismiss();
            }
        });

       binding.tvDontshow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 오늘 하루 그만보기 코드
                // 현재 시간을 통해 오늘 날짜를 구함
                // 오늘 날짜를 dd 포맷으로 변환(18)한 뒤 SharedPreferences에 저장
                // 만약 내일(19) 다시 접속 했다면 SharedPrefereces값과 비교 한 뒤 뺀다.
                // 뺀 결과 값이 1이므로 날짜가 다름 -> 공지 띄움
                // 뺀 결과 값이 0이면 같은 날 이므로 공지 안띄움
                // SplashActivity의 getNoticeSP 메소드 참고 할 것

                noticeShow = getActivity().getSharedPreferences("notice", Activity.MODE_PRIVATE);

                SharedPreferences.Editor autoLoginEdit = noticeShow.edit();
                autoLoginEdit.putString("show", todaySDFormat);
                autoLoginEdit.commit();

                dismiss();
            }
        });

       binding.ivNotice.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getActivity(),EventWebView.class);
               intent.putExtra("title",NoticeInfo.getTitle());
               intent.putExtra("url",NoticeInfo.getLoadUrl());
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               getActivity().startActivity(intent);

               dismiss();
           }
       });
    }

}
