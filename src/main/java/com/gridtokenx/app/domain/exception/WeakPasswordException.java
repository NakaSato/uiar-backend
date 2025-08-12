package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for weak passwords that don't meet security requirements
 */
public class WeakPasswordException extends AuthenticationException {

  public WeakPasswordException() {
    super("Password does not meet security requirements: minimum 8 characters with uppercase, lowercase, and digit");
  }

  public WeakPasswordException(String message) {
    super(message);
  }
}
