package com.gridtokenx.app.infrastructure.persistence.mapper;

import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between Domain Entity and JPA Entity
 * This mapper provides the translation between the domain model and persistence
 * model
 * Following the Adapter pattern to keep domain and infrastructure concerns
 * separate
 */
@Component
public class UserJpaMapper {

  /**
   * Convert domain entity to JPA entity
   */
  public UserJpaEntity toJpaEntity(User domainUser) {
    if (domainUser == null) {
      return null;
    }

    return UserJpaEntity.builder()
        .id(domainUser.getId()) // Will be null for new entities, JPA will generate it
        .username(domainUser.getUsername())
        .email(domainUser.getEmail())
        .firstName(domainUser.getFirstName())
        .lastName(domainUser.getLastName())
        .active(domainUser.isActive())
        .createdAt(domainUser.getCreatedAt())
        .updatedAt(domainUser.getUpdatedAt())
        .build();
  }

  /**
   * Convert JPA entity to domain entity
   */
  public User toDomainEntity(UserJpaEntity jpaEntity) {
    if (jpaEntity == null) {
      return null;
    }

    return User.builder()
        .id(jpaEntity.getId())
        .username(jpaEntity.getUsername())
        .email(jpaEntity.getEmail())
        .firstName(jpaEntity.getFirstName())
        .lastName(jpaEntity.getLastName())
        .active(jpaEntity.getActive())
        .createdAt(jpaEntity.getCreatedAt())
        .updatedAt(jpaEntity.getUpdatedAt())
        .build();
  }
}
