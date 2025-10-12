package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.BookingDto;
import com.silvestre.web_applicationv1.Dto.BookingMapper;
import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository repository;
    @Autowired
    private BookingMapper bookingMapper;


    public Booking save(Booking booking){
        return repository.save(booking);
    }

    public Page<BookingDto> getBookings (Pageable pageable){
        Page<Booking> bookings=  repository.findAll(pageable);

        return bookings.map(b-> bookingMapper.toDTO(b));
    }

    public Optional<Booking> findByQuotation(Quotation quotation) {
        return repository.findByQuotation(quotation);
    }
}
