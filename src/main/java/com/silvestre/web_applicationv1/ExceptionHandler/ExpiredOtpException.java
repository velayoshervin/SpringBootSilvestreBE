package com.silvestre.web_applicationv1.ExceptionHandler;

public class ExpiredOtpException extends RuntimeException {
    public ExpiredOtpException(String message) {
        super(message);
    }
}
