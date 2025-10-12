package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.CalendarAvailability;
import com.silvestre.web_applicationv1.entity.Item;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.CalendarAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("public/api/calendar")
public class CalendarAvailController {
    @Autowired
    private CalendarAvailabilityService calService;

    @GetMapping()
    public PaginatedResponse<CalendarAvailability> getPagedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "30") int pastDays) {
        return calService.getDefaultCalendarPaged(page,size,pastDays);
    }   

    @PostMapping()
    public ResponseEntity<?> addToCalendar(@RequestBody CalendarAvailability calendarAvailability){
        if (calendarAvailability.getDate() == null) {
            return ResponseEntity.badRequest().body("Date must be provided");
        }
        CalendarAvailability created = calService.save(calendarAvailability);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping()
    public ResponseEntity<?> updateCalendar(@RequestBody CalendarAvailability payload){
       CalendarAvailability found = calService.findByLocalDate(payload.getDate());
       found.setDate(payload.getDate());
       found.setBooking(payload.getBooking());
       found.setReason(payload.getReason());
       found.setStatus(payload.getStatus());
       return ResponseEntity.ok(calService.save(found));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteCalendarRecord(@RequestParam LocalDate date){
        CalendarAvailability existing= calService.findByLocalDate(date);
        calService.deleteById(date);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today")
    public ResponseEntity<?> BlockedSinceToday(){
        List<CalendarAvailability> blockedDates = calService.findFromToday();
        return ResponseEntity.ok(blockedDates);
    }

    @GetMapping("/blocked-date")
    public ResponseEntity<?> findByDate(@RequestParam LocalDate date, @RequestParam(defaultValue = "0") int pageNumber){
                Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("date").ascending());
                Page<CalendarAvailability> blockedDates = calService.findByDate(date,pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(blockedDates));
    }


}
