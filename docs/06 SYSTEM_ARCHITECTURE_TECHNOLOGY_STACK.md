# System Architecture and Technology Stack

## 6. Overview
This document defines the comprehensive system architecture and technology stack for the University Institutional Academic Repository (UIAR) system. It outlines the architectural decisions, technology choices, justifications, and implementation strategies that will guide the development and deployment of the system.

### Related Documentation
- **System Requirements**: Architectural requirements from [System Scope](./01%20SYSTEM_SCOPE.md#143-technical-boundaries)
- **User Needs**: Architecture supporting user requirements from [User Types, Personas, and Characteristics](./02%20USER_TYPES_PERSONAS_CHARACTERISTICS.md)
- **Functional Support**: Architecture enabling functional requirements from [Functional Requirements](./03%20FUNCTIONAL_REQUIREMENTS.md)
- **Data Architecture**: Database and data layer design from [Data and Information Architecture](./04%20DATA_INFORMATION_ARCHITECTURE.md)
- **Quality Attributes**: Architecture meeting NFRs from [Non-Functional Requirements](./05%20NON_FUNCTIONAL_REQUIREMENTS.md)
- **Implementation Plan**: Architecture development phases from [Phased Development Roadmap](./07%20PHASED_DEVELOPMENT_ROADMAP.md)

## 6.1. Architectural Model

### 6.1.1. Client-Server Architecture
The system will be built on a **Client-Server Architecture** that provides clear separation of concerns and enables independent development, testing, and scaling of different system components.

```mermaid
graph TB
    subgraph "Client Tier"
        A[React SPA]
        B[Public Website]
        C[Admin Dashboard]
        D[Faculty Portal]
    end
    
    subgraph "Load Balancer"
        E[NGINX/ALB]
    end
    
    subgraph "Application Tier"
        F[Spring Boot API]
        G[Authentication Service]
        H[File Service]
        I[Search Service]
    end
    
    subgraph "Data Tier"
        J[PostgreSQL Database]
        K[Redis Cache]
        L[Elasticsearch]
        M[AWS S3 Storage]
    end
    
    A --> E
    B --> E
    C --> E
    D --> E
    E --> F
    E --> G
    E --> H
    E --> I
    F --> J
    F --> K
    I --> L
    H --> M
```

### 6.1.2. Backend Architecture (Monolithic Spring Boot)
**Design Decision**: The Backend will be a monolithic application built with Spring Boot, serving a stateless RESTful API.

**Architectural Benefits**:
- **Simplicity**: Single deployable unit reduces operational complexity
- **Development Speed**: Faster initial development and testing
- **Consistency**: Unified technology stack and development patterns
- **Transaction Management**: Simplified ACID transactions across components
- **Debugging**: Easier troubleshooting with centralized logging

**Monolithic Structure**:
```
src/main/java/com/gridtokenx/app/
├── controller/          # REST API endpoints
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data transfer objects
├── config/            # Configuration classes
├── security/          # Security configuration
├── exception/         # Exception handling
└── util/              # Utility classes
```

### 6.1.3. Frontend Architecture (React SPA)
**Design Decision**: The Frontend will be a Single-Page Application (SPA) built with React.

**SPA Benefits**:
- **User Experience**: Smooth, app-like user interactions
- **Performance**: Reduced server requests after initial load
- **Offline Capabilities**: Potential for offline functionality
- **State Management**: Centralized application state
- **Component Reusability**: Modular, reusable UI components

**React Application Structure**:
```
src/
├── components/         # Reusable UI components
├── pages/             # Route-based page components
├── hooks/             # Custom React hooks
├── services/          # API service layer
├── store/             # State management (Redux/Zustand)
├── utils/             # Utility functions
├── types/             # TypeScript type definitions
└── assets/            # Static assets
```

### 6.1.4. Separation Benefits
This architectural separation provides several key advantages:

- **Independent Development**: Frontend and backend teams can work in parallel
- **Technology Flexibility**: Each tier can evolve independently
- **Scalability**: Different scaling strategies for different tiers
- **Testing**: Isolated testing of frontend and backend components
- **Deployment**: Independent deployment cycles and rollback capabilities

## 6.2. Technology Stack Justification

### 6.2.1. Frontend Technology Stack

#### 6.2.1.1. React + TypeScript
**React Selection Rationale**:
- **Component-Based Architecture**: Ideal for building complex, interactive UIs like search pages and dashboards
- **Ecosystem Maturity**: Vast ecosystem with extensive community support and libraries
- **Performance**: Virtual DOM optimization for efficient UI updates
- **Developer Experience**: Excellent debugging tools and development workflow
- **Market Adoption**: Large talent pool and industry standard for SPA development

**TypeScript Integration Benefits**:
- **Static Typing**: Significantly reduces runtime errors and improves code reliability
- **Developer Productivity**: Enhanced IDE support with autocomplete and refactoring
- **Code Maintainability**: Self-documenting code with explicit interfaces and types
- **Team Collaboration**: Clear contracts between components and API integration
- **Scalability**: Better handling of large codebases with type safety

**Implementation Example**:
```typescript
// TypeScript interface for type safety
interface Publication {
  id: number;
  title: string;
  abstract?: string;
  authors: Author[];
  publicationDate: Date;
  status: PublicationStatus;
}

interface PublicationListProps {
  publications: Publication[];
  onPublicationSelect: (publication: Publication) => void;
  loading?: boolean;
}

// React component with TypeScript
const PublicationList: React.FC<PublicationListProps> = ({
  publications,
  onPublicationSelect,
  loading = false
}) => {
  return (
    <div className="publication-list">
      {loading ? (
        <LoadingSpinner />
      ) : (
        publications.map(publication => (
          <PublicationCard
            key={publication.id}
            publication={publication}
            onClick={() => onPublicationSelect(publication)}
          />
        ))
      )}
    </div>
  );
};
```

#### 6.2.1.2. UI Library Selection (Mantine/Material-UI)
**UI Library Benefits**:
- **Development Acceleration**: Pre-built, tested components reduce development time
- **Consistency**: Unified design system across the application
- **Accessibility**: Built-in WCAG compliance and screen reader support
- **Professional Appearance**: Modern, polished interface design
- **Customization**: Themeable components that match university branding

**Component Library Comparison**:

| Feature | Mantine | Material-UI (MUI) | Ant Design |
|---------|---------|-------------------|------------|
| Bundle Size | Moderate | Large | Large |
| Customization | Excellent | Good | Limited |
| Accessibility | Excellent | Excellent | Good |
| TypeScript Support | Native | Excellent | Good |
| Documentation | Excellent | Excellent | Good |
| Academic Use Cases | ✅ | ✅ | ⚠️ |

**Recommended Choice**: **Mantine** for its excellent TypeScript support, lightweight bundle, and academic-friendly components.

### 6.2.2. Backend Technology Stack

#### 6.2.2.1. Spring Boot + Java
**Spring Boot Selection Rationale**:
- **Enterprise-Grade Security**: Spring Security provides comprehensive authentication and authorization
- **Rapid Development**: Opinionated configuration reduces boilerplate and accelerates setup
- **Ecosystem Integration**: Seamless integration with databases, caching, and external services
- **Production Ready**: Built-in monitoring, health checks, and metrics
- **Maintainability**: Convention over configuration approach simplifies maintenance

**Java Language Benefits**:
- **Maturity**: Proven track record in enterprise applications
- **Performance**: High-performance JVM with excellent garbage collection
- **Type Safety**: Strong typing reduces runtime errors
- **Multithreading**: Excellent support for concurrent operations
- **Talent Pool**: Large availability of experienced Java developers
- **Tooling**: Excellent IDE support and debugging capabilities

**Spring Boot Configuration Example**:
```java
@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
public class UiarApplication {
    public static void main(String[] args) {
        SpringApplication.run(UiarApplication.class, args);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return mapper;
    }
}
```

#### 6.2.2.2. Spring Security + JWT Authentication
**Authentication Strategy Justification**:
- **Industry Standard**: Battle-tested framework for enterprise security
- **Stateless Design**: JWT tokens enable horizontal scaling
- **Fine-Grained Authorization**: Method-level and URL-based security
- **Integration**: Seamless integration with Spring Boot ecosystem
- **Flexibility**: Support for multiple authentication methods

**Security Implementation**:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/publications/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        
        return http.build();
    }
}
```

### 6.2.3. Database Technology Stack

#### 6.2.3.1. PostgreSQL Database
**PostgreSQL Selection Rationale**:
- **Reliability**: ACID compliance and proven stability in production environments
- **Complex Queries**: Advanced SQL features for complex academic data relationships
- **Full-Text Search**: Built-in full-text search capabilities for publication content
- **JSON Support**: Native JSON support for flexible metadata storage
- **Scalability**: Excellent performance with large datasets
- **Open Source**: No licensing costs with strong community support

**PostgreSQL Features Utilized**:
```sql
-- Full-text search capability
CREATE INDEX idx_publications_fts ON publications 
USING gin(to_tsvector('english', title || ' ' || COALESCE(abstract, '')));

