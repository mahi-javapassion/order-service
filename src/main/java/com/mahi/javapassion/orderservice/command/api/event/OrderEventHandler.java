package com.mahi.javapassion.orderservice.command.api.event;

import com.mahi.javapassion.commonservice.event.OrderCompletedEvent;
import com.mahi.javapassion.orderservice.command.api.entity.OrderEntity;
import com.mahi.javapassion.orderservice.command.api.repository.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private OrderRepository orderRepository;

    public OrderEventHandler(OrderRepository orderRepository) {
        this.orderRepository= orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        OrderEntity entity = new OrderEntity();
        BeanUtils.copyProperties(orderCreatedEvent, entity);
        orderRepository.save(entity);
    }

    @EventHandler
    public void on(OrderCompletedEvent orderCompletedEvent) {
        OrderEntity entity = orderRepository.findById(orderCompletedEvent.getOrderId()).get();
        entity.setOrderStatus(orderCompletedEvent.getOrderStatus());
        orderRepository.save(entity);
    }

}
