package com.silvestre.web_applicationv1.response;


import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDto {

    private Long id;
    private String externalReference;
    private String paymongoPaymentId;
    private String balanceTransactionId;

    private PaymentStatus status;
    private String paymentType;

    // ✅ Converted amounts (centavos → pesos)
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal netAmount;
    private BigDecimal remainingBalance;
    private BigDecimal totalDue;

    private String currency;

    private String description;
    private String statementDescriptor;

    // customer info
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String sourceType;
    private String sourceId;
    private String origin;

    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    // formatted dates for UI
    private String paidAtFormatted;
    private String createdAtFormatted;

    public PaymentResponseDto(Payments payment) {
        this.id = payment.getId();
        this.externalReference = payment.getExternalReference();
        this.paymongoPaymentId = payment.getPaymongoPaymentId();
        this.balanceTransactionId = payment.getBalanceTransactionId();
        this.status = payment.getStatus();
        this.paymentType = payment.getPaymentType();
        this.currency = payment.getCurrency();
        this.description = payment.getDescription();
        this.statementDescriptor = payment.getStatementDescriptor();
        this.customerName = payment.getCustomerName();
        this.customerEmail = payment.getCustomerEmail();
        this.customerPhone = payment.getCustomerPhone();
        this.sourceType = payment.getSourceType();
        this.sourceId = payment.getSourceId();
        this.origin = payment.getOrigin();
        this.paidAt = payment.getPaidAt();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();

        // ✅ Convert centavos → pesos
        this.amount = payment.getAmount();
        this.fee = payment.getFee();
        this.netAmount = payment.getNetAmount();
        this.remainingBalance = payment.getRemainingBalance();
        this.totalDue = payment.getTotalDue();

        // ✅ Format dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a")
                .withZone(ZoneId.systemDefault());
        this.paidAtFormatted = paidAt != null ? formatter.format(paidAt) : null;
        this.createdAtFormatted = createdAt != null ? formatter.format(createdAt) : null;
    }

    private BigDecimal toPesos(Long centavos) {
        return centavos != null ? BigDecimal.valueOf(centavos).movePointLeft(2) : BigDecimal.ZERO;
    }
}
