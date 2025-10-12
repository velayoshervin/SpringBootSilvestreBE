package com.silvestre.web_applicationv1.requests;

import com.silvestre.web_applicationv1.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoleRequest {
    private Role role;
    private Long id;
}
