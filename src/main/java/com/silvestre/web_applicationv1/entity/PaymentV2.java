package com.silvestre.web_applicationv1.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tbl_payment")
public class PaymentV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // PayMongo core identifiers
    @Column(name = "paymongo_payment_id", unique = true)
    private String paymongoPaymentId;

    @Column(name = "paymongo_source_id")
    private String paymongoSourceId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Financial amounts (in PESO, not centavos)
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal netAmount;
    private BigDecimal totalDue;

    private String paymentMethod; // CARD, GCASH, GRAB_PAY, BANK_TRANSFER
    private String lastFourDigits; // For cards: "4242"
    private String bankName; // For bank transfers
    private String cardBrand;

    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public static PaymentV2 fromPaymongoResponse(JsonNode paymongoResponse, Quotation quotation, User user) {
        JsonNode paymentIntent = paymongoResponse.path("data");
        JsonNode attributes = paymentIntent.path("attributes");
        JsonNode payment = attributes.path("payments").get(0);
        JsonNode paymentAttributes = payment.path("attributes");
        JsonNode source = paymentAttributes.path("source");

        PaymentV2 newPayment = new PaymentV2();

        // Essential relationships
        newPayment.setQuotation(quotation);
        newPayment.setUser(user);

        // PayMongo identifiers
        newPayment.setPaymongoPaymentId(payment.path("id").asText());
        newPayment.setPaymongoSourceId(source.path("id").asText());

        // Payment status and type
        newPayment.setStatus(PaymentStatus.valueOf(paymentAttributes.path("status").asText().toUpperCase()));


        // Convert amounts from centavos to peso
        long amountCentavos = paymentAttributes.path("amount").asLong();
        long feeCentavos = paymentAttributes.path("fee").asLong();
        long netAmountCentavos = paymentAttributes.path("net_amount").asLong();

        newPayment.setAmount(BigDecimal.valueOf(amountCentavos).divide(BigDecimal.valueOf(100)));
        newPayment.setFee(BigDecimal.valueOf(feeCentavos).divide(BigDecimal.valueOf(100)));
        newPayment.setNetAmount(BigDecimal.valueOf(netAmountCentavos).divide(BigDecimal.valueOf(100)));
        newPayment.setTotalDue(quotation.getTotal());

        // Payment method info
        String sourceType = source.path("type").asText();
        newPayment.setPaymentMethod(sourceType.toUpperCase());

        if ("card".equals(sourceType)) {
            newPayment.setLastFourDigits(source.path("last4").asText());
            newPayment.setCardBrand(source.path("brand").asText().toUpperCase());
        }

        // Timestamps
        long paidAtTimestamp = paymentAttributes.path("paid_at").asLong();
        if (paidAtTimestamp > 0) {
            newPayment.setPaidAt(Instant.ofEpochSecond(paidAtTimestamp));
        }

        return newPayment;
    }
}
