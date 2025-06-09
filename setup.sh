#!/bin/bash

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    echo "PostgreSQL is not installed. Please install it first."
    exit 1
fi

# Create database
echo "Creating database..."
createdb cursorheat

# Set environment variables
echo "Setting up environment variables..."
export JWT_SECRET=$(openssl rand -base64 32)
echo "JWT_SECRET has been set to a random value"

# Build the application
echo "Building the application..."
mvn clean install

# Run the application
echo "Starting the application..."
mvn spring-boot:run 