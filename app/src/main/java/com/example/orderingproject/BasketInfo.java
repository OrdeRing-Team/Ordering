package com.example.orderingproject;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasketInfo {

    // 장바구니에 음식이 담겨있을 때 플로팅 아이콘 표시 하기 위해 개수만 저장.
    // 나머지 장바구니에 담긴 목록은 장바구니액티비티 진입시 서버에서 불로오도록 설정

    private static int basketCount;

    public static void setBasketCount(int totalCount) {
        basketCount = totalCount;
    }

    public static int getBasketCount(){return basketCount;}

}