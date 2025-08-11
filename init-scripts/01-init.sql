-- Initial database setup script
-- This script will be executed when the PostgreSQL container starts for the first time

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Create initial tables or insert seed data here
-- Example:
-- CREATE TABLE IF NOT EXISTS users (
--     id BIGSERIAL PRIMARY KEY,
--     username VARCHAR(255) UNIQUE NOT NULL,
--     email VARCHAR(255) UNIQUE NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE uiar_db TO uiar_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO uiar_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO uiar_user;
