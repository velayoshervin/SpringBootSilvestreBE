package com.silvestre.web_applicationv1.ExceptionHandler;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
