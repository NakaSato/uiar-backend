package com.gridtokenx.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Domain Entity - Core business object
 * This represents the business domain model, independent of any infrastructure
 * concerns
 * Following DDD principles, this entity contains business logic and rules
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  private UUID id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Authentication fields
  private String password;
  @Builder.Default
  private Set<Role> roles = new HashSet<>();
  @Builder.Default
  private boolean enabled = true;
  @Builder.Default
  private boolean accountNonExpired = true;
  @Builder.Default
  private boolean accountNonLocked = true;
  @Builder.Default
  private boolean credentialsNonExpired = true;
  private LocalDateTime lastLoginAt;
  @Builder.Default
  private int failedLoginAttempts = 0;

  /**
   * Domain business rule: User must have a valid email format
   */
  public boolean isValidEmail() {
    return email != null &&
        email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
  }

  /**
   * Domain business rule: User must have required fields
   */
  public boolean isValid() {
    return username != null && !username.trim().isEmpty() &&
        email != null && !email.trim().isEmpty() &&
        firstName != null && !firstName.trim().isEmpty() &&
        lastName != null && !lastName.trim().isEmpty() &&
        isValidEmail();
  }

  /**
   * Domain business rule: Username must be unique and valid format
   */
  public boolean isValidUsername() {
    return username != null &&
        username.length() >= 3 &&
        username.length() <= 50 &&
        username.matches("^[a-zA-Z0-9_]+$");
  }

  /**
   * Domain method: Get full name
   */
  public String getFullName() {
    return firstName + " " + lastName;
  }

  /**
   * Domain method: Activate user
   */
  public void activate() {
    this.active = true;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain method: Deactivate user
   */
  public void deactivate() {
    this.active = false;
    this.updatedAt = LocalDateTime.now();
  }

  // Authentication-related business methods

  /**
   * Domain business rule: Password validation
   */
  public boolean isValidPassword(String rawPassword) {
    return rawPassword != null &&
        rawPassword.length() >= 8 &&
        rawPassword.matches(".*[A-Z].*") && // At least one uppercase
        rawPassword.matches(".*[a-z].*") && // At least one lowercase
        rawPassword.matches(".*\\d.*"); // At least one digit
  }

  /**
   * Domain method: Check if user has specific role
   */
  public boolean hasRole(Role role) {
    return roles.contains(role);
  }

  /**
   * Domain method: Add role to user
   */
  public void addRole(Role role) {
    this.roles.add(role);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain method: Remove role from user
   */
  public void removeRole(Role role) {
    this.roles.remove(role);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain method: Check if account is active and not locked
   */
  public boolean isAccountActive() {
    return active && enabled && accountNonLocked && accountNonExpired;
  }

  /**
   * Domain method: Update last login time
   */
  public void updateLastLogin() {
    this.lastLoginAt = LocalDateTime.now();
    this.failedLoginAttempts = 0; // Reset failed attempts on successful login
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain method: Increment failed login attempts
   */
  public void incrementFailedAttempts() {
    this.failedLoginAttempts++;
    this.updatedAt = LocalDateTime.now();

    // Lock account after 5 failed attempts
    if (this.failedLoginAttempts >= 5) {
      this.accountNonLocked = false;
    }
  }

  /**
   * Domain method: Reset failed login attempts
   */
  public void resetFailedAttempts() {
    this.failedLoginAttempts = 0;
    this.accountNonLocked = true;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Domain method: Check if account is locked due to failed attempts
   */
  public boolean isAccountLocked() {
    return !accountNonLocked || failedLoginAttempts >= 5;
  }
}
