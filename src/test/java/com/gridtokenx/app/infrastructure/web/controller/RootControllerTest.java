package com.gridtokenx.app.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for RootController
 * Tests the root path endpoints for service information and status
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RootControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testRootEndpoint() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.service", is("UIAR Backend API")))
        .andExpect(jsonPath("$.description", is("University Institutional Academic Repository Backend")))
        .andExpect(jsonPath("$.status", is("UP")))
        .andExpect(jsonPath("$.version", is("1.0.0")))
        .andExpect(jsonPath("$.timestamp", notNullValue()));
  }

  @Test
  void testStatusEndpoint() throws Exception {
    mockMvc.perform(get("/status"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.status", is("UP")))
        .andExpect(jsonPath("$.service", is("UIAR Backend")))
        .andExpect(jsonPath("$.timestamp", notNullValue()))
        .andExpect(jsonPath("$.components", notNullValue()))
        .andExpect(jsonPath("$.components.database", is("UP")))
        .andExpect(jsonPath("$.components.application", is("UP")));
  }

  @Test
  void testApiInfoEndpoint() throws Exception {
    mockMvc.perform(get("/api"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(jsonPath("$.name", is("UIAR Backend API")))
        .andExpect(jsonPath("$.version", is("v1")))
        .andExpect(jsonPath("$.description", is("University Institutional Academic Repository API")))
        .andExpect(jsonPath("$.endpoints", notNullValue()))
        .andExpect(jsonPath("$.endpoints.authentication", notNullValue()))
        .andExpect(jsonPath("$.endpoints.users", notNullValue()))
        .andExpect(jsonPath("$.endpoints.health", notNullValue()))
        .andExpect(jsonPath("$.management", notNullValue()))
        .andExpect(jsonPath("$.management.actuator", is("/actuator")))
        .andExpect(jsonPath("$.management.health", is("/actuator/health")))
        .andExpect(jsonPath("$.management.metrics", is("/actuator/metrics")));
  }

  @Test
  void testApiInfoEndpointStructure() throws Exception {
    mockMvc.perform(get("/api"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.endpoints.authentication.login", is("POST /api/auth/login")))
        .andExpect(jsonPath("$.endpoints.authentication.register", is("POST /api/auth/register")))
        .andExpect(jsonPath("$.endpoints.users.create_user", is("POST /api/v1/users")))
        .andExpect(jsonPath("$.endpoints.users.get_all_users", is("GET /api/v1/users")))
        .andExpect(jsonPath("$.endpoints.health.basic_health", is("GET /api/health")))
        .andExpect(jsonPath("$.endpoints.health.detailed_health", is("GET /api/health/detailed")));
  }
}
