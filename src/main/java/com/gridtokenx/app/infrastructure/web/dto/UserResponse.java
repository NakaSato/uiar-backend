package com.gridtokenx.app.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Web layer response DTO for API responses
 * This DTO is specifically designed for REST API responses
 * It includes additional metadata that might be useful for web clients
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

  private String id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private String fullName;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
