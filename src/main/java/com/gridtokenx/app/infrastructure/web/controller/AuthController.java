package com.gridtokenx.app.infrastructure.web.controller;

import com.gridtokenx.app.application.dto.LoginRequest;
import com.gridtokenx.app.application.dto.LoginResponse;
import com.gridtokenx.app.application.dto.RegisterRequest;
import com.gridtokenx.app.application.service.AuthenticationService;
import com.gridtokenx.app.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication REST Controller
 * Handles login, logout, registration, and token refresh endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthenticationService authenticationService;

  /**
   * User login endpoint
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      LoginResponse response = authenticationService.login(loginRequest);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Login failed for user: {}", loginRequest.getUsername(), e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  /**
   * User registration endpoint
   */
  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
    try {
      User user = authenticationService.register(registerRequest);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(Map.of(
              "message", "User registered successfully",
              "username", user.getUsername()));
    } catch (IllegalArgumentException e) {
      log.error("Registration failed: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      log.error("Registration failed for user: {}", registerRequest.getUsername(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Registration failed"));
    }
  }

  /**
   * User logout endpoint
   */
  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        authenticationService.logout(token);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
      }
      return ResponseEntity.badRequest()
          .body(Map.of("error", "No valid token provided"));
    } catch (Exception e) {
      log.error("Logout failed", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Logout failed"));
    }
  }

  /**
   * Token refresh endpoint
   */
  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
    try {
      String refreshToken = request.get("refreshToken");
      if (refreshToken == null || refreshToken.isEmpty()) {
        return ResponseEntity.badRequest().build();
      }

      LoginResponse response = authenticationService.refreshToken(refreshToken);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Token refresh failed", e);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  /**
   * Health check endpoint for authentication service
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, String>> health() {
    return ResponseEntity.ok(Map.of(
        "status", "UP",
        "service", "Authentication Service"));
  }
}
