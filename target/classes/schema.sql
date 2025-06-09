-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE
);

-- Create projects table
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    domain VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create sessions table
CREATE TABLE IF NOT EXISTS sessions (
    id BIGSERIAL PRIMARY KEY,
    page_url VARCHAR(2048) NOT NULL,
    project_id VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    anonymized_ip VARCHAR(45)
);

-- Create mouse_events table
CREATE TABLE IF NOT EXISTS mouse_events (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    type VARCHAR(10) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    element_id VARCHAR(255),
    element_class VARCHAR(255),
    element_tag VARCHAR(50),
    FOREIGN KEY (session_id) REFERENCES sessions(id)
); 