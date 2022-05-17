package com.example.orderingproject;

public class BasketData {

    private Long basketId;
    private Long foodId;
    private String foodName;
    private String imageUrl;
    private int price;
    private int count;

    public BasketData(Long basketId, Long foodId,String basketFoodName, String basketImageUrl, int basketPrice, int basketCount) {
        this.basketId = basketId;
        this.foodId = foodId;
        this.foodName = basketFoodName;
        this.imageUrl = basketImageUrl;
        this.price = basketPrice;
        this.count = basketCount;
    }

    public Long getBasketId() {
        return basketId;
    }

    public Long getFoodId() { return foodId;}

    public String getBasketFoodName() { return foodName;}

    public String getBasketImageUrl() { return imageUrl;}

    public int getBasketPrice() { return price;}

    public int getBasketCount() { return count;}


}