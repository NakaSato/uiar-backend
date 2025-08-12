#!/bin/bash

# JPA Configuration Verification Script
# This script helps verify that the JPA configuration fixes are working

echo "🔍 JPA Configuration Verification"
echo "================================="

# Check 1: Verify JPA annotations are present
echo "✅ Check 1: JPA Annotations"
if grep -q "@EnableJpaRepositories" src/main/java/com/gridtokenx/app/AppApplication.java; then
    echo "   ✓ @EnableJpaRepositories found"
else
    echo "   ❌ @EnableJpaRepositories missing"
fi

if grep -q "@EntityScan" src/main/java/com/gridtokenx/app/AppApplication.java; then
    echo "   ✓ @EntityScan found"
else
    echo "   ❌ @EntityScan missing"
fi

# Check 2: Verify JPA configuration class exists
echo -e "\n✅ Check 2: JPA Configuration Class"
if [ -f "src/main/java/com/gridtokenx/app/infrastructure/config/JpaConfig.java" ]; then
    echo "   ✓ JpaConfig.java exists"
else
    echo "   ❌ JpaConfig.java missing"
fi

# Check 3: Verify database migration files
echo -e "\n✅ Check 3: Database Migrations"
if [ -f "src/main/resources/db/migration/V1__Create_users_table.sql" ]; then
    echo "   ✓ V1 migration exists"
else
    echo "   ❌ V1 migration missing"
fi

if [ -f "src/main/resources/db/migration/V2__Fix_users_table_schema.sql" ]; then
    echo "   ✓ V2 migration exists (schema fix)"
else
    echo "   ❌ V2 migration missing"
fi

# Check 4: Verify repository adapter annotation
echo -e "\n✅ Check 4: Repository Adapter"
if grep -q "@Component" src/main/java/com/gridtokenx/app/infrastructure/persistence/repository/UserRepositoryAdapter.java; then
    echo "   ✓ UserRepositoryAdapter uses @Component"
else
    echo "   ❌ UserRepositoryAdapter should use @Component"
fi

# Check 5: Verify JPA dependencies in pom.xml
echo -e "\n✅ Check 5: Maven Dependencies"
if grep -q "spring-boot-starter-data-jpa" pom.xml; then
    echo "   ✓ Spring Data JPA dependency found"
else
    echo "   ❌ Spring Data JPA dependency missing"
fi

if grep -q "postgresql" pom.xml; then
    echo "   ✓ PostgreSQL driver found"
else
    echo "   ❌ PostgreSQL driver missing"
fi

# Check 6: Verify application properties
echo -e "\n✅ Check 6: Application Configuration"
for prop_file in "application.properties" "application-docker.properties" "application-prod.properties"; do
    if [ -f "src/main/resources/$prop_file" ]; then
        echo "   ✓ $prop_file exists"
        if grep -q "spring.datasource.url" "src/main/resources/$prop_file"; then
            echo "     ✓ Database URL configured"
        fi
    else
        echo "   ❌ $prop_file missing"
    fi
done

# Check 7: Compile test
echo -e "\n✅ Check 7: Compilation Test"
echo "   Running compilation test..."
if ./mvnw compile -q; then
    echo "   ✓ Application compiles successfully"
else
    echo "   ❌ Compilation failed"
fi

echo -e "\n🎯 Summary"
echo "==========="
echo "If all checks pass, the JPA configuration issue should be resolved."
echo "Next steps:"
echo "1. Start PostgreSQL database: docker-compose up -d db"
echo "2. Run the application: ./mvnw spring-boot:run"
echo "3. Check health endpoint: curl http://localhost:8080/actuator/health"
echo "4. Test user endpoints: curl http://localhost:8080/api/v1/users"
