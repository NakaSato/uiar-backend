package com.gridtokenx.app.domain.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Authentication Exception Tests")
class AuthenticationExceptionTest {

  @Test
  @DisplayName("Should create exception with message")
  void shouldCreateExceptionWithMessage() {
    String message = "Authentication failed";
    AuthenticationException exception = new AuthenticationException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  @DisplayName("Should create exception with message and cause")
  void shouldCreateExceptionWithMessageAndCause() {
    String message = "Authentication failed";
    Throwable cause = new RuntimeException("Root cause");
    AuthenticationException exception = new AuthenticationException(message, cause);

    assertEquals(message, exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  @DisplayName("Should be instance of RuntimeException")
  void shouldBeInstanceOfRuntimeException() {
    AuthenticationException exception = new AuthenticationException("Test");
    assertTrue(exception instanceof RuntimeException);
  }
}
