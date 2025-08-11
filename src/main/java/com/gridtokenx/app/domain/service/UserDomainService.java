package com.gridtokenx.app.domain.service;

import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.domain.exception.InvalidUserDataException;
import com.gridtokenx.app.domain.exception.UserNotFoundException;
import com.gridtokenx.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain Service - Contains business logic that doesn't naturally fit within a
 * single entity
 * This service orchestrates business operations involving users
 * Following Single Responsibility Principle and dependency injection
 */
@RequiredArgsConstructor
public class UserDomainService {

  private final UserRepository userRepository;

  /**
   * Create a new user with business validation
   */
  public User createUser(String username, String email, String firstName, String lastName) {
    // Business rule validation
    validateUserCreation(username, email, firstName, lastName);

    // Check uniqueness constraints
    if (userRepository.existsByUsername(username)) {
      throw new InvalidUserDataException("username", "already exists");
    }

    if (userRepository.existsByEmail(email)) {
      throw new InvalidUserDataException("email", "already exists");
    }

    // Create domain entity
    User user = User.builder()
        .username(username)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    // Final domain validation
    if (!user.isValid()) {
      throw new InvalidUserDataException("User data validation failed");
    }

    return userRepository.save(user);
  }

  /**
   * Update user information
   */
  public User updateUser(UUID userId, String email, String firstName, String lastName) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    // Validate email uniqueness if changed
    if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
      throw new InvalidUserDataException("email", "already exists");
    }

    // Update user data
    user.setEmail(email);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setUpdatedAt(LocalDateTime.now());

    // Domain validation
    if (!user.isValid()) {
      throw new InvalidUserDataException("Updated user data is invalid");
    }

    return userRepository.save(user);
  }

  /**
   * Activate a user account
   */
  public User activateUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    user.activate();
    return userRepository.save(user);
  }

  /**
   * Deactivate a user account
   */
  public User deactivateUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    user.deactivate();
    return userRepository.save(user);
  }

  /**
   * Get all active users
   */
  public List<User> getActiveUsers() {
    return userRepository.findAllActive();
  }

  /**
   * Find user by username
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("username", username));
  }

  /**
   * Find user by email
   */
  public User findByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException("email", email));
  }

  /**
   * Private method for user creation validation
   */
  private void validateUserCreation(String username, String email, String firstName, String lastName) {
    if (username == null || username.trim().isEmpty()) {
      throw new InvalidUserDataException("username", "cannot be empty");
    }

    if (!username.matches("^[a-zA-Z0-9_]+$") || username.length() < 3 || username.length() > 50) {
      throw new InvalidUserDataException("username",
          "must be 3-50 characters and contain only letters, numbers, and underscores");
    }

    if (email == null || email.trim().isEmpty()) {
      throw new InvalidUserDataException("email", "cannot be empty");
    }

    if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
      throw new InvalidUserDataException("email", "invalid format");
    }

    if (firstName == null || firstName.trim().isEmpty()) {
      throw new InvalidUserDataException("firstName", "cannot be empty");
    }

    if (lastName == null || lastName.trim().isEmpty()) {
      throw new InvalidUserDataException("lastName", "cannot be empty");
    }
  }
}
