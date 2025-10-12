package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    Optional<Booking> findByQuotation(Quotation quotation);
}
