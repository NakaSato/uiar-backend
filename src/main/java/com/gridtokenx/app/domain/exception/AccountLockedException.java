package com.gridtokenx.app.domain.exception;

/**
 * Domain exception for locked user accounts
 */
public class AccountLockedException extends AuthenticationException {

  public AccountLockedException() {
    super("User account is locked due to multiple failed login attempts");
  }

  public AccountLockedException(String message) {
    super(message);
  }
}
