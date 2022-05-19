package com.example.orderingproject.Dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.ENUM_CLASS.OrderType;

public class OrderDto {

    private Integer tableNumber;
    private OrderType orderType;
    private Map<Long, Integer> putBaskets; // key: foodId, value: count

    public OrderDto(Integer tableNumber, OrderType orderType, Map<Long, Integer> putBaskets) {
        this.tableNumber = tableNumber;
        this.orderType = orderType;
        this.putBaskets = putBaskets;
    }
}
