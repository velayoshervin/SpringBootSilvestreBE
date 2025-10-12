package com.silvestre.web_applicationv1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class    ItemRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal perUnitExcess;
    private String category;
    private String type;
    private int pax;
    private Set<Long> recommendedForEvents; // set of event IDs
    private List<String> photos;
    private List<String> videos;
}