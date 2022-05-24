package com.example.orderingproject;

public class CouponData {

    private String serialNumber;
    private int value;

    public CouponData(String serialNumber, int value) {
        this.serialNumber = serialNumber;
        this.value = value;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public int getValue() { return value;}

}