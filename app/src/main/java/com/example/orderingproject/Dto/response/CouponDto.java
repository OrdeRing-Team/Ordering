package com.example.orderingproject.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class CouponDto {

    private String serialNumber;
    private int value;
    private Long couponId;

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getValue() {
        return value;
    }

    public Long getCouponId() {
        return couponId;
    }
}