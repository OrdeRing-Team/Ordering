package com.example.orderingproject.Dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class BasketListResultDto {

    private String restaurantName;
    private List<BasketFoodDto> basketFoods;

    public String getRestaurantName() {
        return restaurantName;
    }

    public List<BasketFoodDto> getBasketFoods() {
        return basketFoods;
    }


}