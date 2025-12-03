#!/bin/bash

# Quick test script to verify Docker setup after fix

echo "🔍 Testing Docker Setup..."
echo ""

# Check Docker
echo "1. Checking Docker..."
if docker info > /dev/null 2>&1; then
    echo "   ✅ Docker is running"
else
    echo "   ❌ Docker is not running"
    exit 1
fi
echo ""

# Check .env file
echo "2. Checking configuration..."
if [ -f ".env" ]; then
    source .env
    echo "   ✅ .env file exists"
    echo "   📝 Docker Hub username: $DOCKER_USERNAME"
else
    echo "   ⚠️  .env file not found"
fi
echo ""

# Test building backend
echo "3. Testing backend Docker build..."
echo "   (This will take a few minutes on first run)"
cd backend
if docker build -t test-backend . > /dev/null 2>&1; then
    echo "   ✅ Backend builds successfully!"
    docker rmi test-backend > /dev/null 2>&1
else
    echo "   ❌ Backend build failed"
    echo "   Run 'docker build -t test-backend .' in backend/ to see details"
    exit 1
fi
cd ..
echo ""

# Test building frontend
echo "4. Testing frontend Docker build..."
cd frontend
if docker build -t test-frontend . > /dev/null 2>&1; then
    echo "   ✅ Frontend builds successfully!"
    docker rmi test-frontend > /dev/null 2>&1
else
    echo "   ❌ Frontend build failed"
    echo "   Run 'docker build -t test-frontend .' in frontend/ to see details"
    exit 1
fi
cd ..
echo ""

echo "════════════════════════════════════════════════════════"
echo "🎉 All tests passed!"
echo ""
echo "Your Docker setup is working perfectly."
echo ""
echo "Next steps:"
echo "  1. Run: docker-compose up -d"
echo "  2. Visit: http://localhost"
echo "  3. Or use: ./docker-manager.sh"
echo "════════════════════════════════════════════════════════"

