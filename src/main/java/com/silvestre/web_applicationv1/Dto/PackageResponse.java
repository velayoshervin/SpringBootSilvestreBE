package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.PackageBundle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PackageResponse {
    private Long id;
    private String packageName;
    private String description;
    private List<PackageBundle> bundles;
    private BigDecimal totalPrice;
}
