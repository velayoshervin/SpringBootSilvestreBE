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
public class CustomPMPaymentRequest {

    private Long orderId;
    private BigDecimal amount;
    private String currency;
//    private String paymentMethodType;
    private String description;
    private CustomPMCustomerInfo customer;
}
//this class seems to be the payment intent /!not really



