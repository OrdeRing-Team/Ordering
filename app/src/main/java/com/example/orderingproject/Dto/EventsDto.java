package com.example.orderingproject.Dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class EventsDto {
    private String imageUrl;
    private String loadUrl;

    public void setUrls(String imageUrl, String loadUrl){
        this.imageUrl = imageUrl;
        this.loadUrl = loadUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getLoadUrl(){
        return loadUrl;
    }
}


