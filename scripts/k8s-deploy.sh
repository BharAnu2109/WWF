#!/bin/bash

# Kubernetes Deployment Script for WWF Application

set -e

echo "☸️ WWF Application - Kubernetes Deployment"
echo "==========================================="

# Check prerequisites
echo "Checking prerequisites..."

# Check kubectl
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed. Please install kubectl."
    exit 1
fi
echo "✅ kubectl detected"

# Check if connected to a cluster
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ No Kubernetes cluster connection. Please configure kubectl."
    exit 1
fi
echo "✅ Connected to Kubernetes cluster"

# Build Docker image
echo ""
echo "🔨 Building Docker image..."
docker build -t wwf-application:latest .

# If using a remote registry, tag and push
if [ -n "$DOCKER_REGISTRY" ]; then
    echo "📤 Pushing to registry: $DOCKER_REGISTRY"
    docker tag wwf-application:latest $DOCKER_REGISTRY/wwf-application:latest
    docker push $DOCKER_REGISTRY/wwf-application:latest
    
    # Update image reference in Kubernetes manifests
    sed -i "s|wwf-application:latest|$DOCKER_REGISTRY/wwf-application:latest|g" k8s/04-wwf-app.yaml
fi

# Deploy to Kubernetes
echo ""
echo "🚀 Deploying to Kubernetes..."

# Apply manifests in order
echo "Creating namespace and configuration..."
kubectl apply -f k8s/01-namespace-config.yaml

echo "Deploying PostgreSQL..."
kubectl apply -f k8s/02-postgres.yaml

echo "Deploying Kafka..."
kubectl apply -f k8s/03-kafka.yaml

echo "Deploying WWF Application..."
kubectl apply -f k8s/04-wwf-app.yaml

echo "Applying scaling and monitoring..."
kubectl apply -f k8s/05-scaling-monitoring.yaml

# Wait for deployments
echo ""
echo "⏳ Waiting for deployments to be ready..."

# Wait for PostgreSQL
echo "Waiting for PostgreSQL..."
kubectl wait --for=condition=ready pod -l app=postgres -n wwf --timeout=300s

# Wait for Kafka
echo "Waiting for Kafka..."
kubectl wait --for=condition=ready pod -l app=kafka -n wwf --timeout=300s

# Wait for WWF Application
echo "Waiting for WWF Application..."
kubectl wait --for=condition=ready pod -l app=wwf-app -n wwf --timeout=300s

echo ""
echo "✅ Deployment completed successfully!"

# Show deployment status
echo ""
echo "📊 Deployment Status:"
kubectl get pods -n wwf
echo ""
kubectl get services -n wwf
echo ""
kubectl get ingress -n wwf 2>/dev/null || echo "No ingress configured"

# Show how to access the application
echo ""
echo "🌐 How to access the application:"
echo ""

# Check if LoadBalancer service has external IP
EXTERNAL_IP=$(kubectl get service wwf-app-service -n wwf -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null || echo "")

if [ -n "$EXTERNAL_IP" ]; then
    echo "• External URL: http://$EXTERNAL_IP"
else
    echo "• Port forward: kubectl port-forward service/wwf-app-service 8080:80 -n wwf"
    echo "• Then access: http://localhost:8080/wwf"
fi

echo ""
echo "🔍 Useful commands:"
echo "• View logs: kubectl logs -f deployment/wwf-app -n wwf"
echo "• Scale app: kubectl scale deployment wwf-app --replicas=3 -n wwf"
echo "• Check HPA: kubectl get hpa -n wwf"
echo "• Delete all: kubectl delete namespace wwf"

# Start port forwarding in background for convenience
if [ "$1" = "--port-forward" ]; then
    echo ""
    echo "🔗 Starting port forwarding..."
    kubectl port-forward service/wwf-app-service 8080:80 -n wwf &
    PORT_FORWARD_PID=$!
    echo "Port forwarding started (PID: $PORT_FORWARD_PID)"
    echo "Application available at: http://localhost:8080/wwf"
    echo "To stop port forwarding: kill $PORT_FORWARD_PID"
fi