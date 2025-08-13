package com.gridtokenx.app.infrastructure.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root Controller - Handles requests to the root path
 * Provides basic service information and status
 * This endpoint is accessible without authentication for service discovery
 */
@RestController
public class RootController {

  @Autowired
  private DataSource dataSource;

  /**
   * Root endpoint - provides basic service information
   * GET /
   */
  @GetMapping("/")
  public ResponseEntity<Map<String, Object>> root() {
    Map<String, Object> response = new LinkedHashMap<>();

    response.put("service", "UIAR Backend API");
    response.put("description", "University Institutional Academic Repository Backend");
    response.put("status", "UP");
    response.put("timestamp", Instant.now().toString());
    response.put("version", "1.0.0");
    return ResponseEntity.ok(response);
  }

  /**
   * Status endpoint - quick status check
   * GET /status
   */
  @GetMapping("/status")
  public ResponseEntity<Map<String, Object>> status() {
    Map<String, Object> response = new LinkedHashMap<>();

    boolean dbHealthy = checkDatabaseHealth();

    response.put("status", dbHealthy ? "UP" : "DOWN");
    response.put("timestamp", Instant.now().toString());
    response.put("service", "UIAR Backend");

    Map<String, String> components = new LinkedHashMap<>();
    components.put("database", dbHealthy ? "UP" : "DOWN");
    components.put("application", "UP");
    response.put("components", components);

    return ResponseEntity.ok(response);
  }

  /**
   * API info endpoint - provides API documentation links
   * GET /api
   */
  @GetMapping("/api")
  public ResponseEntity<Map<String, Object>> apiInfo() {
    Map<String, Object> response = new LinkedHashMap<>();

    response.put("name", "UIAR Backend API");
    response.put("version", "v1");
    response.put("description", "University Institutional Academic Repository API");

    Map<String, Object> endpoints = new LinkedHashMap<>();

    // Authentication endpoints
    Map<String, String> authEndpoints = new LinkedHashMap<>();
    authEndpoints.put("login", "POST /api/auth/login");
    authEndpoints.put("register", "POST /api/auth/register");
    authEndpoints.put("logout", "POST /api/auth/logout");
    authEndpoints.put("refresh", "POST /api/auth/refresh");
    authEndpoints.put("health", "GET /api/auth/health");
    endpoints.put("authentication", authEndpoints);

    // User management endpoints
    Map<String, String> userEndpoints = new LinkedHashMap<>();
    userEndpoints.put("create_user", "POST /api/v1/users");
    userEndpoints.put("get_all_users", "GET /api/v1/users");
    userEndpoints.put("get_user_by_id", "GET /api/v1/users/{id}");
    userEndpoints.put("get_user_by_username", "GET /api/v1/users/username/{username}");
    userEndpoints.put("get_user_by_email", "GET /api/v1/users/email/{email}");
    userEndpoints.put("update_user", "PUT /api/v1/users/{id}");
    userEndpoints.put("activate_user", "PATCH /api/v1/users/{id}/activate");
    userEndpoints.put("deactivate_user", "PATCH /api/v1/users/{id}/deactivate");
    userEndpoints.put("delete_user", "DELETE /api/v1/users/{id}");
    endpoints.put("users", userEndpoints);

    // Health check endpoints
    Map<String, String> healthEndpoints = new LinkedHashMap<>();
    healthEndpoints.put("basic_health", "GET /api/health");
    healthEndpoints.put("detailed_health", "GET /api/health/detailed");
    healthEndpoints.put("readiness", "GET /api/health/ready");
    healthEndpoints.put("liveness", "GET /api/health/live");
    endpoints.put("health", healthEndpoints);

    response.put("endpoints", endpoints);

    Map<String, String> links = new LinkedHashMap<>();
    links.put("actuator", "/actuator");
    links.put("health", "/actuator/health");
    links.put("metrics", "/actuator/metrics");
    response.put("management", links);

    return ResponseEntity.ok(response);
  }

  /**
   * Check database connectivity
   */
  private boolean checkDatabaseHealth() {
    try (Connection connection = dataSource.getConnection()) {
      return connection.isValid(5); // 5 second timeout
    } catch (SQLException e) {
      return false;
    }
  }
}
