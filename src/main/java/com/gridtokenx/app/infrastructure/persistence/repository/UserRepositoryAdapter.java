package com.gridtokenx.app.infrastructure.persistence.repository;

import com.gridtokenx.app.application.port.UserOutputPort;
import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.domain.repository.UserRepository;
import com.gridtokenx.app.infrastructure.persistence.mapper.UserJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository Adapter - Implementation of both domain repository and output port
 * This class adapts the JPA repository to the domain repository interface
 * Following the Adapter pattern and implementing both interfaces for clean
 * architecture
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository, UserOutputPort {

  private final UserJpaRepository userJpaRepository;
  private final UserJpaMapper userJpaMapper;

  @Override
  public User save(User user) {
    var jpaEntity = userJpaMapper.toJpaEntity(user);
    var savedEntity = userJpaRepository.save(jpaEntity);
    return userJpaMapper.toDomainEntity(savedEntity);
  }

  @Override
  public Optional<User> findById(UUID id) {
    return userJpaRepository.findById(id)
        .map(userJpaMapper::toDomainEntity);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userJpaRepository.findByUsername(username)
        .map(userJpaMapper::toDomainEntity);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email)
        .map(userJpaMapper::toDomainEntity);
  }

  @Override
  public List<User> findAll() {
    return userJpaRepository.findAll()
        .stream()
        .map(userJpaMapper::toDomainEntity)
        .collect(Collectors.toList());
  }

  @Override
  public List<User> findAllActive() {
    return userJpaRepository.findAllActive()
        .stream()
        .map(userJpaMapper::toDomainEntity)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsByUsername(String username) {
    return userJpaRepository.existsByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public void deleteById(UUID id) {
    userJpaRepository.deleteById(id);
  }

  @Override
  public long count() {
    return userJpaRepository.count();
  }
}
