package com.gridtokenx.app.application.service;

import com.gridtokenx.app.application.dto.LoginRequest;
import com.gridtokenx.app.application.dto.LoginResponse;
import com.gridtokenx.app.application.dto.RegisterRequest;
import com.gridtokenx.app.domain.entity.Role;
import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.domain.repository.UserRepository;
import com.gridtokenx.app.infrastructure.security.jwt.JwtTokenProvider;
import com.gridtokenx.app.infrastructure.service.JwtBlacklistService;
import com.gridtokenx.app.infrastructure.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Authentication Service for handling login, logout, and registration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtBlacklistService jwtBlacklistService;

  /**
   * Authenticate user and generate JWT tokens
   */
  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

    if (!user.isAccountActive()) {
      throw new BadCredentialsException("Account is inactive or locked");
    }

    if (!passwordService.matches(request.getPassword(), user.getPassword())) {
      user.incrementFailedAttempts();
      userRepository.save(user);
      throw new BadCredentialsException("Invalid credentials");
    }

    // Reset failed attempts and update last login
    user.updateLastLogin();
    userRepository.save(user);

    // Generate tokens
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    log.info("User logged in successfully: {}", user.getUsername());

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(jwtTokenProvider.extractExpiration(accessToken))
        .user(mapToUserResponse(user))
        .build();
  }

  /**
   * Register new user
   */
  public User register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("Username already exists");
    }

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Email already exists");
    }

    User user = User.builder()
        .id(UUID.randomUUID())
        .username(request.getUsername())
        .email(request.getEmail())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(passwordService.encodePassword(request.getPassword()))
        .roles(Set.of(Role.USER))
        .enabled(true)
        .accountNonExpired(true)
        .accountNonLocked(true)
        .credentialsNonExpired(true)
        .active(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    user = userRepository.save(user);
    log.info("New user registered: {}", user.getUsername());

    return user;
  }

  /**
   * Logout user by blacklisting the token
   */
  public void logout(String token) {
    if (jwtTokenProvider.isTokenValid(token)) {
      LocalDateTime expiration = jwtTokenProvider.extractExpiration(token);
      jwtBlacklistService.blacklistToken(token, expiration);

      String username = jwtTokenProvider.extractUsername(token);
      log.info("User logged out: {}", username);
    }
  }

  /**
   * Refresh JWT token
   */
  public LoginResponse refreshToken(String refreshToken) {
    if (!jwtTokenProvider.isTokenValid(refreshToken)) {
      throw new BadCredentialsException("Invalid refresh token");
    }

    String tokenType = jwtTokenProvider.extractTokenType(refreshToken);
    if (!"refresh".equals(tokenType)) {
      throw new BadCredentialsException("Token is not a refresh token");
    }

    String username = jwtTokenProvider.extractUsername(refreshToken);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    // Generate new access token
    String newAccessToken = jwtTokenProvider.generateAccessToken(user);

    return LoginResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken) // Keep the same refresh token
        .tokenType("Bearer")
        .expiresIn(jwtTokenProvider.extractExpiration(newAccessToken))
        .user(mapToUserResponse(user))
        .build();
  }

  /**
   * Map User entity to UserResponse DTO
   */
  private LoginResponse.UserResponse mapToUserResponse(User user) {
    return LoginResponse.UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .roles(user.getRoles())
        .build();
  }
}
