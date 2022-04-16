package com.example.orderingproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EventsDto {
    private String imageUrl;
    private String loadUrl;
    private String title;

    public void setUrls(String imageUrl, String loadUrl, String title) {
        this.imageUrl = imageUrl;
        this.loadUrl = loadUrl;
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public String getTitle() {
        return title;
    }
}


