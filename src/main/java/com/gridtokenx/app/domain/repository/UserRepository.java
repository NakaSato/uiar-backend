package com.gridtokenx.app.domain.repository;

import com.gridtokenx.app.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository Interface
 * This interface defines the contract for user persistence without depending on
 * any specific implementation
 * Following the Dependency Inversion Principle - the domain defines the
 * interface,
 * and the infrastructure layer provides the implementation
 */
public interface UserRepository {

  /**
   * Save a user entity
   * 
   * @param user the user to save
   * @return the saved user with generated ID
   */
  User save(User user);

  /**
   * Find a user by ID
   * 
   * @param id the user ID
   * @return optional containing the user if found
   */
  Optional<User> findById(UUID id);

  /**
   * Find a user by username
   * 
   * @param username the username
   * @return optional containing the user if found
   */
  Optional<User> findByUsername(String username);

  /**
   * Find a user by email
   * 
   * @param email the email address
   * @return optional containing the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Find all users
   * 
   * @return list of all users
   */
  List<User> findAll();

  /**
   * Find all active users
   * 
   * @return list of active users
   */
  List<User> findAllActive();

  /**
   * Check if username exists
   * 
   * @param username the username to check
   * @return true if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists
   * 
   * @param email the email to check
   * @return true if email exists
   */
  boolean existsByEmail(String email);

  /**
   * Delete a user by ID
   * 
   * @param id the user ID
   */
  void deleteById(UUID id);

  /**
   * Count total users
   * 
   * @return total number of users
   */
  long count();
}
