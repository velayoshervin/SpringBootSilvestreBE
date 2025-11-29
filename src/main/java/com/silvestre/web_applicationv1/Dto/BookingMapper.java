package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.Payments;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingDto toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());

        // User info with avatar
        if (booking.getUser() != null) {
            dto.setFullName(booking.getUser().getFirstname() + " " + booking.getUser().getLastname());
            dto.setUserEmail(booking.getUser().getEmail());
            dto.setAvatarUrl(booking.getUser().getAvatarUrl());
        }

        // Convert amounts from centavos to real value
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setAmountPaid(convertToRealValue(booking.getAmountPaid().longValueExact()));
        dto.setBalance(convertToRealValue(booking.getBalance().longValueExact()));

        // Basic info
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setRequestedDate(booking.getRequestedDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());

        // Event description from quotation
        if (booking.getQuotation() != null) {
            dto.setEventDescription(booking.getQuotation().getEventType() + " Event");
        }

        // Payments - map from Payments entity
        if (booking.getPayments() != null) {
            dto.setPayments(booking.getPayments().stream()
                    .map(this::toPaymentDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public PaymentDto toPaymentDTO(Payments payment) {
        if (payment == null) {
            return null;
        }

        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());

        // Convert amounts from centavos to real value
        dto.setAmount(payment.getAmount());
        dto.setFee(payment.getFee());
        dto.setNetAmount(payment.getNetAmount());

        // Payment info
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getSourceType()); // Use sourceType as payment method
        dto.setTransactionId(payment.getPaymongoPaymentId());
        dto.setPaymentDate(payment.getPaidAt()); // Use paidAt as payment date
        dto.setCustomerName(payment.getCustomerName());
        dto.setCustomerEmail(payment.getCustomerEmail());
        dto.setDescription(payment.getDescription());
        dto.setCurrency(payment.getCurrency());

        return dto;
    }

    private BigDecimal convertToRealValue(Long amountInCentavos) {
        if (amountInCentavos == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amountInCentavos)
                .movePointLeft(2) // Equivalent to divide by 100
                .setScale(2, java.math.RoundingMode.HALF_UP); }

}
