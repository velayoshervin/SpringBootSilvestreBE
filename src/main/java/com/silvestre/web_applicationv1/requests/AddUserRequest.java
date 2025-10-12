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
public class AddUserRequest {
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
}
