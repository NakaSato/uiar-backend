package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for invalid user data
 */
public class InvalidUserDataException extends DomainException {

  public InvalidUserDataException(String message) {
    super("Invalid user data: " + message);
  }

  public InvalidUserDataException(String field, String reason) {
    super("Invalid " + field + ": " + reason);
  }
}
