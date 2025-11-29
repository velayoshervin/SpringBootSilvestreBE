package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BookingStaffAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;


    @Column(name = "assigned_role")
    private String assignedRole;

    @Column(name = "assignment_status")
    private String assignmentStatus = "PENDING"; // PENDING, CONFIRMED, COMPLETED

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;


    @PrePersist
    protected void onCreate() {
        this.assignedAt = LocalDateTime.now();
    }

}
