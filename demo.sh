#!/bin/bash

# WWF Application Demo Scripts
# These scripts demonstrate the key features of the WWF application

echo "🌍 WWF Wildlife Conservation Application Demo"
echo "============================================="

BASE_URL="http://localhost:8080/wwf"

# Function to make API calls with better formatting
api_call() {
    local method=$1
    local endpoint=$2
    local data=$3
    
    echo ""
    echo "🔄 $method $endpoint"
    if [ -n "$data" ]; then
        echo "📤 Data: $data"
    fi
    
    if [ -n "$data" ]; then
        curl -s -X $method "$BASE_URL$endpoint" \
             -H "Content-Type: application/json" \
             -d "$data" | jq '.' || echo "Response not JSON"
    else
        curl -s -X $method "$BASE_URL$endpoint" | jq '.' || echo "Response not JSON"
    fi
    echo ""
}

echo ""
echo "📊 Application Health Check"
echo "=========================="
api_call "GET" "/actuator/health"

echo ""
echo "🐾 Wildlife Species Management"
echo "=============================="

# Create some wildlife species
echo "Creating wildlife species..."

api_call "POST" "/api/species" '{
    "name": "Amur Leopard",
    "scientificName": "Panthera pardus orientalis", 
    "conservationStatus": "CRITICALLY_ENDANGERED",
    "description": "One of the rarest big cats in the world",
    "habitat": "Temperate forests",
    "populationEstimate": 120
}'

api_call "POST" "/api/species" '{
    "name": "Vaquita Porpoise",
    "scientificName": "Phocoena sinus",
    "conservationStatus": "CRITICALLY_ENDANGERED", 
    "description": "The worlds most endangered marine mammal",
    "habitat": "Gulf of California",
    "populationEstimate": 10
}'

# Get all species
echo "Retrieving all species..."
api_call "GET" "/api/species"

# Get endangered species
echo "Retrieving endangered species..."
api_call "GET" "/api/species/endangered"

echo ""
echo "🏗️ Conservation Projects"
echo "======================="

# Create conservation projects
echo "Creating conservation projects..."

api_call "POST" "/api/projects" '{
    "name": "Amur Leopard Recovery Program",
    "description": "Protecting the last remaining Amur leopards in Russia and China",
    "startDate": "2024-01-01",
    "endDate": "2027-12-31",
    "budget": 2000000.00,
    "status": "ACTIVE",
    "location": "Primorsky Krai, Russia"
}'

api_call "POST" "/api/projects" '{
    "name": "Vaquita Conservation Initiative",
    "description": "Emergency conservation efforts for vaquita porpoises",
    "startDate": "2024-03-01", 
    "endDate": "2026-02-28",
    "budget": 5000000.00,
    "status": "ACTIVE",
    "location": "Gulf of California, Mexico"
}'

# Get all projects
echo "Retrieving all projects..."
api_call "GET" "/api/projects"

# Get projects needing funding
echo "Retrieving projects needing funding..."
api_call "GET" "/api/projects/funding-needed"

echo ""
echo "💰 Donation Processing"
echo "====================="

# Create donations
echo "Creating donations..."

api_call "POST" "/api/donations" '{
    "amount": 500.00,
    "donorName": "John Conservation",
    "donorEmail": "john@conservation.org",
    "message": "For the Amur leopards!"
}'

api_call "POST" "/api/donations" '{
    "amount": 1000.00,
    "donorName": "Wildlife Lover",
    "donorEmail": "wildlife@email.com", 
    "message": "Save the vaquitas!"
}'

# Get all donations
echo "Retrieving all donations..."
api_call "GET" "/api/donations"

# Process donations (simulate payment)
echo "Processing first donation..."
api_call "POST" "/api/donations/1/process"

echo "Processing second donation..."
api_call "POST" "/api/donations/2/process"

# Get recent donations
echo "Retrieving recent donations..."
api_call "GET" "/api/donations/recent?days=7"

echo ""
echo "📈 Statistics and Analytics"
echo "=========================="

# Get species statistics
echo "Species statistics by conservation status..."
api_call "GET" "/api/species/statistics"

# Get project statistics  
echo "Project statistics by status..."
api_call "GET" "/api/projects/statistics"

# Get donation statistics
echo "Donation statistics by status..."
api_call "GET" "/api/donations/statistics"

# Get financial summary
echo "Project financial summary..."
api_call "GET" "/api/projects/financial-summary"

echo ""
echo "🔍 Search and Filtering"
echo "======================"

# Search species by name
echo "Searching species by name 'leopard'..."
api_call "GET" "/api/species/search?name=leopard"

# Search projects by location
echo "Searching projects by location 'Russia'..."
api_call "GET" "/api/projects/location?location=Russia"

# Get large donations
echo "Finding large donations over $750..."
api_call "GET" "/api/donations/large?minAmount=750"

echo ""
echo "✅ Demo completed!"
echo "=================="
echo ""
echo "📖 API Documentation:"
echo "• Species API: $BASE_URL/api/species"
echo "• Projects API: $BASE_URL/api/projects" 
echo "• Donations API: $BASE_URL/api/donations"
echo "• Health Check: $BASE_URL/actuator/health"
echo "• H2 Console: $BASE_URL/h2-console (dev mode)"
echo ""
echo "🐳 To start the application:"
echo "• Development: ./mvnw spring-boot:run"
echo "• Docker: docker-compose up"
echo "• Kubernetes: kubectl apply -f k8s/"