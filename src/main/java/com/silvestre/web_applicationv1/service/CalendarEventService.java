package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.CalendarCalendars;
import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.entity.Invitation;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.RSVP;
import com.silvestre.web_applicationv1.repository.CalendarEventRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.CalendarEventRequest;
import com.silvestre.web_applicationv1.response.CalendarEventResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Service
public class CalendarEventService {

    @Autowired private CalendarEventRepository calendarEventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CalendarCalendarsService calendarsService;

    public CalendarEvent findWithDetailsByEventId(Long id){
        return calendarEventRepository.findWithDetailsByEventId(id).orElseThrow(()-> new ResourceNotFoundException("event not found"));
    }

    public CalendarEventResponse create(CalendarEventRequest calendarEventRequest){

        CalendarCalendars calendar =calendarsService.findById(calendarEventRequest.getCalendarId());

        CalendarEvent calendarEvent= new CalendarEvent();
        calendarEvent.setCalendar(calendar);
        calendarEvent.setAllDay(calendarEventRequest.isAllDay());
        if (calendarEventRequest.getStartTime() != null) {
            calendarEvent.setStartTime(calendarEventRequest.getStartTime());
        }
        if (calendarEventRequest.getEndTime() != null) {
            calendarEvent.setEndTime(calendarEventRequest.getEndTime());
        }
        calendarEvent.setEventCategory(calendarEventRequest.getEventCategory());
        calendarEvent.setTitle(calendarEventRequest.getTitle());
        if(calendarEventRequest.getBookingId()!= null){
            calendarEvent.setBooking(calendarEvent.getBooking());
        }
        calendarEvent.setLocation(calendarEventRequest.getLocation());

        User eventOwner = userRepository.findById(calendarEventRequest.getCreatorId()).orElseThrow(()-> new ResourceNotFoundException("event owner not found"));
        calendarEvent.setEventCreator(eventOwner);
        List<Long> invited = calendarEventRequest.getAttendeeIds();


        List<User> users= new ArrayList<>(invited.stream().map((id) -> {
            return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        }).toList());

        if (users.stream().noneMatch(u -> u.getId().equals(eventOwner.getId()))) {
            users.add(eventOwner);
        }

        String defaultReminder = "P1D,PT30M";

        List<Invitation>  invitationList =users.stream().map((u)->
        {
            if(Objects.equals(u.getId(), eventOwner.getId()))
                return Invitation.builder().user(u).status(RSVP.ACCEPTED).reminderIntervals(calendarEventRequest.getReminderDurations()).calendarEvent(calendarEvent).build();
            return Invitation.builder().user(u).status(RSVP.INVITED).calendarEvent(calendarEvent).reminderIntervals(defaultReminder).build();
        }).toList();
        calendarEvent.setInvitations(invitationList);
        CalendarEvent saved=  calendarEventRepository.save(calendarEvent);
        return  new CalendarEventResponse(saved);
    }

    public CalendarEventResponse update(CalendarEvent calendarEvent){
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<CalendarEventResponse> getAllEvents(){
        List<CalendarEvent> list = calendarEventRepository.findAll();
      return list.stream().map(CalendarEventResponse::new).toList();
    }


    public List<CalendarEvent> findAllEventsUserIsInvitedTo(Long userId) {
        return  calendarEventRepository.findAllEventsUserIsInvitedTo(userId);
    }
}
