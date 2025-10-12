package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.Payments;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
    Page<Payments> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
            "FROM Payments p " +
            "WHERE p.quotation.id = :quotationId " +
            "AND p.status = :status")
    BigDecimal sumOfAllPaymentsForQuotation(@Param("quotationId") Long quotationId,
                                            @Param("status") PaymentStatus status);
}
