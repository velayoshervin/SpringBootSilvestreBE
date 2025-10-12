package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.CalendarCalendars;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.repository.CalendarCalendarsRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.requests.CalendarCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalendarCalendarsService {

    @Autowired
    private CalendarCalendarsRepository repository;
    @Autowired
    private UserRepository userRepo;

    public CalendarCalendars create(CalendarCreateRequest request){
        CalendarCalendars calendars = new CalendarCalendars();

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        calendars.setName(request.getName());
        calendars.setColor(request.getColor());
        calendars.getUsers().add(user);

        return repository.save(calendars);
    }
    public CalendarCalendars update(CalendarCreateRequest request, Long calId, CalendarCalendars calendarCalendars){

        calendarCalendars.setName(request.getName());
        calendarCalendars.setColor(request.getColor());
        return repository.save(calendarCalendars);
    }
    //problematic
    public void delete(Long id){
        CalendarCalendars existing = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException(" calendar not found"));
        repository.delete(existing);
    }

    public List<CalendarCalendars> findByUserId(Long id){
        return  repository.findAllByUserId(id);
    }

    public CalendarCalendars findById(Long calendarId) {
        return  repository.findById(calendarId).orElse(new CalendarCalendars());
    }
}
