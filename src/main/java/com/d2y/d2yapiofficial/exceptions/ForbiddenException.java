package com.d2y.d2yapiofficial.exceptions;

public class ForbiddenException extends RuntimeException {
  public ForbiddenException() {
    super("Forbidden");
  }

  public ForbiddenException(String message) {
    super(message);
  }

  public ForbiddenException(String message, Throwable cause) {
    super(message, cause);
  }
}
