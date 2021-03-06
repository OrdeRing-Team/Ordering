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

        if(currentPw.equals(newPw)) showPopUp("???????????? ?????? ??????","?????? ???????????? ??????????????? ???????????????.");
        else if(!newPw.equals(checkPw)) showPopUp("???????????? ?????? ??????","?????? ????????? ??????????????? ?????? ????????? ?????????.");
        else if(currentPw.length() < 6 || newPw.length() < 6 || checkPw.length() < 6){
            showPopUp("???????????? ?????? ??????", "??????????????? 6??? ??????????????? ?????????.");
        }
        else if(newPw.equals(checkPw)){
            // ????????? ???????????? == ???????????? ??????
            changePassword(currentPw, newPw);
        }
    }
    private void showPopUp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                            .baseUrl("http://www.ordering.ml/api/customer/" + UserInfo.getCustomerId() + "/password/")
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
                                            MainActivity.showLongToast(getActivity(),"??????????????? ??????????????? ?????????????????????");
                                            clearSharedPreferences();
                                            hideProgress();
                                        }
                                    });
                                }else{
                                    MainActivity.showLongToast(getActivity(),"?????? ??????????????? ?????? ????????? ?????????.");
                                    hideProgress();
                                }
                            }else{
                                Log.e("asdasdasd","dasdas");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????");
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    /* ???????????? */
    public void logout() {
        clearSharedPreferences();

        startActivity(new Intent(getActivity(), StartActivity.class));
        Toast.makeText(getActivity(), "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
        getActivity().finishAffinity();
    }

    /* ???????????? */
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

                                            // ???????????? ?????? activity ??????
                                            getActivity().finishAffinity();
                                            MainActivity.hideProgress(getActivity());
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            MainActivity.showLongToast(getActivity(), "?????? ????????? ????????? ??????????????????.");
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
                            MainActivity.showLongToast(getActivity(), "?????? ????????? ????????? ??????????????????.");
                            MainActivity.hideProgress(getActivity());
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                "??????????????? ???????????????????",
                "???????????? ???????????? ???????????? ????????? ??? ????????????.\n????????? ?????????????????????????",
                "????????????","??????",
                positiveButton,negativeButton, "#FF0000");

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();
        MainActivity.showLongToast(getActivity(),"???????????? ???????????????. ??????, ?????? ??? ??????!");
        deleteAccount();
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

    private void reverify(){
        String phoneNum = binding.etPhoneNum.getText().toString();
        if(phoneNum.length() != 11){
            showPopUp("???????????? ?????? ??????", "??????????????? ?????? ????????? ?????????.");
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
                                MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????");
                                hideProgress();
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                                    MainActivity.showLongToast(getActivity(), "??????????????? ???????????? ????????????");
                                    hideProgress();
                                }
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????");
                            hideProgress();
                            Log.e("e = onFailure : ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
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
                                            MainActivity.showLongToast(getActivity(),"????????? ????????? ?????????????????????");
                                        }
                                    });
                                }
                                else{
                                    Log.e("result.getData",Boolean.toString(result.getData()));
                                    MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????.");
                                    hideProgress();
                                }
                            }else{
                                MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????.");
                                hideProgress();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            MainActivity.showLongToast(getActivity(), "???????????? ????????? ??????????????????");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    private void showCustomDialogInputType(){
        dialogInputType = new CustomDialogInputType(getActivity(),
                "????????? ??????",
                "????????? ?????? 6?????? ??????????????? ????????? ?????????",
                "??????","??????",
                positiveButtonInputType,negativeButtonInputType);

        dialogInputType.show();
    }

    private void showPhoneNumberErrorPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("????????? ?????? ?????? ??????").setMessage("?????? ????????? ???????????????.\n????????? ????????? ??????????????? ????????? ?????????.");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
