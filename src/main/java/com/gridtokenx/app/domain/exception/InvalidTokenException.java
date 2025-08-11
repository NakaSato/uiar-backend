package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for invalid or expired JWT tokens
 */
public class InvalidTokenException extends AuthenticationException {

  public InvalidTokenException() {
    super("Invalid or expired token");
  }

  public InvalidTokenException(String message) {
    super(message);
  }

  public InvalidTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
