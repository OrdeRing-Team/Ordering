package com.example.orderingproject.favoriteStores;

import java.util.List;

public class FavStoreData {
    String storeIcon;
    String storeName;
    List<String> storeSigMenus;
    Long restaurantId;
    String storeSigMenu;


    public FavStoreData(String storeIcon, String storeName, List<String> storeSigMenus, Long restaurantId, String storeSigMenu) {
        this.storeIcon = storeIcon;
        this.storeName = storeName;
        this.storeSigMenus = storeSigMenus;
        this.restaurantId = restaurantId;
        this.storeSigMenu = storeSigMenu;
    }

    public String getStoreIcon() {
        return storeIcon;
    }

    public void setStoreIcon(String storeIcon) {
        this.storeIcon = storeIcon;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<String> getStoreSigMenus() {
        return storeSigMenus;
    }

    public void setStoreSigMenus(List<String> storeSigMenus) {
        this.storeSigMenus = storeSigMenus;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStoreSigMenu() {
        return storeSigMenu;
    }

    public void setStoreSigMenu(String storeSigMenu) {
        this.storeSigMenu = storeSigMenu;
    }
}
