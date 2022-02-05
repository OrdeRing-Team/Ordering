package com.example.orderingproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import androidx.annotation.NonNull;

import com.example.orderingproject.databinding.ActivityAuthBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthOptions;
//import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends BasicActivity {


    //viewbinding
    private ActivityAuthBinding binding;

    //코드 전송에 실패하면 재전송 코드를 위해 선언
//    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId; // 코드를 담을 변수

    private static final String TAG = "SIGNUP_TAG";

//    private FirebaseAuth firebaseAuth;

    int minute, second;

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
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            @Override
//            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//
//                signInWithPhoneAuthCredential(phoneAuthCredential);
//                Log.e("AuthActivity", "::onVerificationCompleted 실행");
//                Toast.makeText(AuthActivity.this, "인증되었습니다.", Toast.LENGTH_SHORT).show();
//            }

//            @Override
//            public void onVerificationFailed(@NonNull FirebaseException e) {
//                // ProgressBar 제거
//                binding.progressBar.setVisibility(View.GONE);
//                Toast.makeText(AuthActivity.this, "번호를 올바르게 입력해 주세요.", Toast.LENGTH_SHORT).show();
//            }

//            @Override
//            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                super.onCodeSent(verificationId, forceResendingToken);
//                binding.tvResend.setVisibility(View.VISIBLE);
//                binding.tvVerifyCode.setVisibility(View.VISIBLE);
//                binding.etVerifyCode.setVisibility(View.VISIBLE);
//                binding.btnVerifyCode.setVisibility(View.VISIBLE);
//                binding.btnSendSMS.setBackgroundColor(Color.parseColor("#5E5E5E"));
//                binding.btnSendSMS.setEnabled(false);
//                binding.etPhoneSignup.setEnabled(false);
//                binding.etPhoneSignup.setTextColor(Color.parseColor("#9A9A9A"));
//
//                binding.etVerifyCode.requestFocus();
//
//                // progressBar 제거
//                binding.progressBar.setVisibility(View.GONE);
//
//                mVerificationId = verificationId;
//                forceResendingToken = token;
//
//                //키보드 보이게 하는 부분
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//
//                TimerStart();
//            }
//        };

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

                FinishWithAnim();
            }
        });


        binding.btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 010부터 받은 전화번호를 +8210... 으로 변환해준다.
                String phoneNum = "+82" + binding.etPhoneSignup.getText().toString().trim().substring(1);
                Toast.makeText(AuthActivity.this, phoneNum, Toast.LENGTH_SHORT).show();

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(), 0);

                // 문자 전송
//                startPhoneNumberVerification(phoneNum);

                // progressBar 실행
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });


//        binding.tvResend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String phoneNum = "+82" + binding.etPhoneSignup.getText().toString().trim().substring(1);
//
//                // progressBar 실행
//                binding.progressBar.setVisibility(View.VISIBLE);
//
//                // 인증번호 재전송 함수 호출
//                ResendVerificationCode(phoneNum, forceResendingToken);
//
//                // 인증번호 입력란 clear
//                binding.etVerifyCode.setText(null);
//            }
//        });


//        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String codeNum = binding.etVerifyCode.getText().toString().trim();
//                verifyCode(mVerificationId, codeNum);
//
//                // 에러 메세지 숨기기
//                binding.ivError.setVisibility(View.GONE);
//                binding.tvErrorcode.setVisibility(View.GONE);
//
//                // 키보드 내리기
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(), 0);
//            }
//        });

//        binding.btnGoInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AuthActivity.this, InfoActivity.class);
//                startActivity(intent);
//
//                FinishWithAnim();
//            }
//        });
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


//    /* 문자 전송 함수 */
//    private void startPhoneNumberVerification(String phoneNum) {
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(firebaseAuth)
//                        .setPhoneNumber(phoneNum)
//                        .setTimeout(120L, TimeUnit.SECONDS)
//                        .setActivity(this)
//                        .setCallbacks(mCallbacks)
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//    }
//
//
//    /* 인증번호 재전송 함수 */
//    private void ResendVerificationCode(String phoneNum, PhoneAuthProvider.ForceResendingToken token) {
//        // ProgressBar 실행
//        binding.progressBar.setVisibility(View.VISIBLE);
//
//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(firebaseAuth)
//                        .setPhoneNumber(phoneNum)
//                        .setTimeout(120L, TimeUnit.SECONDS)
//                        .setActivity(this)
//                        .setCallbacks(mCallbacks)
//                        .setForceResendingToken(token)
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);

//        // 타이머 초기화
//        if (timerTask != null) {
//            timerTask.cancel();
//            timerTask = null;
//        }
//
//        Toast.makeText(AuthActivity.this, "인증번호가 재전송되었습니다.", Toast.LENGTH_SHORT).show();
//
//        // progressBar 실행
//        binding.progressBar.setVisibility(View.VISIBLE);
//    }
//
//
//    /* 인증번호 확인 함수 */
//    private void verifyCode(String mVerificationId, String codeNum) {
//        // ProgressBar 실행
//        binding.progressBar.setVisibility(View.VISIBLE);
//
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeNum);
//        signInWithPhoneAuthCredential(credential);
//
//    }
//
//
//    /* 계정 생성 함수 */
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        // ProgressBar 실행
//        binding.progressBar.setVisibility(View.VISIBLE);
//
//        firebaseAuth.signInWithCredential(credential)
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        // 계정 생성 성공시
//
//                        // ProgressBar 제거
//                        binding.progressBar.setVisibility(View.GONE);
//
//                        String phoneNum = "0" + firebaseAuth.getCurrentUser().getPhoneNumber().substring(3);
//                        Log.e("AuthActivity: signInWithPhoneAuthCredential", "PhoneNum = " + phoneNum);
//                        Toast.makeText(AuthActivity.this, "인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
//
//                        // 휴대폰으로 생성된 계정은 탈퇴처리
//                        FirebaseAuth.getInstance().getCurrentUser().delete();
//
//                        Intent intent = new Intent(AuthActivity.this, SignupActivity.class);
//                        intent.putExtra("phoneNum", phoneNum);
//                        startActivity(intent);
//
//                        FinishWithAnim();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // 계정 생성 실패시
//
//                        Animation error = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_shake);
//
//                        // ProgressBar 제거
//                        binding.progressBar.setVisibility(View.GONE);
//
//                        // 에러메세지 출력
//                        binding.ivError.setVisibility(View.VISIBLE);
//                        binding.tvErrorcode.setVisibility(View.VISIBLE);
//
//                        // 에러메세지 애니메이션 실행
//                        binding.ivError.startAnimation(error);
//                        binding.tvErrorcode.startAnimation(error);
//
//                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                        long[] pattern = {10, 50, 10, 50}; // miliSecond
//                        //               대기,진동,대기,진동
//                        // 짝수 인덱스 : 대기시간
//                        // 홀수 인덱스 : 진동시간
//                        vibrator.vibrate(pattern, -1);
//                        // 0 : 무한반복, -1: 반복없음,
//                        // 양의정수 : 진동패턴배열의 해당 인덱스부터 진동 무한반복
//
//                        //Toast.makeText(AuthActivity.this, "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//    }
}
