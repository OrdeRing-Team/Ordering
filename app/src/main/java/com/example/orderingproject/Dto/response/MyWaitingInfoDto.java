package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class MyWaitingInfoDto {

    private Long waitingId;
    private Integer myWaitingNumber;
    private Long numInFrontOfMe;
    private int estimatedWaitingTime;
    private String waitingRegisterTime;
    private Long restaurantId;
    private String restaurantName;
    private String profileImageUrl;
    private String backgroundImageUrl;

    public Long getWaitingId() {
        return waitingId;
    }

    public Integer getMyWaitingNumber() {
        return myWaitingNumber;
    }

    public Long getNumInFrontOfMe() {
        return numInFrontOfMe;
    }

    public int getEstimatedWaitingTime() {
        return estimatedWaitingTime;
    }

    public String getWaitingRegisterTime() {
        return waitingRegisterTime;
    }

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
}

