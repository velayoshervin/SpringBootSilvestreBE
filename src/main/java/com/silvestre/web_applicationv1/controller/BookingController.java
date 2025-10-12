package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.BookingDto;
import com.silvestre.web_applicationv1.Dto.BookingMapper;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class        BookingController {

    @Autowired
    private BookingService bookingService;


    @GetMapping
    public ResponseEntity<?> getPagedBooking( @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<BookingDto> bookings = bookingService.getBookings(pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(bookings));
    }
}
