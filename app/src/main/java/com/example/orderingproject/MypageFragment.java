package com.example.orderingproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderingproject.Dialog.CustomDialog;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.MemberIdDto;
import com.example.orderingproject.databinding.FragmentHomeBinding;
import com.example.orderingproject.databinding.FragmentMypageBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MypageFragment extends Fragment {

    private View view;
    private FragmentMypageBinding binding;

    private CustomDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMypageBinding.inflate(inflater, container, false);

        view = binding.getRoot();

        initButtonClickListener();

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
                    showCustomDialog();
                    break;
            }
        }
    };



    /* 로그아웃 */
    public void logout() {
        clearSharedPreferences();

        startActivity(new Intent(getActivity(), StartActivity.class));
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        getActivity().finishAffinity();
    }

    /* 회원탈퇴 */
    public void deleteAccount() {
        try {
            MainActivity.showProgress(getActivity());
            MainActivity.showProgress(getActivity());

            MemberIdDto memberId = new MemberIdDto(UserInfo.getCusetomerId());

            new Thread() {
                @SneakyThrows
                public void run() {
                    // login

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/"+ UserInfo.getCusetomerId().toString()+"/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.deleteaccount(UserInfo.getCusetomerId());

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    // 아이디 비밀번호 일치할 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            clearSharedPreferences();
                                            startActivity(new Intent(getActivity(), StartActivity.class));

                                            // 켜져있던 모든 activity 종료
                                            getActivity().finishAffinity();
                                            MainActivity.hideProgress(getActivity());
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.showLongToast(getActivity(), "서버 연결에 문제가 발생했습니다.");
                                            MainActivity.hideProgress(getActivity());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "서버 연결에 문제가 발생했습니다.");
                            MainActivity.hideProgress(getActivity());
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            MainActivity.hideProgress(getActivity());
            Log.e("e = ", e.getMessage());
        }
    }
    private void clearSharedPreferences(){
        SharedPreferences loginSP = getActivity().getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = loginSP.edit();
        spEdit.clear();
        spEdit.commit();
    }

    private void showCustomDialog(){
        dialog = new CustomDialog(
                getContext(),
                "회원탈퇴를 하시겠습니까?",
                "회원탈퇴 이후에는 데이터를 복구할 수 없습니다.\n그래도 진행하시겠습니까?",
                "회원탈퇴","취소",
                positiveButton,negativeButton, "#FF0000");

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();
        MainActivity.showLongToast(getActivity(),"회원탈퇴 되었습니다. 우리, 다시 또 봐요!");
        deleteAccount();
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

}
