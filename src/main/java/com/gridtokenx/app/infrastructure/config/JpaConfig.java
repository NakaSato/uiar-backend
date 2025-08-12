package com.gridtokenx.app.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration
 * Configures JPA repositories and entity scanning
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.gridtokenx.app.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.gridtokenx.app.infrastructure.persistence.entity")
@EnableTransactionManagement
public class JpaConfig {
  // Spring Boot auto-configuration handles EntityManagerFactory and
  // TransactionManager
  // This class ensures proper package scanning for JPA repositories and entities
}
