package com.example.orderingproject;

import static com.example.orderingproject.Utillity.showToast;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingproject.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    Intent intent;
    String phoneNum;

    Boolean isNickNameWritten = true;
    Boolean isEmailWritten = false;
    Boolean isPasswordWritten = false;
    Boolean isPasswordCheckAccord = false;

    Animation complete;

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
        String convertedPhoneNum = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3,7) + "-" + phoneNum.substring(7);
        binding.tvSignupPhoneNum.setText(convertedPhoneNum);

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
        binding.editTextEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.ivEmailComplete.setVisibility(View.GONE);

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = binding.editTextEmail.getText().toString();
                Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
                if(pattern.matcher(input).matches()){
                    isEmailWritten = true;
                } else {
                    isEmailWritten = false;
                    binding.ivError4.setVisibility(View.VISIBLE);
                    binding.tvEmailError.setVisibility(View.VISIBLE);
                }
                if(isEmailWritten){

                    binding.ivError4.setVisibility(View.GONE);
                    binding.tvEmailError.setVisibility(View.GONE);

                    // 통과 표시 출력
                    binding.ivEmailComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivEmailComplete.startAnimation(complete);
                }
                else{
                    binding.ivEmailComplete.setVisibility(View.GONE);
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
        if(isEmailWritten && isPasswordWritten && isNickNameWritten && isPasswordCheckAccord){
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

        String Nickname = binding.editTextNickname.getText().toString();
        String Email = binding.editTextEmail.getText().toString();
        String Password = binding.editTextPassword.getText().toString();

        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;

        // 이메일 계정 생성 시작
        if (pattern.matcher(Email).matches() && Password.length() > 5 && Nickname.length() > 2) {
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                updateUI(Nickname, Email, Password);
                            } else {
                                // 실패시
                                    String ErrorEmailAlreadyUse = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.";

                                    String Errormsg = task.getException().toString();
                                    Log.w(TAG, "이메일 생성 실패", task.getException());
                                    if (Errormsg.equals(ErrorEmailAlreadyUse)) {

                                        Toast.makeText(SignupActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        }
                    });
        }

    }

    private void updateUI(String Nickname, String Email, String Password) {

        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            if(mAuth.getCurrentUser()!=null){
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateDB(user, phoneNum, Nickname, Email);
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                showToast(SignupActivity.this,"회원가입 완료");
                                finish();
                            }
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            showToast(SignupActivity.this,"회원가입에 실패하였습니다.");
                        }
                        // ...
                    }
                });
    }

    private void updateDB(FirebaseUser user, String phoneNum, String Nickname, String Email) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //UserInfo userinfo = new UserInfo(name, birthyear, birthmonth, birthday,followerlist,followinglist,contents);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("이메일", Email);
        userInfo.put("휴대폰번호", phoneNum);
        userInfo.put("닉네임", Nickname);
        userInfo.put("매장정보","False");

        // 새로운 사용자 DB 생성
        db.collection("users")
                .document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, phoneNum + "의 DB 생성 완료  ::  " + user.getUid());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "의 DB 생성 실패", e);
                    }
                });

    }
}