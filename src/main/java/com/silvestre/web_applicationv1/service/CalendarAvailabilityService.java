package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.ExceptionHandler.ResourceNotFoundException;
import com.silvestre.web_applicationv1.entity.CalendarAvailability;
import com.silvestre.web_applicationv1.entity.Item;
import com.silvestre.web_applicationv1.repository.CalendarAvailabilityRepository;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CalendarAvailabilityService {

    @Autowired
    private CalendarAvailabilityRepository calendarAvailabilityRepository;

    public CalendarAvailability save(CalendarAvailability calendarAvailability){
        return calendarAvailabilityRepository.save(calendarAvailability);
    }

    public CalendarAvailability findByLocalDate(LocalDate localDate){
        return calendarAvailabilityRepository.findById(localDate).orElseThrow(()-> new ResourceNotFoundException("no date record found using "+ localDate));
    }

    public List<CalendarAvailability> findAll(){
        return calendarAvailabilityRepository.findAll();
    }

    public void deleteById(LocalDate date) {
         calendarAvailabilityRepository.deleteById(date);
    }

    public PaginatedResponse<CalendarAvailability> getCalendarPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        Page<CalendarAvailability> pageData = calendarAvailabilityRepository.findAll(pageable);
        return new PaginatedResponse<>(pageData);
    }

    public PaginatedResponse<CalendarAvailability> getDefaultCalendarPaged(int page, int size, int pastDays){
        page = Math.max(0, page - 1);
        Pageable pageable= PageRequest.of(page,size);
        LocalDate startDate = LocalDate.now().minusDays(pastDays);
        Page<CalendarAvailability> pageData= calendarAvailabilityRepository.findByDateGreaterThanEqualOrderByDateAsc(startDate, pageable);
        return new PaginatedResponse<>(pageData);
    }


    public List<CalendarAvailability> findFromToday() {
        LocalDate localDate= LocalDate.now();
        return calendarAvailabilityRepository.findFrom(localDate);
    }

    public Page<CalendarAvailability> findByDate(LocalDate date, Pageable pageable) {
        return calendarAvailabilityRepository.findAllByDate(date,pageable);
    }

    public CalendarAvailability createOrUpdate(LocalDate date, String status, String reason) {
        CalendarAvailability availability = calendarAvailabilityRepository.findById(date)
                .orElse(new CalendarAvailability(date)); // Date is set in constructor

        availability.setStatus(status);
        availability.setReason(reason);

        return calendarAvailabilityRepository.save(availability);
    }

    public CalendarAvailability reserveDate(LocalDate date) {


        return calendarAvailabilityRepository.findById(date)
                .map(existing -> {
                    if ("RESCHEDULED".equals(existing.getStatus()) ||
                            "AVAILABLE".equals(existing.getStatus()) ||
                            "RESERVED_FOR_PAYMENT".equals(existing.getStatus())) {

                        existing.setStatus("RESERVED_FOR_PAYMENT");
                        existing.setReason("currently reserved until payment is made or payment time expired");
                        return existing;
                    } else {
                        throw new IllegalStateException("Date " + date + " is not available for reservation. Current status: " + existing.getStatus());
                    }
                })
                .orElseGet(() -> {
                    return CalendarAvailability.builder()
                            .date(date)
                            .status("RESERVED_FOR_PAYMENT")
                            .reason("currently reserved until payment is made or payment time expired")
                            .build();
                });
    }

}
