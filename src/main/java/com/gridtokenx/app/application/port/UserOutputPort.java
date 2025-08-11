package com.gridtokenx.app.application.port;

import com.gridtokenx.app.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port Interface - Defines how the application interacts with external
 * systems
 * This interface follows the Dependency Inversion Principle
 * The application layer defines this interface, and the infrastructure layer
 * implements it
 */
public interface UserOutputPort {

  /**
   * Save a user
   */
  User save(User user);

  /**
   * Find user by ID
   */
  Optional<User> findById(UUID id);

  /**
   * Find user by username
   */
  Optional<User> findByUsername(String username);

  /**
   * Find user by email
   */
  Optional<User> findByEmail(String email);

  /**
   * Find all users
   */
  List<User> findAll();

  /**
   * Find all active users
   */
  List<User> findAllActive();

  /**
   * Check if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists
   */
  boolean existsByEmail(String email);

  /**
   * Delete user by ID
   */
  void deleteById(UUID id);

  /**
   * Count total users
   */
  long count();
}
