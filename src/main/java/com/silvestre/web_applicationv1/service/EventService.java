package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Event;
import com.silvestre.web_applicationv1.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> findAllEvents(){
        return eventRepository.findAll();
    }

    public void SaveEvent(Event event){
        eventRepository.save(event);
    }

}
