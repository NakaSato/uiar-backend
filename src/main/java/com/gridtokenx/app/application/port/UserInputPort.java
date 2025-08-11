package com.gridtokenx.app.application.port;

import com.gridtokenx.app.application.dto.CreateUserDto;
import com.gridtokenx.app.application.dto.UpdateUserDto;
import com.gridtokenx.app.application.dto.UserDto;

import java.util.List;
import java.util.UUID;

/**
 * Input Port Interface - Defines the application's use cases
 * This interface follows the Ports and Adapters pattern
 * It defines what the application can do without specifying how it's done
 */
public interface UserInputPort {

  /**
   * Create a new user
   */
  UserDto createUser(CreateUserDto createUserDto);

  /**
   * Update an existing user
   */
  UserDto updateUser(UUID userId, UpdateUserDto updateUserDto);

  /**
   * Get user by ID
   */
  UserDto getUserById(UUID userId);

  /**
   * Get user by username
   */
  UserDto getUserByUsername(String username);

  /**
   * Get user by email
   */
  UserDto getUserByEmail(String email);

  /**
   * Get all users
   */
  List<UserDto> getAllUsers();

  /**
   * Get all active users
   */
  List<UserDto> getAllActiveUsers();

  /**
   * Activate user
   */
  UserDto activateUser(UUID userId);

  /**
   * Deactivate user
   */
  UserDto deactivateUser(UUID userId);

  /**
   * Delete user
   */
  void deleteUser(UUID userId);
}
