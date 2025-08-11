package com.gridtokenx.app.infrastructure.web.exception;

import com.gridtokenx.app.domain.exception.DomainException;
import com.gridtokenx.app.domain.exception.InvalidUserDataException;
import com.gridtokenx.app.domain.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the web layer
 * This handler translates domain exceptions to appropriate HTTP responses
 * Following the principle of separating concerns - domain exceptions are
 * translated to web responses
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle user not found exceptions
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(
      UserNotFoundException ex, WebRequest request) {

    log.warn("User not found: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .details(List.of("The requested user resource was not found", "Please check the user ID and try again"))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle invalid user data exceptions
   */
  @ExceptionHandler(InvalidUserDataException.class)
  public ResponseEntity<ErrorResponse> handleInvalidUserDataException(
      InvalidUserDataException ex, WebRequest request) {

    log.warn("Invalid user data: {}", ex.getMessage());

    // Extract more details based on the exception message
    List<String> details = new ArrayList<>();
    String message = ex.getMessage();

    if (message.contains("username") && message.contains("already exists")) {
      details.add("The username provided is already taken by another user");
      details.add("Please choose a different username");
      details.add("Username must be unique across the system");
    } else if (message.contains("email") && message.contains("already exists")) {
      details.add("The email address provided is already registered");
      details.add("Please use a different email address");
      details.add("Email must be unique across the system");
    } else if (message.contains("validation failed")) {
      details.add("One or more user data fields failed validation");
      details.add("Please check the required format for each field");
    } else {
      details.add("The provided user data does not meet the system requirements");
      details.add("Please verify all fields and try again");
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .details(details)
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle all domain exceptions
   */
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ErrorResponse> handleDomainException(
      DomainException ex, WebRequest request) {

    log.warn("Domain exception: {}", ex.getMessage());

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .details(List.of("A business rule violation occurred", "Please check your request and try again"))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle validation exceptions
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    List<String> details = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      details.add(error.getField() + ": " + error.getDefaultMessage());
    }

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Input validation failed")
        .details(details)
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle all other exceptions
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {

    log.error("Unexpected error: ", ex);

    ErrorResponse errorResponse = ErrorResponse.builder()
        .timestamp(LocalDateTime.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .details(List.of("An internal server error occurred",
            "Please try again later or contact support if the problem persists"))
        .build();

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
