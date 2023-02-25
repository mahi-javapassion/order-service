package com.mahi.javapassion.commonservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDetails {
    private String name;
    private String cardNumber;
    private Integer validTillMonth;
    private Integer validTillYear;
    private Integer cvv;
}
