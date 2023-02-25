package com.mahi.javapassion.commonservice.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteOrderCommand {
    private String orderId;
    private String orderStatus;
}
