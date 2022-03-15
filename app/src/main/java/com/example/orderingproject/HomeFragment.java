package com.example.orderingproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.orderingproject.Dto.HttpApi;
import com.example.orderingproject.Dto.PhoneNumberDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.MalformedURLException;
import java.net.URL;

public class HomeFragment extends Fragment {

    private View view;

    private FragmentHomeBinding binding;

    Button button1234;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        view = binding.getRoot();

        initButtonClickListener();

        button1234 = view.findViewById(R.id.button1234);

        button1234.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                push();
            }
        });
        return view;
    }

    private void initButtonClickListener(){
        binding.btnLogout.setOnClickListener(onClickListener);
        binding.btnDeleteAccount.setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_logout:
                    logout();
                    break;
                case R.id.btn_deleteAccount:
                    deleteAccount();
                    break;
            }
        }
    };


    public void push(){
        Toast.makeText(getActivity(),"버튽1234 클릭" ,Toast.LENGTH_SHORT).show();
        try {
            // test obj
            PhoneNumberDto phoneNumberDto = new PhoneNumberDto();
            phoneNumberDto.setPhoneNumber("01073868114");

            URL url = new URL("http://www.ordering.ml/api/customer/verification/get");
            HttpApi<Boolean> httpApi = new HttpApi<>(url, "POST");

            /* 3.15 오늘의 교훈 http 요청은 쓰레드 새로 파서 하자!!!!!!!!!!!!!!!!!!!!!!!!!! 꼭!!!!!!!!! */
            // 안드로이드는 기본적으로 https 프로토콜만 지원함 http를 사용하려면 예외 처리를 해주어야 한다
            // 매니페스트에서 cleartext HTTP를 활성화 시켜주면 끝!
            new Thread() {
                public void run() {
                    ResultDto<Boolean> result = httpApi.requestToServer(phoneNumberDto);
                }
            }.start();

            //System.out.println(result.getData());

        } catch ( MalformedURLException e) {
            Log.e("e = " , e.getMessage());
        }
    }
    /* 로그아웃 */
    public void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), StartActivity.class));
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    /* 회원탈퇴 */
    public void deleteAccount() {
        FirebaseAuth.getInstance().getCurrentUser().delete();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 유저 DB 삭제
                        db.collection("users").document(user.getUid())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("유저DB 삭제", "성공");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("유저DB 삭제", "실패", e);
                                    }
                                });
                        Toast.makeText(getActivity(), "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), StartActivity.class));
                    }
                });

    }


}