package com.silvestre.web_applicationv1.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaymentIntentAttributes {
    private List<PaymentData> payments;
}
