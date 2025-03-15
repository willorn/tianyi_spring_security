package com.spring.statemachine.config;

/**
 * 订单状态
 */
public enum OrderStatus {
    //待支付，待发货，待收货，订单结束
    INIT , PAYED, SHIPPED, RECEIVED;
}