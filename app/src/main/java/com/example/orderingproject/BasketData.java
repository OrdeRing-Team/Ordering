package com.example.orderingproject;

public class BasketData {

    private Long basketId;
    private String foodName;
    private String imageUrl;
    private int price;
    private int count;

    public BasketData(Long basketId, String foodName, String imageUrl, int price, int count) {
        this.basketId = basketId;
        this.foodName = foodName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.count = count;
    }

    public Long getBasketId() {
        return basketId;
    }

    public String getFoodName() { return foodName;}

    public String getImageUrl() { return imageUrl;}

    public int getPrice() { return price;}

    public int getCount() { return count;}


}