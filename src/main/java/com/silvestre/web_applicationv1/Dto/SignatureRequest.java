package com.silvestre.web_applicationv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignatureRequest {
    private String signatureData;
    private String signerName;
}
