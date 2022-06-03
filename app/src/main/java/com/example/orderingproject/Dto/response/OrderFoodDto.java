package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class OrderFoodDto {

    private String foodName;
    private int price;
    private int count;

    public String getFoodName() {
        return foodName;
    }

    public int getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }
}