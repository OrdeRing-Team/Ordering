package com.example.orderingproject.Dto.request;

import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class BasketDto {

    private Long foodId;
    private int price;
    private int count;

    public BasketDto(long foodId, int price, int count){
        this.foodId = foodId;
        this.price = price;
        this.count = count;
    }
}
