package com.silvestre.web_applicationv1.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PaymentIntentResponse {
    private PaymentIntentData data;
}
