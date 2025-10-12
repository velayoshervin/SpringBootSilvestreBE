package com.silvestre.web_applicationv1.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    @JsonBackReference
    private Booking booking;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    // paymongo details
    private String externalReference;

    @Column(name = "paymongo_payment_id", nullable = false, unique = true, length = 50)
    private String paymongoPaymentId;

    private String balanceTransactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentType = "full"; // default value

    // amounts
    private Long amount;            // centavos
    private Long fee;
    private Long netAmount;
    private Long remainingBalance;
    private Long totalDue;

    private String currency = "PHP";

    @Column(columnDefinition = "TEXT")
    private String description;

    private String statementDescriptor;

    // customer info
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String sourceType;

    @Column(name = "source_id", length = 100)
    private String sourceId;

    @Column(name = "origin", length = 50)
    private String origin;

    private Instant paidAt;
    private Instant createdAt;
    private Instant updatedAt;

    // raw JSON webhook payload
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode rawPayload;
}
