package com.gridtokenx.app.infrastructure.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token Blacklist Service using Caffeine cache
 * Manages blacklisted tokens for logout and security purposes
 */
@Service
@Slf4j
public class JwtBlacklistService {

  private final Cache<String, LocalDateTime> blacklistedTokens;

  public JwtBlacklistService() {
    this.blacklistedTokens = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterWrite(24, TimeUnit.HOURS) // Tokens expire after 24 hours
        .build();
  }

  /**
   * Add token to blacklist
   */
  public void blacklistToken(String token, LocalDateTime expirationTime) {
    blacklistedTokens.put(token, expirationTime);
    log.debug("Token blacklisted: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
  }

  /**
   * Check if token is blacklisted
   */
  public boolean isTokenBlacklisted(String token) {
    LocalDateTime expirationTime = blacklistedTokens.getIfPresent(token);

    if (expirationTime == null) {
      return false;
    }

    // Check if the blacklisted token has expired
    if (LocalDateTime.now().isAfter(expirationTime)) {
      blacklistedTokens.invalidate(token);
      return false;
    }

    return true;
  }

  /**
   * Remove token from blacklist (cleanup)
   */
  public void removeToken(String token) {
    blacklistedTokens.invalidate(token);
  }

  /**
   * Get cache statistics for monitoring
   */
  public String getCacheStats() {
    return blacklistedTokens.stats().toString();
  }

  /**
   * Clear all blacklisted tokens (admin operation)
   */
  public void clearAllTokens() {
    blacklistedTokens.invalidateAll();
    log.info("All blacklisted tokens cleared");
  }
}
