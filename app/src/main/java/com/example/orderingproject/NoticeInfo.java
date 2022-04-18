package com.example.orderingproject;

import android.annotation.SuppressLint;

import com.example.orderingproject.Dto.NoticeDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeInfo {

    // customerId : uid
    // signInId : 로그인 아이디

    private static String noticeImageUrl;
    private static String loadUrl;
    private static String title;
    private static boolean show = true;

    public static void setNoticeInfo(String mNoticeImageUrl, String mLoadUrl, String mTitle) {
        noticeImageUrl = mNoticeImageUrl;
        loadUrl = mLoadUrl;
        title = mTitle;
    }

    public static String getDate(long currentTime){
        String todaySDFormat = "0";

        // 현재 시간을 Date 형으로 저장
        Date today = new Date(currentTime);

        // 'dd' 포맷 선언 (@SuppressLint 는 warning을 없애기 위함)
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat SDFormat = new SimpleDateFormat("dd");
        // today를 'dd'형으로 변경하고 반환
        return (todaySDFormat = SDFormat.format(today));
    }

    public static String getNoticeImageUrl(){return noticeImageUrl;}

    public static String getLoadUrl(){return loadUrl;}

    public static String getTitle(){return title;}

    public static boolean getShow(){return show; }

    public static void setShow(boolean input){
        show = input;
    }
}