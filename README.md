# WWF Wildlife Conservation Application

A comprehensive Spring Boot application for wildlife conservation management with Docker containerization, Kubernetes deployment, and Kafka event streaming.

## Features

### Core Functionality
- **Wildlife Species Management**: Track endangered species with conservation status
- **Conservation Projects**: Manage conservation initiatives with funding tracking
- **Donation Processing**: Handle donations with payment processing simulation
- **Event-Driven Architecture**: Real-time notifications via Kafka messaging

### Technical Features
- **Spring Boot 3.2**: Modern Java framework with auto-configuration
- **JPA/Hibernate**: Database persistence with PostgreSQL/H2 support
- **Apache Kafka**: Event streaming and messaging
- **Docker**: Containerized deployment
- **Kubernetes**: Orchestrated container management
- **RESTful APIs**: Comprehensive REST endpoints
- **Health Monitoring**: Actuator endpoints for monitoring

## Architecture

### Domain Models
- **WildlifeSpecies**: Endangered species tracking
- **ConservationProject**: Conservation initiatives
- **Donation**: Funding and donation management

### Event-Driven Components
- **SpeciesAddedEvent**: Published when new species are added
- **ProjectCreatedEvent**: Published when conservation projects are created
- **DonationProcessedEvent**: Published when donations are processed

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- Kubernetes cluster (optional)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WWF
   ```

2. **Build the application**
   ```bash
   ./mvnw clean package
   ```

3. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

4. **Access the application**
   - Application: http://localhost:8080/wwf
   - H2 Console: http://localhost:8080/wwf/h2-console
   - Kafka UI: http://localhost:8081

### API Endpoints

#### Wildlife Species
- `GET /api/species` - Get all species
- `POST /api/species` - Create new species
- `GET /api/species/{id}` - Get species by ID
- `PUT /api/species/{id}` - Update species
- `DELETE /api/species/{id}` - Delete species
- `GET /api/species/endangered` - Get endangered species
- `GET /api/species/search?name={name}` - Search by name

#### Conservation Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `GET /api/projects/active` - Get active projects
- `POST /api/projects/{id}/add-funds` - Add funds to project

#### Donations
- `GET /api/donations` - Get all donations
- `POST /api/donations` - Create new donation
- `POST /api/donations/{id}/process` - Process donation
- `GET /api/donations/{id}` - Get donation by ID
- `GET /api/donations/recent` - Get recent donations
- `POST /api/donations/{id}/refund` - Refund donation

### Kubernetes Deployment

1. **Apply Kubernetes manifests**
   ```bash
   kubectl apply -f k8s/
   ```

2. **Monitor deployment**
   ```bash
   kubectl get pods -n wwf
   kubectl logs -f deployment/wwf-app -n wwf
   ```

3. **Access the application**
   ```bash
   kubectl port-forward service/wwf-app-service 8080:80 -n wwf
   ```

### Sample Data

The application includes sample data:
- 5 wildlife species (Giant Panda, Snow Leopard, African Elephant, etc.)
- 5 conservation projects with funding goals
- Sample donations with different statuses

### Configuration

#### Database
- **Development**: H2 in-memory database
- **Production**: PostgreSQL with persistent storage

#### Kafka Topics
- `wwf.species.events` - Species-related events
- `wwf.project.events` - Project-related events
- `wwf.donation.events` - Donation-related events

### Monitoring

- **Health Check**: `/wwf/actuator/health`
- **Metrics**: `/wwf/actuator/metrics`
- **Info**: `/wwf/actuator/info`

### Testing

```bash
./mvnw test
```

## Development

### Adding New Features
1. Create domain models in `model` package
2. Add repository interfaces in `repository` package
3. Implement business logic in `service` package
4. Create REST controllers in `controller` package
5. Add Kafka events in `kafka` package

### Environment Variables
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.