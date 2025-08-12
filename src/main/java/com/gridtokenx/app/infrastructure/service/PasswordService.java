package com.gridtokenx.app.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Password service for encoding and validating passwords
 * Encapsulates password operations for clean architecture
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

  private final PasswordEncoder passwordEncoder;

  /**
   * Encode raw password using BCrypt
   */
  public String encodePassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  /**
   * Verify raw password against encoded password
   */
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  /**
   * Check if password needs to be rehashed (e.g., strength changed)
   */
  public boolean upgradeEncoding(String encodedPassword) {
    return passwordEncoder.upgradeEncoding(encodedPassword);
  }
}
