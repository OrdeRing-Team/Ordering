package com.example.orderingproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.orderingproject.BasicActivity;
import com.example.orderingproject.Dto.request.PhoneNumberDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.SignupActivity;
import com.example.orderingproject.StartActivity;
import com.example.orderingproject.databinding.ActivityAuthBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.PhoneAuthProvider;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthActivity extends BasicActivity {


    //viewbinding
    private ActivityAuthBinding binding;

    //코드 전송에 실패하면 재전송 코드를 위해 선언
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId; // 코드를 담을 변수

    private static final String TAG = "SIGNUP_TAG";

    int minute, second;
    String totalPhoneNum;
    // 재전송 버튼을 누르면 초기화 하기 위해 전역으로 선언
    Timer timer = new Timer();
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etPhoneSignup.setVisibility(View.VISIBLE);
        binding.btnSendSMS.setVisibility(View.VISIBLE);
        binding.etVerifyCode.setVisibility(View.GONE);
        binding.btnVerifyCode.setVisibility(View.GONE);
        binding.tvVerifyCode.setVisibility(View.GONE);
        binding.tvResend.setVisibility(View.GONE);
        binding.tvTimerLeft.setVisibility(View.GONE);
        binding.tvTimer.setVisibility(View.GONE);
        binding.tvTimerMin.setVisibility(View.GONE);
        binding.tvTimerSec.setVisibility(View.GONE);
        ButtonLock(binding.btnSendSMS);
        ButtonLock(binding.btnVerifyCode);


        /* 전화번호 입력란 글자수 리스너 입니다 */
        binding.etPhoneSignup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int input = binding.etPhoneSignup.getText().toString().length();
                if (input > 10) {
                    ButtonRelease(binding.btnSendSMS);
                } else ButtonLock(binding.btnSendSMS);
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* 인증번호 입력란 글자수 리스너 입니다 */
        binding.etVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int input = binding.etVerifyCode.getText().toString().length();
                if (input > 5) {
                    ButtonRelease(binding.btnVerifyCode);
                } else ButtonLock(binding.btnVerifyCode);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(), 0);

                startActivity(new Intent(AuthActivity.this, StartActivity.class));
                FinishWithAnim();
            }
        });

        binding.btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 010부터 받은 전화번호를 +8210... 으로 변환해준다.
                totalPhoneNum = "+82" + binding.etPhoneSignup.getText().toString();

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(), 0);

                // 문자 전송
                startPhoneNumberVerification();

                // progressBar 실행
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });

        binding.tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNum = "+82" + binding.etPhoneSignup.getText().toString().trim().substring(1);

                // progressBar 실행
                binding.progressBar.setVisibility(View.VISIBLE);

                // 인증번호 재전송 함수 호출
                ResendVerificationCode(phoneNum, forceResendingToken);

                // 인증번호 입력란 clear
                binding.etVerifyCode.setText(null);
            }
        });

        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codeNum = binding.etVerifyCode.getText().toString().trim();
                verifyCode(codeNum);

                // 에러 메세지 숨기기
                binding.ivError.setVisibility(View.GONE);
                binding.tvErrorcode.setVisibility(View.GONE);

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(), 0);
            }
        });
    }

    private void TimerStart() {
        String min = Integer.toString(2);
        String sec = Integer.toString(0);

        binding.tvTimerMin.setText(min);
        binding.tvTimerSec.setText(sec);
        binding.tvTimerMin.setVisibility(View.VISIBLE);
        binding.tvTimerSec.setVisibility(View.VISIBLE);
        binding.tvTimer.setVisibility(View.VISIBLE);
        binding.tvTimerLeft.setVisibility(View.VISIBLE);

        minute = Integer.parseInt(binding.tvTimerMin.getText().toString());
        second = Integer.parseInt(binding.tvTimerSec.getText().toString());

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 반복실행할 구문

                        // 0초 이상이면
                        if (second != 0) {
                            //1초씩 감소
                            second--;

                            // 0분 이상이면
                        } else if (minute != 0) {
                            // 1분 = 60초
                            second = 60;
                            second--;
                            minute--;

                        }

                        // 분, 초가 10이하(한자리수) 라면
                        // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                        if (second <= 9) {
                            binding.tvTimerSec.setText("0" + second);
                        } else {
                            binding.tvTimerSec.setText(Integer.toString(second));
                        }

                        if (minute <= 9) {
                            binding.tvTimerMin.setText("0" + minute);
                        } else {
                            binding.tvTimerMin.setText(Integer.toString(minute));
                        }

                        // 분, 초가 다 0이라면 메세지를 출력한다.
                        if (minute == 0 && second == 0) {

                            timer.cancel();//타이머 종료

                            binding.tvTimerMin.setVisibility(View.GONE);
                            binding.tvTimer.setVisibility(View.GONE);
                            binding.tvTimerLeft.setVisibility(View.GONE);

                            binding.tvTimerSec.setText("(시간초과) 처음부터 다시 시작해주세요.");
                            binding.tvTimerSec.setTextColor(Color.parseColor("#FF0000"));
                            binding.etVerifyCode.setEnabled(false);
                            ButtonLock(binding.btnVerifyCode);
                        }
                    }
                });
            }
        };

        //타이머를 실행
        timer.schedule(timerTask, 0, 1000); //Timer 실행
    }

    /* 버튼 Lock거는 함수 */
    private void ButtonLock(Button button) {
        if (button.equals(binding.btnSendSMS)) {
            binding.btnSendSMS.setBackgroundColor(Color.parseColor("#5E5E5E"));
            binding.btnSendSMS.setEnabled(false);
        } else if (button.equals(binding.btnVerifyCode)) {
            binding.btnVerifyCode.setBackgroundColor(Color.parseColor("#5E5E5E"));
            binding.btnVerifyCode.setEnabled(false);
        }
    }


    /* 버튼 Lock푸는 함수 */
    private void ButtonRelease(Button button) {
        if (button.equals(binding.btnSendSMS)) {
            binding.btnSendSMS.setBackgroundColor(Color.parseColor("#0D70E6"));
            binding.btnSendSMS.setEnabled(true);
        } else if (button.equals(binding.btnVerifyCode)) {
            binding.btnVerifyCode.setBackgroundColor(Color.parseColor("#0D70E6"));
            binding.btnVerifyCode.setEnabled(true);
        }
    }

    /* twilio PhoneNumberVerification */
    public void startPhoneNumberVerification(){
        try {
            String phoneNum = binding.etPhoneSignup.getText().toString();

            PhoneNumberDto phoneNumberDto = new PhoneNumberDto(phoneNum);

            /* 3.15 오늘의 교훈 http 요청은 쓰레드 새로 파서 하자!!!!!!!!!!!!!!!!!!!!!!!!!! 꼭!!!!!!!!! */
            // 안드로이드는 기본적으로 https 프로토콜만 지원함 http를 사용하려면 예외 처리를 해주어야 한다
            // 매니페스트에서 cleartext HTTP를 활성화 시켜주면 끝!
            new Thread() {
                @SneakyThrows
                public void run() {
                    // login

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/verification/get/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.phoneNumber(phoneNumberDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result = response.body();
                            if (response.isSuccessful()) {
                                if(result.getData()){
                                    // 사용 가능한 번호일 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onCodeSent();
                                        }
                                    });
                                }
                                else{
                                    // 이미 가입된 번호일 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.progressBar.setVisibility(View.GONE);
                                            Toast.makeText(AuthActivity.this, "이미 가입된 전화번호 입니다.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(AuthActivity.this, StartActivity.class));
                                            finish();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(AuthActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                            Log.e("e = " , t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(AuthActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
            Log.e("e = " , e.getMessage());
        }
    }

    /* 인증번호 재전송 함수 */
    private void ResendVerificationCode(String phoneNum, PhoneAuthProvider.ForceResendingToken token) {
        // ProgressBar 실행
        binding.progressBar.setVisibility(View.VISIBLE);

        startPhoneNumberVerification();

        // 타이머 초기화
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        Toast.makeText(AuthActivity.this, "인증번호가 재전송되었습니다.", Toast.LENGTH_SHORT).show();

        // progressBar 실행
        binding.progressBar.setVisibility(View.VISIBLE);
    }


    /* 인증번호 확인 함수 */
    private void verifyCode(String codeNum) {
        // ProgressBar 실행
        binding.progressBar.setVisibility(View.VISIBLE);

        try {
            VerificationDto verificationDto = new VerificationDto(totalPhoneNum, codeNum);

            /* 3.15 오늘의 교훈 http 요청은 쓰레드 새로 파서 하자 */
            // 안드로이드는 기본적으로 https 프로토콜만 지원함 http를 사용하려면 예외 처리를 해주어야 한다
            // 매니페스트에서 cleartext HTTP를 활성화 시켜주면 끝!
            new Thread() {
                @SneakyThrows
                public void run() {
                    // login

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/customer/verification/check/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.verification(verificationDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result = response.body();
                            if (response.isSuccessful()) {
                                if(result.getData()){
                                    // 인증번호가 올바를 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onCodeSuccess();
                                        }
                                    });
                                }
                                else{
                                    // 인증번호가 올바르지 않을 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Animation error = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);

                                            // ProgressBar 제거
                                            binding.progressBar.setVisibility(View.GONE);

                                            // 에러메세지 출력
                                            binding.ivError.setVisibility(View.VISIBLE);
                                            binding.tvErrorcode.setVisibility(View.VISIBLE);

                                            // 에러메세지 애니메이션 실행
                                            binding.ivError.startAnimation(error);
                                            binding.tvErrorcode.startAnimation(error);

                                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            long[] pattern = {10, 50, 10, 50}; // miliSecond
                                            //               대기,진동,대기,진동
                                            // 짝수 인덱스 : 대기시간
                                            // 홀수 인덱스 : 진동시간
                                            vibrator.vibrate(pattern, -1);
                                            // 0 : 무한반복, -1: 반복없음,
                                            // 양의정수 : 진동패턴배열의 해당 인덱스부터 진동 무한반복
                                            Toast.makeText(AuthActivity.this, "인증번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                                            Log.e("resultCode", "False");
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(AuthActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                            Log.e("e = " , t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(AuthActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
            Log.e("e = " , e.getMessage());
        }
    }

    private void onCodeSuccess(){
        binding.progressBar.setVisibility(View.GONE);

        String phoneNum = totalPhoneNum.trim().substring(3);
        Toast.makeText(AuthActivity.this, "인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(AuthActivity.this, SignupActivity.class);
        intent.putExtra("phoneNum", phoneNum);
        startActivity(intent);

        FinishWithAnim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(AuthActivity.this, StartActivity.class));
        finish();
    }

    public void onCodeSent(){
        binding.tvResend.setVisibility(View.VISIBLE);
        binding.tvVerifyCode.setVisibility(View.VISIBLE);
        binding.etVerifyCode.setVisibility(View.VISIBLE);
        binding.btnVerifyCode.setVisibility(View.VISIBLE);
        binding.btnSendSMS.setBackgroundColor(Color.parseColor("#5E5E5E"));
        binding.btnSendSMS.setEnabled(false);
        binding.etPhoneSignup.setEnabled(false);
        binding.etPhoneSignup.setTextColor(Color.parseColor("#9A9A9A"));

        binding.etVerifyCode.requestFocus();

        // progressBar 제거
        binding.progressBar.setVisibility(View.GONE);

        //키보드 보이게 하는 부분
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        TimerStart();
    }

}