-- JSON metadata storage
ALTER TABLE profiles ADD COLUMN metadata JSONB;
CREATE INDEX idx_profiles_metadata ON profiles USING gin(metadata);

-- Advanced indexing for performance
CREATE INDEX idx_publications_composite ON publications(status, visibility, publication_date DESC);

-- Partial indexes for optimization
CREATE INDEX idx_published_publications ON publications(publication_date DESC) 
WHERE status = 'PUBLISHED' AND visibility = 'PUBLIC';
```

#### 6.2.3.2. Redis Caching Layer
**Redis Integration Benefits**:
- **Performance**: Sub-millisecond response times for cached data
- **Session Storage**: Distributed session management for horizontal scaling
- **Rate Limiting**: API rate limiting and abuse prevention
- **Cache Invalidation**: Sophisticated cache invalidation strategies

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

#### 6.2.3.3. Elasticsearch (Optional Enhancement)
**Search Enhancement**:
- **Advanced Search**: Sophisticated search queries with faceting
- **Performance**: High-performance search across large document collections
- **Relevance**: Configurable relevance scoring and boosting
- **Analytics**: Search analytics and usage patterns

### 6.2.4. File Storage Solution

#### 6.2.4.1. AWS S3 (Primary) or Equivalent
**Object Storage Selection Rationale**:
- **Scalability**: Virtually unlimited storage capacity
- **Durability**: 99.999999999% (11 9's) durability SLA
- **Security**: Fine-grained access controls and encryption
- **Cost-Effectiveness**: Pay-per-use pricing model
- **Integration**: Excellent SDK integration with Java/Spring Boot
- **CDN Integration**: CloudFront integration for global content delivery

**S3 Integration Implementation**:
```java
@Service
public class S3FileStorageService implements FileStorageService {
    
