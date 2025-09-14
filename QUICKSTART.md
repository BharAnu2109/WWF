# WWF Application Quick Start Guide

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- jq (for demo script)

### Option 1: Run with Docker Compose (Recommended)
```bash
# Start all services (PostgreSQL, Kafka, WWF App)
docker-compose up -d

# Wait for services to start (about 30-60 seconds)
docker-compose logs -f wwf-app

# Run the demo
./demo.sh
```

### Option 2: Run Locally (Development)
```bash
# Build the application
./mvnw clean package

# Run with H2 database (no external dependencies)
./mvnw spring-boot:run

# Application will be available at http://localhost:8080/wwf
```

### Option 3: Kubernetes Deployment
```bash
# Deploy to Kubernetes cluster
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n wwf

# Port forward to access locally
kubectl port-forward service/wwf-app-service 8080:80 -n wwf
```

## 📡 API Endpoints

### Wildlife Species
- `GET /api/species` - List all species
- `POST /api/species` - Create new species
- `GET /api/species/endangered` - Get endangered species
- `GET /api/species/search?name=panda` - Search by name

### Conservation Projects  
- `GET /api/projects` - List all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/active` - Get active projects
- `GET /api/projects/funding-needed` - Get projects needing funding

### Donations
- `GET /api/donations` - List all donations
- `POST /api/donations` - Create new donation
- `POST /api/donations/{id}/process` - Process donation
- `GET /api/donations/recent` - Get recent donations

## 🔧 Configuration

### Environment Variables
- `DB_USERNAME` - Database username (default: wwfuser)
- `DB_PASSWORD` - Database password (default: wwfpassword)
- `KAFKA_BOOTSTRAP_SERVERS` - Kafka servers (default: localhost:9092)
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/prod/test)

### Profiles
- `default` - Development with H2 database
- `prod` - Production with PostgreSQL
- `test` - Testing with mocked services

## 📊 Monitoring

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

## 🐳 Docker Services

When running with `docker-compose up`:
- **WWF App**: http://localhost:8080/wwf
- **Kafka UI**: http://localhost:8081
- **PostgreSQL**: localhost:5432

## 🎯 Sample Usage

### Create a Species
```bash
curl -X POST http://localhost:8080/wwf/api/species \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Snow Leopard",
    "scientificName": "Panthera uncia",
    "conservationStatus": "VULNERABLE",
    "habitat": "Mountain ranges",
    "populationEstimate": 4000
  }'
```

### Create a Project
```bash
curl -X POST http://localhost:8080/wwf/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Snow Leopard Protection",
    "description": "Protecting snow leopards in the Himalayas",
    "startDate": "2024-01-01",
    "budget": 1000000.00,
    "status": "ACTIVE",
    "location": "Himalayas"
  }'
```

### Make a Donation
```bash
curl -X POST http://localhost:8080/wwf/api/donations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "donorName": "John Doe",
    "donorEmail": "john@example.com",
    "message": "For wildlife conservation"
  }'
```

## 🔍 Troubleshooting

### Application won't start
- Check Java version: `java -version` (needs Java 17+)
- Check if ports are available: `netstat -an | grep 8080`
- Check logs: `docker-compose logs wwf-app`

### Database connection issues
- Verify PostgreSQL is running: `docker-compose ps postgres`
- Check environment variables
- Verify network connectivity

### Kafka issues
- Check if Kafka is running: `docker-compose ps kafka`
- Verify Zookeeper is healthy: `docker-compose ps zookeeper`
- Check Kafka UI at http://localhost:8081

## 📚 Learn More

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)