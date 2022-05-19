package com.example.orderingproject.Dto.request;

import com.example.orderingproject.ENUM_CLASS.FoodCategory;
import com.example.orderingproject.ENUM_CLASS.RestaurantType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantDto {

    private Long ownerId;
    private String restaurantName;
    private String ownerName;
    private String address;
    private int tableCount;
    private FoodCategory foodCategory;
    private RestaurantType restaurantType;
}
