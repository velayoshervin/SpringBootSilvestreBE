package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.BookingStatus;
import com.silvestre.web_applicationv1.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // PENDING, PAID, CANCELLED, etc.

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
