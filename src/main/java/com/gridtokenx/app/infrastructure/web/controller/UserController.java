package com.gridtokenx.app.infrastructure.web.controller;

import com.gridtokenx.app.application.dto.CreateUserDto;
import com.gridtokenx.app.application.dto.UpdateUserDto;
import com.gridtokenx.app.application.dto.UserDto;
import com.gridtokenx.app.application.port.UserInputPort;
import com.gridtokenx.app.infrastructure.web.dto.CreateUserRequest;
import com.gridtokenx.app.infrastructure.web.dto.UpdateUserRequest;
import com.gridtokenx.app.infrastructure.web.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for User operations
 * This controller is part of the infrastructure layer - it's an adapter for web
 * requests
 * It translates HTTP requests to application layer calls and responses back to
 * HTTP
 * Following the Controller-Service-Repository pattern within Clean Architecture
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

  private final UserInputPort userInputPort;

  /**
   * Create a new user
   */
  @PostMapping
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
    log.info("Creating user with username: {}", request.getUsername());

    CreateUserDto createUserDto = CreateUserDto.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .build();

    UserDto userDto = userInputPort.createUser(createUserDto);
    UserResponse response = mapToResponse(userDto);

    log.info("User created successfully with ID: {}", response.getId());
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * Get user by ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    log.info("Fetching user with ID: {}", id);

    UserDto userDto = userInputPort.getUserById(id);
    UserResponse response = mapToResponse(userDto);

    return ResponseEntity.ok(response);
  }

  /**
   * Get user by username
   */
  @GetMapping("/username/{username}")
  public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
    log.info("Fetching user with username: {}", username);

    UserDto userDto = userInputPort.getUserByUsername(username);
    UserResponse response = mapToResponse(userDto);

    return ResponseEntity.ok(response);
  }

  /**
   * Get user by email
   */
  @GetMapping("/email/{email}")
  public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
    log.info("Fetching user with email: {}", email);

    UserDto userDto = userInputPort.getUserByEmail(email);
    UserResponse response = mapToResponse(userDto);

    return ResponseEntity.ok(response);
  }

  /**
   * Get all users
   */
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "false") boolean activeOnly) {
    log.info("Fetching all users, activeOnly: {}", activeOnly);

    List<UserDto> userDtos = activeOnly ? userInputPort.getAllActiveUsers() : userInputPort.getAllUsers();

    List<UserResponse> responses = userDtos.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());

    return ResponseEntity.ok(responses);
  }

  /**
   * Update user
   */
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id,
      @Valid @RequestBody UpdateUserRequest request) {

    log.info("Updating user with ID: {}", id);

    UpdateUserDto updateUserDto = UpdateUserDto.builder()
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .build();

    UserDto userDto = userInputPort.updateUser(id, updateUserDto);
    UserResponse response = mapToResponse(userDto);

    log.info("User updated successfully with ID: {}", id);
    return ResponseEntity.ok(response);
  }

  /**
   * Activate user
   */
  @PatchMapping("/{id}/activate")
  public ResponseEntity<UserResponse> activateUser(@PathVariable UUID id) {
    log.info("Activating user with ID: {}", id);

    UserDto userDto = userInputPort.activateUser(id);
    UserResponse response = mapToResponse(userDto);

    log.info("User activated successfully with ID: {}", id);
    return ResponseEntity.ok(response);
  }

  /**
   * Deactivate user
   */
  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<UserResponse> deactivateUser(@PathVariable UUID id) {
    log.info("Deactivating user with ID: {}", id);

    UserDto userDto = userInputPort.deactivateUser(id);
    UserResponse response = mapToResponse(userDto);

    log.info("User deactivated successfully with ID: {}", id);
    return ResponseEntity.ok(response);
  }

  /**
   * Delete user
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    log.info("Deleting user with ID: {}", id);

    userInputPort.deleteUser(id);

    log.info("User deleted successfully with ID: {}", id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Map UserDto to UserResponse
   * This mapping prevents application DTOs from leaking to web layer
   */
  private UserResponse mapToResponse(UserDto userDto) {
    return UserResponse.builder()
        .id(userDto.getId().toString())
        .username(userDto.getUsername())
        .email(userDto.getEmail())
        .firstName(userDto.getFirstName())
        .lastName(userDto.getLastName())
        .fullName(userDto.getFullName())
        .active(userDto.isActive())
        .createdAt(userDto.getCreatedAt())
        .updatedAt(userDto.getUpdatedAt())
        .build();
  }
}
