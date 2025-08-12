# Clean Architecture Implementation

This project implements Clean Architecture principles with Spring Boot, following Domain-Driven Design (DDD) and SOLID principles.

## Architecture Overview

The application is organized in concentric layers:

```
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                Application Layer                      │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │               Domain Layer                      │  │  │
│  │  │                                                 │  │  │
│  │  │  • Entities (Business Objects)                 │  │  │
│  │  │  • Domain Services (Business Logic)            │  │  │
│  │  │  • Repository Interfaces                       │  │  │
│  │  │  • Domain Exceptions                           │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  │                                                       │  │
│  │  • Use Cases (Application Services)                  │  │
│  │  • Input/Output Ports                                │  │
│  │  • Application DTOs                                  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  • Web Controllers (REST API)                              │
│  • JPA Entities & Repositories                             │
│  • Configuration Classes                                   │
│  • Exception Handlers                                      │
└─────────────────────────────────────────────────────────────┘
```

## Layer Details

### 1. Domain Layer (`com.gridtokenx.app.domain`)
**Pure business logic, framework-independent**

- **Entities**: Core business objects with business rules
  - `User` - Domain entity with business logic
  
- **Repository Interfaces**: Contracts for data access
  - `UserRepository` - Domain repository interface
  
- **Domain Services**: Business logic that doesn't fit in entities
  - `UserDomainService` - Orchestrates user business operations
  
- **Exceptions**: Domain-specific exceptions
  - `DomainException` - Base domain exception
  - `UserNotFoundException` - User not found
  - `InvalidUserDataException` - Invalid user data

### 2. Application Layer (`com.gridtokenx.app.application`)
**Orchestrates business operations, coordinates with external systems**

- **Use Cases**: Application services implementing business use cases
  - `UserUseCase` - Implements user-related use cases
  
- **Ports**: Interfaces for external communication
  - `UserInputPort` - Defines what the application can do
  - `UserOutputPort` - Defines how to interact with external systems
  
- **DTOs**: Data transfer objects for application layer
  - `UserDto` - Application layer user data transfer
  - `CreateUserDto` - Create user request data
  - `UpdateUserDto` - Update user request data

### 3. Infrastructure Layer (`com.gridtokenx.app.infrastructure`)
**Framework-specific implementations, external concerns**

- **Web Layer**: REST API implementation
  - `UserController` - REST endpoints for user operations
  - Web DTOs for request/response mapping
  - `GlobalExceptionHandler` - Exception handling for web layer
  
- **Persistence Layer**: Database implementation
  - `UserJpaEntity` - JPA entity for database mapping
  - `UserJpaRepository` - Spring Data JPA repository
  - `UserRepositoryAdapter` - Adapter implementing domain repository
  - `UserJpaMapper` - Maps between domain and JPA entities

## Key Principles Applied

### 1. **Dependency Inversion Principle**
- Inner layers define interfaces
- Outer layers implement those interfaces
- Dependencies point inward

### 2. **Single Responsibility Principle**
- Each class has one reason to change
- Clear separation of concerns

### 3. **Open/Closed Principle**
- Open for extension, closed for modification
- Use of interfaces and adapters

### 4. **Interface Segregation Principle**
- Specific interfaces rather than general-purpose ones
- Input/Output ports are focused

### 5. **Liskov Substitution Principle**
- Implementations can be substituted without breaking functionality

## Benefits

1. **Framework Independence**: Domain logic is not coupled to Spring Boot
2. **Database Independence**: Can switch databases without changing business logic
3. **Testability**: Each layer can be tested independently
4. **Maintainability**: Clear boundaries and responsibilities
5. **Flexibility**: Easy to modify or extend without affecting other layers

## API Endpoints

### User Management
- `POST /api/v1/users` - Create user
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users/username/{username}` - Get user by username
- `GET /api/v1/users/email/{email}` - Get user by email
- `GET /api/v1/users` - Get all users (supports `activeOnly` parameter)
- `PUT /api/v1/users/{id}` - Update user
- `PATCH /api/v1/users/{id}/activate` - Activate user
- `PATCH /api/v1/users/{id}/deactivate` - Deactivate user
- `DELETE /api/v1/users/{id}` - Delete user

## Running the Application

1. **Start PostgreSQL**: `docker-compose up postgres -d`
2. **Run Application**: `./mvnw spring-boot:run`
3. **Test API**: `curl http://localhost:8080/actuator/health`

## Testing Example

Create a user:
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

## Architecture Benefits in Practice

- **Easy to test**: Mock interfaces, not implementations
- **Easy to change**: Modify one layer without affecting others
- **Easy to understand**: Clear responsibility boundaries
- **Easy to extend**: Add new features without breaking existing code
- **Framework agnostic**: Business logic survives framework changes
