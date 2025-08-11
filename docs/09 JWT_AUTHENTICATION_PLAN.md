# JWT Authentication Implementation Plan

## Overview
This document outlines the implementation plan for adding JWT (JSON Web Token) authentication to the UIAR backend application while maintaining Clean Architecture principles.

## 1. Dependencies Required

### Maven Dependencies to Add
```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT Library -->
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

<!-- Password Encryption -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

## 2. Domain Layer Changes

### 2.1 Enhance User Domain Entity
- Add password field to User entity
- Add roles/authorities field for authorization
- Add account status fields (enabled, locked, expired)
- Add password-related business methods

### 2.2 New Domain Services
- `AuthenticationDomainService`: Handle authentication business logic
- `JwtTokenDomainService`: JWT token generation/validation logic (interface)

### 2.3 New Domain Exceptions
- `AuthenticationException`: Base authentication exception
- `InvalidCredentialsException`: Wrong username/password
- `TokenExpiredException`: JWT token expired
- `InvalidTokenException`: JWT token invalid

### 2.4 New Domain Repositories
- `AuthenticationRepository`: User authentication queries

## 3. Application Layer Changes

### 3.1 New DTOs
```
application/dto/auth/
├── LoginRequestDto.java
├── LoginResponseDto.java
├── RefreshTokenRequestDto.java
├── RefreshTokenResponseDto.java
├── RegisterRequestDto.java
└── RegisterResponseDto.java
```

### 3.2 New Ports
```
application/port/
├── AuthenticationInputPort.java
├── AuthenticationOutputPort.java
├── JwtTokenPort.java
└── PasswordEncoderPort.java
```

### 3.3 New Use Cases
```
application/usecase/
├── LoginUseCase.java
├── LogoutUseCase.java
├── RefreshTokenUseCase.java
└── RegisterUserUseCase.java
```

## 4. Infrastructure Layer Changes

### 4.1 Security Configuration
```
infrastructure/security/
├── SecurityConfig.java
├── JwtAuthenticationEntryPoint.java
├── JwtAuthenticationFilter.java
├── JwtTokenProvider.java
├── PasswordEncoderAdapter.java
└── UserDetailsServiceImpl.java
```

### 4.2 New Controllers
```
infrastructure/web/controller/
└── AuthController.java
```

### 4.3 New Web DTOs
```
infrastructure/web/dto/auth/
├── LoginRequest.java
├── LoginResponse.java
├── RefreshTokenRequest.java
├── RefreshTokenResponse.java
├── RegisterRequest.java
└── RegisterResponse.java
```

### 4.4 Database Changes
- Update UserJpaEntity to include password and roles
- Create database migration scripts
- Update UserJpaRepository with authentication queries

## 5. Configuration Changes

### 5.1 Application Properties
```properties
# JWT Configuration
app.jwt.secret=your-secret-key
app.jwt.expiration=86400000
app.jwt.refresh-expiration=604800000
app.jwt.issuer=gridtokenx-app

