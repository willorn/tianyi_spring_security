package com.spring.statemachine.config;

import com.spring.statemachine.domain.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.EnumSet;

/**
 * 订单状态机配置
 * 
 * 1\说明我这个是一个配置类
 * 2\并且起一个名字，这是我们订单的状态机，我们后续监听的时候也是需要用到这个状态机的
 */
@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvents> {

    /**
     * 配置：状态机状态
     *
     * @param states
     * @throws Exception
     */
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvents> states) throws Exception {
        states.withStates().initial(OrderStatus.INIT).states(EnumSet.allOf(OrderStatus.class));
    }

    /**
     * 配置：状态转换事件关系
     *
     * @param transitions
     * @throws Exception
     */
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvents> transitions) throws Exception {
        transitions.withExternal().source(OrderStatus.INIT).target(OrderStatus.PAYED)
                .event(OrderEvents.PAY)
                .and().withExternal().source(OrderStatus.PAYED).target(OrderStatus.SHIPPED).event(OrderEvents.SHIPPING)
                .and().withExternal().source(OrderStatus.SHIPPED).target(OrderStatus.RECEIVED).event(OrderEvents.RECEIVE);
    }

    /**
     * 持久化配置
     * 在实际使用中，可以配合Redis等进行持久化操作
     *
     * @return
     */
    @Bean
    public DefaultStateMachinePersister persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<Object, Object, Order>() {
            @Override
            public void write(StateMachineContext<Object, Object> context, Order order) throws Exception {
                //此处并没有进行持久化操作
            }

            @Override
            public StateMachineContext<Object, Object> read(Order order) throws Exception {
                //  抓取订单中我们需要去看的一个状态值[关键]
                //此处直接获取Order中的状态，其实并没有进行持久化读取操作
                return new DefaultStateMachineContext(order.getStatus(), null, null, null);
            }
        });
    }
}