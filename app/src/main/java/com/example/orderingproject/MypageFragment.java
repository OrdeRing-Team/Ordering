package com.example.orderingproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.orderingproject.Dialog.CustomDialog;
import com.example.orderingproject.Dialog.CustomDialogInputType;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.MemberIdDto;
import com.example.orderingproject.Dto.request.PasswordChangeDto;
import com.example.orderingproject.Dto.request.PhoneNumberDto;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.databinding.FragmentMypageBinding;

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
    private CustomDialogInputType dialogInputType;

    Animation complete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMypageBinding.inflate(inflater, container, false);

        complete = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale);

        view = binding.getRoot();

        initData();
        initButtonClickListener();

        return view;
    }


    private void initData() {
        binding.tvName.setText(UserInfo.getNickname());
        binding.tvId.setText(UserInfo.getSignInId());
    }

    private void initButtonClickListener(){
        binding.btnLogout.setOnClickListener(onClickListener);
        binding.btnDeleteAccount.setOnClickListener(onClickListener);
        binding.btnChangePW.setOnClickListener(onClickListener);
        binding.btnReverify.setOnClickListener(onClickListener);
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
                case R.id.btn_changePW:
                    passwordCheck();
                    break;
                case R.id.btn_reverify:
                    reverify();
                    break;
            }
        }
    };

    private void passwordCheck(){
        String currentPw = binding.etNowPW.getText().toString();
        String newPw = binding.etNewPW.getText().toString();
        String checkPw = binding.etCheckPW.getText().toString();

        if(currentPw.equals(newPw)) showPopUp("비밀번호 변경 실패","현재 사용중인 비밀번호와 동일합니다.");
        else if(!newPw.equals(checkPw)) showPopUp("비밀번호 변경 실패","새로 사용할 비밀번호를 다시 확인해 주세요.");
        else if(currentPw.length() < 6 || newPw.length() < 6 || checkPw.length() < 6){
            showPopUp("비밀번호 변경 실패", "비밀번호는 6자 이상이어야 합니다.");
        }
        else if(newPw.equals(checkPw)){
            // 새로운 비밀번호 == 비밀번호 확인
            changePassword(currentPw, newPw);
        }
    }
    private void showPopUp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void changePassword(String currentPw, String newPw) {
        try {
            showProgress();

            PasswordChangeDto passwordChangeDto = new PasswordChangeDto(currentPw, newPw);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/" + Long.toString(UserInfo.getCustomerId()) + "/password/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.changePassword(UserInfo.getCustomerId(), passwordChangeDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.showLongToast(getActivity(),"비밀번호를 성공적으로 변경하였습니다");
                                            clearSharedPreferences();
                                            hideProgress();
                                        }
                                    });
                                }
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다");
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

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

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/"+ Long.toString(UserInfo.getCustomerId())+"/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.deleteaccount(UserInfo.getCustomerId());

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                Log.e("response###","successful");
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("result.getData()###","true");

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
                            }else{
                                Log.e("response###","failed");

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

    private void initTextChangedListener(){
        binding.etNewPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = binding.etNewPW.getText().toString();
                String inputCheck = binding.etCheckPW.getText().toString();
                int inputLength = binding.etNewPW.getText().toString().length();
                if (inputLength > 5) {
                    binding.ivNewPWcomplete.setVisibility(View.VISIBLE);
                    binding.ivNewPWcomplete.startAnimation(complete);
                    if(input.equals(inputCheck)){
                        binding.ivCheckPWcomplete.setVisibility(View.VISIBLE);
                        binding.ivCheckPWcomplete.startAnimation(complete);
                    }
                } else if(inputLength <6){
                    binding.ivNewPWcomplete.setVisibility(View.GONE);
                }

                if (input.length() > 0) {
                    if (!input.equals(binding.etCheckPW.getText().toString())) {
                        binding.ivCheckPWcomplete.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.etCheckPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = binding.etNewPW.getText().toString();
                String inputCheck = binding.etCheckPW.getText().toString();
                int inputLength = binding.etNewPW.getText().toString().length();
                if (inputLength > 5 && inputCheck.equals(input)) {
                    binding.ivCheckPWcomplete.setVisibility(View.VISIBLE);
                    binding.ivCheckPWcomplete.startAnimation(complete);
                } else {
                    binding.ivCheckPWcomplete.setVisibility(View.GONE);
                }
            }
        });
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

    private void reverify(){
        String phoneNum = binding.etPhoneNum.getText().toString();
        if(phoneNum.length() != 11){
            showPopUp("전화번호 변경 실패", "전화번호를 다시 확인해 주세요.");
        }
        else{
            showProgress();
            try {

                PhoneNumberDto phoneNumberDto = new PhoneNumberDto(phoneNum);

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/customer/verification/get/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call = service.phoneNumber(phoneNumberDto);

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                if (response.isSuccessful()) {
                                    if (response.body().getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showCustomDialogInputType();
                                            }
                                        });
                                    } else {
                                        showPhoneNumberErrorPopup();
                                    }
                                }
                                else{
                                    Log.e("ChangePhoneNum", "Failed");
                                }
                                hideProgress();
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다");
                                hideProgress();
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                hideProgress();
                Log.e("e = ", e.getMessage());
            }
        }
    }

    private void verifyCode(String codeNum) {
        showProgress();
        try {
            String totalPhoneNum = "+82" + binding.etPhoneNum.getText().toString();
            VerificationDto verificationDto = new VerificationDto(totalPhoneNum, codeNum);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/verification/check/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.verification(verificationDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideProgress();
                                            changePhoneNum();
                                            dialogInputType.dismiss();
                                        }
                                    });
                                }
                                else{
                                    MainActivity.showLongToast(getActivity(), "인증번호가 일치하지 않습니다");
                                    hideProgress();
                                }
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = onFailure : ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = catchException : ", e.getMessage());
        }
    }

    private void changePhoneNum(){
        showProgress();
        try {
            String successPhoneNum = binding.etPhoneNum.getText().toString();
            PhoneNumberDto phoneNumberDto = new PhoneNumberDto(successPhoneNum);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/"+UserInfo.getCustomerId()+"/phone_number/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.reverify(UserInfo.getCustomerId(), phoneNumberDto);
                    Log.e("run()","aaaaaaaaaaa");
                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                            Log.e("onResponse","aaaaaaaaaaa");
                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                Log.e("response.isSuccessful","aaaaaaaaaaa");
                                if (result.getData()) {
                                    Log.e("result.getData",Boolean.toString(result.getData()));
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("run2","zzzzzzzzzzzz");
                                            hideProgress();
                                            MainActivity.showLongToast(getActivity(),"휴대폰 번호를 변경하였습니다");
                                        }
                                    });
                                }
                                else{
                                    Log.e("result.getData",Boolean.toString(result.getData()));
                                    MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다.");
                                    hideProgress();
                                }
                            }else{
                                MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다.");
                                hideProgress();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    private void showCustomDialogInputType(){
        dialogInputType = new CustomDialogInputType(getActivity(),
                "휴대폰 인증",
                "문자로 받은 6자리 인증번호를 입력해 주세요",
                "인증","취소",
                positiveButtonInputType,negativeButtonInputType);

        dialogInputType.show();
    }

    private void showPhoneNumberErrorPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("휴대폰 번호 변경 실패").setMessage("이미 등록된 번호입니다.\n자세한 사항은 관리자에게 문의해 주세요.");
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private final View.OnClickListener positiveButtonInputType = view -> {
        showProgress();
        verifyCode(dialogInputType.getReverifyCode());
    };

    private final View.OnClickListener negativeButtonInputType = view -> {
        dialogInputType.dismiss();
    };
    private void showProgress(){
        binding.progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress(){
        binding.progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
