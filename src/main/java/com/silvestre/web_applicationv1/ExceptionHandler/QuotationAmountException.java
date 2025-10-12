package com.silvestre.web_applicationv1.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class QuotationAmountException extends RuntimeException {
    public QuotationAmountException(String message) {
        super(message);
    }
}
