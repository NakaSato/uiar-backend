package com.gridtokenx.app.domain.exception;

/**
 * Base domain exception for authentication-related errors
 */
public class AuthenticationException extends RuntimeException {

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
