package com.gridtokenx.app.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties
 * Maps JWT-related properties from application.properties
 */
@ConfigurationProperties(prefix = "jwt")
@Data
@Component
public class JwtProperties {

  private String secret;
  private long accessTokenExpiration;
  private long refreshTokenExpiration;
  private String issuer;
  private String audience;
}
