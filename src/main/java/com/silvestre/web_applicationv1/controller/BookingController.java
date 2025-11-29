package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.Dto.BookingDto;
import com.silvestre.web_applicationv1.Dto.BookingMapper;
import com.silvestre.web_applicationv1.Dto.StaffAssignmentRequest;
import com.silvestre.web_applicationv1.Dto.StaffDTO;
import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.response.PaginatedResponse;
import com.silvestre.web_applicationv1.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class        BookingController {

    @Autowired
    private BookingService bookingService;


//    @GetMapping
//    public ResponseEntity<?> getPagedBooking( @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
//        Page<BookingDto> bookings = bookingService.getBookings(pageable);
//        return ResponseEntity.ok(new PaginatedResponse<>(bookings));
//    }


    @GetMapping("/{bookingId}/staff")
    public ResponseEntity<List<StaffDTO>> getBookingStaff(@PathVariable Long bookingId) {
        List<StaffDTO> staff = bookingService.getAssignedStaffForBooking(bookingId);
        return ResponseEntity.ok(staff);
    }


    @GetMapping
    public ResponseEntity<Page<BookingDto>> getBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) boolean includeStaff) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookingDto> bookings;

        if (includeStaff) {
            bookings = bookingService.getBookingsWithStaff(pageable);
        } else {
            bookings = bookingService.getBookings(pageable);
        }

        return ResponseEntity.ok(bookings);
    }


    @PostMapping("/{bookingId}/assign-staff")
    public ResponseEntity<Booking> assignStaffToBooking(
            @PathVariable Long bookingId,
            @RequestBody StaffAssignmentRequest request,
            Principal principal) {

        Booking updatedBooking = bookingService.assignStaffToBooking(
                bookingId, request.getStaffIds(), principal.getName());
        return ResponseEntity.ok(updatedBooking);
    }
}
