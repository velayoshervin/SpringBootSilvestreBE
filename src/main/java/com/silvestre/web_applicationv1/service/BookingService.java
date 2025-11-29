package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.BookingDto;
import com.silvestre.web_applicationv1.Dto.BookingMapper;
import com.silvestre.web_applicationv1.Dto.StaffDTO;
import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.BookingStaffAssignment;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.entity.Staff;
import com.silvestre.web_applicationv1.repository.BookingRepository;
import com.silvestre.web_applicationv1.repository.BookingStaffAssignmentRepository;
import com.silvestre.web_applicationv1.repository.StaffRepository;
import com.silvestre.web_applicationv1.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BookingStaffAssignmentRepository assignmentRepository;

    @Autowired
    private UserRepository userRepository;


    public Booking save(Booking booking){
        return repository.save(booking);
    }

    public Page<BookingDto> getBookings (Pageable pageable){
        Page<Booking> bookings=  repository.findAll(pageable);

        bookings.forEach(booking -> {
            if (booking.getStaffAssignments() != null) {
                booking.getStaffAssignments().size(); // Trigger loading
            }
        });

        return bookings.map(b-> bookingMapper.toDTO(b));
    }

    public Optional<Booking> findByQuotation(Quotation quotation) {
        return repository.findByQuotation(quotation);
    }


    // ✅ ADD THIS METHOD: Get bookings with staff assignments fully loaded
    public Page<BookingDto> getBookingsWithStaff(Pageable pageable) {
        Page<Booking> bookings = repository.findAll(pageable);

        // Eagerly load all related data including staff
        bookings.forEach(booking -> {
            // Force loading of staff assignments and their relationships
            if (booking.getStaffAssignments() != null) {
                booking.getStaffAssignments().forEach(assignment -> {
                    // Trigger loading of staff data
                    if (assignment.getStaff() != null) {
                        assignment.getStaff().getName(); // This loads the staff entity
                    }
                });
            }
            // Also ensure payments are loaded if needed
            if (booking.getPayments() != null) {
                booking.getPayments().size();
            }
        });

        return bookings.map(bookingMapper::toDTO);
    }


    @Transactional
    public Booking assignStaffToBooking(Long bookingId, List<Long> staffIds, String assignedByUsername) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        // Clear existing assignments
        List<BookingStaffAssignment> existingAssignments = assignmentRepository.findByBookingId(bookingId);
        assignmentRepository.deleteAll(existingAssignments);

        // Create new assignments
        for (Long staffId : staffIds) {
            Staff staff = staffRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

            BookingStaffAssignment assignment = new BookingStaffAssignment();
            assignment.setBooking(booking);
            assignment.setStaff(staff);
            assignment.setAssignedRole(staff.getRole());
            assignment.setAssignmentStatus("CONFIRMED");
            assignment.setAssignedAt(LocalDateTime.now());

            // Set assigned by user if needed
            userRepository.findByEmail(assignedByUsername).ifPresent(assignment::setAssignedBy);

            assignmentRepository.save(assignment);
        }

        // Refresh and return the booking with staff assignments
        return repository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found after assignment"));
    }

    public List<StaffDTO> getAssignedStaffForBooking(Long bookingId) {
        List<BookingStaffAssignment> assignments = assignmentRepository.findByBookingId(bookingId);

        return assignments.stream()
                .map(assignment -> {
                    Staff staff = assignment.getStaff();
                    StaffDTO staffDTO = new StaffDTO();
                    staffDTO.setStaffId(staff.getStaffId());
                    staffDTO.setName(staff.getName());
                    staffDTO.setRole(staff.getRole());
                    staffDTO.setCategory(staff.getCategory());
                    staffDTO.setAvatarInitials(staff.getAvatarInitials());
                    staffDTO.setContactInfo(staff.getContactInfo());
                    staffDTO.setIsActive(staff.getIsActive());
                    return staffDTO;
                })
                .collect(Collectors.toList());
    }

    public Optional<Booking> findById(Long bookingId) {
        return repository.findById(bookingId);
    }
}
