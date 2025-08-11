# JWT Authentication - Quick Start Templates

## 1. Maven Dependencies to Add

```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

## 2. Application Properties Template

```properties
# JWT Configuration
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin
```

## 3. Domain Layer Templates

### User Entity Update
```java
// Add to existing User.java
private String password;
private Set<String> roles = new HashSet<>();
private boolean enabled = true;
private boolean accountNonExpired = true;
private boolean accountNonLocked = true;
private boolean credentialsNonExpired = true;
private LocalDateTime lastLoginAt;
private int failedLoginAttempts = 0;

// Add validation method
public boolean isValidPassword(String rawPassword) {
    // Password validation logic
    return rawPassword != null && rawPassword.length() >= 8;
}

// Add convenience methods
public boolean hasRole(String role) {
    return roles.contains(role);
}

public void addRole(String role) {
    this.roles.add(role);
}
```

### Authentication Domain Service Interface
```java
package com.gridtokenx.app.domain.service;

public interface AuthenticationDomainService {
    User authenticate(String username, String password);
    User register(String username, String email, String password, String firstName, String lastName);
    void updateLastLogin(UUID userId);
    void incrementFailedAttempts(String username);
    void resetFailedAttempts(String username);
}
```

### Domain Exceptions
```java
// AuthenticationException.java
package com.gridtokenx.app.domain.exception;

public class AuthenticationException extends DomainException {
    public AuthenticationException(String message) {
        super(message);
    }
}

// InvalidCredentialsException.java
public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
```

## 4. Application Layer Templates

### Authentication DTOs
```java
// LoginRequestDto.java
package com.gridtokenx.app.application.dto.auth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    private String username;
    private String password;
}

// LoginResponseDto.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDto user;
}
```

### Authentication Use Case
```java
// LoginUseCase.java
package com.gridtokenx.app.application.usecase.auth;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginUseCase {
    
    private final AuthenticationDomainService authService;
    private final TokenInputPort tokenService;
    private final UserMapper userMapper;
    
    public LoginResponseDto login(LoginRequestDto request) {
        // Authenticate user
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        
        // Generate tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        
        // Update last login
        authService.updateLastLogin(user.getId());
        
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenService.getAccessTokenExpiration())
                .user(userMapper.toDto(user))
                .build();
    }
}
```

## 5. Infrastructure Layer Templates

### JWT Token Provider
```java
// JwtTokenProvider.java
package com.gridtokenx.app.infrastructure.security.jwt;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;
    
    public String generateToken(UserDetails userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### JWT Authentication Filter
```java
// JwtAuthenticationFilter.java
package com.gridtokenx.app.infrastructure.security.jwt;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String jwt = getJwtFromRequest(request);
        
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String username = tokenProvider.getUsernameFromToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### Security Configuration
```java
// SecurityConfig.java
package com.gridtokenx.app.infrastructure.security.config;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> 
                auth.requestMatchers("/api/v1/auth/**").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .anyRequest().authenticated()
            );
            
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### Authentication Controller
```java
// AuthController.java
package com.gridtokenx.app.infrastructure.web.controller;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {
    
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final AuthMapper authMapper;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        
        LoginRequestDto dto = authMapper.toDto(request);
        LoginResponseDto response = loginUseCase.login(dto);
        
        return ResponseEntity.ok(authMapper.toResponse(response));
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());
        
        RegisterRequestDto dto = authMapper.toDto(request);
        RegisterResponseDto response = registerUseCase.register(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authMapper.toResponse(response));
    }
}
```

## 6. Database Migration Template

```sql
-- V2__add_authentication.sql
ALTER TABLE users ADD COLUMN password VARCHAR(255);
ALTER TABLE users ADD COLUMN roles VARCHAR(255) NOT NULL DEFAULT 'USER';
ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN account_non_expired BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;
ALTER TABLE users ADD COLUMN failed_login_attempts INTEGER DEFAULT 0;

-- Create refresh tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
```

## 7. Testing Templates

### Unit Test Example
```java
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
    
    @Mock
    private AuthenticationDomainService authService;
    
    @Mock
    private TokenInputPort tokenService;
    
    @InjectMocks
    private LoginUseCase loginUseCase;
    
    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() {
        // Given
        LoginRequestDto request = LoginRequestDto.builder()
                .username("testuser")
                .password("password123")
                .build();
                
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .build();
                
        when(authService.authenticate("testuser", "password123")).thenReturn(user);
        when(tokenService.generateAccessToken(user)).thenReturn("access-token");
        when(tokenService.generateRefreshToken(user)).thenReturn("refresh-token");
        
        // When
        LoginResponseDto response = loginUseCase.login(request);
        
        // Then
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(authService).updateLastLogin(user.getId());
    }
}
```

### Integration Test Example
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        // Given
        LoginRequest request = new LoginRequest("testuser", "password123");
        
        // When
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/login", request, LoginResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccessToken()).isNotNull();
    }
}
```

## 8. Next Steps Checklist

### Immediate Actions (Phase 1)
- [ ] Add Maven dependencies to pom.xml
- [ ] Update User domain entity with authentication fields
- [ ] Create authentication domain service interface
- [ ] Create authentication domain exceptions

### Short Term (Phase 2-3)
- [ ] Implement authentication use cases
- [ ] Create JWT token provider
- [ ] Configure Spring Security
- [ ] Create authentication controller

### Medium Term (Phase 4-5)
- [ ] Update database schema
- [ ] Create database migration scripts
- [ ] Add comprehensive error handling
- [ ] Implement refresh token functionality

### Long Term (Phase 6-8)
- [ ] Add comprehensive testing
- [ ] Implement role-based authorization
- [ ] Add security monitoring
- [ ] Performance optimization

This template provides the essential code structure needed to implement JWT authentication following Clean Architecture principles.
