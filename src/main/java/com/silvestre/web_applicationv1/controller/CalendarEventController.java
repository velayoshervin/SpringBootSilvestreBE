package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.CalendarEvent;
import com.silvestre.web_applicationv1.requests.CalendarCreateRequest;
import com.silvestre.web_applicationv1.requests.CalendarEventRequest;
import com.silvestre.web_applicationv1.response.CalendarEventResponse;
import com.silvestre.web_applicationv1.service.CalendarEventService;
import com.silvestre.web_applicationv1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("calendarEvents")
public class CalendarEventController {

    @Autowired private CalendarEventService calendarEventService;


    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id){
      CalendarEvent calEvent= calendarEventService.findWithDetailsByEventId(id);
      CalendarEventResponse response = new CalendarEventResponse(calEvent);
      return  ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents(){
        List<CalendarEventResponse> responseList = calendarEventService.getAllEvents();
        return ResponseEntity.ok(responseList);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CalendarEventRequest request){
        CalendarEventResponse response= calendarEventService.create(request);


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
