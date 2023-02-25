package com.mahi.javapassion.commonservice.command;

import com.mahi.javapassion.commonservice.model.CardDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidatePaymentCommand {
    private String paymentId;
    private String orderId;
    private CardDetails cardDetails;
}
