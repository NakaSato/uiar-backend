# JWT Authentication Implementation Roadmap

## Phase 1: Domain Layer Foundation 

### Step 1.1: Update User Domain Entity
**File: `src/main/java/com/gridtokenx/app/domain/entity/User.java`**

Add these fields to the existing User entity:
```java
private String password;
private Set<Role> roles;
private boolean enabled;
private boolean accountNonExpired;
private boolean accountNonLocked;
private boolean credentialsNonExpired;
private LocalDateTime lastLoginAt;
private int failedLoginAttempts;
```

### Step 1.2: Create Role Enum
**File: `src/main/java/com/gridtokenx/app/domain/entity/Role.java`**
```java
public enum Role {
    USER("USER"),
    ADMIN("ADMIN"),
    MODERATOR("MODERATOR");
    
    private final String authority;
    // implementation details
}
```

### Step 1.3: Create Authentication Domain Service Interface
**File: `src/main/java/com/gridtokenx/app/domain/service/AuthenticationDomainService.java`**

### Step 1.4: Create JWT Token Service Interface (Domain)
**File: `src/main/java/com/gridtokenx/app/domain/service/TokenService.java`**

### Step 1.5: Create Authentication Domain Exceptions
**Files:**
- `src/main/java/com/gridtokenx/app/domain/exception/AuthenticationException.java`
- `src/main/java/com/gridtokenx/app/domain/exception/InvalidCredentialsException.java`
- `src/main/java/com/gridtokenx/app/domain/exception/TokenExpiredException.java`
- `src/main/java/com/gridtokenx/app/domain/exception/InvalidTokenException.java`

## Phase 2: Application Layer (Day 3-4)

### Step 2.1: Create Authentication DTOs
**Directory: `src/main/java/com/gridtokenx/app/application/dto/auth/`**

Files to create:
- `LoginRequestDto.java`
- `LoginResponseDto.java`
- `RegisterRequestDto.java`
- `RegisterResponseDto.java`
- `RefreshTokenRequestDto.java`
- `RefreshTokenResponseDto.java`
- `UserProfileDto.java`

### Step 2.2: Create Authentication Ports
**Directory: `src/main/java/com/gridtokenx/app/application/port/auth/`**

Files to create:
- `AuthenticationInputPort.java`
- `AuthenticationOutputPort.java`
- `TokenInputPort.java`
- `TokenOutputPort.java`
- `PasswordEncoderPort.java`

### Step 2.3: Create Authentication Use Cases
**Directory: `src/main/java/com/gridtokenx/app/application/usecase/auth/`**

Files to create:
- `LoginUseCase.java`
- `RegisterUseCase.java`
- `RefreshTokenUseCase.java`
- `LogoutUseCase.java`
- `GetUserProfileUseCase.java`

## Phase 3: Infrastructure Layer - Security Core (Day 5-6)

### Step 3.1: Add Maven Dependencies
**File: `pom.xml`**

Add the security and JWT dependencies as specified in the plan.

### Step 3.2: Create JWT Infrastructure
**Directory: `src/main/java/com/gridtokenx/app/infrastructure/security/jwt/`**

Files to create:
- `JwtTokenProvider.java` - JWT token creation/validation
- `JwtAuthenticationFilter.java` - JWT filter for requests
- `JwtAuthenticationEntryPoint.java` - Handle auth errors
- `JwtProperties.java` - JWT configuration properties

### Step 3.3: Create Security Configuration
**Directory: `src/main/java/com/gridtokenx/app/infrastructure/security/config/`**

Files to create:
- `SecurityConfig.java` - Main security configuration
- `CorsConfig.java` - CORS configuration
- `PasswordEncoderConfig.java` - Password encoder bean

### Step 3.4: Create User Details Service
**File: `src/main/java/com/gridtokenx/app/infrastructure/security/UserDetailsServiceImpl.java`**

## Phase 4: Database Layer Updates (Day 7)

### Step 4.1: Update JPA Entity
**File: `src/main/java/com/gridtokenx/app/infrastructure/persistence/entity/UserJpaEntity.java`**

Add authentication-related fields and relationships.

### Step 4.2: Create Refresh Token Entity
**File: `src/main/java/com/gridtokenx/app/infrastructure/persistence/entity/RefreshTokenJpaEntity.java`**

### Step 4.3: Update Repositories
**Files:**
- Update `src/main/java/com/gridtokenx/app/infrastructure/persistence/repository/UserJpaRepository.java`
- Create `src/main/java/com/gridtokenx/app/infrastructure/persistence/repository/RefreshTokenJpaRepository.java`

### Step 4.4: Create Database Migration
**File: `src/main/resources/db/migration/V2__add_authentication.sql`**

## Phase 5: Web Layer - Controllers (Day 8)

