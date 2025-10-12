package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.EventThemes;
import com.silvestre.web_applicationv1.repository.EventThemesRepository;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventThemeService {

    @Autowired
    private EventThemesRepository repository;

    public List<EventThemes> getEventThemes(){
        return repository.findAll();
    }

    public EventThemes create(EventThemes eventThemes){
        return repository.save(eventThemes);
    }

    public EventThemes update(EventThemes eventThemes, Long id){
        EventThemes existing = repository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("theme doesn't exist"));


        return repository.save(existing);
    }

    public EventThemes getById(Long id){
        return repository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("theme doesn't exist"));
    }
}
