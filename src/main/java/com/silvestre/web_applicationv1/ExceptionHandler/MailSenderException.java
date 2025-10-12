package com.silvestre.web_applicationv1.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class MailSenderException extends RuntimeException {
    public MailSenderException(String message) {
        super(message);
    }
}
