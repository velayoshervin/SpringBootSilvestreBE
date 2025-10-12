package com.silvestre.web_applicationv1.response;

import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.EventCategory;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEventResponse {

    private long eventId;
    private Long calendarId;
    private boolean allDay;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String title;
    private CalendarUserDto creator;
    private List<CalendarUserDto> attendees;
    private EventCategory eventCategory;

    public CalendarEventResponse(CalendarEvent calendarEvent){
        this.eventId= calendarEvent.getEventId();
        if (calendarEvent.getCalendar() != null) {
            this.calendarId = calendarEvent.getCalendar().getId();
        } else {
            this.calendarId = null; // Or throw a custom exception if this shouldn't happen
        }
        this.allDay = calendarEvent.isAllDay();
        this.startTime = calendarEvent.getStartTime();
        this.endTime = calendarEvent.getEndTime();
        this.creator = new CalendarUserDto(calendarEvent.getEventCreator());
        this.title = calendarEvent.getTitle();
        this.attendees = calendarEvent.getInvitations().stream().map((i)-> new CalendarUserDto(i.getUser())).toList();
        this.eventCategory = calendarEvent.getEventCategory();

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class CalendarUserDto{
        private Long id;
        private String email;
        private String firstname;
        private String lastname;
        private String avatarUrl;

        public CalendarUserDto(User user){
            this.id= user.getId();
            this.email = user.getEmail();
            this.firstname = user.getFirstname();
            this.lastname = user.getLastname();
            this.avatarUrl = user.getAvatarUrl();
        }
    }
}
