package com.example.orderingproject;

public class MenuData {

    private String iv_menu;
    private String name;
    private String price;
    private String intro;
    private Long foodId;
    private boolean soldout;

    public MenuData(Long foodId, String iv_menu, String name, String price, String intro, boolean soldout) {
        this.foodId = foodId;
        this.iv_menu = iv_menu;
        this.name = name;
        this.price = price;
        this.intro = intro;
        this.soldout = soldout;

    }

    public Long getFoodId() { return foodId;}

    public void setFoodId(Long id){this.foodId = id;}

    public String getIv_menu() {
        return iv_menu;
    }

    public void setIv_menu(String iv_menu) {
        this.iv_menu = iv_menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String tv_name) {
        this.name = tv_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public boolean getSoldout() { return soldout; }

    public void setSoldout(boolean soldout) { this.soldout = soldout; }
}
