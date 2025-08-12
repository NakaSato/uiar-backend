#!/bin/bash

# Spring Boot Application Startup Script
# This script helps resolve common startup issues

echo "🚀 Starting UIAR Backend Application..."

# Check if PostgreSQL is running via Docker
echo "📋 Checking database availability..."

if ! docker ps | grep -q uiar-postgres; then
    echo "⚠️  PostgreSQL container not running. Starting database..."
    cd /Users/wit/Developments/UIAR/backend
    docker-compose up -d postgres
    
    echo "⏳ Waiting for PostgreSQL to be ready..."
    sleep 10
    
    # Wait for database to be healthy
    while ! docker exec uiar-postgres pg_isready -U uiar_user -d uiar_db >/dev/null 2>&1; do
        echo "⏳ Waiting for database connection..."
        sleep 2
    done
    
    echo "✅ Database is ready!"
else
    echo "✅ PostgreSQL container is already running"
fi

# Clean and compile the application
echo "🔧 Cleaning and compiling application..."
./mvnw clean compile

# Run the application
echo "🏃 Starting Spring Boot application..."
./mvnw spring-boot:run
