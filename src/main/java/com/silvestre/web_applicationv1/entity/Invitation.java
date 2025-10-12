package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.RSVP;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Invitation {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private CalendarEvent calendarEvent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVP status;
    private String reminderIntervals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<Duration> getReminderDurations() {
        if (reminderIntervals == null || reminderIntervals.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(reminderIntervals.split(","))
                .map(String::trim)
                .map(Duration::parse) // ISO-8601 duration
                .collect(Collectors.toList());
    }

    public void setReminderDurations(List<String> durations) {
        this.reminderIntervals = String.join(",", durations);
    }

    public List<String> getReminderDurationsAsStrings() {
        return Arrays.asList(reminderIntervals.split(","));
    }
}
