package com.silvestre.web_applicationv1.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.GONE)
public class EmailTokenExpiredException extends RuntimeException {
    public EmailTokenExpiredException(String message) {
        super(message);
    }
}
