package com.silvestre.web_applicationv1.response;




import com.silvestre.web_applicationv1.Dto.UserDto;
import com.silvestre.web_applicationv1.entity.QuotationLineItem;
import com.silvestre.web_applicationv1.entity.Venue;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QuotationResponse {
        private Long quotationId;
        private List<QuotationLineItem> lineItems;
        private QuotationStatus status;
        private LocalDateTime creationTime;
        private LocalDateTime modificationTime;
        private BigDecimal total;
        private LocalDate requestedEventDate;
        private String eventType;
        private Integer pax;
        private Venue venue;
        private UserDto user;

        // New fields
        private String celebrants;
        private String customerName;
        private String contactNumber;
        private String address;
        private Map<String, List<CustomFoodItem>> customFoodByCategory;
        private Long packageId;

        public QuotationResponse(Long quotationId, List<QuotationLineItem> lineItems,
                                 QuotationStatus status, LocalDateTime creationTime,
                                 LocalDateTime modificationTime, BigDecimal total,
                                 LocalDate requestedEventDate, String eventType,
                                 Integer pax, Venue venue, UserDto user,
                                 String celebrants, String customerName,
                                 String contactNumber, String address,
                                 Map<String, List<CustomFoodItem>> customFoodByCategory, Long packageId) {
                this.quotationId = quotationId;
                this.lineItems = lineItems;
                this.status = status;
                this.creationTime = creationTime;
                this.modificationTime = modificationTime;
                this.total = total;
                this.requestedEventDate = requestedEventDate;
                this.eventType = eventType;
                this.pax = pax;
                this.venue = venue;
                this.user = user;
                this.celebrants = celebrants;
                this.customerName = customerName;
                this.contactNumber = contactNumber;
                this.address = address;
                this.customFoodByCategory = customFoodByCategory;
                this.packageId= packageId;
        }

        @Getter
        @Setter
        public static class CustomFoodItem {
                private Long itemId;
                private String name;
                private String category;
        }
}