package com.silvestre.web_applicationv1.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.silvestre.web_applicationv1.entity.QuotationLineItem;
import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.enums.QuotationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QuotationRequest {

    private Long userId;
    private Long quotationId;

    private List<QuotationLineItem> lineItems;
    private LocalDate eventDate;
    private String eventType;
    private int pax;
    private Long venueId;
    private String celebrants;
    private String customerName;
    private String contactNumber;
    private String address;
    private Map<String, List<CustomFoodItem>> customFoodByCategory;
    private Long packageId;
    private Long menuBundleId;

    @Getter
    @Setter
    public static class CustomFoodItem {
        private Long itemId;
        private String name;
        private String category;
    }

}
