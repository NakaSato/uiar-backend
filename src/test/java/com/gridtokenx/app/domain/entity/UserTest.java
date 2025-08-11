package com.gridtokenx.app.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserTest {

  private User user;
  private UUID userId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    now = LocalDateTime.now();
    user = User.builder()
        .id(userId)
        .username("testuser")
        .email("test@example.com")
        .firstName("Test")
        .lastName("User")
        .active(true)
        .createdAt(now)
        .updatedAt(now)
        .password("hashedPassword123")
        .build();
  }

  @Nested
  @DisplayName("Builder Tests")
  class BuilderTests {

    @Test
    @DisplayName("Should create user with default values")
    void shouldCreateUserWithDefaultValues() {
      User newUser = User.builder()
          .username("newuser")
          .email("new@example.com")
          .build();

      assertTrue(newUser.isEnabled());
      assertTrue(newUser.isAccountNonExpired());
      assertTrue(newUser.isAccountNonLocked());
      assertTrue(newUser.isCredentialsNonExpired());
      assertEquals(0, newUser.getFailedLoginAttempts());
      assertNotNull(newUser.getRoles());
      assertTrue(newUser.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Should create user with custom roles")
    void shouldCreateUserWithCustomRoles() {
      Set<Role> roles = new HashSet<>();
      roles.add(Role.USER);
      roles.add(Role.ADMIN);

      User userWithRoles = User.builder()
          .username("adminuser")
          .email("admin@example.com")
          .roles(roles)
          .build();

      assertEquals(2, userWithRoles.getRoles().size());
      assertTrue(userWithRoles.hasRole(Role.USER));
      assertTrue(userWithRoles.hasRole(Role.ADMIN));
    }
  }

  @Nested
  @DisplayName("Password Validation Tests")
  class PasswordValidationTests {

    @Test
    @DisplayName("Should validate strong passwords")
    void shouldValidateStrongPasswords() {
      assertTrue(user.isValidPassword("Password123"));
      assertTrue(user.isValidPassword("StrongPass1"));
      assertTrue(user.isValidPassword("MySecure123"));
      assertTrue(user.isValidPassword("Complex1Password"));
    }

    @Test
    @DisplayName("Should reject weak passwords")
    void shouldRejectWeakPasswords() {
      // Too short
      assertFalse(user.isValidPassword("Pass1"));
      assertFalse(user.isValidPassword("Short1"));

      // No uppercase
      assertFalse(user.isValidPassword("password123"));

      // No lowercase
      assertFalse(user.isValidPassword("PASSWORD123"));

      // No digit
      assertFalse(user.isValidPassword("Password"));

      // Null or empty
      assertFalse(user.isValidPassword(null));
      assertFalse(user.isValidPassword(""));
    }

    @Test
    @DisplayName("Should handle edge cases in password validation")
    void shouldHandleEdgeCasesInPasswordValidation() {
      // Exactly 8 characters
      assertTrue(user.isValidPassword("Pass123A"));

      // Very long password
      assertTrue(user.isValidPassword("ThisIsAVeryLongPasswordWithNumbers123AndUppercase"));

      // Special characters (should still be valid)
      assertTrue(user.isValidPassword("Pass123!@#"));
    }
  }

  @Nested
  @DisplayName("Role Management Tests")
  class RoleManagementTests {

    @Test
    @DisplayName("Should add roles correctly")
    void shouldAddRolesCorrectly() {
      LocalDateTime beforeUpdate = user.getUpdatedAt();

      user.addRole(Role.USER);
      user.addRole(Role.ADMIN);

      assertTrue(user.hasRole(Role.USER));
      assertTrue(user.hasRole(Role.ADMIN));
      assertFalse(user.hasRole(Role.MODERATOR));
      assertEquals(2, user.getRoles().size());
      assertTrue(user.getUpdatedAt().isAfter(beforeUpdate));
    }

    @Test
    @DisplayName("Should remove roles correctly")
    void shouldRemoveRolesCorrectly() {
      user.addRole(Role.USER);
      user.addRole(Role.ADMIN);
      LocalDateTime beforeRemove = user.getUpdatedAt();

      user.removeRole(Role.USER);

      assertFalse(user.hasRole(Role.USER));
      assertTrue(user.hasRole(Role.ADMIN));
      assertEquals(1, user.getRoles().size());
      assertTrue(user.getUpdatedAt().isAfter(beforeRemove));
    }

    @Test
    @DisplayName("Should handle removing non-existent role")
    void shouldHandleRemovingNonExistentRole() {
      user.addRole(Role.USER);
      int originalSize = user.getRoles().size();

      user.removeRole(Role.ADMIN); // Role not present

      assertEquals(originalSize, user.getRoles().size());
      assertTrue(user.hasRole(Role.USER));
    }

    @Test
    @DisplayName("Should not add duplicate roles")
    void shouldNotAddDuplicateRoles() {
      user.addRole(Role.USER);
      user.addRole(Role.USER); // Adding same role again

      assertEquals(1, user.getRoles().size());
      assertTrue(user.hasRole(Role.USER));
    }
  }

  @Nested
  @DisplayName("Account Status Tests")
  class AccountStatusTests {

    @Test
    @DisplayName("Should check account active status correctly")
    void shouldCheckAccountActiveStatusCorrectly() {
      // All flags true = active account
      assertTrue(user.isAccountActive());

      // Test each flag individually
      user.setActive(false);
      assertFalse(user.isAccountActive());
      user.setActive(true);

      user.setEnabled(false);
      assertFalse(user.isAccountActive());
      user.setEnabled(true);

      user.setAccountNonLocked(false);
      assertFalse(user.isAccountActive());
      user.setAccountNonLocked(true);

      user.setAccountNonExpired(false);
      assertFalse(user.isAccountActive());
    }

    @Test
    @DisplayName("Should check account locked status correctly")
    void shouldCheckAccountLockedStatusCorrectly() {
      assertFalse(user.isAccountLocked());

      user.setAccountNonLocked(false);
      assertTrue(user.isAccountLocked());

      user.setAccountNonLocked(true);
      user.setFailedLoginAttempts(5);
      assertTrue(user.isAccountLocked());

      user.setFailedLoginAttempts(3);
      assertFalse(user.isAccountLocked());
    }

    @Test
    @DisplayName("Should deactivate user correctly")
    void shouldDeactivateUserCorrectly() {
      LocalDateTime beforeDeactivation = user.getUpdatedAt();

      user.deactivate();

      assertFalse(user.isActive());
      assertTrue(user.getUpdatedAt().isAfter(beforeDeactivation));
    }
  }

  @Nested
  @DisplayName("Login Management Tests")
  class LoginManagementTests {

    @Test
    @DisplayName("Should update last login correctly")
    void shouldUpdateLastLoginCorrectly() {
      assertNull(user.getLastLoginAt());
      user.setFailedLoginAttempts(3);
      LocalDateTime beforeLogin = LocalDateTime.now();

      user.updateLastLogin();

      assertNotNull(user.getLastLoginAt());
      assertTrue(user.getLastLoginAt().isAfter(beforeLogin));
      assertEquals(0, user.getFailedLoginAttempts());
    }

    @Test
    @DisplayName("Should increment failed attempts correctly")
    void shouldIncrementFailedAttemptsCorrectly() {
      assertEquals(0, user.getFailedLoginAttempts());
      LocalDateTime beforeIncrement = user.getUpdatedAt();

      user.incrementFailedAttempts();

      assertEquals(1, user.getFailedLoginAttempts());
      assertTrue(user.getUpdatedAt().isAfter(beforeIncrement));
      assertTrue(user.isAccountNonLocked()); // Should still be unlocked
    }

    @Test
    @DisplayName("Should lock account after 5 failed attempts")
    void shouldLockAccountAfterFiveFailedAttempts() {
      // Increment to 4 attempts
      for (int i = 0; i < 4; i++) {
        user.incrementFailedAttempts();
        assertTrue(user.isAccountNonLocked());
      }

      // 5th attempt should lock the account
      user.incrementFailedAttempts();
      assertEquals(5, user.getFailedLoginAttempts());
      assertFalse(user.isAccountNonLocked());
      assertTrue(user.isAccountLocked());
    }

    @Test
    @DisplayName("Should reset failed attempts correctly")
    void shouldResetFailedAttemptsCorrectly() {
      user.setFailedLoginAttempts(5);
      user.setAccountNonLocked(false);
      LocalDateTime beforeReset = user.getUpdatedAt();

      user.resetFailedAttempts();

      assertEquals(0, user.getFailedLoginAttempts());
      assertTrue(user.isAccountNonLocked());
      assertTrue(user.getUpdatedAt().isAfter(beforeReset));
    }
  }

  @Nested
  @DisplayName("Entity Validation Tests")
  class EntityValidationTests {

    @Test
    @DisplayName("Should maintain entity integrity")
    void shouldMaintainEntityIntegrity() {
      assertNotNull(user.getId());
      assertNotNull(user.getUsername());
      assertNotNull(user.getEmail());
      assertNotNull(user.getRoles());
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValuesAppropriately() {
      User userWithNulls = User.builder()
          .username("test")
          .build();

      // Default values should be set
      assertNotNull(userWithNulls.getRoles());
      assertTrue(userWithNulls.isEnabled());
      assertEquals(0, userWithNulls.getFailedLoginAttempts());
    }
  }
}
