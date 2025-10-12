package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private Long id;
    private BigDecimal amount; // In real value (PHP) - converted from centavos
    private BigDecimal fee; // In real value (PHP)
    private BigDecimal netAmount; // In real value (PHP)
    private PaymentStatus status;
    private String paymentMethod; // From sourceType
    private String transactionId; // From paymongoPaymentId
    private Instant paymentDate; // From paidAt
    private String customerName;
    private String customerEmail;
    private String description;
    private String currency;

}
