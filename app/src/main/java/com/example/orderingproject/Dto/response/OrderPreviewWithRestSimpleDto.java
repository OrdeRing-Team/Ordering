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
    private String profileUrl;
    private String restaurantName;

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