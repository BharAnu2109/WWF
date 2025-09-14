#!/bin/bash

# Docker Compose Deployment Script for WWF Application

set -e

echo "🐳 WWF Application - Docker Compose Deployment"
echo "=============================================="

# Check prerequisites
echo "Checking prerequisites..."

# Check Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker."
    exit 1
fi
echo "✅ Docker detected"

# Check Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi
echo "✅ Docker Compose detected"

# Build the application
echo ""
echo "🔨 Building WWF Application..."
./mvnw clean package -DskipTests

# Stop any existing containers
echo ""
echo "🛑 Stopping any existing containers..."
docker-compose down --remove-orphans

# Pull latest images
echo ""
echo "📥 Pulling latest images..."
docker-compose pull postgres kafka zookeeper kafka-ui

# Build WWF application image
echo ""
echo "🔨 Building WWF application Docker image..."
docker-compose build wwf-app

# Start all services
echo ""
echo "🚀 Starting all services..."
docker-compose up -d

# Wait for services to be ready
echo ""
echo "⏳ Waiting for services to be ready..."

# Function to check if a service is healthy
check_service() {
    local service=$1
    local max_attempts=30
    local attempt=0
    
    echo "Checking $service..."
    while [ $attempt -lt $max_attempts ]; do
        if [ "$service" = "postgres" ]; then
            if docker-compose exec postgres pg_isready -U wwfuser -d wwfdb &> /dev/null; then
                echo "✅ $service is ready"
                return 0
            fi
        elif [ "$service" = "kafka" ]; then
            if docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 &> /dev/null; then
                echo "✅ $service is ready"
                return 0
            fi
        elif [ "$service" = "wwf-app" ]; then
            if curl -s http://localhost:8080/wwf/actuator/health | grep -q "UP" &> /dev/null; then
                echo "✅ $service is ready"
                return 0
            fi
        fi
        
        attempt=$((attempt + 1))
        echo "Waiting for $service... (attempt $attempt/$max_attempts)"
        sleep 10
    done
    
    echo "❌ $service failed to become ready"
    return 1
}

# Check each service
check_service "postgres"
check_service "kafka"
check_service "wwf-app"

echo ""
echo "✅ All services are running successfully!"

# Show container status
echo ""
echo "📊 Container Status:"
docker-compose ps

# Show service URLs
echo ""
echo "🌐 Service URLs:"
echo "• WWF Application: http://localhost:8080/wwf"
echo "• Health Check: http://localhost:8080/wwf/actuator/health"
echo "• Kafka UI: http://localhost:8081"
echo "• PostgreSQL: localhost:5432"

# Show logs
echo ""
echo "📝 Recent logs from WWF Application:"
docker-compose logs --tail=20 wwf-app

echo ""
echo "🎯 What's next:"
echo "• Run the demo: ./demo.sh"
echo "• View logs: docker-compose logs -f wwf-app"
echo "• Monitor Kafka: http://localhost:8081"
echo "• Stop services: docker-compose down"

# Optional: Run demo automatically
if [ "$1" = "--demo" ]; then
    echo ""
    echo "🎭 Running demo automatically..."
    sleep 5
    ./demo.sh
fi

# Optional: Follow logs
if [ "$1" = "--logs" ]; then
    echo ""
    echo "📋 Following application logs..."
    docker-compose logs -f wwf-app
fi