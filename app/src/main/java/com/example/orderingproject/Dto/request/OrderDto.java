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

    public OrderDto(Integer tableNumber, OrderType orderType) {
        this.tableNumber = tableNumber;
        this.orderType = orderType;
    }
}
