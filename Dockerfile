
# Stage 1: Build stage
FROM eclipse-temurin:17-jdk AS build

# Set build arguments
ARG MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
ARG SKIP_TESTS=true

# Set working directory
WORKDIR /app

# Copy Maven wrapper and configuration files
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN if [ "$SKIP_TESTS" = "true" ]; then \
        ./mvnw clean package -DskipTests -B; \
    else \
        ./mvnw clean package -B; \
    fi

# Stage 2: Runtime stage (minimal and secure)
FROM eclipse-temurin:17-jre

# Metadata
LABEL maintainer="UIAR Backend Team"
LABEL version="1.0.0"
LABEL description="UIAR Backend Spring Boot Application"

# Install essential tools for production monitoring
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    bash \
    netcat-openbsd \
    tzdata && \
    # Set timezone
    ln -sf /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone && \
    # Clean up
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -g 1001 appuser && \
    useradd -r -u 1001 -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR file from build stage
COPY --from=build /app/target/*.jar app.jar

# Create necessary directories and set permissions
RUN mkdir -p /app/logs /app/tmp && \
    chown -R appuser:appuser /app && \
    chmod 755 /app && \
    chmod 644 /app/app.jar

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check for container orchestration
HEALTHCHECK --interval=30s \
            --timeout=10s \
            --start-period=60s \
            --retries=3 \
            CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables for JVM optimization
ENV JAVA_OPTS="-server \
               -XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -XX:+UseCompressedOops \
               -Djava.security.egd=file:/dev/./urandom \
               -Djava.awt.headless=true \
               -Dfile.encoding=UTF-8 \
               -Duser.timezone=UTC"

# Spring Boot specific environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Application startup command
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

# Default command (can be overridden)
CMD []