package com.silvestre.web_applicationv1.ExceptionHandler;

public class ResourceNotFoundException extends RuntimeException  {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
