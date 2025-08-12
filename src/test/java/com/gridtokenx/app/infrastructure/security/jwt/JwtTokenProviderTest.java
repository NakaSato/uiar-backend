package com.gridtokenx.app.infrastructure.security.jwt;

import com.gridtokenx.app.domain.entity.Role;
import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.infrastructure.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JwtTokenProvider
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenProviderTest {

  @Mock
  private JwtProperties jwtProperties;

  private JwtTokenProvider jwtTokenProvider;
  private User testUser;

  @BeforeEach
  void setUp() {
    when(jwtProperties.getSecret()).thenReturn("mySecretKeyForTestingThatShouldBeLongEnoughForHS256Algorithm");
    when(jwtProperties.getAccessTokenExpiration()).thenReturn(3600000L); // 1 hour
    when(jwtProperties.getRefreshTokenExpiration()).thenReturn(86400000L); // 24 hours
    when(jwtProperties.getIssuer()).thenReturn("test-issuer");
    when(jwtProperties.getAudience()).thenReturn("test-audience");

    jwtTokenProvider = new JwtTokenProvider(jwtProperties);

    testUser = User.builder()
        .id(UUID.randomUUID())
        .username("testuser")
        .email("test@example.com")
        .firstName("Test")
        .lastName("User")
        .password("encodedPassword")
        .roles(Set.of(Role.USER))
        .enabled(true)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  void shouldGenerateAccessToken() {
    // When
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // Then
    assertThat(token).isNotEmpty();
    assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("testuser");
    assertThat(jwtTokenProvider.extractTokenType(token)).isEqualTo("access");
    assertThat(jwtTokenProvider.isTokenValid(token)).isTrue();
  }

  @Test
  void shouldGenerateRefreshToken() {
    // When
    String token = jwtTokenProvider.generateRefreshToken(testUser);

    // Then
    assertThat(token).isNotEmpty();
    assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("testuser");
    assertThat(jwtTokenProvider.extractTokenType(token)).isEqualTo("refresh");
    assertThat(jwtTokenProvider.isTokenValid(token)).isTrue();
  }

  @Test
  void shouldExtractUserIdFromToken() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    String extractedUserId = jwtTokenProvider.extractUserId(token);

    // Then
    assertThat(extractedUserId).isEqualTo(testUser.getId().toString());
  }

  @Test
  void shouldValidateTokenForUser() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    boolean isValid = jwtTokenProvider.isTokenValidForUser(token, testUser);

    // Then
    assertThat(isValid).isTrue();
  }

  @Test
  void shouldReturnFalseForInvalidToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When
    boolean isValid = jwtTokenProvider.isTokenValid(invalidToken);

    // Then
    assertThat(isValid).isFalse();
  }

  @Test
  void shouldReturnTrueForNonExpiredToken() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    boolean isExpired = jwtTokenProvider.isTokenExpired(token);

    // Then
    assertThat(isExpired).isFalse();
  }
}
