package com.silvestre.web_applicationv1.controller;

import com.silvestre.web_applicationv1.entity.PaymentV2;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.repository.PaymentV2Repository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("public/paymentV2")
public class PaymentV2Controller {


    @Autowired
    private PaymentV2Repository paymentV2Repository;

    @Getter
    @Setter
    public static class PaymentTableDto {
        private Long id;
        private String paymentId;
        private Long quotationId;
        private String customerName;
        private BigDecimal amount;        // What customer paid (gross)
        private BigDecimal netAmount;     // What you receive (after fees)
        private BigDecimal fee;           // PayMongo fees
        private BigDecimal quotationTotal; // Total amount for the quotation
        private String status;
        private String paymentMethod;
        private String paidAt;

        public PaymentTableDto(PaymentV2 payment) {
            this.id = payment.getId();
            this.paymentId = payment.getPaymongoPaymentId();
            this.quotationId = payment.getQuotation().getId();
            this.customerName = payment.getUser().getFirstname() + " " + payment.getUser().getLastname();
            this.amount = payment.getAmount();        // Gross amount customer paid
            this.netAmount = payment.getNetAmount();  // Net amount you receive
            this.fee = payment.getFee();              // PayMongo fees deducted
            this.quotationTotal = payment.getQuotation().getTotal(); // Quotation total
            this.status = payment.getStatus().toString();
            this.paymentMethod = payment.getPaymentMethod();

            if (payment.getPaidAt() != null) {
                this.paidAt = payment.getPaidAt().atZone(java.time.ZoneId.of("Asia/Manila"))
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"));
            }
        }
    }

    @GetMapping
    public ResponseEntity<List<PaymentTableDto>> getAllPayments() {
        List<PaymentV2> payments = paymentV2Repository.findAllByOrderByCreatedAtDesc();
        List<PaymentTableDto> paymentDtos = payments.stream()
                .map(PaymentTableDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDtos);
    }

    // Get payments by quotation - sorted by latest first
    @GetMapping("/quotation/{quotationId}")
    public ResponseEntity<List<PaymentTableDto>> getPaymentsByQuotation(@PathVariable Long quotationId) {
        List<PaymentV2> payments = paymentV2Repository.findByQuotationIdOrderByCreatedAtDesc(quotationId);
        List<PaymentTableDto> paymentDtos = payments.stream()
                .map(PaymentTableDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDtos);
    }

    // Get payments by user - sorted by latest first
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentTableDto>> getPaymentsByUser(@PathVariable Long userId) {
        List<PaymentV2> payments = paymentV2Repository.findByUserIdOrderByCreatedAtDesc(userId);
        List<PaymentTableDto> paymentDtos = payments.stream()
                .map(PaymentTableDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paymentDtos);
    }

    @GetMapping("/reports/revenue-trend")
    public ResponseEntity<Map<String, RevenueTrendDto>> getRevenueTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {


        List<PaymentV2> payments;
        if (startDate != null && endDate != null) {
            // Filter payments by date range

            Instant start = LocalDate.parse(startDate).atStartOfDay(ZoneId.of("Asia/Manila")).toInstant();
            Instant end = LocalDate.parse(endDate).atTime(23, 59, 59).atZone(ZoneId.of("Asia/Manila")).toInstant();


            payments = paymentV2Repository.findByPaidAtBetween(start, end);
        }
        else if (endDate != null) {
            Instant end = LocalDate.parse(endDate).atTime(23, 59, 59).atZone(ZoneId.of("Asia/Manila")).toInstant();
            payments = paymentV2Repository.findByPaidAtBefore(end);
        } else {
            payments = paymentV2Repository.findAll();
        }
        payments = paymentV2Repository.findAll();

        Map<String, RevenueTrendDto> monthlyTrend = payments.stream()
                .filter(p -> p.getPaidAt() != null && "PAID".equals(p.getStatus().toString()))
                .collect(Collectors.groupingBy(
                        p -> {
                            Instant paidAt = p.getPaidAt();
                            LocalDateTime localDateTime = paidAt.atZone(ZoneId.of("Asia/Manila")).toLocalDateTime();
                            return localDateTime.getYear() + "-" + String.format("%02d", localDateTime.getMonthValue());
                        },
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            BigDecimal grossRevenue = list.stream()
                                    .map(PaymentV2::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal netRevenue = list.stream()
                                    .map(PaymentV2::getNetAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal totalFees = list.stream()
                                    .map(PaymentV2::getFee)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            long transactionCount = list.size();

                            return new RevenueTrendDto(grossRevenue, netRevenue, totalFees, transactionCount);
                        })
                ));

        return ResponseEntity.ok(monthlyTrend);
    }

