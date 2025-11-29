package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingHistoryRepository extends JpaRepository<BookingHistory,Long> {
}
