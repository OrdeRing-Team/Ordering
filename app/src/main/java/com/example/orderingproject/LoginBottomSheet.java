package com.example.orderingproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.SignInDto;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginBottomSheet extends BottomSheetDialogFragment {

    private View view;

    private ImageButton ib_close;
    private Button btn_login;
    private EditText memberIdEditText;
    private EditText passwordEditText;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.bottom_sheet_dialog_login, container, false);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

        bindViews();

        initListener();

        return view;
    }

    public void bindViews(){
        ib_close = view.findViewById(R.id.ib_close);
        btn_login = view.findViewById(R.id.btn_login);
        memberIdEditText = view.findViewById(R.id.et_memberId);
        passwordEditText = view.findViewById(R.id.et_password);
    }

    public void initListener(){
        ib_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dismiss();
            }
        });

       btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String memberId = getMemberIdText();
                String password = getPasswordEditText();

                // 로그인 조건 처리
                if (memberId.length() > 0 && password.length() > 0) {
                    try {
                        SignInDto signInDto = new SignInDto(memberId, password);

                        new Thread() {
                            @SneakyThrows
                            public void run() {
                                // login

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://www.ordering.ml/api/customer/signin/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<ResultDto<CustomerSignInResultDto>> call = service.customerSignIn(signInDto);

                                call.enqueue(new Callback<ResultDto<CustomerSignInResultDto>>() {
                                    @Override
                                    public void onResponse(Call<ResultDto<CustomerSignInResultDto>> call, Response<ResultDto<CustomerSignInResultDto>> response) {

                                        ResultDto<CustomerSignInResultDto> result = response.body();
                                        if(result.getData() != null){
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                                    dismiss();
                                                    getActivity().finish();
                                                }
                                            });
                                        }
                                        else{
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
                                                    showLoginErrorPopup();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultDto<CustomerSignInResultDto>> call, Throwable t) {
                                        Toast.makeText(getActivity(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                                        Log.e("e = " , t.getMessage());
                                    }
                                });
                            }
                        }.start();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(),"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                        Log.e("e = " , e.getMessage());
                    }
                } else {
                    Toast.makeText(getActivity(), "아이디와 비밀번호를 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoginErrorPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("로그인 실패").setMessage("아이디와 비밀번호를 다시 확인해 주세요.");
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getMemberIdText(){
        return memberIdEditText.getText().toString();
    }

    public String getPasswordEditText(){
        return passwordEditText.getText().toString();
    }

}
