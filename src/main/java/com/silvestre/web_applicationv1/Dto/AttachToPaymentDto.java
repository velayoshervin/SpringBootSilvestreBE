package com.silvestre.web_applicationv1.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachToPaymentDto {
    private String id; //id of PaymentIntent
    private String payment_method; //id of PaymentMethod to attach to the PaymentIntent
     //An optional value for card payment method but required for shopee_pay, brankas, gcash, maya, grab_pay, dob
    private String phone;
    private String email;
    private String name;
}
