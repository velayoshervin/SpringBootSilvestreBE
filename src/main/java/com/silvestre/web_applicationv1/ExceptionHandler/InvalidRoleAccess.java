package com.silvestre.web_applicationv1.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidRoleAccess extends RuntimeException {
    public InvalidRoleAccess(String message) {
        super(message);
    }
}
