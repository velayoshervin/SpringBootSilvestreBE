package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.EventThemes;
import com.silvestre.web_applicationv1.service.EventThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/themes")
public class EventThemeController {

    @Autowired
    private EventThemeService service;

    @GetMapping
    public ResponseEntity<?> getThemes(){
        return  ResponseEntity.ok(service.getEventThemes());
    }

    @PostMapping
    public ResponseEntity<?> createTheme(@RequestBody EventThemes theme){
        EventThemes eventThemes = service.create(theme);
        return  ResponseEntity.status(HttpStatus.CREATED).body(eventThemes);
    }

    


}
