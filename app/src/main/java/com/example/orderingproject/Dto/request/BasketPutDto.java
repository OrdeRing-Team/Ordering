package com.example.orderingproject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class BasketPutDto {

    private Long basketId;
    private int count;

    public Long getBasketId(){
        return basketId;
    }
    public int getCount(){
        return count;
    }
}