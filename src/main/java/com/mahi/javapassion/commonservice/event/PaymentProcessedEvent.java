package com.mahi.javapassion.commonservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentProcessedEvent {
    private String paymentId;
    private String orderId;
}
