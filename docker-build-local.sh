#!/bin/bash

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}  Expense Tracker - Quick Build (Local)${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""

# Build backend
echo -e "${YELLOW}Building backend...${NC}"
cd backend
docker build -t expense-tracker-backend:latest .
cd ..
echo -e "${GREEN}✓ Backend built${NC}"
echo ""

# Build frontend
echo -e "${YELLOW}Building frontend...${NC}"
cd frontend
docker build -t expense-tracker-frontend:latest .
cd ..
echo -e "${GREEN}✓ Frontend built${NC}"
echo ""

echo -e "${GREEN}Build complete! Run with: docker-compose up -d${NC}"

