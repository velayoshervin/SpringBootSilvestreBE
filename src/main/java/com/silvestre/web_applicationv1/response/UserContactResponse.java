package com.silvestre.web_applicationv1.response;

import com.silvestre.web_applicationv1.enums.Role;

public record UserContactResponse(Long id, String firstname, String Lastname, String email , Role role) { }
