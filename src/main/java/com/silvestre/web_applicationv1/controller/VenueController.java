package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/venue")
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    @GetMapping()
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(venueRepository.findAll());
    }

}
