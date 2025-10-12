package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Quotation;
import jakarta.mail.Quota;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.desktop.QuitEvent;
import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation,Long> {
    List<Quotation> findAllByUserId(Long userId);

    Optional<Quotation> findByIdAndUserId(Long quotationId, Long userId);

    Page<Quotation> findByUserId(Long userId, Pageable pageable);
}
