package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.Dto.request.RestaurantPreviewDto;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantPreviewWithDistanceDto extends RestaurantPreviewDto {

    private int distanceMeter;
}