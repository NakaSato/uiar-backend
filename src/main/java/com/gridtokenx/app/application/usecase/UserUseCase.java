package com.gridtokenx.app.application.usecase;

import com.gridtokenx.app.application.dto.CreateUserDto;
import com.gridtokenx.app.application.dto.UpdateUserDto;
import com.gridtokenx.app.application.dto.UserDto;
import com.gridtokenx.app.application.port.UserInputPort;
import com.gridtokenx.app.application.port.UserOutputPort;
import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.domain.exception.UserNotFoundException;
import com.gridtokenx.app.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use Case Implementation - Application Service
 * This class implements the business use cases defined in the input port
 * It orchestrates domain services and coordinates with output ports
 * Following the Hexagonal Architecture pattern
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserUseCase implements UserInputPort {

  private final UserDomainService userDomainService;
  private final UserOutputPort userOutputPort;

  @Override
  public UserDto createUser(CreateUserDto createUserDto) {
    User user = userDomainService.createUser(
        createUserDto.getUsername(),
        createUserDto.getEmail(),
        createUserDto.getFirstName(),
        createUserDto.getLastName());

    return mapToDto(user);
  }

  @Override
  public UserDto updateUser(UUID userId, UpdateUserDto updateUserDto) {
    User user = userDomainService.updateUser(
        userId,
        updateUserDto.getEmail(),
        updateUserDto.getFirstName(),
        updateUserDto.getLastName());

    return mapToDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserById(UUID userId) {
    User user = userOutputPort.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    return mapToDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserByUsername(String username) {
    User user = userDomainService.findByUsername(username);
    return mapToDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto getUserByEmail(String email) {
    User user = userDomainService.findByEmail(email);
    return mapToDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllUsers() {
    List<User> users = userOutputPort.findAll();
    return users.stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> getAllActiveUsers() {
    List<User> users = userDomainService.getActiveUsers();
    return users.stream()
        .map(this::mapToDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserDto activateUser(UUID userId) {
    User user = userDomainService.activateUser(userId);
    return mapToDto(user);
  }

  @Override
  public UserDto deactivateUser(UUID userId) {
    User user = userDomainService.deactivateUser(userId);
    return mapToDto(user);
  }

  @Override
  public void deleteUser(UUID userId) {
    // Verify user exists before deletion
    userOutputPort.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    userOutputPort.deleteById(userId);
  }

  /**
   * Maps domain entity to DTO
   * This mapping prevents domain entities from leaking to external layers
   */
  private UserDto mapToDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .fullName(user.getFullName())
        .active(user.isActive())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}
