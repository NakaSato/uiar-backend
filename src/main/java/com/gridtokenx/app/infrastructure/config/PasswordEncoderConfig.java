package com.gridtokenx.app.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password encoding configuration
 * Provides BCrypt password encoder for secure password hashing
 */
@Configuration
public class PasswordEncoderConfig {

  /**
   * BCrypt password encoder with strength 12
   * Higher strength provides better security but slower hashing
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
