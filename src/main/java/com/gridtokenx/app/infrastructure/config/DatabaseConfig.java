package com.gridtokenx.app.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Database Configuration
 * Provides additional database configuration when needed
 */
@Configuration
public class DatabaseConfig {

  /**
   * Custom DataSource configuration for debugging
   * Only active when custom.datasource.enabled is true
   */
  @Bean
  @ConditionalOnProperty(name = "custom.datasource.enabled", havingValue = "true")
  public DataSource customDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");

    // These will be overridden by application properties
    dataSource.setUrl("jdbc:postgresql://localhost:5432/uiar_db");
    dataSource.setUsername("uiar_user");
    dataSource.setPassword("uiar_password");

    return dataSource;
  }
}
