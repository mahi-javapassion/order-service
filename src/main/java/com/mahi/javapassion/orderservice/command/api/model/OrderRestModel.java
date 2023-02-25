package com.mahi.javapassion.orderservice.command.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRestModel {
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
}