    private final AmazonS3 s3Client;
    
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    
    public String uploadFile(MultipartFile file, String folder) {
        String key = generateFileKey(folder, file.getOriginalFilename());
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
        
        try {
            PutObjectRequest request = new PutObjectRequest(
                bucketName, key, file.getInputStream(), metadata
            );
            
            s3Client.putObject(request);
            return key;
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload file", e);
        }
    }
    
    public PresignedUrlRequest generateDownloadUrl(String fileKey, Duration expiration) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
            bucketName, fileKey, HttpMethod.GET
        );
        request.setExpiration(Date.from(Instant.now().plus(expiration)));
        
        return s3Client.generatePresignedUrl(request);
    }
}
```

### 6.2.5. Deployment Strategy

#### 6.2.5.1. Frontend Deployment (Vercel/Netlify)
**Static Site Hosting Benefits**:
- **Global CDN**: Worldwide content distribution for optimal performance
- **CI/CD Integration**: Automatic deployments from Git repositories
- **Branch Previews**: Preview deployments for feature branches
- **SSL/TLS**: Automatic HTTPS certificate management
- **Edge Functions**: Serverless functions for dynamic content

**Deployment Configuration**:
```json
// vercel.json
{
  "version": 2,
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": {
        "distDir": "dist"
      }
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ],
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "X-Content-Type-Options",
          "value": "nosniff"
        },
        {
          "key": "X-Frame-Options",
          "value": "DENY"
        }
      ]
    }
  ]
}
```

#### 6.2.5.2. Backend Deployment (AWS/Render)
**Cloud Deployment Options**:

| Platform | Pros | Cons | Use Case |
|----------|------|------|----------|
| AWS Elastic Beanstalk | Full AWS integration, scalable | Complex setup | Enterprise deployment |
| AWS ECS/Fargate | Container-based, highly scalable | Requires container expertise | Microservices evolution |
| Render | Simple setup, automatic scaling | Limited customization | Rapid deployment |
| Railway | Developer-friendly, simple pricing | Newer platform | Development/staging |

**Docker Configuration**:
```dockerfile
# Multi-stage build for optimized production image
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Security: Run as non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 6.3. API Endpoint Requirements

*Cross-Reference: API requirements support functional features from [Functional Requirements §3](./03%20FUNCTIONAL_REQUIREMENTS.md)*

### 6.3.1. RESTful API Design Principles
The API follows REST architectural principles with consistent patterns:
- **Resource-Based URLs**: Clear resource identification
- **HTTP Method Semantics**: Proper use of GET, POST, PUT, DELETE
- **Stateless Communication**: Each request contains all necessary information
- **Standard HTTP Status Codes**: Consistent error handling
- **JSON Payload**: Standardized request/response format