### Step 5.1: Create Authentication Controller
**File: `src/main/java/com/gridtokenx/app/infrastructure/web/controller/AuthController.java`**

Endpoints:
- POST `/api/v1/auth/login`
- POST `/api/v1/auth/register`
- POST `/api/v1/auth/refresh`
- POST `/api/v1/auth/logout`
- GET `/api/v1/auth/me`

### Step 5.2: Create Web DTOs
**Directory: `src/main/java/com/gridtokenx/app/infrastructure/web/dto/auth/`**

Files to create:
- `LoginRequest.java`
- `LoginResponse.java`
- `RegisterRequest.java`
- `RegisterResponse.java`
- `RefreshTokenRequest.java`
- `RefreshTokenResponse.java`

### Step 5.3: Update Existing Controllers
Add security annotations to existing UserController endpoints.

## Phase 6: Configuration and Properties (Day 9)

### Step 6.1: Update Application Properties
**File: `src/main/resources/application.properties`**

Add JWT and security configuration properties.

### Step 6.2: Create Configuration Classes
**Directory: `src/main/java/com/gridtokenx/app/config/`**

Files to create or update:
- `SecurityConfigProperties.java`
- `JwtConfigProperties.java`
- Update existing `DomainConfig.java`

## Phase 7: Error Handling Updates (Day 10)

### Step 7.1: Update Global Exception Handler
**File: `src/main/java/com/gridtokenx/app/infrastructure/web/exception/GlobalExceptionHandler.java`**

Add handlers for authentication exceptions.

### Step 7.2: Create Security-Specific Error Responses
Add methods to handle JWT and authentication errors.

## Phase 8: Testing (Day 11-12)

### Step 8.1: Unit Tests
Create unit tests for:
- Domain services
- Use cases
- JWT token provider
- Security configurations

### Step 8.2: Integration Tests
Create integration tests for:
- Authentication endpoints
- JWT filter functionality
- Security configuration
- Database operations

### Step 8.3: End-to-End Tests
Create e2e tests for:
- Complete authentication flows
- Protected endpoint access
- Token refresh scenarios

## Phase 9: Documentation and Deployment (Day 13-14)

### Step 9.1: API Documentation
Update API documentation with:
- Authentication endpoints
- Security requirements for existing endpoints
- JWT token format and usage

### Step 9.2: Security Documentation
Create documentation for:
- JWT configuration
- Security best practices
- Deployment security considerations

### Step 9.3: Migration Scripts
Create scripts for:
- Database migration
- Existing user migration
- Configuration deployment

## Implementation Priority Matrix

### High Priority (Must Have)
1. ✅ Domain layer authentication entities and services
2. ✅ Basic JWT token generation and validation
3. ✅ Login and registration endpoints
4. ✅ Security configuration for existing endpoints
5. ✅ Database schema updates

### Medium Priority (Should Have)
1. ✅ Refresh token functionality
2. ✅ User profile management
3. ✅ Role-based authorization
4. ✅ Comprehensive error handling
5. ✅ Unit and integration tests

### Low Priority (Could Have)
1. 🔄 Advanced security features (MFA, account lockout)
2. 🔄 Social login integration
3. 🔄 Advanced user management features
4. 🔄 Security monitoring and logging
5. 🔄 Performance optimizations

## Risk Mitigation Strategies

### Technical Risks
1. **Breaking Changes**: Implement feature flags and gradual rollout
2. **Security Vulnerabilities**: Follow OWASP guidelines and conduct security reviews
3. **Performance Impact**: Implement caching and optimize JWT validation
4. **Database Migration Issues**: Test migration scripts thoroughly

### Business Risks
1. **User Experience**: Provide smooth migration path for existing users
2. **Downtime**: Implement zero-downtime deployment strategies
3. **Adoption**: Provide clear documentation and migration guides

## Success Criteria

### Functional Requirements ✅
- [ ] Users can register and login with JWT tokens
- [ ] JWT tokens are properly validated on protected endpoints
- [ ] Refresh token functionality works correctly
- [ ] Role-based authorization is implemented
- [ ] Existing functionality remains unaffected

### Non-Functional Requirements ✅
- [ ] Authentication response time < 200ms
- [ ] JWT token validation time < 50ms
- [ ] System maintains 99.9% uptime during migration
- [ ] All security best practices are followed
- [ ] Comprehensive test coverage (>90%)

## Next Steps

1. **Start with Phase 1** - Domain layer foundation
2. **Review and approve** each phase before proceeding
3. **Conduct security review** after Phase 3
4. **Perform load testing** after Phase 6
5. **Plan migration strategy** before Phase 9

This roadmap provides a structured approach to implementing JWT authentication while maintaining Clean Architecture principles and ensuring security best practices.
