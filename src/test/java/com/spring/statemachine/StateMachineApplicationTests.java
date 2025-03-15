package com.spring.statemachine;

import com.spring.statemachine.config.OrderEvents;
import com.spring.statemachine.config.OrderStatus;
import com.spring.statemachine.domain.Order;
import com.spring.statemachine.service.OrderProcessor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class StateMachineApplicationTests {
    @Resource
    private OrderProcessor orderProcessor;
    @Test
    void pay() {
        //准备业务数据
        Order order = new Order(OrderStatus.INIT);
        // 现在业务数据是初始化状态，我让他进行到已支付的状态
        orderProcessor.process(order, OrderEvents.PAY);
    }

    @Test
    void shipping() {
        //准备业务数据
        Order order = new Order(OrderStatus.PAYED);
        //现在状态是已支付，然后我现在使用状态机，让他进行送快递事件
        Boolean process = orderProcessor.process(order, OrderEvents.SHIPPING);
        System.out.println(process);
    }

    @Test
    void receive() {
        //准备业务数据
        Order order = new Order(OrderStatus.SHIPPED);
        //现在状态是已经送达了，那么我现在让他跑一下签收事件
        orderProcessor.process(order, OrderEvents.RECEIVE);
    }

    @Test
    void invalidOrder() {
        //准备业务数据
        Order order = new Order(OrderStatus.PAYED);
        // 现在是已支付的状态，我让他执行一下签收状态，当然这个是不行的
        Boolean process = orderProcessor.process(order, OrderEvents.RECEIVE);
        log.info("状态机接收结果：{}",process);
    }

}
