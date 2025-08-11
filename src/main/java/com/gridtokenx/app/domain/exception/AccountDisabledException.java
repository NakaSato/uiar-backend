package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for disabled user accounts
 */
public class AccountDisabledException extends AuthenticationException {

  public AccountDisabledException() {
    super("User account is disabled");
  }

  public AccountDisabledException(String message) {
    super(message);
  }
}
