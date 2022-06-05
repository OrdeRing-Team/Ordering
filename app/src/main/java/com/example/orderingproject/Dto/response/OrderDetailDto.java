package com.example.orderingproject.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class OrderDetailDto extends OrderPreviewWithRestSimpleDto {

    List<OrderFoodDto> orderFoods;

    public List<OrderFoodDto> getOrderFoods() {
        return orderFoods;
    }
}