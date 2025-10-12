package com.silvestre.web_applicationv1.entity;

import com.silvestre.web_applicationv1.enums.EventCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "calendar_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarEvent {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long eventId;

@ManyToOne
@JoinColumn(name = "calendar_id", nullable = false)
private CalendarCalendars calendar;

@ManyToOne
@JoinColumn(name = "booking_id", nullable = true)
private Booking booking;

private String title;

private boolean allDay;

//private LocalDateTime startTime;
//
//private LocalDateTime endTime;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

private String location;

@Enumerated(EnumType.STRING)
private EventCategory eventCategory;

@ManyToOne
@JoinColumn(name = "creator_id", nullable = false)
private User eventCreator;

@OneToMany(mappedBy = "calendarEvent", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Invitation> invitations = new ArrayList<>();

@ManyToOne
@JoinColumn(name = "quotation_id", nullable = true)
private Quotation quotation;

}
