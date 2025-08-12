package com.gridtokenx.app.util;

import com.gridtokenx.app.domain.entity.Role;
import com.gridtokenx.app.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Test utility class for creating test data
 * Provides factory methods for creating test entities
 */
public class TestDataFactory {

  public static User createValidUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .username("testuser")
        .email("test@example.com")
        .firstName("Test")
        .lastName("User")
        .password("hashedPassword123")
        .active(true)
        .enabled(true)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .failedLoginAttempts(0)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public static User createUserWithRole(Role role) {
    User user = createValidUser();
    user.addRole(role);
    return user;
  }

  public static User createUserWithRoles(Set<Role> roles) {
    User user = createValidUser();
    roles.forEach(user::addRole);
    return user;
  }

  public static User createAdminUser() {
    return createUserWithRole(Role.ADMIN);
  }

  public static User createModeratorUser() {
    return createUserWithRole(Role.MODERATOR);
  }

  public static User createRegularUser() {
    return createUserWithRole(Role.USER);
  }

  public static User createDisabledUser() {
    User user = createValidUser();
    user.setEnabled(false);
    return user;
  }

  public static User createLockedUser() {
    User user = createValidUser();
    user.setAccountNonLocked(false);
    user.setFailedLoginAttempts(5);
    return user;
  }

  public static User createExpiredUser() {
    User user = createValidUser();
    user.setAccountNonExpired(false);
    return user;
  }

  public static User createUserWithCredentialsExpired() {
    User user = createValidUser();
    user.setCredentialsNonExpired(false);
    return user;
  }

  public static User createInactiveUser() {
    User user = createValidUser();
    user.setActive(false);
    return user;
  }

  public static User createUserWithFailedAttempts(int attempts) {
    User user = createValidUser();
    user.setFailedLoginAttempts(attempts);
    if (attempts >= 5) {
      user.setAccountNonLocked(false);
    }
    return user;
  }

  public static User createUserWithLastLogin() {
    User user = createValidUser();
    user.setLastLoginAt(LocalDateTime.now().minusHours(1));
    return user;
  }

  // Helper methods for creating test scenarios
  public static User createUserForPasswordTest(String username, String email) {
    return User.builder()
        .id(UUID.randomUUID())
        .username(username)
        .email(email)
        .firstName("Test")
        .lastName("User")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  public static User createUserWithCustomData(String username, String email, String firstName, String lastName) {
    return User.builder()
        .id(UUID.randomUUID())
        .username(username)
        .email(email)
        .firstName(firstName)
        .lastName(lastName)
        .password("hashedPassword123")
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }
}
