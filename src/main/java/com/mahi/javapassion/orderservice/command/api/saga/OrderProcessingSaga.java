package com.mahi.javapassion.orderservice.command.api.saga;

import com.mahi.javapassion.commonservice.command.ValidatePaymentCommand;
import com.mahi.javapassion.commonservice.model.User;
import com.mahi.javapassion.commonservice.query.GetUserPaymentDetailsQuery;
import com.mahi.javapassion.orderservice.command.api.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


@Saga
@Slf4j
public class OrderProcessingSaga {

    private CommandGateway commandGateway;
    private QueryGateway queryGateway;

    @Autowired
    public OrderProcessingSaga(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent orderCreatedEvent) {
        log.info("OrderCreatedEvent in saga for orderId : {}", orderCreatedEvent.getOrderId());
        GetUserPaymentDetailsQuery query =
                new GetUserPaymentDetailsQuery(orderCreatedEvent.getUserId());
        User user = null;
        try {
            user = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
        }
        catch(Exception e) {
            log.error(e.getMessage());
            //Start The compensating Transaction
        }
        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .cardDetails(user.getCardDetails())
                .orderId(orderCreatedEvent.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .build();
        commandGateway.sendAndWait(validatePaymentCommand);
    }

}
