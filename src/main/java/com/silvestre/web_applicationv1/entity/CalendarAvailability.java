package com.silvestre.web_applicationv1.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
    public class CalendarAvailability {
        @Id //primary key
        private LocalDate date;
        private String status;
        private String reason;
        @OneToOne
        @JoinColumn(name="booking_id")
        private Booking booking;

    public CalendarAvailability(LocalDate date) {
        this.date= date;
    }
}
