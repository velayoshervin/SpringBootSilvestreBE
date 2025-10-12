package com.silvestre.web_applicationv1.requests;

import com.silvestre.web_applicationv1.enums.EventCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarEventRequest {
    private Long calendarId;
    private Long bookingId;
    private String title;
    private boolean allDay;
    private OffsetDateTime startTime;   // <-- use OffsetDateTime
    private OffsetDateTime endTime;
    private String location;
    private EventCategory eventCategory;
    private Long creatorId;
    private List<Long> attendeeIds;
    private String reminderDurations;
}
