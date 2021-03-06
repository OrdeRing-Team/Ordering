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

        // ?????? ????????? ??????????????? ??????
        // NoticeInfo??? getDate ???????????? long ?????? ??????????????? ?????? ????????? ???????????? String????????? ??????
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
                // ?????? ?????? ???????????? ??????
                // ?????? ????????? ?????? ?????? ????????? ??????
                // ?????? ????????? dd ???????????? ??????(18)??? ??? SharedPreferences??? ??????
                // ?????? ??????(19) ?????? ?????? ????????? SharedPrefereces?????? ?????? ??? ??? ??????.
                // ??? ?????? ?????? 1????????? ????????? ?????? -> ?????? ??????
                // ??? ?????? ?????? 0?????? ?????? ??? ????????? ?????? ?????????
                // SplashActivity??? getNoticeSP ????????? ?????? ??? ???

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
