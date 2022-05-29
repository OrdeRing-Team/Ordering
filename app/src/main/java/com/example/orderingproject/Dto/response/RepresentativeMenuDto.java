package com.example.orderingproject.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.Dto.FoodDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RepresentativeMenuDto extends FoodDto {

    private Long representativeMenuId;

    public Long getRepresentativeMenuId() {
        return representativeMenuId;
    }

    public RepresentativeMenuDto(String foodName, int price, String menuIntro, Long foodId, String imageUrl, boolean soldOut) {
        super(foodName, price, menuIntro, foodId, imageUrl, soldOut);
    }
}