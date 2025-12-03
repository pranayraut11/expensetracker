#!/bin/bash

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Docker Setup Verification${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
echo ""

# Check Docker
echo -e "${YELLOW}Checking Docker...${NC}"
if docker info > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Docker is running${NC}"
else
    echo -e "${RED}✗ Docker is not running. Please start Docker Desktop.${NC}"
    exit 1
fi
echo ""

# Check files
echo -e "${YELLOW}Checking Docker files...${NC}"
files=(
    "backend/Dockerfile"
    "frontend/Dockerfile"
    "docker-compose.yml"
    "frontend/nginx.conf"
    "setup-docker.sh"
    "docker-build-push.sh"
    "docker-build-local.sh"
    "docker-manager.sh"
)

all_exist=true
for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $file"
    else
        echo -e "${RED}✗${NC} $file ${RED}(missing)${NC}"
        all_exist=false
    fi
done
echo ""

# Check scripts are executable
echo -e "${YELLOW}Checking script permissions...${NC}"
scripts=(
    "setup-docker.sh"
    "docker-build-push.sh"
    "docker-build-local.sh"
    "docker-manager.sh"
)

for script in "${scripts[@]}"; do
    if [ -x "$script" ]; then
        echo -e "${GREEN}✓${NC} $script (executable)"
    else
        echo -e "${YELLOW}⚠${NC} $script (not executable, fixing...)"
        chmod +x "$script"
        echo -e "${GREEN}  Fixed!${NC}"
    fi
done
echo ""

# Check .env
echo -e "${YELLOW}Checking configuration...${NC}"
if [ -f ".env" ]; then
    source .env
    if [ -n "$DOCKER_USERNAME" ]; then
        echo -e "${GREEN}✓${NC} .env file exists (username: $DOCKER_USERNAME)"
    else
        echo -e "${YELLOW}⚠${NC} .env exists but DOCKER_USERNAME not set"
        echo -e "  Run: ${BLUE}./setup-docker.sh${NC}"
    fi
else
    echo -e "${YELLOW}⚠${NC} .env file not found"
    echo -e "  Run: ${BLUE}./setup-docker.sh${NC}"
fi
echo ""

# Check if containers are running
echo -e "${YELLOW}Checking running containers...${NC}"
if docker-compose ps | grep -q "Up"; then
    echo -e "${GREEN}✓ Containers are running:${NC}"
    docker-compose ps
else
    echo -e "${YELLOW}⚠${NC} No containers running"
    echo -e "  To start: ${BLUE}docker-compose up -d${NC}"
fi
echo ""

# Summary
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"
if [ "$all_exist" = true ]; then
    echo -e "${GREEN}✓ All Docker files present${NC}"
    echo ""
    echo -e "${BLUE}Next steps:${NC}"
    echo -e "  1. Setup Docker Hub: ${GREEN}./setup-docker.sh${NC}"
    echo -e "  2. Run locally: ${GREEN}docker-compose up -d${NC}"
    echo -e "  3. Or use interactive manager: ${GREEN}./docker-manager.sh${NC}"
else
    echo -e "${RED}✗ Some files are missing${NC}"
    echo -e "  Please re-run the Docker setup"
fi
echo -e "${BLUE}════════════════════════════════════════════════════${NC}"

