package com.silvestre.web_applicationv1.repository;

import com.silvestre.web_applicationv1.entity.PaymentV2;
import com.silvestre.web_applicationv1.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentV2Repository extends JpaRepository<PaymentV2,Long> {


    List<PaymentV2> findByPaidAtBetween(Instant startDate, Instant endDate);
    List<PaymentV2> findByPaidAtAfter(Instant startDate);
    List<PaymentV2> findByPaidAtBefore(Instant endDate);

    List<PaymentV2> findByQuotationId(Long quotationId);

    Optional<PaymentV2> findByPaymongoPaymentId(String paymongoPaymentId);

    List<PaymentV2> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(p.netAmount), 0) FROM PaymentV2 p WHERE p.quotation.id = :quotationId AND p.status = 'PAID'")
    BigDecimal getTotalPaidForQuotation(@Param("quotationId") Long quotationId);

    boolean existsByQuotationId(Long quotationId);

    List<PaymentV2> findByStatus(PaymentStatus status);

    List<PaymentV2> findByQuotationIdAndStatus(Long quotationId, PaymentStatus status);

    List<PaymentV2> findAllByOrderByCreatedAtDesc();

    List<PaymentV2> findByQuotationIdOrderByCreatedAtDesc(Long quotationId);

    List<PaymentV2> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM PaymentV2 p WHERE p.quotation.id = :quotationId ORDER BY COALESCE(p.paidAt, p.createdAt) DESC")
    List<PaymentV2> findByQuotationIdOrderByPaymentDateDesc(@Param("quotationId") Long quotationId);

    @Query("SELECT p FROM PaymentV2 p ORDER BY " +
            "CASE WHEN p.status = 'PAID' THEN 1 " +
            "     WHEN p.status = 'PENDING' THEN 2 " +
            "     ELSE 3 END, " +
            "COALESCE(p.paidAt, p.createdAt) DESC")
    List<PaymentV2> findAllByOrderByStatusAndDateDesc();

    List<PaymentV2> findByPaidAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
