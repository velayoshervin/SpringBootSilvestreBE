package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.Event;
import com.silvestre.web_applicationv1.repository.EventRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/public/api/event")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @PostMapping

    public ResponseEntity<?> saveEvent(@RequestBody String event){
        Event event1= new Event();
        event1.setEventName(event);
        eventRepository.save(event1);

        return  ResponseEntity.ok( Map.of("message","created"));
    }

    @GetMapping
    public ResponseEntity<?> getEvents(){
       List<Event> events =eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

}
