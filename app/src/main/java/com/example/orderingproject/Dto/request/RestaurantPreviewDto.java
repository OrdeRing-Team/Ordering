package com.example.orderingproject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantPreviewDto {

    private Long restaurantId;
    // private Integer ratings;
    private String restaurantName;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private List<String> representativeMenus;

    public String getRestaurantName(){
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName){
        this.restaurantName = restaurantName;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

    public String getBackgroundImageUrl(){
        return backgroundImageUrl;
    }
    public void setBackgroundImageUrl(String backgroundImageUrl){
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public List<String> getRepresentativeMenus(){
        return representativeMenus;
    }
    public void setRepresentativeMenus(List<String> representativeMenus){
        this.representativeMenus = representativeMenus;
    }
}