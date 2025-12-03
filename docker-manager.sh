#!/bin/bash

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

clear
echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                                                        ║${NC}"
echo -e "${BLUE}║     🐳 Expense Tracker - Docker Build & Deploy        ║${NC}"
echo -e "${BLUE}║                                                        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${YELLOW}⚠️  Docker is not running. Please start Docker Desktop.${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Check for .env file
if [ ! -f .env ]; then
    echo -e "${YELLOW}No .env file found. Running setup...${NC}"
    ./setup-docker.sh
    echo ""
fi

source .env

# Menu
echo -e "${BLUE}What would you like to do?${NC}"
echo ""
echo "  1) Build images locally and run"
echo "  2) Build images and push to Docker Hub"
echo "  3) Pull from Docker Hub and run"
echo "  4) Stop running containers"
echo "  5) View logs"
echo "  6) Clean up (remove containers, images, volumes)"
echo "  7) Exit"
echo ""
read -p "Enter your choice (1-7): " choice

case $choice in
    1)
        echo ""
        echo -e "${YELLOW}Building images locally...${NC}"
        ./docker-build-local.sh
        echo ""
        echo -e "${YELLOW}Starting services...${NC}"
        docker-compose up -d
        echo ""
        echo -e "${GREEN}✓ Services started!${NC}"
        echo ""
        echo -e "${BLUE}Access your application:${NC}"
        echo -e "  Frontend:  ${GREEN}http://localhost${NC}"
        echo -e "  Backend:   ${GREEN}http://localhost:8080${NC}"
        echo -e "  H2 Console: ${GREEN}http://localhost:8080/h2-console${NC}"
        ;;
    2)
        echo ""
        echo -e "${YELLOW}Building and pushing to Docker Hub...${NC}"
        ./docker-build-push.sh
        ;;
    3)
        echo ""
        echo -e "${YELLOW}Pulling images from Docker Hub...${NC}"
        docker-compose pull
        echo ""
        echo -e "${YELLOW}Starting services...${NC}"
        docker-compose up -d
        echo ""
        echo -e "${GREEN}✓ Services started!${NC}"
        ;;
    4)
        echo ""
        echo -e "${YELLOW}Stopping services...${NC}"
        docker-compose down
        echo -e "${GREEN}✓ Services stopped${NC}"
        ;;
    5)
        echo ""
        echo -e "${BLUE}Showing logs (Ctrl+C to exit)...${NC}"
        docker-compose logs -f
        ;;
    6)
        echo ""
        echo -e "${YELLOW}⚠️  This will remove all containers, images, and volumes!${NC}"
        read -p "Are you sure? (yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker-compose down -v
            docker rmi expense-tracker-backend:latest expense-tracker-frontend:latest 2>/dev/null
            docker rmi ${DOCKER_USERNAME}/expense-tracker-backend:latest ${DOCKER_USERNAME}/expense-tracker-frontend:latest 2>/dev/null
            echo -e "${GREEN}✓ Cleanup complete${NC}"
        else
            echo "Cancelled."
        fi
        ;;
    7)
        echo "Goodbye!"
        exit 0
        ;;
    *)
        echo -e "${YELLOW}Invalid choice${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${BLUE}────────────────────────────────────────────────────────${NC}"
echo ""

