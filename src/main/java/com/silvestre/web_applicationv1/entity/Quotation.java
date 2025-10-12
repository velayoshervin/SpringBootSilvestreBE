package com.silvestre.web_applicationv1.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="quotation_id")
    private long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationLineItem> lineItems;

    @Enumerated(EnumType.STRING)
    private QuotationStatus status; 

    @CreationTimestamp
    private LocalDateTime creationTime;

    @UpdateTimestamp
    private LocalDateTime modificationTime;

    private BigDecimal total;

    private LocalDate requestedEventDate;

    //adding pax, eventType, venue

    private Integer pax;

    private String eventType;

    @ManyToOne
    @JoinColumn(name = "venue_id",nullable = true) // foreign key column in the quotation table
    private Venue venue;


    public BigDecimal getTotalAmount() {
        return lineItems.stream()
                .map(item -> item.getPriceAtQuotation().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

    private String celebrants;
    private String customerName;
    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT") // Store as JSON
    private String customFoodSelection;

    private Long packageId;

    }

