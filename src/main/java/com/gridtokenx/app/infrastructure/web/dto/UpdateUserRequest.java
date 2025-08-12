package com.gridtokenx.app.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Web layer request DTO for updating users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name cannot exceed 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name cannot exceed 50 characters")
  private String lastName;
}
