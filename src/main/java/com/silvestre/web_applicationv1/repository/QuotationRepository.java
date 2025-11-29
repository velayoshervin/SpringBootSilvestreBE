package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Quotation;
import jakarta.mail.Quota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.desktop.QuitEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation,Long> {
    List<Quotation> findAllByUserId(Long userId);

    Optional<Quotation> findByIdAndUserId(Long quotationId, Long userId);

    Page<Quotation> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT DISTINCT q.requestedEventDate  FROM Quotation q WHERE q.user.id = :userId")
    List<LocalDate> findAllEventDatesByUserId(@Param("userId") Long userId);
}
