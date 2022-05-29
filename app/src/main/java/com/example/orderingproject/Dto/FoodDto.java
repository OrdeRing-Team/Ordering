package com.example.orderingproject.Dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class FoodDto {

    // 불러오기
    private Long foodId;
    private String imageUrl;

    private String foodName;
    private int price;
    private boolean soldOut;
    private String menuIntro;

    public FoodDto(String foodName, int price, String menuIntro, Long foodId, String imageUrl, boolean soldOut){
        this.foodName = foodName;
        this.price = price;
        this.menuIntro = menuIntro;
        this.foodId = foodId;
        this.imageUrl = imageUrl;
        this.soldOut = soldOut;
    }


    public boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.soldOut = soldOut;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMenuIntro() {
        return menuIntro;
    }

    public void setMenuIntro(String menuIntro) {
        this.menuIntro = menuIntro;
    }
}