package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class BasketResponseDto {

    private Long basketId;
    private String foodName;
    private String imageUrl;
    private int price;
    private int count;

    public Long getBasketId() { return basketId;}
    public String getFoodName() { return foodName;}
    public String getImageUrl() { return imageUrl;}
    public int getPrice() { return price;}
    public int getCount(){ return count;}
}
