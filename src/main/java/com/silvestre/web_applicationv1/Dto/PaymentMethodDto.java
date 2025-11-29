package com.silvestre.web_applicationv1.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodDto {
    private String type; //gcash, card etc
    private String email;
    private String phone;
    private String name;
    private String card_number;
    private int exp_month;
    private int exp_year;
    private String cvc;
    private String bank_code;
}
