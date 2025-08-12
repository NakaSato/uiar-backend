package com.gridtokenx.app.infrastructure.security.jwt;

import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating and validating JWT tokens
 * Handles access tokens and refresh tokens with proper claims
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;

  /**
   * Generate JWT access token for authenticated user
   */
  public String generateAccessToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId().toString());
    claims.put("roles", user.getRoles().stream()
        .map(role -> role.getAuthority())
        .collect(Collectors.toList()));
    claims.put("type", "access");

    return createToken(claims, user.getUsername(), jwtProperties.getAccessTokenExpiration());
  }

  /**
   * Generate JWT refresh token for user
   */
  public String generateRefreshToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId().toString());
    claims.put("type", "refresh");

    return createToken(claims, user.getUsername(), jwtProperties.getRefreshTokenExpiration());
  }

  /**
   * Extract username from JWT token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extract user ID from JWT token
   */
  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", String.class));
  }

  /**
   * Extract token expiration time
   */
  public LocalDateTime extractExpiration(String token) {
    Date expiration = extractClaim(token, Claims::getExpiration);
    return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  /**
   * Extract token type (access or refresh)
   */
  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get("type", String.class));
  }

  /**
   * Validate JWT token
   */
  public boolean isTokenValid(String token) {
    try {
      extractAllClaims(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      log.error("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Validate JWT token for specific user
   */
  public boolean isTokenValidForUser(String token, User user) {
    final String username = extractUsername(token);
    return (username.equals(user.getUsername()) && isTokenValid(token));
  }

  /**
   * Check if token is expired
   */
  public boolean isTokenExpired(String token) {
    return extractExpiration(token).isBefore(LocalDateTime.now());
  }

  /**
   * Extract specific claim from token
   */
  public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.resolve(claims);
  }

  /**
   * Extract all claims from token
   */
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Create JWT token with claims and subject
   */
  private String createToken(Map<String, Object> claims, String subject, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuer(jwtProperties.getIssuer())
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Get signing key for JWT tokens
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Functional interface for extracting claims
   */
  @FunctionalInterface
  public interface ClaimsResolver<T> {
    T resolve(Claims claims);
  }
}