### 6.3.2. Complete API Specification

*Note: Authentication and authorization align with user roles from [User Types §2.1](./02%20USER_TYPES_PERSONAS_CHARACTERISTICS.md#21-user-role-definitions)*

| HTTP Method | Endpoint Path | Description | Authentication | Authorization |
|-------------|---------------|-------------|----------------|---------------|
| **Public Endpoints** |||||
| GET | `/api/publications` | Retrieve paginated list of published works | No | Public |
| GET | `/api/publications/search` | Advanced search and filtering | No | Public |
| GET | `/api/publications/{id}` | Retrieve details of a single publication | No | Public |
| GET | `/api/publications/{id}/download` | Download publication file | No | Public* |
| GET | `/api/faculty/{id}` | Retrieve faculty member's public profile | No | Public |
| GET | `/api/departments` | List all departments | No | Public |
| GET | `/api/departments/{id}` | Retrieve department details | No | Public |
| **Authentication Endpoints** |||||
| POST | `/api/auth/login` | Authenticate user and receive JWT | No | Public |
| POST | `/api/auth/refresh` | Refresh JWT token | Yes | Any |
| POST | `/api/auth/logout` | Invalidate JWT token | Yes | Any |
| POST | `/api/auth/forgot-password` | Request password reset | No | Public |
| POST | `/api/auth/reset-password` | Reset password with token | No | Public |
| **Faculty Endpoints** |||||
| GET | `/api/faculty/profile` | Get current user's profile | Yes | Faculty/Admin |
| PUT | `/api/faculty/profile` | Update current user's profile | Yes | Faculty/Admin |
| GET | `/api/faculty/publications` | Get current user's publications | Yes | Faculty/Admin |
| POST | `/api/faculty/publications` | Create new publication | Yes | Faculty/Admin |
| PUT | `/api/faculty/publications/{id}` | Update existing publication | Yes | Owner/Admin |
| DELETE | `/api/faculty/publications/{id}` | Delete publication | Yes | Owner/Admin |
| PATCH | `/api/faculty/publications/{id}/status` | Change publication status | Yes | Owner/Admin |
| POST | `/api/faculty/publications/{id}/upload` | Upload publication file | Yes | Owner/Admin |
| GET | `/api/faculty/analytics` | Get publication analytics | Yes | Faculty/Admin |
| **Administrative Endpoints** |||||
| GET | `/api/admin/users` | List all users | Yes | Admin |
| POST | `/api/admin/users` | Create new user | Yes | Admin |
| PUT | `/api/admin/users/{id}` | Update user details | Yes | Admin |
| DELETE | `/api/admin/users/{id}` | Disable user account | Yes | Admin |
| GET | `/api/admin/publications` | List all publications | Yes | Admin |
| PUT | `/api/admin/publications/{id}` | Update any publication | Yes | Admin |
| GET | `/api/admin/analytics` | System-wide analytics | Yes | Admin |
| POST | `/api/admin/featured` | Manage featured content | Yes | Admin |
| **System Endpoints** |||||
| GET | `/actuator/health` | System health check | No | Public |
| GET | `/actuator/info` | System information | No | Public |
| GET | `/actuator/metrics` | System metrics | Yes | Admin |

*Note: File downloads may require authentication based on publication visibility settings.

### 6.3.3. API Request/Response Examples

#### 6.3.3.1. Publication Search
```http
GET /api/publications/search?query=machine%20learning&type=JOURNAL_ARTICLE&year=2024&department=Computer%20Science&page=0&size=20
```

**Response**:
```json
{
  "content": [
    {
      "id": 123,
      "title": "Advances in Machine Learning for Healthcare",
      "abstract": "This paper presents novel approaches...",
      "authors": [
        {
          "id": 456,
          "name": "Dr. Jane Smith",
          "affiliation": "Computer Science Department",
          "isCorresponding": true
        }
      ],
      "publicationDate": "2024-03-15",
      "publicationType": "JOURNAL_ARTICLE",
      "journal": "Journal of AI Research",
      "doi": "10.1234/jair.2024.123",
      "keywords": ["machine learning", "healthcare", "AI"],
      "downloadCount": 145,
      "viewCount": 1203
    }
  ],
  "page": {
    "size": 20,
    "number": 0,
    "totalElements": 1,
    "totalPages": 1
  },
  "facets": {
    "departments": {
      "Computer Science": 15,
      "Engineering": 8,
      "Medicine": 5
    },
    "publicationTypes": {
      "JOURNAL_ARTICLE": 18,
      "CONFERENCE_PAPER": 7,
      "BOOK_CHAPTER": 3
    },
    "years": {
      "2024": 12,
      "2023": 10,
      "2022": 6
    }
  }
}
```

#### 6.3.3.2. Create Publication
```http
POST /api/faculty/publications
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Novel Approaches to Quantum Computing",
  "abstract": "This research explores new methodologies...",
  "publicationType": "JOURNAL_ARTICLE",
  "keywords": ["quantum computing", "algorithms", "optimization"],
  "authors": [
    {
      "profileId": 789,
      "order": 1,
      "isCorresponding": true
    },
    {
      "externalAuthorName": "Dr. External Collaborator",
      "externalAuthorAffiliation": "MIT",
      "order": 2,
      "isCorresponding": false
    }
  ],
  "journal": "Nature Quantum Information",
  "publicationDate": "2024-06-01",
  "doi": "10.1038/s41534-024-00123-4"
}
```

#### 6.3.3.3. Error Response Format
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "title",
        "message": "Title is required and must be between 5 and 500 characters"
      },
      {
        "field": "publicationType",
        "message": "Publication type must be one of: JOURNAL_ARTICLE, CONFERENCE_PAPER, BOOK_CHAPTER, TECHNICAL_REPORT"
      }
    ],
    "timestamp": "2024-08-12T10:30:00Z",
    "path": "/api/faculty/publications"
  }
}
```

### 6.3.4. API Documentation Strategy
```java
// OpenAPI documentation configuration
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("UIAR API")
                .version("1.0.0")
                .description("University Institutional Academic Repository API")
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@university.edu"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
```

## 6.4. Integration Architecture

### 6.4.1. External System Integration
```mermaid
graph LR
    subgraph "UIAR System"
        A[Spring Boot API]
        B[Database]
        C[File Storage]
    end
    
    subgraph "External Systems"
        D[University SSO]
        E[DOI Resolution Service]
        F[ORCID API]
        G[Citation Services]
        H[Analytics Services]
    end
    
    A --> D
    A --> E
    A --> F
    A --> G
    A --> H
