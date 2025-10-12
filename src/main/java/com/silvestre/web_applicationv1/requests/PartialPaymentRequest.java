package com.silvestre.web_applicationv1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartialPaymentRequest {

    private Long quotationId;
    private String paymentType;
    private Long amount;
    private LocalDate eventDate;
}
