package com.silvestre.web_applicationv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPMAddress {
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String state;
    private String country = "PH";
}
