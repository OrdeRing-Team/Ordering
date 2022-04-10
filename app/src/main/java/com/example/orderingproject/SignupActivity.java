package com.example.orderingproject;

import static com.example.orderingproject.Utillity.showToast;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingproject.Dto.RetrofitService;
import com.example.orderingproject.Dto.request.CustomerSignUpDto;
import com.example.orderingproject.Dto.ResultDto;
import com.example.orderingproject.Dto.request.VerificationDto;
import com.example.orderingproject.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    Intent intent;
    String phoneNum;

    Boolean isNickNameWritten = true;
    Boolean isMemberIdWritten = false;
    Boolean isPasswordWritten = false;
    Boolean isPasswordCheckAccord = false;

    Animation complete;

    // 아이디 형식 패턴(영문 소문자, 숫자만)
    Pattern ps = Pattern.compile("^[a-zA-Z0-9_]*$");

    private static final String TAG = "SignupActivity_TAG";

    //viewbinding
    private ActivitySignupBinding binding;

    // 닉네임 리스트 생성
    private List<String> firstNick = new ArrayList<String>();
    private List<String> lastNick = new ArrayList<String>();

    // 파이어베이스 인증
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        // 전화번호 받아온 뒤 표시
        intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        Log.e("phoneNum",phoneNum);
        // String convertedPhoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3,7) + "-" + phoneNum.substring(7);
        // String convertedPhoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3,7) + "-" + phoneNum.substring(7);
        binding.tvSignupPhoneNum.setText(phoneNum);

        ButtonLock(binding.btnSignup);

        initNicknameSet();
        initRandomButton();
        initSignupButton();
        initTextChangedListener();


    }

    private void initNicknameSet(){

        // 첫번째 들어갈 닉네임 초기화 - 음식 관련 형용사를 위주로 추가해주세요!! 재밌게 표현하는것도 가능!!
        firstNick.add("뜨끈한 ");firstNick.add("든든한 ");firstNick.add("달콤한 ");firstNick.add("신선한 ");
        firstNick.add("바삭바삭한 ");firstNick.add("부드러운 ");firstNick.add("쫀득쫀득한 ");firstNick.add("살살녹는 ");
        firstNick.add("시큼한 ");firstNick.add("따뜻한 ");firstNick.add("불같이매운 ");firstNick.add("빛깔좋은 ");
        firstNick.add("상상도못한 ");firstNick.add("이탈리안 ");firstNick.add("코리안 ");firstNick.add("아메리칸 ");firstNick.add("질긴 ");
        firstNick.add("삶은 ");firstNick.add("울고있는 ");firstNick.add("웃고있는 ");firstNick.add("화난 ");firstNick.add("감동먹은 ");
        firstNick.add("놀란 ");firstNick.add("의미심장한 ");firstNick.add("기쁜 ");

        // 두번째 들어갈 닉네임 초기화 - 음식 이름 위주로 추가해주세요!!
        lastNick.add("치킨");lastNick.add("국밥");lastNick.add("아이스크림");lastNick.add("통닭");lastNick.add("피자");lastNick.add("달걀");
        lastNick.add("비빔밥");lastNick.add("탕수육");lastNick.add("짜장면");lastNick.add("마라탕");lastNick.add("떡볶이");lastNick.add("치즈");
        lastNick.add("냉면");lastNick.add("파스타");lastNick.add("우동");lastNick.add("라면");lastNick.add("만두");lastNick.add("왕갈비");
        lastNick.add("부대찌개");lastNick.add("두루치기");lastNick.add("제육볶음");lastNick.add("햄버거");lastNick.add("통삼겹");
        lastNick.add("돈까스");lastNick.add("초밥");lastNick.add("회");lastNick.add("닭발");lastNick.add("짬뽕");lastNick.add("족발");
        lastNick.add("곱창");lastNick.add("케이크");lastNick.add("아메리카노");lastNick.add("샐러드");lastNick.add("샌드위치");lastNick.add("팥빙수");

        /* 닉네임 설정 */
        randomNickname();
        binding.ivNickNameComplete.setVisibility(View.VISIBLE);
        binding.ivNickNameComplete.startAnimation(complete);
    }

    private void randomNickname(){
        // 리스트 순서 섞기
        Collections.shuffle(firstNick);
        Collections.shuffle(lastNick);

        binding.editTextNickname.setText(firstNick.get(0) + lastNick.get(0));
    }

    private void initTextChangedListener(){
        /* 닉네임 입력란 변경 리스너 */
        binding.editTextNickname.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.ivError.setVisibility(View.GONE);
                binding.tvNickNameError.setVisibility(View.GONE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int input = binding.editTextNickname.getText().toString().length();

                if (input > 2) {
                    isNickNameWritten = true;
                    binding.ivError.setVisibility(View.GONE);
                    binding.tvNickNameError.setVisibility(View.GONE);
                    binding.ivNickNameComplete.setVisibility(View.GONE);
                } else {
                    isNickNameWritten = false;
                    binding.ivError.setVisibility(View.VISIBLE);
                    binding.tvNickNameError.setVisibility(View.VISIBLE);
                    binding.ivNickNameComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isNickNameWritten){
                    // 통과 표시 출력
                    binding.ivNickNameComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivNickNameComplete.startAnimation(complete);
                }
                else{
                    binding.ivNickNameComplete.setVisibility(View.GONE);
                }
            }
        });

        /* 이메일 입력란 변경 리스너 */
        binding.etMemberId.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.ivIdComplete.setVisibility(View.GONE);

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = binding.etMemberId.getText().toString();

                if(input.length()> 4 && input.length() < 21 && ps.matcher(input).matches()){
                    isMemberIdWritten = true;
                } else {
                    isMemberIdWritten = false;
                    binding.ivError4.setVisibility(View.VISIBLE);
                    binding.tvIdError.setVisibility(View.VISIBLE);
                }
                if(isMemberIdWritten){

                    binding.ivError4.setVisibility(View.GONE);
                    binding.tvIdError.setVisibility(View.GONE);

                    // 통과 표시 출력
                    binding.ivIdComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivIdComplete.startAnimation(complete);
                }
                else{
                    binding.ivIdComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });

        /* 비밀번호 입력란 변경 리스너 */
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.ivError2.setVisibility(View.GONE);
                binding.tvPsLength.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.editTextPassword.getText().toString();
                String passwordCheckText = binding.editTextPasswordCheck.getText().toString();
                int inputLength = binding.editTextPassword.getText().toString().length();
                if (inputLength > 5) {
                    isPasswordWritten = true;
                    binding.ivError2.setVisibility(View.GONE);
                    binding.tvPsLength.setVisibility(View.GONE);
                    binding.ivPsComplete.setVisibility(View.GONE);

                    // 비밀번호확인까지 입력한 뒤 비밀번호를 수정한 경우 조건문 추가
                    if (passwordCheckText.length() > 0) {
                        if(input.equals(passwordCheckText)){
                            isPasswordCheckAccord = true;
                            binding.ivError3.setVisibility(View.GONE);
                            binding.tvPsAccord.setVisibility(View.GONE);
                            binding.ivPsCheckComplete.setVisibility(View.VISIBLE);
                            binding.ivPsCheckComplete.startAnimation(complete);
                        } else {
                            isPasswordCheckAccord = false;
                            binding.ivError3.setVisibility(View.VISIBLE);
                            binding.tvPsAccord.setVisibility(View.VISIBLE);
                            binding.ivPsCheckComplete.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if(passwordCheckText.length() > 0){
                        isPasswordCheckAccord = false;
                        binding.ivError3.setVisibility(View.VISIBLE);
                        binding.tvPsAccord.setVisibility(View.VISIBLE);
                        binding.ivPsCheckComplete.setVisibility(View.GONE);
                    }
                    isPasswordWritten = false;
                    binding.ivError2.setVisibility(View.VISIBLE);
                    binding.tvPsLength.setVisibility(View.VISIBLE);
                    binding.ivPsComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isPasswordWritten){
                    // 통과 표시 출력
                    binding.ivPsComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivPsComplete.startAnimation(complete);
                }
                else{
                    binding.ivPsComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });

        /* 비밀번호확인 입력란 변경 리스너 */
        binding.editTextPasswordCheck.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.editTextPasswordCheck.getText().toString();
                if (input.equals(binding.editTextPassword.getText().toString())) {
                    if(input.length() > 0 && input.length()<6){
                        isPasswordCheckAccord = false;
                        binding.ivError3.setVisibility(View.VISIBLE);
                        binding.tvPsAccord.setVisibility(View.VISIBLE);
                        binding.ivPsCheckComplete.setVisibility(View.GONE);
                        return;
                    }
                    isPasswordCheckAccord = true;
                    binding.ivError3.setVisibility(View.GONE);
                    binding.tvPsAccord.setVisibility(View.GONE);
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                } else {
                    isPasswordCheckAccord = false;
                    binding.ivError3.setVisibility(View.VISIBLE);
                    binding.tvPsAccord.setVisibility(View.VISIBLE);
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                }

                checkAllWritten();

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isPasswordCheckAccord){
                    // 통과 표시 출력
                    binding.ivPsCheckComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivPsCheckComplete.startAnimation(complete);
                }
                else{
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });
    }

    private void initRandomButton(){
        binding.ibRandomNick.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                randomNickname();

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.editTextNickname.getWindowToken(), 0);
            }
        });
    }

    private void initSignupButton(){
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void checkAllWritten(){
        if(isMemberIdWritten && isPasswordWritten && isNickNameWritten && isPasswordCheckAccord){
            ButtonRelease(binding.btnSignup);
        }
        else{
            ButtonLock(binding.btnSignup);
        }
    }
    /* 버튼 Lock거는 함수 */
    private void ButtonLock(Button button) {
        button.setBackgroundColor(Color.parseColor("#5E5E5E"));
        button.setEnabled(false);
    }

    /* 버튼 Lock푸는 함수 */
    private void ButtonRelease(Button button) {
        button.setBackgroundColor(Color.parseColor("#0D70E6"));
        button.setEnabled(true);
    }

    /* 이메일 계정 생성 */
    private void createAccount() {

        String nickname = binding.editTextNickname.getText().toString();
        String memberId = binding.etMemberId.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        String phoneNum = binding.tvSignupPhoneNum.getText().toString();

        // 이메일 계정 생성 시작
        if (memberId.length() > 4 && memberId.length() < 21 && password.length() > 5 && nickname.length() > 2) {
            try {
                Log.e("닉네임", nickname);
                Log.e("아이디", memberId);
                Log.e("비밀번호", password);
                Log.e("전화번호", phoneNum);
                CustomerSignUpDto customerSignUpDto = new CustomerSignUpDto(nickname, memberId, password, phoneNum);

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        // login
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/customer/signup/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        Log.e("aaaaaa","aaaaaaa");
                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Long>> call = service.customerSignUp(customerSignUpDto);

                        call.enqueue(new Callback<ResultDto<Long>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Long>> call, Response<ResultDto<Long>> response) {

                                Log.e("bbbbbbbb","bbbbbbbbbb");
                                ResultDto<Long> result = response.body();
                                if(result == null){
                                    Log.e("result","nullllllllll");
                                }
                                else{
                                    Log.e("result","notNullllllll");
                                }
                                if (response.isSuccessful()) {
                                    Log.e("xxxxxx","xxxxxxx");
                                    if (result.getData() != null) {
                                        Log.e("cccc","cccccc");
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {

                                                Log.e("dddddddd","ddddddddd");
                                                updateDB(memberId, phoneNum, nickname);
                                                startActivity(new Intent(SignupActivity.this, StartActivity.class));
                                                finish();
                                                showToast(SignupActivity.this, "회원가입을 완료하였습니다.");
                                            }
                                        });
                                    } else {
                                        Log.e("eeeeee","eeeeee");
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("fffff","ffffff");
                                                Log.w(TAG, "회원가입 실패");
                                                showToast(SignupActivity.this, "이미 존재하는 아이디입니다.");
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Long>> call, Throwable t) {
                                Log.e("ggggg","gggggg");
                                Toast.makeText(SignupActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요1111",Toast.LENGTH_LONG).show();
                                Log.e("e = " , t.getMessage());
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                Log.e("hhhhhhh","hhhhhhh");
                Toast.makeText(SignupActivity.this,"일시적인 오류가 발생하였습니다\n다시 시도해 주세요",Toast.LENGTH_LONG).show();
                Log.e("e = " , e.getMessage());
            }
        }

    }

    private void updateDB(String memberId, String phoneNum, String Nickname) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("아이디", memberId);
        userInfo.put("휴대폰번호", phoneNum);
        userInfo.put("닉네임", Nickname);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(SignupActivity.this, StartActivity.class));
        finish();
    }

}