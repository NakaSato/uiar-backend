package com.gridtokenx.app.infrastructure.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller - Infrastructure Layer
 * Provides health check endpoints for monitoring and deployment validation
 * This endpoint is accessible without authentication for monitoring purposes
 */
@RestController
@RequestMapping("/api")
public class HealthController {

  @Autowired
  private DataSource dataSource;

  /**
   * Simple health check endpoint
   * Returns basic application status
   */
  @GetMapping("/health")
  public ResponseEntity<Map<String, Object>> health() {
    Map<String, Object> response = new HashMap<>();

    try {
      // Check database connectivity
      boolean dbHealthy = checkDatabaseHealth();

      response.put("status", dbHealthy ? "UP" : "DOWN");
      response.put("timestamp", Instant.now().toString());
      response.put("service", "UIAR Backend");
      response.put("version", getClass().getPackage().getImplementationVersion());

      // Database status
      Map<String, Object> database = new HashMap<>();
      database.put("status", dbHealthy ? "UP" : "DOWN");
      response.put("database", database);

      return dbHealthy ? ResponseEntity.ok(response) : ResponseEntity.status(503).body(response);

    } catch (Exception e) {
      response.put("status", "DOWN");
      response.put("timestamp", Instant.now().toString());
      response.put("error", e.getMessage());
      return ResponseEntity.status(503).body(response);
    }
  }

  /**
   * Detailed health check endpoint
   * Returns comprehensive application and dependency status
   */
  @GetMapping("/health/detailed")
  public ResponseEntity<Map<String, Object>> detailedHealth() {
    Map<String, Object> response = new HashMap<>();

    try {
      // Manual health check
      boolean dbHealthy = checkDatabaseHealth();

      response.put("status", dbHealthy ? "UP" : "DOWN");
      response.put("timestamp", Instant.now().toString());
      response.put("service", "UIAR Backend");
      response.put("version", getClass().getPackage().getImplementationVersion());

      // Component status
      Map<String, Object> components = new HashMap<>();
      components.put("database", Map.of("status", dbHealthy ? "UP" : "DOWN"));
      components.put("application", Map.of("status", "UP"));
      response.put("components", components);

      return dbHealthy ? ResponseEntity.ok(response) : ResponseEntity.status(503).body(response);

    } catch (Exception e) {
      response.put("status", "DOWN");
      response.put("timestamp", Instant.now().toString());
      response.put("error", e.getMessage());
      return ResponseEntity.status(503).body(response);
    }
  }

  /**
   * Readiness probe endpoint for Kubernetes/container orchestration
   * Checks if the application is ready to serve traffic
   */
  @GetMapping("/health/ready")
  public ResponseEntity<Map<String, String>> readiness() {
    Map<String, String> response = new HashMap<>();

    try {
      boolean ready = checkDatabaseHealth();

      response.put("status", ready ? "READY" : "NOT_READY");
      response.put("timestamp", Instant.now().toString());

      return ready ? ResponseEntity.ok(response) : ResponseEntity.status(503).body(response);

    } catch (Exception e) {
      response.put("status", "NOT_READY");
      response.put("timestamp", Instant.now().toString());
      response.put("error", e.getMessage());
      return ResponseEntity.status(503).body(response);
    }
  }

  /**
   * Liveness probe endpoint for Kubernetes/container orchestration
   * Checks if the application is alive and should not be restarted
   */
  @GetMapping("/health/live")
  public ResponseEntity<Map<String, String>> liveness() {
    Map<String, String> response = new HashMap<>();
    response.put("status", "ALIVE");
    response.put("timestamp", Instant.now().toString());
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
