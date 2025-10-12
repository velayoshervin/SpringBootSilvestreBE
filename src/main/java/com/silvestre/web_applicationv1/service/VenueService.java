package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.entity.Venue;
import com.silvestre.web_applicationv1.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {
    @Autowired
    private VenueRepository repository;


    public Venue create(Venue venue){
        return repository.save(venue);
    }

    public List<Venue> findAll(){
        return repository.findAll();
    }
}
