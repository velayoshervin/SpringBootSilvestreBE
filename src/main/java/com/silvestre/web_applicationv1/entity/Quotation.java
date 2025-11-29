package com.silvestre.web_applicationv1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @Builder.Default
    private List<QuotationLineItem> lineItems =new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private QuotationStatus status; 

    @CreationTimestamp
    private LocalDateTime creationTime;

    @UpdateTimestamp
    private LocalDateTime modificationTime;

    private BigDecimal total;

    private LocalDate requestedEventDate;


    private Integer pax;

    private String eventType;

    @ManyToOne
    @JoinColumn(name = "venue_id",nullable = true) // foreign key column in the quotation table
    private Venue venue;

    private String clientVenue;


    public BigDecimal getTotalAmount() {

        if (lineItems == null || lineItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

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

    //new
    private Long menuBundleId;

    @Column(nullable = true)
    private LocalDateTime approvalTime;
    @Column(nullable = true)
    private LocalDateTime paymentDeadline;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookingHistory> history = new ArrayList<>();


    private LocalDate rescheduleTo;



    @OneToMany(mappedBy = "quotation")
    @JsonBackReference
    private List<Booking> bookings;



    // Contract fields
    private String contractPdfPath;
    private String signedContractPdfPath;

    @Column(columnDefinition = "TEXT")
    private String signatureData; // Base64 signature image
    private String signerName;
    private LocalDateTime signedAt;
    private Boolean contractSigned = false;

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus = ContractStatus.PENDING;
    }


enum ContractStatus {
    PENDING, SENT, SIGNED, EXPIRED
}