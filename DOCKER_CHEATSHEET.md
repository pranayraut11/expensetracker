# 🐳 Docker Commands Cheat Sheet

## 🚀 Quick Start

```bash
# Interactive menu (easiest)
./docker-manager.sh

# Direct start
docker-compose up -d

# View logs
docker-compose logs -f
```

---

## 📦 Build & Push

```bash
# Setup Docker Hub username (first time)
./setup-docker.sh

# Build locally
./docker-build-local.sh
# OR
docker-compose build

# Build and push to Docker Hub
./docker-build-push.sh
```

---

## 🏃 Run & Stop

```bash
# Start services
docker-compose up -d

# Start with logs
docker-compose up

# Stop services
docker-compose down

# Stop and remove volumes (⚠️ deletes data)
docker-compose down -v

# Restart services
docker-compose restart

# Restart specific service
docker-compose restart backend
docker-compose restart frontend
```

---

## 📊 Monitor & Debug

```bash
# Check status
docker-compose ps

# View logs (all)
docker-compose logs -f

# View logs (specific service)
docker-compose logs -f backend
docker-compose logs -f frontend

# View recent logs
docker-compose logs --tail=100

# Execute command in container
docker exec -it expense-tracker-backend sh
docker exec -it expense-tracker-frontend sh

# Check resource usage
docker stats

# Inspect container
docker inspect expense-tracker-backend
docker inspect expense-tracker-frontend
```

---

## 🔧 Maintenance

```bash
# Rebuild after code changes
docker-compose up -d --build

# Rebuild without cache
docker-compose build --no-cache

# Pull latest images
docker-compose pull

# Verify setup
./verify-docker-setup.sh

# Clean up stopped containers
docker container prune

# Clean up unused images
docker image prune

# Clean up everything (⚠️ careful)
docker system prune -a

# Clean up volumes (⚠️ deletes data)
docker volume prune
```

---

## 💾 Database

```bash
# Backup database
docker cp expense-tracker-backend:/app/data ./db-backup

# Restore database
docker cp ./db-backup/expense-tracker-db.mv.db expense-tracker-backend:/app/data/

# View database location
docker volume inspect expensetracker_backend-data

# Access H2 Console
open http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:file:/app/data/expense-tracker-db
# Username: sa
# Password: (empty)
```

---

## 🌐 Access URLs

```bash
# Frontend
http://localhost

# Backend API
http://localhost:8080

# Backend Health Check
http://localhost:8080/actuator/health

# H2 Database Console
http://localhost:8080/h2-console

# Sample API endpoints
http://localhost:8080/api/transactions
http://localhost:8080/api/summary
http://localhost:8080/rules
```

---

## 🐛 Troubleshooting

```bash
# Check if Docker is running
docker info

# Check if ports are available
lsof -i :80
lsof -i :8080

# View Docker disk usage
docker system df

# Check network
docker network ls
docker network inspect expensetracker_expense-tracker-network

# Check volumes
docker volume ls
docker volume inspect expensetracker_backend-data

# Remove specific container
docker-compose rm -f backend
docker-compose rm -f frontend

# Restart Docker
# macOS: Restart Docker Desktop app
# Linux: sudo systemctl restart docker
```

---

## 📤 Push to Docker Hub

```bash
# Login to Docker Hub
docker login

# Tag images
docker tag expense-tracker-backend:latest yourusername/expense-tracker-backend:latest
docker tag expense-tracker-frontend:latest yourusername/expense-tracker-frontend:latest

# Push images
docker push yourusername/expense-tracker-backend:latest
docker push yourusername/expense-tracker-frontend:latest

# OR use the automated script
./docker-build-push.sh
```

---

## 📥 Pull from Docker Hub

```bash
# Pull images
docker pull yourusername/expense-tracker-backend:latest
docker pull yourusername/expense-tracker-frontend:latest

# OR use docker-compose
docker-compose pull

# Then run
docker-compose up -d
```

---

## 🔄 Update After Code Changes

```bash
# Backend changes
docker-compose up -d --build backend

# Frontend changes
docker-compose up -d --build frontend

# Both
docker-compose up -d --build
```

---

## 🎯 Common Scenarios

### First Time Setup
```bash
./setup-docker.sh
docker-compose up -d
open http://localhost
```

### Daily Development
```bash
# Start
docker-compose up -d

# View logs
docker-compose logs -f

# Stop when done
docker-compose down
```

### Deploy New Version
```bash
# Build and push
./docker-build-push.sh

# On server
docker-compose pull
docker-compose up -d
```

### Clean Slate Restart
```bash
docker-compose down -v
docker system prune -a
docker-compose up -d
```

---

## 📝 Environment Variables

```bash
# Set in docker-compose.yml or .env file

# Backend
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/expense-tracker-db
JAVA_OPTS=-Xms256m -Xmx512m

# Docker Hub
DOCKER_USERNAME=yourusername
```

---

## 🆘 Emergency Commands

```bash
# Force stop all containers
docker stop $(docker ps -aq)

# Remove all containers
docker rm $(docker ps -aq)

# Remove all images
docker rmi $(docker images -q)

# Nuclear option (⚠️ removes everything)
docker system prune -a --volumes
```

---

## ✅ Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost/health

# Or in browser
open http://localhost:8080/actuator/health
```

---

## 📚 Documentation

```bash
# View documentation
cat DOCKER.md
cat DOCKER_QUICKSTART.md
cat DOCKER_README.md

# Or open in browser
open DOCKER.md
```

---

**Keep this cheat sheet handy! 📋**

For interactive help: `./docker-manager.sh`

