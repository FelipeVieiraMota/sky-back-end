#!/bin/bash

clear
echo "🚀 Running infra-gateway-app containers..."

# Run all containers
docker run -d -p 8080:8080 --name infra-gateway-app-8080 infra-gateway-app

echo "📡 Streaming logs from all containers (Press Ctrl+C to exit)..."

# Show logs from all 3 containers in parallel
docker logs -f infra-gateway-app-8080

# Wait for background jobs (clean exit if Ctrl+C)
wait
