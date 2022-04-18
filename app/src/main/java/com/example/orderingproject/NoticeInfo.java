package com.example.orderingproject;

import com.example.orderingproject.Dto.NoticeDto;
import com.example.orderingproject.Dto.response.CustomerSignInResultDto;

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

    public static String getNoticeImageUrl(){return noticeImageUrl;}

    public static String getLoadUrl(){return loadUrl;}

    public static String getTitle(){return title;}

    public static boolean getShow(){return show;}

    public static void setShow(boolean input){
        show = input;
    }
}