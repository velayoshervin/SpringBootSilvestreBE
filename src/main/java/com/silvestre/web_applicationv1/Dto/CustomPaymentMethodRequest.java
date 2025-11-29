package com.silvestre.web_applicationv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPaymentMethodRequest {
    private String paymentMethodType; // "gcash", "card", "paymaya"
    private CustomPMCustomerInfo customer;
    private String returnUrl;
}