    @Getter
    @Setter
    class RevenueTrendDto {
        private BigDecimal grossRevenue;
        private BigDecimal netRevenue;
        private BigDecimal totalFees;
        private long transactionCount;
        private BigDecimal feePercentage;

        public RevenueTrendDto(BigDecimal gross, BigDecimal net, BigDecimal fees, long count) {
            this.grossRevenue = gross;
            this.netRevenue = net;
            this.totalFees = fees;
            this.transactionCount = count;
            this.feePercentage = gross.compareTo(BigDecimal.ZERO) > 0
                    ? fees.divide(gross, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
        }
    }

    @GetMapping("/reports/payment-method-stats")
    public ResponseEntity<List<PaymentMethodStatsDto>> getPaymentMethodStats() {
        List<PaymentV2> payments = paymentV2Repository.findAll();

        // Get total for percentage calculations
        long totalTransactions = payments.size();
        BigDecimal totalRevenue = payments.stream()
                .map(PaymentV2::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentMethodStatsDto> stats = payments.stream()
                .collect(Collectors.groupingBy(
                        PaymentV2::getPaymentMethod,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            long count = list.size();
                            BigDecimal methodRevenue = list.stream()
                                    .map(PaymentV2::getNetAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal avgTransaction = count > 0
                                    ? methodRevenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                                    : BigDecimal.ZERO;
                            double usagePercentage = totalTransactions > 0
                                    ? (count * 100.0) / totalTransactions
                                    : 0.0;
                            double revenuePercentage = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                                    ? methodRevenue.divide(totalRevenue, 4, RoundingMode.HALF_UP).doubleValue() * 100
                                    : 0.0;

                            return new PaymentMethodStatsDto(
                                    list.get(0).getPaymentMethod(), count, methodRevenue,
                                    avgTransaction, usagePercentage, revenuePercentage
                            );
                        })
                ))
                .values().stream()
                .sorted((a, b) -> b.getTransactionCount().compareTo(a.getTransactionCount()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(stats);
    }

    @Getter
    @Setter
    class PaymentMethodStatsDto {
        private String paymentMethod;
        private Long transactionCount;
        private BigDecimal totalRevenue;
        private BigDecimal averageTransaction;
        private Double usagePercentage;
        private Double revenuePercentage;

        public PaymentMethodStatsDto(String method, Long count, BigDecimal revenue,
                                     BigDecimal average, Double usagePct, Double revenuePct) {
            this.paymentMethod = method;
            this.transactionCount = count;
            this.totalRevenue = revenue;
            this.averageTransaction = average;
            this.usagePercentage = usagePct;
            this.revenuePercentage = revenuePct;
        }
    }

    @GetMapping("/reports/quotation-completion-rates")
    public ResponseEntity<List<QuotationCompletionDto>> getQuotationCompletionRates() {
        List<PaymentV2> payments = paymentV2Repository.findAll();

        // Group payments by quotation and calculate completion
        List<QuotationCompletionDto> completionRates = payments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getQuotation().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            Quotation quotation = list.get(0).getQuotation();
                            BigDecimal quotationTotal = quotation.getTotal();
                            BigDecimal totalPaid = list.stream()
                                    .filter(p -> "PAID".equals(p.getStatus().toString()))
                                    .map(PaymentV2::getNetAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            BigDecimal completionPercentage = quotationTotal.compareTo(BigDecimal.ZERO) > 0
                                    ? totalPaid.divide(quotationTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                                    : BigDecimal.ZERO;

                            String status = completionPercentage.compareTo(BigDecimal.valueOf(100)) >= 0 ? "FULLY_PAID"
                                    : completionPercentage.compareTo(BigDecimal.ZERO) > 0 ? "PARTIALLY_PAID"
                                    : "UNPAID";

                            long paymentCount = list.stream()
                                    .filter(p -> "PAID".equals(p.getStatus().toString()))
                                    .count();

                            return new QuotationCompletionDto(
                                    quotation.getId(), quotationTotal, totalPaid,
                                    completionPercentage, status, paymentCount
                            );
                        })
                ))
                .values().stream()
                .sorted((a, b) -> b.getCompletionPercentage().compareTo(a.getCompletionPercentage()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(completionRates);
    }

    @Getter
    @Setter
    class QuotationCompletionDto {
        private Long quotationId;
        private BigDecimal quotationTotal;
        private BigDecimal amountPaid;
        private BigDecimal completionPercentage;
        private String paymentStatus;
        private Long paymentCount;

        public QuotationCompletionDto(Long id, BigDecimal total, BigDecimal paid,
                                      BigDecimal completionPct, String status, Long count) {
            this.quotationId = id;
            this.quotationTotal = total;
            this.amountPaid = paid;
            this.completionPercentage = completionPct;
            this.paymentStatus = status;
            this.paymentCount = count;
        }
    }



}
