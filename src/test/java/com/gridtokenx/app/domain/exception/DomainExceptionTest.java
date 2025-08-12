package com.gridtokenx.app.domain.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exception Tests")
class DomainExceptionTest {

  @Test
  @DisplayName("InvalidCredentialsException should have default message")
  void invalidCredentialsExceptionShouldHaveDefaultMessage() {
    InvalidCredentialsException exception = new InvalidCredentialsException();
    assertEquals("Invalid username or password", exception.getMessage());
    assertTrue(exception instanceof AuthenticationException);
  }

  @Test
  @DisplayName("InvalidCredentialsException should accept custom message")
  void invalidCredentialsExceptionShouldAcceptCustomMessage() {
    String customMessage = "Custom credentials error";
    InvalidCredentialsException exception = new InvalidCredentialsException(customMessage);
    assertEquals(customMessage, exception.getMessage());
  }

  @Test
  @DisplayName("AccountDisabledException should have default message")
  void accountDisabledExceptionShouldHaveDefaultMessage() {
    AccountDisabledException exception = new AccountDisabledException();
    assertEquals("User account is disabled", exception.getMessage());
    assertTrue(exception instanceof AuthenticationException);
  }

  @Test
  @DisplayName("AccountDisabledException should accept custom message")
  void accountDisabledExceptionShouldAcceptCustomMessage() {
    String customMessage = "Account temporarily disabled";
    AccountDisabledException exception = new AccountDisabledException(customMessage);
    assertEquals(customMessage, exception.getMessage());
  }

  @Test
  @DisplayName("AccountLockedException should have default message")
  void accountLockedExceptionShouldHaveDefaultMessage() {
    AccountLockedException exception = new AccountLockedException();
    assertEquals("User account is locked due to multiple failed login attempts", exception.getMessage());
    assertTrue(exception instanceof AuthenticationException);
  }

  @Test
  @DisplayName("AccountLockedException should accept custom message")
  void accountLockedExceptionShouldAcceptCustomMessage() {
    String customMessage = "Account locked for security reasons";
    AccountLockedException exception = new AccountLockedException(customMessage);
    assertEquals(customMessage, exception.getMessage());
  }

  @Test
  @DisplayName("InvalidTokenException should have default message")
  void invalidTokenExceptionShouldHaveDefaultMessage() {
    InvalidTokenException exception = new InvalidTokenException();
    assertEquals("Invalid or expired token", exception.getMessage());
    assertTrue(exception instanceof AuthenticationException);
  }

  @Test
  @DisplayName("InvalidTokenException should accept custom message")
  void invalidTokenExceptionShouldAcceptCustomMessage() {
    String customMessage = "Token signature invalid";
    InvalidTokenException exception = new InvalidTokenException(customMessage);
    assertEquals(customMessage, exception.getMessage());
  }

  @Test
  @DisplayName("InvalidTokenException should accept message and cause")
  void invalidTokenExceptionShouldAcceptMessageAndCause() {
    String message = "Token parsing failed";
    Throwable cause = new RuntimeException("JWT parsing error");
    InvalidTokenException exception = new InvalidTokenException(message, cause);

    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  @DisplayName("WeakPasswordException should have default message")
  void weakPasswordExceptionShouldHaveDefaultMessage() {
    WeakPasswordException exception = new WeakPasswordException();
    assertEquals(
        "Password does not meet security requirements: minimum 8 characters with uppercase, lowercase, and digit",
        exception.getMessage());
    assertTrue(exception instanceof AuthenticationException);
  }

  @Test
  @DisplayName("WeakPasswordException should accept custom message")
  void weakPasswordExceptionShouldAcceptCustomMessage() {
    String customMessage = "Password too simple";
    WeakPasswordException exception = new WeakPasswordException(customMessage);
    assertEquals(customMessage, exception.getMessage());
  }

  @Test
  @DisplayName("All exceptions should extend AuthenticationException")
  void allExceptionsShouldExtendAuthenticationException() {
    assertTrue(new InvalidCredentialsException() instanceof AuthenticationException);
    assertTrue(new AccountDisabledException() instanceof AuthenticationException);
    assertTrue(new AccountLockedException() instanceof AuthenticationException);
    assertTrue(new InvalidTokenException() instanceof AuthenticationException);
    assertTrue(new WeakPasswordException() instanceof AuthenticationException);
  }

  @Test
  @DisplayName("All exceptions should be RuntimeExceptions")
  void allExceptionsShouldBeRuntimeExceptions() {
    assertTrue(new InvalidCredentialsException() instanceof RuntimeException);
    assertTrue(new AccountDisabledException() instanceof RuntimeException);
    assertTrue(new AccountLockedException() instanceof RuntimeException);
    assertTrue(new InvalidTokenException() instanceof RuntimeException);
    assertTrue(new WeakPasswordException() instanceof RuntimeException);
  }
}
