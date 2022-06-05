package com.example.orderingproject.Dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class OrderPreviewWithRestSimpleDto extends OrderPreviewDto {

    private Long restaurantId;
    private Long reviewId;
    private String profileUrl;
    private String restaurantName;
    private int orderingWaitingTime;

    public Long getReviewId() {
        return reviewId;
    }

    public int getOrderingWaitingTime() {
        return orderingWaitingTime;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
}