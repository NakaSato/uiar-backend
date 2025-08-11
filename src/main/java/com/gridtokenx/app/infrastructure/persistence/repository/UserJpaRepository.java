package com.gridtokenx.app.infrastructure.persistence.repository;

import com.gridtokenx.app.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface
 * This interface extends JpaRepository to provide CRUD operations
 * It's part of the infrastructure layer - a detail that implements persistence
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

  /**
   * Find user by username
   */
  Optional<UserJpaEntity> findByUsername(String username);

  /**
   * Find user by email
   */
  Optional<UserJpaEntity> findByEmail(String email);

  /**
   * Check if username exists
   */
  boolean existsByUsername(String username);

  /**
   * Check if email exists
   */
  boolean existsByEmail(String email);

  /**
   * Find all active users
   */
  @Query("SELECT u FROM UserJpaEntity u WHERE u.active = true ORDER BY u.createdAt DESC")
  List<UserJpaEntity> findAllActive();

  /**
   * Find users by active status
   */
  List<UserJpaEntity> findByActiveOrderByCreatedAtDesc(Boolean active);
}
