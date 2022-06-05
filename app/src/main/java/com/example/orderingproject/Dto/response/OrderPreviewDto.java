package com.example.orderingproject.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingproject.ENUM_CLASS.OrderStatus;
import com.example.orderingproject.ENUM_CLASS.OrderType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class OrderPreviewDto {

    private Long orderId;
    private Integer myOrderNumber;
    private String orderSummary;
    private String checkTime;
    private String cancelOrCompletedTime;
    private String receivedTime;
    private int totalPrice;
    private OrderType orderType;
    private Integer tableNumber;
    private OrderStatus orderStatus;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setMyOrderNumber(Integer myOrderNumber) {
        this.myOrderNumber = myOrderNumber;
    }

    public void setOrderSummary(String orderSummary) {
        this.orderSummary = orderSummary;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public void setCancelOrCompletedTime(String cancelOrCompletedTime) {
        this.cancelOrCompletedTime = cancelOrCompletedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }


    public Long getOrderId(){
        return orderId;
    }

    public Integer getMyOrderNumber(){
        return myOrderNumber;
    }

    public String getOrderSummary(){
        return orderSummary;
    }

    public String getCheckTime(){
        return checkTime;
    }

    public String getCancelOrCompletedTime(){
        return cancelOrCompletedTime;
    }

    public String getReceivedTime(){ return receivedTime; }

    public int getTotalPrice(){
        return totalPrice;
    }

    public OrderType getOrderType(){
        return orderType;
    }

    public Integer getTableNumber(){
        return tableNumber;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
