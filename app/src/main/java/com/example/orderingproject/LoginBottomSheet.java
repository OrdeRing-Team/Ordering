package com.example.orderingproject;

import static com.example.orderingproject.Utillity.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.orderingproject.Dto.CustomerSignUpDto;
import com.example.orderingproject.Dto.HttpApi;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.SignInDto;
import com.example.orderingproject.databinding.BottomSheetDialogLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

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

                        URL url = new URL("http://www.ordering.ml/api/customer/signin");
                        HttpApi<Boolean> httpApi = new HttpApi<>(url, "POST");

                        new Thread() {
                            public void run() {
                                ResultDto<Boolean> result = httpApi.requestToServer(signInDto);
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
                                            showToast(getActivity(),"아이디 혹은 비밀번호가 틀렸습니다.");
                                        }
                                    });
                                }
                            }
                        }.start();

                    } catch ( MalformedURLException e) {
                        Toast.makeText(getActivity(),"로그인 도중 일시적인 오류가 발생하였습니다.",Toast.LENGTH_LONG).show();
                        Log.e("e = " , e.getMessage());
                    }
                } else {
                    Toast.makeText(getActivity(), "아이디와 비밀번호를 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getMemberIdText(){
        return memberIdEditText.getText().toString();
    }

    public String getPasswordEditText(){
        return passwordEditText.getText().toString();
    }

}
