package com.gridtokenx.app.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA Configuration
 * Transaction management configuration only
 * Entity scanning and repository configuration moved to AppApplication.java
 */
@Configuration
@EnableTransactionManagement
public class JpaConfig {
  // Spring Boot auto-configuration handles EntityManagerFactory and
  // TransactionManager
  // Entity scanning and JPA repositories are configured in the main application
  // class
}
