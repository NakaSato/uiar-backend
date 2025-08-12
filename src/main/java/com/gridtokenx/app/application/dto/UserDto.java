package com.gridtokenx.app.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application DTO for User data transfer
 * This DTO is used for communication between application layer and external
 * layers
 * It's independent of domain entities to maintain loose coupling
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private UUID id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
