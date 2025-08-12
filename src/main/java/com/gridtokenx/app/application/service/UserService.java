package com.gridtokenx.app.application.service;

import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for User operations
 * Implements UserDetailsService for Spring Security integration
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  /**
   * Find user by username
   */
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
  }

  /**
   * Find user by email
   */
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  /**
   * Find user by ID
   */
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  /**
   * Check if username exists
   */
  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  /**
   * Check if email exists
   */
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  /**
   * Save user
   */
  public User save(User user) {
    return userRepository.save(user);
  }

  /**
   * Spring Security UserDetailsService implementation
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return findByUsername(username);
  }
}
