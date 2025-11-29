package com.silvestre.web_applicationv1.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentIntentData {
    private String id;
    private String type;
    private PaymentIntentAttributes attributes;
}
