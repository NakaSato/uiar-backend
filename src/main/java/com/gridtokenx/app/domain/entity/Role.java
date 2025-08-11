package com.gridtokenx.app.domain.entity;

/**
 * Domain entity representing user roles for authorization
 * Following DDD principles - this is a value object that represents
 * the different roles a user can have in the system
 */
public enum Role {
  USER("USER", "Standard user with basic permissions"),
  ADMIN("ADMIN", "Administrator with full system access"),
  MODERATOR("MODERATOR", "Moderator with elevated permissions");

  private final String authority;
  private final String description;

  Role(String authority, String description) {
    this.authority = authority;
    this.description = description;
  }

  public String getAuthority() {
    return authority;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Convert string to Role enum
   */
  public static Role fromAuthority(String authority) {
    for (Role role : values()) {
      if (role.authority.equals(authority)) {
        return role;
      }
    }
    throw new IllegalArgumentException("Unknown role authority: " + authority);
  }

  /**
   * Check if this role has higher or equal privilege than the given role
   */
  public boolean hasPrivilege(Role role) {
    return this.ordinal() >= role.ordinal();
  }
}