# Security Configuration
app.security.cors.allowed-origins=http://localhost:3000
app.security.auth.public-endpoints=/api/v1/auth/**,/actuator/health
```

### 5.2 Security Configuration Class
- Configure JWT filter chain
- Configure CORS
- Configure exception handling
- Configure public endpoints

## 6. Implementation Steps

### Phase 1: Domain Layer Foundation
1. **Update User Entity**
   - Add password field
   - Add roles field (enum or string)
   - Add account status fields
   - Add password validation methods

2. **Create Authentication Domain Service**
   - Password validation logic
   - User authentication business rules
   - Account status validation

3. **Create Domain Exceptions**
   - Authentication-specific exceptions
   - Extend existing exception hierarchy

4. **Create Domain Interfaces**
   - JWT token service interface
   - Authentication repository interface

### Phase 2: Application Layer
1. **Create Authentication DTOs**
   - Login request/response
   - Register request/response
   - Refresh token request/response

2. **Create Authentication Ports**
   - Input/output ports for authentication
   - JWT token management port
   - Password encoder port

3. **Create Authentication Use Cases**
   - Login use case
   - Registration use case
   - Token refresh use case
   - Logout use case

### Phase 3: Infrastructure Layer
1. **Add Security Dependencies**
   - Update pom.xml with required dependencies

2. **Create JWT Infrastructure**
   - JWT token provider implementation
   - JWT authentication filter
   - JWT authentication entry point

3. **Create Security Configuration**
   - Spring Security configuration
   - CORS configuration
   - Authentication manager configuration

4. **Create Authentication Controller**
   - Login endpoint
   - Register endpoint
   - Refresh token endpoint
   - Logout endpoint

5. **Update Database Layer**
   - Update User JPA entity
   - Create database migration
   - Update repository with auth queries

### Phase 4: Integration and Testing
1. **Update Existing Controllers**
   - Add @PreAuthorize annotations
   - Update error handling

2. **Create Integration Tests**
   - Authentication flow tests
   - JWT token validation tests
   - Security configuration tests

3. **Update API Documentation**
   - Document authentication endpoints
   - Update existing endpoint security requirements

## 7. Security Considerations

### 7.1 JWT Token Security
- Use strong secret key (at least 256 bits)
- Implement token rotation
- Short access token expiration (15-30 minutes)
- Longer refresh token expiration (7 days)
- Store refresh tokens securely

### 7.2 Password Security
- Use BCrypt for password hashing
- Implement password complexity requirements
- Add password history to prevent reuse
- Implement account lockout after failed attempts

### 7.3 CORS Configuration
- Configure specific allowed origins
- Restrict allowed methods and headers
- Don't use wildcard in production

## 8. API Endpoints Design

### 8.1 Authentication Endpoints
```
POST /api/v1/auth/login
POST /api/v1/auth/register  
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
GET  /api/v1/auth/me
```

### 8.2 Protected Endpoints
```
All existing user endpoints will require authentication:
GET    /api/v1/users (Admin only)
GET    /api/v1/users/{id} (Self or Admin)
PUT    /api/v1/users/{id} (Self or Admin)
DELETE /api/v1/users/{id} (Admin only)
```

## 9. Database Schema Updates

### 9.1 Users Table Updates
```sql
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;
ALTER TABLE users ADD COLUMN roles VARCHAR(255) NOT NULL DEFAULT 'USER';
ALTER TABLE users ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN account_non_expired BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE users ADD COLUMN last_login_at TIMESTAMP;
ALTER TABLE users ADD COLUMN failed_login_attempts INTEGER DEFAULT 0;
```

### 9.2 Refresh Tokens Table
```sql
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);
```

## 10. Clean Architecture Compliance

### 10.1 Dependency Direction
- Domain layer remains independent
- Application layer depends only on domain
- Infrastructure layer implements application and domain interfaces
- Security concerns isolated in infrastructure layer

### 10.2 Separation of Concerns
- JWT logic in infrastructure layer
- Authentication business rules in domain layer
- Use case orchestration in application layer
- HTTP/REST concerns in web layer

### 10.3 Testability
- Domain services unit testable
- Use cases testable with mocks
- Infrastructure adapters integration testable
- End-to-end authentication flow testable

## 11. Migration Strategy

### 11.1 Backward Compatibility
- Implement feature flags for gradual rollout
- Support both authenticated and unauthenticated access initially
- Migrate existing users with default passwords
- Provide password reset functionality

### 11.2 Data Migration
- Create migration scripts for existing users
- Generate temporary passwords for existing users
- Send password reset emails to existing users
- Implement grace period for migration

## 12. Monitoring and Logging

### 12.1 Security Events
- Log authentication attempts
- Log token generation/validation
- Log authorization failures
- Monitor suspicious activities

### 12.2 Metrics
- Authentication success/failure rates
- Token usage patterns
- User session durations
- Failed login attempts by IP

This plan ensures JWT authentication is implemented following Clean Architecture principles while maintaining security best practices and providing a smooth migration path for existing users.
