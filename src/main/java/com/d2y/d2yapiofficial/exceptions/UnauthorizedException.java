package com.d2y.d2yapiofficial.exceptions;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException() {
    super("Unauthorized");
  }

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}
