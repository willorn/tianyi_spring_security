package com.spring.statemachine.config;

/**
 * 订单状态改变事件
 */
public enum OrderEvents {
    //支付，发货，确认收货
    PAY, SHIPPING, RECEIVE;
}