#!/bin/bash

echo "🔥 Rebuilding and Starting Expense Tracker Docker Containers"
echo "=============================================================="

cd /Users/p.raut/demoprojects/expensetracker

# Stop any running containers
echo "1. Stopping existing containers..."
docker-compose down

# Remove old images (optional - uncomment if you want clean rebuild)
# echo "2. Removing old images..."
# docker rmi pranayraut11/expense-tracker-backend:latest 2>/dev/null || true
# docker rmi pranayraut11/expense-tracker-frontend:latest 2>/dev/null || true

# Build backend
echo "2. Building backend image..."
docker-compose build backend

# Check if backend build succeeded
if [ $? -ne 0 ]; then
    echo "❌ Backend build failed!"
    exit 1
fi

echo "✅ Backend built successfully"

# Build frontend
echo "3. Building frontend image..."
docker-compose build frontend

# Check if frontend build succeeded
if [ $? -ne 0 ]; then
    echo "❌ Frontend build failed!"
    exit 1
fi

echo "✅ Frontend built successfully"

# Start containers
echo "4. Starting containers..."
docker-compose up -d

# Wait a bit for containers to start
sleep 5

# Check status
echo ""
echo "5. Container Status:"
docker-compose ps

echo ""
echo "6. Checking logs..."
docker-compose logs --tail=30

echo ""
echo "=============================================================="
echo "✅ Done! Check if containers are running above."
echo ""
echo "To access the app:"
echo "  - Frontend: http://localhost"
echo "  - Backend:  http://localhost:8080"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop:"
echo "  docker-compose down"
echo "=============================================================="