```

### 6.4.2. Future Microservices Migration Path
While starting with a monolithic architecture, the system is designed to enable future migration to microservices:

```mermaid
graph TB
    subgraph "Phase 1: Monolith"
        A[Spring Boot Application]
    end
    
    subgraph "Phase 2: Modular Monolith"
        B[User Service Module]
        C[Publication Service Module]
        D[Search Service Module]
        E[File Service Module]
    end
    
    subgraph "Phase 3: Microservices"
        F[User Service]
        G[Publication Service]
        H[Search Service]
        I[File Service]
        J[API Gateway]
    end
    
    A --> B
    A --> C
    A --> D
    A --> E
    
    B --> F
    C --> G
    D --> H
    E --> I
```

---

**Document Version**: 1.0  
**Last Updated**: August 12, 2025  
**Next Review**: September 12, 2025

## Related Documentation

### Core Documentation Suite
1. **[System Scope](./01%20SYSTEM_SCOPE.md)** - System boundaries and technical constraints
2. **[User Types, Personas, and Characteristics](./02%20USER_TYPES_PERSONAS_CHARACTERISTICS.md)** - User requirements driving architecture decisions
3. **[Functional Requirements](./03%20FUNCTIONAL_REQUIREMENTS.md)** - Features requiring architectural support
4. **[Data and Information Architecture](./04%20DATA_INFORMATION_ARCHITECTURE.md)** - Data layer design and database architecture
5. **[Non-Functional Requirements](./05%20NON_FUNCTIONAL_REQUIREMENTS.md)** - Quality attributes and architectural constraints
6. **[Phased Development Roadmap](./07%20PHASED_DEVELOPMENT_ROADMAP.md)** - Architecture implementation timeline

### Key Cross-References
- **Technology Justification**: Architecture choices support NFRs in Document 05
- **API Design**: Endpoints enable functional requirements in Document 03
- **Security Architecture**: Implementation of security requirements in Document 05
- **Data Integration**: Database design supports data architecture in Document 04
- **User Experience**: Architecture supports user requirements in Document 02
- **Implementation Strategy**: Development approach detailed in Document 07

**Implementation Priority**: Critical - Technical foundation for all development  
**Next Review**: September 12, 2025  
**Related Documents**: All previous architecture and requirements documents  
**Implementation Priority**: Critical - Foundation for development work
