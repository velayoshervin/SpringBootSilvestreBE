package com.silvestre.web_applicationv1.Dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentCustomerDto {

    private String quotationReference;   // e.g., QUO-13
    private PaymentStatus status;               // PAID, PENDING, FAILED
    private String paymentType;          // full / partial
    private BigDecimal amountFormatted;      // e.g., PHP 270,800.00
    private BigDecimal remainingBalanceFormatted; // e.g., PHP 0.00
    private String description;          // optional text from payment
    private String statementDescriptor;  // optional
    private Instant paidAt; // time when payment is accepted
    private Instant createdAt; // time when it is initiated
    private BigDecimal totalAmountDue;
    private String paidAtFormatted;
    private String createdAtFormatted;
    private JsonNode rawPayload;



    public PaymentCustomerDto(Payments payment){

        this.quotationReference= payment.getExternalReference();
        this.status= payment.getStatus();
        this.createdAt= payment.getCreatedAt();
        this.paidAt = payment.getPaidAt();
        this.statementDescriptor = payment.getStatementDescriptor();
        this.description = payment.getDescription();
        this.paymentType = payment.getPaymentType();
        this.amountFormatted = payment.getAmount()
                .movePointLeft(2); // divides by 100 accurately
        this.remainingBalanceFormatted = payment.getRemainingBalance()
                .movePointLeft(2);
        this.totalAmountDue = payment.getTotalDue()
                .movePointLeft(2);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a")
                .withZone(ZoneId.systemDefault());

        this.paidAtFormatted = paidAt !=null ? formatter.format(paidAt): null;
        this.createdAtFormatted = createdAt !=null ? formatter.format(createdAt): null;
        this.rawPayload=payment.getRawPayload();
    }
}
