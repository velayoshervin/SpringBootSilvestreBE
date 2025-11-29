package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.BookingStaffAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingStaffAssignmentRepository extends JpaRepository<BookingStaffAssignment,Long> {
    List<BookingStaffAssignment> findByBookingId(Long bookingId);
    void deleteByBookingId(Long bookingId);
}
