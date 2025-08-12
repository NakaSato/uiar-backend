package com.gridtokenx.app.infrastructure.security.jwt;

import com.gridtokenx.app.application.service.UserService;
import com.gridtokenx.app.domain.entity.User;
import com.gridtokenx.app.infrastructure.service.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter for processing JWT tokens in requests
 * Validates tokens and sets up Spring Security context
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserService userService;
  private final JwtBlacklistService jwtBlacklistService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // Check if Authorization header is present and starts with Bearer
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // Extract JWT token
    jwt = authHeader.substring(7);

    try {
      // Extract username from token
      username = jwtTokenProvider.extractUsername(jwt);

      // Check if token is blacklisted
      if (jwtBlacklistService.isTokenBlacklisted(jwt)) {
        log.debug("Token is blacklisted: {}", jwt.substring(0, Math.min(jwt.length(), 10)) + "...");
        filterChain.doFilter(request, response);
        return;
      } // If username is present and no authentication is set in context
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // Load user details
        User user = userService.findByUsername(username);

        // Validate token
        if (jwtTokenProvider.isTokenValidForUser(jwt, user)) {

          // Create authentication token
          List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
              .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
              .collect(Collectors.toList());

          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              user,
              null,
              authorities);

          // Set authentication details
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          // Set authentication in security context
          SecurityContextHolder.getContext().setAuthentication(authToken);

          log.debug("Successfully authenticated user: {}", username);
        }
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }
}
