package com.example.orderingproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.orderingproject.databinding.BottomSheetDialogLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import javax.annotation.Nullable;

public class BottomSheetLogin extends BottomSheetDialogFragment {

    private View view;


    private FirebaseAuth mAuth;

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
                String Password = getPasswordEditText();

                mAuth = FirebaseAuth.getInstance();

                // 로그인 조건 처리
                if (memberId.length() > 0 && Password.length() > 0) {
                    /*mAuth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if(mAuth.getCurrentUser() != null){
                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                            dismiss();
                                            getActivity().finish();
                                        }
                                    } else {

                                        Toast.makeText(getActivity(), "로그인 실패",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });*/

                } else {
                    Toast.makeText(getActivity(), "아이디와 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
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
