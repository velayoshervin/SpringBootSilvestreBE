package com.silvestre.web_applicationv1.response;

import com.silvestre.web_applicationv1.Dto.UserChatDto;
import com.silvestre.web_applicationv1.entity.Booking;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {

    private UserChatDto user;

    private Long quotationId;

    private LocalDateTime eventDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balance;


    private BookingStatus bookingStatus;

    public BookingDto(Booking booking){

        this.user= new UserChatDto(booking.getUser());
        this.quotationId= booking.getQuotation().getId();
        this.eventDate = booking.getRequestedDate();
        this.createdAt= booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.totalAmount = booking.getTotalAmount();
        this.amountPaid = booking.getAmountPaid();
        this.balance = booking.getBalance();


    }


}
