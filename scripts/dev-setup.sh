#!/bin/bash

# Local Development Setup Script for WWF Application

set -e

echo "🌍 WWF Application - Local Development Setup"
echo "============================================"

# Check prerequisites
echo "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi
echo "✅ Java $JAVA_VERSION detected"

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

# Start infrastructure services
echo ""
echo "🚀 Starting infrastructure services..."
docker-compose up -d postgres kafka zookeeper

# Wait for services to be ready
echo ""
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check if PostgreSQL is ready
echo "Checking PostgreSQL..."
until docker-compose exec postgres pg_isready -U wwfuser -d wwfdb &> /dev/null; do
    echo "Waiting for PostgreSQL..."
    sleep 5
done
echo "✅ PostgreSQL is ready"

# Check if Kafka is ready
echo "Checking Kafka..."
until docker-compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092 &> /dev/null; do
    echo "Waiting for Kafka..."
    sleep 5
done
echo "✅ Kafka is ready"

# Start the WWF application
echo ""
echo "🌍 Starting WWF Application..."
SPRING_PROFILES_ACTIVE=prod \
DB_USERNAME=wwfuser \
DB_PASSWORD=wwfpassword \
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
./mvnw spring-boot:run &

APP_PID=$!

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 30

# Check if application is running
if curl -s http://localhost:8080/wwf/actuator/health | grep -q "UP"; then
    echo "✅ WWF Application is running successfully!"
    echo ""
    echo "📊 Application URLs:"
    echo "• WWF Application: http://localhost:8080/wwf"
    echo "• Health Check: http://localhost:8080/wwf/actuator/health"
    echo "• H2 Console: http://localhost:8080/wwf/h2-console"
    echo "• Kafka UI: http://localhost:8081"
    echo ""
    echo "🎯 To test the application, run: ./demo.sh"
    echo ""
    echo "🛑 To stop the application:"
    echo "• Kill application: kill $APP_PID"
    echo "• Stop infrastructure: docker-compose down"
else
    echo "❌ Application failed to start. Check logs above."
    kill $APP_PID 2>/dev/null || true
    exit 1
fi