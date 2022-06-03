package com.example.orderingproject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.ENUM_CLASS.FoodCategory;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantPreviewListReqDto {

    private double latitude;      // 고객 위치 Y (위도)
    private double longitude;     // 고객 위치 X (경도)
    private FoodCategory foodCategory;
}