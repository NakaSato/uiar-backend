package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for invalid credentials
 */
public class InvalidCredentialsException extends AuthenticationException {

  public InvalidCredentialsException() {
    super("Invalid username or password");
  }

  public InvalidCredentialsException(String message) {
    super(message);
  }
}
