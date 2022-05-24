package com.example.orderingproject;

public class CouponData {

    private String serialNumber;
    private int value;
    private Long couponId;

    public CouponData(String serialNumber, int value, Long couponId) {
        this.serialNumber = serialNumber;
        this.value = value;
        this.couponId = couponId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getValue() { return value;}

    public Long getCouponId() { return couponId;}

}