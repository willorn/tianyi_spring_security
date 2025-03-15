package com.spring.statemachine.service;

import com.spring.statemachine.config.OrderEvents;
import com.spring.statemachine.config.OrderStatus;
import com.spring.statemachine.domain.Order;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;

@Component("orderProcessor")
@Slf4j
public class OrderProcessor {

    // [INIT] --PAY事件--> [PAYED] --SHIPPING事件--> [SHIPPED] --RECEIVE事件--> [RECEIVED]
    @Resource
    private StateMachine<OrderStatus, OrderEvents> orderStateMachine;

    // persister 本身是无状态的；真正的状态只存在于 order 订单对象里
    @Resource
    private StateMachinePersister<OrderStatus, OrderEvents, Order> persister;

    // 订单业务发生了个某某事件, 调用这个方法
    // 把事件包装到 message 类里面，并且在 header 去指定这个状态机，需要流向哪个流程？
    public Boolean process(Order order, OrderEvents event) {
        Message<OrderEvents> message = MessageBuilder.withPayload(event)
                .setHeader("order", order).build();
        return sendEvent(message);
    }

    // 事件真正开始处理了. 
    // sendEvent 触发 OrderStateListener 的监听方法
    @SneakyThrows
    private boolean sendEvent(Message<OrderEvents> message) {
        Order order = (Order) message.getHeaders().get("order");
        persister.restore(orderStateMachine, order);
        return orderStateMachine.sendEvent(message);
    }

}