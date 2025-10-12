package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private String fullName;
    private String userEmail;
    private String avatarUrl; // ✅ Added avatar URL
    private BookingStatus bookingStatus;
    private BigDecimal totalAmount;  // In real value (PHP)
    private BigDecimal amountPaid;   // In real value (PHP)
    private BigDecimal balance;      // In real value (PHP)
    private LocalDateTime requestedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PaymentDto> payments;
    private String eventDescription; // e.g., "Wedding", "Birthday Party"

    // Helper methods for the frontend
    public int getPaymentPercentage() {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return amountPaid.divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }

    public boolean hasBalance() {
        return balance != null && balance.compareTo(BigDecimal.ZERO) > 0;
    }

}