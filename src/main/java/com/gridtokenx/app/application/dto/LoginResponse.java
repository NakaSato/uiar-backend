package com.gridtokenx.app.application.dto;

import com.gridtokenx.app.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Login response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private LocalDateTime expiresIn;
  private UserResponse user;

  /**
   * User response nested DTO
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Set<Role> roles;
  }
}
