package com.spring.statemachine.service;

import com.spring.statemachine.config.OrderStatus;
import com.spring.statemachine.config.OrderEvents;
import com.spring.statemachine.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

/**
 * 当状态发生变化时，我们在干什么？
 * 
 * 方法名是随意定义的. 但这里我们用到的状态机的扭转，肯定要指定一个状态机。所以这里我们指定了orderStateMachine
 */
@Component("orderStateListener")
@Slf4j
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListener {
    @OnTransition( source = "INIT" ,target = "PAYED")
    public boolean pay(Message<OrderEvents> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.PAYED);
        log.info("订单表保存数据");
        log.info("发送站内消息");
        log.info("回调支付接口");
        log.info("同步至数据仓库");
        return true;
    }

    @OnTransition( source = "PAYED" , target = "SHIPPED")
    public boolean shipping(Message<OrderEvents> message) {
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.SHIPPED);
        log.info("创建物流订单");
        log.info("更新物流订单状态");
        log.info("同步至数据仓库");
        return true;
    }

    @OnTransition(source = "SHIPPED" , target = "RECEIVED")
    public boolean receive(Message<OrderEvents> message){
        Order order = (Order) message.getHeaders().get("order");
        order.setStatus(OrderStatus.PAYED);

        log.info("更新订单数据");
        log.info("同步至数据仓库");
        return true;
    }

    /**
     * 另一种写法: 对订单进行发货  
     */
    // public Order deliver(Long id) {
    //     Order order = orderMapper.selectById(id);
    //     log.info("线程名称：{},尝试发货，订单号：{}" ,Thread.currentThread().getName() , id);
    //     if (!sendEvent(OrderStatusChangeEvent.DELIVERY, order)) {
    //         log.error("线程名称：{},发货失败, 状态异常，订单信息：{}", Thread.currentThread().getName(), order);
    //         throw new RuntimeException("发货失败, 订单状态异常");
    //     }
    //     return order;
    // }
}