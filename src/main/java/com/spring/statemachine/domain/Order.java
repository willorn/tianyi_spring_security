package com.spring.statemachine.domain;

import com.spring.statemachine.config.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Order {
    private Long id;
    //其他字段自行添加
    private OrderStatus status;

}