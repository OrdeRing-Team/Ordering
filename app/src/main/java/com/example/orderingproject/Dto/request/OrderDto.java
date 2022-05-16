package com.example.orderingproject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class OrderDto {

    private Integer tableNumber;
    private OrderType orderType;
    private Map<Long, Integer> putBaskets; // key: foodId, value: count
}
