package com.silvestre.web_applicationv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaffDTO {
    private Long staffId;
    private String name;
    private String role;
    private String category;
    private String avatarInitials;
    private String contactInfo;
    private Boolean isActive;
}
