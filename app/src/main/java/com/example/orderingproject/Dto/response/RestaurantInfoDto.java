package com.example.orderingproject.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.ENUM_CLASS.FoodCategory;
import com.example.orderingproject.ENUM_CLASS.RestaurantType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoDto {
    private String ownerName;
    private String restaurantName;
    private String address;
    private String notice;
    private RestaurantType restaurantType;
    private FoodCategory foodCategory;
    private Integer tableCount;
    private Integer orderingWaitingTime;
    private Integer admissionWaitingTime;
    private double latitude;
    private double longitude;

    public String getOwnerName() {
        return ownerName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getAddress() {
        return address;
    }

    public String getNotice() {
        return notice;
    }

    public RestaurantType getRestaurantType() {
        return restaurantType;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public Integer getTableCount() {
        return tableCount;
    }

    public Integer getOrderingWaitingTime() {
        return orderingWaitingTime;
    }

    public Integer getAdmissionWaitingTime() {
        return admissionWaitingTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}