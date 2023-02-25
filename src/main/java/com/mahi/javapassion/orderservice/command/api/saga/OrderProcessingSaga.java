package com.mahi.javapassion.orderservice.command.api.saga;

import com.mahi.javapassion.commonservice.command.CompleteOrderCommand;
import com.mahi.javapassion.commonservice.command.ShipOrderCommand;
import com.mahi.javapassion.commonservice.command.ValidatePaymentCommand;
import com.mahi.javapassion.commonservice.event.OrderCompletedEvent;
import com.mahi.javapassion.commonservice.event.OrderShippedEvent;
import com.mahi.javapassion.commonservice.event.PaymentProcessedEvent;
import com.mahi.javapassion.commonservice.model.CardDetails;
import com.mahi.javapassion.commonservice.model.User;
import com.mahi.javapassion.commonservice.query.GetUserPaymentDetailsQuery;
import com.mahi.javapassion.orderservice.command.api.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


@Saga
@Slf4j
public class OrderProcessingSaga {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private QueryGateway queryGateway;

    public OrderProcessingSaga() {}


    public OrderProcessingSaga(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        log.info("OrderCreatedEvent in saga for orderId : {}", orderCreatedEvent.getOrderId());
        GetUserPaymentDetailsQuery query =
                new GetUserPaymentDetailsQuery(orderCreatedEvent.getUserId());
        User user = null;
        try {
            CardDetails cardDetails = CardDetails.builder()
                    .name("Mahesh Yerudkar")
                    .cardNumber("1234567890")
                    .validTillMonth(12)
                    .validTillYear(2030)
                    .cvv(235)
                    .build();
            user =  User.builder()
                    .userId(query.getUserId())
                    .firstName("Mahesh")
                    .lastName("Yerudkar")
                    .cardDetails(cardDetails)
                    .build();
        }
        catch(Exception e) {
            log.error(e.getMessage());
            //Start The compensating Transaction
        }
        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
//                .cardDetails(user.getCardDetails())
                .orderId(orderCreatedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .build();
        commandGateway.send(validatePaymentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent paymentProcessedEvent) {
        log.info("PaymentProcessedEvent in saga for orderId : {}", paymentProcessedEvent.getOrderId());
        try {
            ShipOrderCommand shipOrderCommand = ShipOrderCommand.builder()
                    .shipmentId(UUID.randomUUID().toString())
                    .orderId(paymentProcessedEvent.getOrderId())
                    .build();
            commandGateway.send(shipOrderCommand);
        }
        catch(Exception e) {
            log.error("Error during PaymentProcessedEvent in saga", e.getMessage());
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent orderShippedEvent) {
        log.info("OrderShippedEvent in saga for orderId : {}", orderShippedEvent.getOrderId());
        try {
            CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                    .orderId(orderShippedEvent.getOrderId())
                    .orderStatus("APPROVED")
                    .build();
            commandGateway.send(completeOrderCommand);
        }
        catch(Exception e) {
            log.error("Error during OrderShippedEvent in saga", e.getMessage());
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent in saga for orderId : {}", event.getOrderId());
    }
}
