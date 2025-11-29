package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.QuotationLineItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponse {
    private Long quotationId;
    private String customerName;
    private String contactNumber;
    private LocalDate eventDate;
    private String eventType;
    private Integer pax;
    private String venue;
    private String celebrants;
    private BigDecimal totalAmount;
    private List<QuotationLineItem> lineItems;
    private Boolean contractSigned;
}
