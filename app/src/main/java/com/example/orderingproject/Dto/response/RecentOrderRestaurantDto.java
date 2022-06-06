package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RecentOrderRestaurantDto {

    private Long restaurantId;
    private String restaurantName;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private float rating;
    private int orderingWaitingTime;


    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public float getRating() {
        return rating;
    }

    public int getOrderingWaitingTime() {
        return orderingWaitingTime;
    }
}