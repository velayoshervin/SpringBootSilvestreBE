package com.silvestre.web_applicationv1.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.silvestre.web_applicationv1.enums.BookingStatus;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinColumn(name = "quotation_id", nullable = false, unique = true) // enforce uniqueness
    private Quotation quotation;


    private LocalDateTime requestedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;  // CONFIRMED, CANCELLED, etc.

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;

    // ✅ Payment info
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balance;

    @JsonManagedReference
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payments> payments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addPayment(Payments payment) {
        payments.add(payment);
        payment.setBooking(this);
    }


    // ✅ ADD THIS: Staff assignments
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingStaffAssignment> staffAssignments = new ArrayList<>();

    // ✅ ADD THIS: Staff assignment methods
    public void addStaffAssignment(BookingStaffAssignment assignment) {
        staffAssignments.add(assignment);
        assignment.setBooking(this);
    }

    public void removeStaffAssignment(BookingStaffAssignment assignment) {
        staffAssignments.remove(assignment);
        assignment.setBooking(null);
    }
}
