package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ConsultationRepository extends JpaRepository<Consultation,Long> {
    @Query("SELECT c FROM Consultation c ORDER BY " +
            "CASE WHEN c.status = 'SUBMITTED' THEN 0 ELSE 1 END, c.eventDate DESC")
    Page<Consultation> findAllSubmittedFirst(Pageable pageable);
}

//SUBMITTED