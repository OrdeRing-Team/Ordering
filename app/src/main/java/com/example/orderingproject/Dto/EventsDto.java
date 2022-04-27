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
    private String gif;

    public void setUrls(String imageUrl, String loadUrl, String title, String gif) {
        this.imageUrl = imageUrl;
        this.loadUrl = loadUrl;
        this.title = title;
        this.gif = gif;
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

    public boolean getGif() { return Boolean.parseBoolean(gif); }
}


