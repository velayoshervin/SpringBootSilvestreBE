package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.CalendarCalendars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalendarCalendarsRepository extends JpaRepository<CalendarCalendars, Long> {
    @Query("SELECT c FROM CalendarCalendars c JOIN c.users u WHERE u.id = :userId")
    List<CalendarCalendars> findAllByUserId(@Param("userId") Long userId);
}
