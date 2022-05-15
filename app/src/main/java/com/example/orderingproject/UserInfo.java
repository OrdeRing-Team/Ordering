package com.example.orderingproject;

import com.example.orderingproject.Dto.response.CustomerSignInResultDto;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    // customerId : uid
    // signInId : 로그인 아이디

    private static Long customerId;
    private static String signInId;
    private static String nickname;

    public static void setUserInfo(CustomerSignInResultDto dto, String mSignInId) {
        customerId = dto.getCustomerId();
        nickname = dto.getNickname();
        signInId = mSignInId;
    }

    public static void setCustomerId(Long id){customerId = id;}
    public static Long getCustomerId(){return customerId;}

    public static void setSignInId(String id){
        signInId = id;
    }
    public static String getSignInId(){return signInId;}

    public static void setNickname(String name) {nickname = name;}
    public static String getNickname(){return nickname;}
}