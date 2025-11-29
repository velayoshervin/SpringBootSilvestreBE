package com.silvestre.web_applicationv1.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CustomPaymentIntentResponse {
    private String paymentIntentId; //map to id
    private String type; // map to type (payment_intent)
    private String clientKey;  //
    private String status;
    private String redirectUrl;
    private String returnUrl;
    private BigDecimal amount;
    private String currency;
    private List<String> paymentMethodAllowed;
}
