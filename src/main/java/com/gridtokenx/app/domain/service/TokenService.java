package com.gridtokenx.app.domain.service;

import com.gridtokenx.app.domain.entity.User;
import java.time.LocalDateTime;

/**
 * Domain service for JWT token business logic
 * Defines the contract for token operations without infrastructure dependencies
 */
public interface TokenService {

  /**
   * Generate JWT access token for authenticated user
   * 
   * @param user the authenticated user
   * @return JWT access token
   */
  String generateAccessToken(User user);

  /**
   * Generate JWT refresh token for user
   * 
   * @param user the authenticated user
   * @return JWT refresh token
   */
  String generateRefreshToken(User user);

  /**
   * Extract username from JWT token
   * 
   * @param token the JWT token
   * @return username from token claims
   */
  String extractUsername(String token);

  /**
   * Extract user ID from JWT token
   * 
   * @param token the JWT token
   * @return user ID from token claims
   */
  Long extractUserId(String token);

  /**
   * Extract token expiration time
   * 
   * @param token the JWT token
   * @return expiration timestamp
   */
  LocalDateTime extractExpiration(String token);

  /**
   * Validate JWT token
   * 
   * @param token the JWT token to validate
   * @return true if token is valid and not expired
   */
  boolean isTokenValid(String token);

  /**
   * Validate JWT token for specific user
   * 
   * @param token the JWT token
   * @param user  the user to validate against
   * @return true if token is valid for the user
   */
  boolean isTokenValidForUser(String token, User user);

  /**
   * Check if token is expired
   * 
   * @param token the JWT token
   * @return true if token is expired
   */
  boolean isTokenExpired(String token);

  /**
   * Invalidate/blacklist token (for logout)
   * 
   * @param token the JWT token to invalidate
   */
  void invalidateToken(String token);

  /**
   * Check if token is blacklisted
   * 
   * @param token the JWT token to check
   * @return true if token is blacklisted
   */
  boolean isTokenBlacklisted(String token);
}
