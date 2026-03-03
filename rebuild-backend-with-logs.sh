#!/bin/bash

echo "🔨 Rebuilding Backend with Debug Logging..."

# Navigate to project root
cd /Users/p.raut/expensetracker_2

# Stop containers
echo "⏹️  Stopping containers..."
docker-compose down

# Rebuild backend
echo "🏗️  Building backend..."
cd backend
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Maven build failed!"
    exit 1
fi

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t pranayraut11/expensetracker-backend:latest .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

# Go back to root
cd ..

# Start containers
echo "🚀 Starting containers..."
docker-compose up -d

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 10

# Show logs
echo ""
echo "✅ Backend rebuilt with debug logging!"
echo ""
echo "📋 View logs with:"
echo "   docker-compose logs -f backend"
echo ""
echo "🧪 Test your transaction with:"
echo '   curl -X POST http://localhost:8080/api/rules/test-match \'
echo '     -H "Content-Type: application/json" \'
echo '     -d '"'"'{"description": "YOUR_DESCRIPTION", "type": "DEBIT"}'"'"
echo ""
echo "🔍 To see debug logs, run:"
echo "   docker-compose logs backend | grep -A 100 'CALCULATING MATCH SCORE'"
echo ""

# Optionally follow logs
read -p "📖 Do you want to follow logs now? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    docker-compose logs -f backend
fi
