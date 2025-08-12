# UIAR Backend Development Guide

## Project Overview
University Institutional Academic Repository (UIAR) - A Spring Boot 3.5.4 backend for managing academic publications and research using Clean Architecture principles with Java 17, PostgreSQL, and JWT authentication.

## Architecture Pattern - Clean Architecture
```
src/main/java/com/gridtokenx/app/
├── domain/          # Core business logic (entities, repositories interfaces)
├── application/     # Use cases, DTOs, ports (service interfaces)
├── infrastructure/  # External adapters (web, persistence, security)
└── config/         # Cross-cutting configuration
```

**Key Rule**: Dependencies flow inward only. Domain never depends on infrastructure.

## Essential Patterns

### 1. Domain Entities
- Located in `domain/entity/` (e.g., `User.java`)
- Pure business objects with no framework dependencies
- Implement business logic and validation rules
- Use Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

### 2. Repository Pattern
- Domain interfaces in `domain/repository/` (e.g., `UserRepository.java`)
- JPA implementations in `infrastructure/persistence/repository/` (e.g., `UserJpaRepository.java`)
- JPA entities in `infrastructure/persistence/entity/` (e.g., `UserJpaEntity.java`)

### 3. Service Layer Split
- **Application Services**: `application/service/` - orchestrate use cases, no business logic
- **Domain Services**: `domain/service/` - contain business logic that doesn't belong to entities
- **Infrastructure Services**: `infrastructure/service/` - technical concerns (JWT, password hashing)

### 4. Controller Pattern
- All in `infrastructure/web/controller/`
- Use `@RestController`, `@RequestMapping("/api/v1/{resource}")`
- Inject application layer ports via `UserInputPort`, not services directly
- Convert between web DTOs and application DTOs

## Technology Stack Specifics

### Database & Migrations
- **Flyway migrations**: `src/main/resources/db/migration/V{version}__{description}.sql`
- **Multi-environment configs**: 
  - `application.properties` (Supabase)
  - `application-docker.properties` (local Docker)
  - `application-prod.properties` (production)

### JWT Authentication
- Configuration in `application.properties`: `jwt.secret`, `jwt.access-token-expiration`
- Implementation follows complete guide in `docs/09 JWT_COMPLETE_IMPLEMENTATION_GUIDE.md`
- Token blacklisting via `JwtBlacklistService`

### Testing Strategy
- **Unit tests**: `@SpringBootTest` with `@ActiveProfiles("test")`
- **Integration tests**: Maven profile `integration-test`
- **Test containers**: PostgreSQL for integration tests
- **H2**: In-memory database for unit tests

## Build & Run Commands

### Local Development
```bash
# Database first
docker-compose up -d postgres

# Run application (with automatic DB setup)
./start-app.sh

# Manual build and run
./mvnw clean compile spring-boot:run

# Production build
./mvnw clean package -Pprod
```

### Testing
```bash
# Unit tests
./mvnw test

# Integration tests
./mvnw verify -Pintegration-test

# All tests with output
./mvnw clean test > all-tests-output.log 2>&1
```

## Key Configuration Files

### Maven Profiles
- **Default**: Development with Supabase
- **`prod`**: Production optimizations (`spring.profiles.active=prod`)
- **`integration-test`**: Runs Failsafe plugin for integration tests

### Database Configuration
- **Local**: Docker Compose with PostgreSQL 15-alpine
- **Cloud**: Supabase PostgreSQL (configured in default profile)
- **Connection**: `jdbc:postgresql://...` with specific user/password per environment

## Common Development Workflows

### Adding New Entity
1. Create domain entity in `domain/entity/`
2. Define repository interface in `domain/repository/`
3. Create JPA entity in `infrastructure/persistence/entity/`
4. Implement JPA repository in `infrastructure/persistence/repository/`
5. Add Flyway migration in `src/main/resources/db/migration/`

### Adding New Endpoint
1. Create DTOs in `application/dto/` and `infrastructure/web/dto/`
2. Define use case in `application/usecase/`
3. Create controller in `infrastructure/web/controller/`
4. Add route with `@RequestMapping("/api/v1/{resource}")`

### Security Integration
- All protected endpoints require JWT tokens
- Use `@PreAuthorize("hasRole('ROLE_ADMIN')")` for role-based access
- Configure in `infrastructure/security/` package

## Project-Specific Notes
- **Base package**: `com.gridtokenx.app`
- **Main class**: `AppApplication.java`
- **Database name**: `uiar_db` (Docker), `postgres` (Supabase)
- **Default admin**: username=`admin`, password=`admin123` (change in production)
- **API prefix**: All endpoints start with `/api/v1/`
- **Documentation**: Extensive guides in `docs/` directory for system scope, architecture, and JWT implementation

## Important Dependencies
- **Spring Boot 3.5.4** with Spring Security, JPA, Validation
- **JWT**: `io.jsonwebtoken:jjwt-*:0.12.3`
- **Database**: PostgreSQL driver, Flyway 9.22.3
- **Caching**: Caffeine cache
- **Testing**: TestContainers, H2 for tests
- **Build**: Maven with specific annotation processor paths for Lombok
