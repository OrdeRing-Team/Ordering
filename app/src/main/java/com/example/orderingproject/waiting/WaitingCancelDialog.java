package com.example.orderingproject.waiting;

import static android.graphics.Color.TRANSPARENT;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.WaitingRegisterDto;
import com.example.orderingproject.R;
import com.example.orderingproject.UserInfo;
import com.example.orderingproject.databinding.DialogWaitingInfoBinding;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaitingCancelDialog extends DialogFragment {

    public static WaitingCancelDialog getInstance() { return new WaitingCancelDialog(); }

    private View view;
    private DialogWaitingInfoBinding binding;


    // 웨이팅 취소 확인 팝업창
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogWaitingInfoBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //Custom Dialog 배경 투명하게 -> 모서리 둥글게 커스텀했더니 각진 DialogFragment의 뒷 배경이 보이기 때문
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        initButtonListeners();

        return view;
    }

    public void initButtonListeners() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void buttonLock (View view){
        view.setClickable(false);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_gray)));
    }
    private void buttonRelease (View view){
        view.setClickable(true);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.button_black)));
    }



}
