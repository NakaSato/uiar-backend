package com.gridtokenx.app.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Role Entity Tests")
class RoleTest {

  @Test
  @DisplayName("Should have correct authority values")
  void shouldHaveCorrectAuthorityValues() {
    assertEquals("USER", Role.USER.getAuthority());
    assertEquals("ADMIN", Role.ADMIN.getAuthority());
    assertEquals("MODERATOR", Role.MODERATOR.getAuthority());
  }

  @Test
  @DisplayName("Should have correct descriptions")
  void shouldHaveCorrectDescriptions() {
    assertEquals("Standard user with basic permissions", Role.USER.getDescription());
    assertEquals("Administrator with full system access", Role.ADMIN.getDescription());
    assertEquals("Moderator with elevated permissions", Role.MODERATOR.getDescription());
  }

  @Test
  @DisplayName("Should convert string to Role enum correctly")
  void shouldConvertStringToRoleEnum() {
    assertEquals(Role.USER, Role.fromAuthority("USER"));
    assertEquals(Role.ADMIN, Role.fromAuthority("ADMIN"));
    assertEquals(Role.MODERATOR, Role.fromAuthority("MODERATOR"));
  }

  @Test
  @DisplayName("Should handle invalid role string")
  void shouldHandleInvalidRoleString() {
    assertThrows(IllegalArgumentException.class, () -> Role.fromAuthority("INVALID"));
    assertThrows(IllegalArgumentException.class, () -> Role.fromAuthority(null));
    assertThrows(IllegalArgumentException.class, () -> Role.fromAuthority(""));
  }

  @Test
  @DisplayName("Should check role privileges correctly")
  void shouldCheckRolePrivilegesCorrectly() {
    // MODERATOR has highest privilege (ordinal 2)
    assertTrue(Role.MODERATOR.hasPrivilege(Role.USER));
    assertTrue(Role.MODERATOR.hasPrivilege(Role.ADMIN));
    assertTrue(Role.MODERATOR.hasPrivilege(Role.MODERATOR));

    // ADMIN has middle privilege (ordinal 1)
    assertTrue(Role.ADMIN.hasPrivilege(Role.USER));
    assertFalse(Role.ADMIN.hasPrivilege(Role.MODERATOR));
    assertTrue(Role.ADMIN.hasPrivilege(Role.ADMIN));

    // USER has lowest privilege (ordinal 0)
    assertFalse(Role.USER.hasPrivilege(Role.ADMIN));
    assertFalse(Role.USER.hasPrivilege(Role.MODERATOR));
    assertTrue(Role.USER.hasPrivilege(Role.USER));
  }

  @Test
  @DisplayName("Should maintain enum ordering")
  void shouldMaintainEnumOrdering() {
    Role[] roles = Role.values();
    assertEquals(3, roles.length);
    assertEquals(Role.USER, roles[0]);
    assertEquals(Role.ADMIN, roles[1]);
    assertEquals(Role.MODERATOR, roles[2]);
  }
}
