package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomPMPaymentMethods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(precision = 5, scale = 4)
    private BigDecimal feeRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumFee;

    @Builder.Default
    private Boolean isActive   = true;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
