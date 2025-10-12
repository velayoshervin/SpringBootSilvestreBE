package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.CalendarAvailability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarAvailabilityRepository extends JpaRepository<CalendarAvailability, LocalDate> {

    Page<CalendarAvailability> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date, Pageable pageable);

    Page<CalendarAvailability> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate ,Pageable pageable);

    @Query("SELECT c FROM CalendarAvailability c " +
            "WHERE c.date >= :localDate " +
            "AND c.status NOT IN ('RESCHEDULED', 'AVAILABLE')")
    List<CalendarAvailability> findFrom(@Param("localDate") LocalDate localDate);

    Page<CalendarAvailability> findAllByDate(LocalDate date, Pageable pageable);
}
