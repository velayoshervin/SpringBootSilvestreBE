package com.silvestre.web_applicationv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPMCardPaymentRequest {
    private String paymentIntentId; // If already created
    private BigDecimal amount;      // If creating new intent
    private String currency;
    private String description;

    // Card specific
    private CustomPMCardDetails card;
    private CustomPMCustomerInfo customer;
}
