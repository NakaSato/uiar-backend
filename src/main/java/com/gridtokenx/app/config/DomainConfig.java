package com.gridtokenx.app.config;

import com.gridtokenx.app.domain.repository.UserRepository;
import com.gridtokenx.app.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain services
 * This configuration wires up the domain layer components
 * Following the Dependency Injection pattern to maintain loose coupling
 */
@Configuration
public class DomainConfig {

  /**
   * Create UserDomainService bean
   * This bean is created in the configuration layer to avoid Spring annotations
   * in domain layer
   * Keeping domain layer pure and framework-independent
   */
  @Bean
  public UserDomainService userDomainService(UserRepository userRepository) {
    return new UserDomainService(userRepository);
  }
}
