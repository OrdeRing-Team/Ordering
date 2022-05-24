package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class CouponDto {

    private String serialNumber;
    private int value;

    public String getSerialNumber(){
        return serialNumber;
    }
    public int getValue(){
        return value;
    }
}