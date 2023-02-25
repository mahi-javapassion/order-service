package com.mahi.javapassion.orderservice.command.api.aggregate;

import com.mahi.javapassion.commonservice.command.CompleteOrderCommand;
import com.mahi.javapassion.commonservice.event.OrderCompletedEvent;
import com.mahi.javapassion.orderservice.command.api.command.CreateOrderCommand;
import com.mahi.javapassion.orderservice.command.api.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {
    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;

    public OrderAggregate() {}

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        //Validate the Order
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);

    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent orderCreatedEvent) {
        this.orderId = orderCreatedEvent.getOrderId();
        this.productId = orderCreatedEvent.getProductId();
        this.userId = orderCreatedEvent.getUserId();
        this.addressId = orderCreatedEvent.getAddressId();
        this.orderStatus = orderCreatedEvent.getOrderStatus();
   }

    @CommandHandler
    public OrderAggregate(CompleteOrderCommand completeOrderCommand) {
        //Validate the Order
        OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                .orderId(completeOrderCommand.getOrderId())
                .orderStatus(completeOrderCommand.getOrderStatus())
                .build();
        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent orderCompletedEvent) {
        this.orderStatus = orderCompletedEvent.getOrderStatus();
    }
}
