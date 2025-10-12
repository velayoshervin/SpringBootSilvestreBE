package com.silvestre.web_applicationv1.ExceptionHandler;

public class EmailExistsException extends RuntimeException {
  public EmailExistsException(String message) {
    super(message);
  }
}
