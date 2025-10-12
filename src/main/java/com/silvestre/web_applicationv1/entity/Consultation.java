package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.requests.ConsultationRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consultationId;

    private String fullName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    private String contact;

    private LocalDate eventDate;

    private String eventType;

    private String status;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String address;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Automatically update updatedAt before updating
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Consultation(ConsultationRequest request){
        this.fullName = request.getFullName();
        this.email = request.getEmail();
        this.contact = request.getContact();
        this.eventDate = request.getEventDate();
        this.eventType= request.getEventType();
        this.address= request.getAddress();
    }



}
