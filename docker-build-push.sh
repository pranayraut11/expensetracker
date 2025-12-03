#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}  Expense Tracker - Docker Build & Push Script${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}Warning: .env file not found!${NC}"
    echo -e "${YELLOW}Please create .env file from .env.example${NC}"
    echo ""
    read -p "Enter your Docker Hub username: " DOCKER_USERNAME
    echo "DOCKER_USERNAME=$DOCKER_USERNAME" > .env
    echo -e "${GREEN}Created .env file${NC}"
fi

# Load environment variables
source .env

if [ -z "$DOCKER_USERNAME" ]; then
    echo -e "${RED}Error: DOCKER_USERNAME not set in .env file${NC}"
    exit 1
fi

echo -e "${YELLOW}Using Docker Hub username: $DOCKER_USERNAME${NC}"
echo ""

# Login to Docker Hub
echo -e "${YELLOW}Step 1: Logging in to Docker Hub...${NC}"
docker login
if [ $? -ne 0 ]; then
    echo -e "${RED}Docker login failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker login successful${NC}"
echo ""

# Build backend image
echo -e "${YELLOW}Step 2: Building backend image...${NC}"
cd backend
docker build -t ${DOCKER_USERNAME}/expense-tracker-backend:latest .
if [ $? -ne 0 ]; then
    echo -e "${RED}Backend build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Backend image built successfully${NC}"
cd ..
echo ""

# Build frontend image
echo -e "${YELLOW}Step 3: Building frontend image...${NC}"
cd frontend
docker build -t ${DOCKER_USERNAME}/expense-tracker-frontend:latest .
if [ $? -ne 0 ]; then
    echo -e "${RED}Frontend build failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Frontend image built successfully${NC}"
cd ..
echo ""

# Tag images with version
VERSION=$(date +%Y%m%d-%H%M%S)
echo -e "${YELLOW}Step 4: Tagging images with version: $VERSION${NC}"
docker tag ${DOCKER_USERNAME}/expense-tracker-backend:latest ${DOCKER_USERNAME}/expense-tracker-backend:$VERSION
docker tag ${DOCKER_USERNAME}/expense-tracker-frontend:latest ${DOCKER_USERNAME}/expense-tracker-frontend:$VERSION
echo -e "${GREEN}✓ Images tagged successfully${NC}"
echo ""

# Push backend image
echo -e "${YELLOW}Step 5: Pushing backend image to Docker Hub...${NC}"
docker push ${DOCKER_USERNAME}/expense-tracker-backend:latest
docker push ${DOCKER_USERNAME}/expense-tracker-backend:$VERSION
if [ $? -ne 0 ]; then
    echo -e "${RED}Backend push failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Backend image pushed successfully${NC}"
echo ""

# Push frontend image
echo -e "${YELLOW}Step 6: Pushing frontend image to Docker Hub...${NC}"
docker push ${DOCKER_USERNAME}/expense-tracker-frontend:latest
docker push ${DOCKER_USERNAME}/expense-tracker-frontend:$VERSION
if [ $? -ne 0 ]; then
    echo -e "${RED}Frontend push failed!${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Frontend image pushed successfully${NC}"
echo ""

# Summary
echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}  ✓ Build and Push Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""
echo -e "Images pushed to Docker Hub:"
echo -e "  • ${DOCKER_USERNAME}/expense-tracker-backend:latest"
echo -e "  • ${DOCKER_USERNAME}/expense-tracker-backend:$VERSION"
echo -e "  • ${DOCKER_USERNAME}/expense-tracker-frontend:latest"
echo -e "  • ${DOCKER_USERNAME}/expense-tracker-frontend:$VERSION"
echo ""
echo -e "To run the application:"
echo -e "  ${YELLOW}docker-compose up -d${NC}"
echo ""
echo -e "To view logs:"
echo -e "  ${YELLOW}docker-compose logs -f${NC}"
echo ""
echo -e "To stop the application:"
echo -e "  ${YELLOW}docker-compose down${NC}"
echo ""

