package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.CalendarCalendarsDto;
import com.silvestre.web_applicationv1.entity.CalendarCalendars;
import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.requests.CalendarCreateRequest;
import com.silvestre.web_applicationv1.response.CalendarEventResponse;
import com.silvestre.web_applicationv1.service.CalendarCalendarsService;
import com.silvestre.web_applicationv1.service.CalendarDataResponse;
import com.silvestre.web_applicationv1.service.CalendarEventService;
import com.silvestre.web_applicationv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/calendar/data")
public class CalendarDataController {

    @Autowired
    private CalendarCalendarsService calendarCalendarsService;
    @Autowired
    private CalendarEventService calendarEventService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<CalendarDataResponse> getCalendarData(@RequestParam Long userId) {
        List<CalendarCalendars> calendars = calendarCalendarsService.findByUserId(userId);
        List<CalendarCalendarsDto> calendarsDtos = calendars.stream().map(CalendarCalendarsDto::new).toList();

        // Load all events user is invited to or created
        List<CalendarEvent> events = calendarEventService.findAllEventsUserIsInvitedTo(userId);
        List<CalendarEventResponse> eventResponses = events.stream()
                .map(CalendarEventResponse::new)
                .toList();
        return ResponseEntity.ok(new CalendarDataResponse(calendarsDtos, eventResponses));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/calendars")
    public ResponseEntity<?> create(@RequestBody CalendarCreateRequest request){
        calendarCalendarsService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("created");
    }
    @PutMapping("/calendars")
    public ResponseEntity<?> update(@RequestBody CalendarCreateRequest request, @RequestParam Long calendarId){
        CalendarCalendars existing =  calendarCalendarsService.findById(calendarId);
        User user= userService.findUserById(request.getUserId());

        if(!user.getId().equals(request.getUserId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not matching user ID");

        calendarCalendarsService.update(request,calendarId, existing);
        return ResponseEntity.ok("saved");
    }

}
