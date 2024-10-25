#!/bin/bash

# Exit script on any error
set -e

# Define necessary variables
DOCKER_COMPOSE_FILE="docker-compose.yml"

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo "Docker is not running. Please start Docker."
        exit 1
    fi
}

# Function to clean up Docker containers on exit
cleanup() {
    echo "Stopping Docker containers..."
    docker-compose -f $DOCKER_COMPOSE_FILE down
}

# Trap SIGINT (Ctrl+C) and call the cleanup function
trap cleanup SIGINT SIGTERM


# Step 1: Check Docker status
check_docker

# Step 2: Start Docker containers (including Python, Flink, and Kafka)
echo "Starting Docker containers..."
docker-compose -f $DOCKER_COMPOSE_FILE up -d

# Give Docker containers some time to spin up
echo "Waiting for Docker containers to initialize..."
sleep 10

# Step 3: Tail logs to verify that everything is working
echo "Tailing logs for all services..."
docker-compose logs -f

