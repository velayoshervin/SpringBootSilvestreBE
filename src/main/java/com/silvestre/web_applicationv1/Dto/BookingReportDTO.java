package com.silvestre.web_applicationv1.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingReportDTO {
    private Long id;
    private String customer;
    private String bookingStatus;
    private BigDecimal totalAmount;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private String venue;
    private String package1;
    // getters and setters
}



