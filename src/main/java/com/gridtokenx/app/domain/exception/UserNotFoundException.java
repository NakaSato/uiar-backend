package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for when a user is not found
 */
public class UserNotFoundException extends DomainException {

  public UserNotFoundException(String userId) {
    super("User not found with ID: " + userId);
  }

  public UserNotFoundException(String field, String value) {
    super("User not found with " + field + ": " + value);
  }
}
