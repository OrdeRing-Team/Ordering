package com.example.orderingproject.Dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class NoticeDto {
    private String noticeImage;
    private String loadUrl;
    private String title;

    public void setUrls(String imageUrl, String loadUrl, String title) {
        this.noticeImage = imageUrl;
        this.loadUrl = loadUrl;
        this.title = title;
    }

    public String getNoticeImage() {
        return noticeImage;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public String getTitle() {
        return title;
    }
}


