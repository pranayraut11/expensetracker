#!/bin/bash

echo "========================================"
echo "  Docker Hub Setup"
echo "========================================"
echo ""

if [ -f .env ]; then
    echo "⚠️  .env file already exists!"
    source .env
    echo "Current Docker Hub username: $DOCKER_USERNAME"
    echo ""
    read -p "Do you want to change it? (y/n): " change
    if [ "$change" != "y" ]; then
        echo "Keeping existing configuration."
        exit 0
    fi
fi

echo ""
read -p "Enter your Docker Hub username: " username

if [ -z "$username" ]; then
    echo "❌ Username cannot be empty!"
    exit 1
fi

echo "DOCKER_USERNAME=$username" > .env
echo ""
echo "✅ Configuration saved to .env"
echo "Docker Hub username: $username"
echo ""
echo "Next steps:"
echo "  1. Run: ./docker-build-push.sh (to build and push to Docker Hub)"
echo "  2. Or run: docker-compose up -d (to run locally)"

