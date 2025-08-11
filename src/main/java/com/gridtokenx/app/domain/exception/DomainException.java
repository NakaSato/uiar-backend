package com.gridtokenx.app.domain.exception;

/**
 * Base domain exception class
 * All domain-specific exceptions should extend this class
 */
public abstract class DomainException extends RuntimeException {

  protected DomainException(String message) {
    super(message);
  }

  protected DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
