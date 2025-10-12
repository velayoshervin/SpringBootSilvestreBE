package com.silvestre.web_applicationv1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuotationLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    @JsonBackReference
    private Quotation quotation;

    private String description;
    private BigDecimal priceAtQuotation;
    private int quantity;


    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    // any other fields relevant

    @Transient
    private Long itemId;
}