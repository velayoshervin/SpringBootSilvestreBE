package com.silvestre.web_applicationv1.Dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.silvestre.web_applicationv1.entity.Item;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PackageBundleDTO {
    private Long packageBundleId;
    private String name;
    private String description;
    private boolean customizable;
    private List<Item> items;
}
