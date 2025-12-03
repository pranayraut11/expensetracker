# 🐳 Docker Deployment Guide - Expense Tracker

This guide explains how to build, run, and deploy the Expense Tracker application using Docker.

---

## 📋 Prerequisites

- Docker Desktop installed (version 20.10+)
- Docker Hub account (for pushing images)
- Git (optional, for version control)

---

## 🏗️ Project Structure

```
expensetracker/
├── backend/
│   ├── Dockerfile
│   ├── .dockerignore
│   └── ... (Spring Boot app)
├── frontend/
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   └── ... (React app)
├── docker-compose.yml
├── docker-build-push.sh
├── docker-build-local.sh
├── .env.example
└── DOCKER_DEPLOYMENT.md (this file)
```

---

## 🚀 Quick Start (Local Development)

### Option 1: Using docker-compose (Recommended)

1. **Build and run both services:**
   ```bash
   docker-compose up -d
   ```

2. **Access the application:**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

3. **View logs:**
   ```bash
   docker-compose logs -f
   ```

4. **Stop the application:**
   ```bash
   docker-compose down
   ```

### Option 2: Using build script

1. **Build images locally:**
   ```bash
   ./docker-build-local.sh
   ```

2. **Run with docker-compose:**
   ```bash
   docker-compose up -d
   ```

---

## 📦 Building Individual Images

### Backend (Spring Boot)

```bash
cd backend
docker build -t expense-tracker-backend:latest .
```

### Frontend (React + Nginx)

```bash
cd frontend
docker build -t expense-tracker-frontend:latest .
```

---

## 🌐 Push to Docker Hub

### Setup

1. **Create .env file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit .env and set your Docker Hub username:**
   ```bash
   DOCKER_USERNAME=your-dockerhub-username
   ```

### Build and Push

Run the automated script:

```bash
./docker-build-push.sh
```

This script will:
1. ✅ Login to Docker Hub
2. ✅ Build backend image
3. ✅ Build frontend image
4. ✅ Tag images with timestamp
5. ✅ Push all images to Docker Hub

### Manual Push

If you prefer manual control:

```bash
# Login to Docker Hub
docker login

# Build images
docker build -t yourusername/expense-tracker-backend:latest ./backend
docker build -t yourusername/expense-tracker-frontend:latest ./frontend

# Push images
docker push yourusername/expense-tracker-backend:latest
docker push yourusername/expense-tracker-frontend:latest
```

---

## 🔧 Configuration

### Environment Variables

**Backend (.env or docker-compose.yml):**
- `SPRING_PROFILES_ACTIVE` - Spring profile (default: prod)
- `SPRING_DATASOURCE_URL` - H2 database path
- `JAVA_OPTS` - JVM options (default: -Xms256m -Xmx512m)

**Frontend (nginx.conf):**
- Backend proxy configured to route `/api` and `/rules` to backend service

### Ports

- Frontend: `80` (nginx)
- Backend: `8080` (Spring Boot)

### Volumes

- `backend-data` - Persistent storage for H2 database

---

## 📊 Docker Images

### Backend Image Details

- **Base Image:** eclipse-temurin:17-jre-alpine
- **Build Type:** Multi-stage (Maven build + JRE runtime)
- **Size:** ~250 MB
- **Exposed Port:** 8080
- **Health Check:** `/actuator/health`

### Frontend Image Details

- **Base Image:** nginx:alpine
- **Build Type:** Multi-stage (Node build + Nginx serve)
- **Size:** ~30 MB
- **Exposed Port:** 80
- **Health Check:** `/health`

---

## 🛠️ Useful Docker Commands

### View running containers
```bash
docker ps
```

### View all containers
```bash
docker ps -a
```

### View logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Restart services
```bash
docker-compose restart
```

### Rebuild and restart
```bash
docker-compose up -d --build
```

### Stop and remove containers
```bash
docker-compose down
```

### Remove volumes (⚠️ deletes database)
```bash
docker-compose down -v
```

### Execute commands in container
```bash
# Backend
docker exec -it expense-tracker-backend sh

# Frontend
docker exec -it expense-tracker-frontend sh
```

---

## 🔍 Troubleshooting

### Issue: Backend not starting

**Check logs:**
```bash
docker-compose logs backend
```

**Common causes:**
- Port 8080 already in use
- Memory issues (increase JAVA_OPTS)
- Database initialization error

### Issue: Frontend can't connect to backend

**Verify:**
1. Backend is healthy: `docker ps` (check STATUS)
2. Network connectivity: `docker network inspect expense-tracker_expense-tracker-network`
3. nginx.conf proxy settings

### Issue: Database data lost

**Solution:** Volumes are persisted. To check:
```bash
docker volume ls
docker volume inspect expensetracker_backend-data
```

### Issue: Build fails

**Clear Docker cache:**
```bash
docker system prune -a
docker volume prune
```

---

## 🚢 Production Deployment

### Deploy to Cloud (AWS/GCP/Azure)

1. **Push images to Docker Hub** (as shown above)

2. **Pull and run on server:**
   ```bash
   # On production server
   docker pull yourusername/expense-tracker-backend:latest
   docker pull yourusername/expense-tracker-frontend:latest
   docker-compose up -d
   ```

### Deploy to Kubernetes

Convert docker-compose to Kubernetes manifests:
```bash
# Install kompose
brew install kompose

# Convert
kompose convert -f docker-compose.yml
```

### Deploy to AWS ECS

Use AWS CLI to deploy:
```bash
# Create ECR repositories
aws ecr create-repository --repository-name expense-tracker-backend
aws ecr create-repository --repository-name expense-tracker-frontend

# Tag and push
docker tag expense-tracker-backend:latest <aws-account-id>.dkr.ecr.<region>.amazonaws.com/expense-tracker-backend:latest
docker push <aws-account-id>.dkr.ecr.<region>.amazonaws.com/expense-tracker-backend:latest
```

---

## 📝 Image Tags

Images are tagged with:
- `latest` - Most recent build
- `YYYYMMDD-HHMMSS` - Timestamp-based version

Example:
```
yourusername/expense-tracker-backend:latest
yourusername/expense-tracker-backend:20250101-143022
```

---

## 🔐 Security Best Practices

1. **Don't commit .env file** (use .env.example as template)
2. **Use secrets management** in production (AWS Secrets Manager, Vault)
3. **Scan images** for vulnerabilities:
   ```bash
   docker scan expense-tracker-backend:latest
   ```
4. **Update base images** regularly
5. **Use non-root user** in production Dockerfiles

---

## 📈 Monitoring

### View resource usage
```bash
docker stats
```

### Health checks
```bash
# Backend
curl http://localhost:8080/actuator/health

# Frontend
curl http://localhost/health
```

---

## 🆘 Support

For issues:
1. Check logs: `docker-compose logs -f`
2. Verify health: `docker ps`
3. Review configuration: `docker-compose config`
4. Restart services: `docker-compose restart`

---

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Docker Hub](https://hub.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)

---

## ✅ Checklist

Before deployment:
- [ ] .env file created with correct Docker Hub username
- [ ] Docker Desktop running
- [ ] Logged in to Docker Hub
- [ ] Ports 80 and 8080 available
- [ ] Sufficient disk space for images and volumes
- [ ] Backend builds successfully
- [ ] Frontend builds successfully
- [ ] Both services start and pass health checks
- [ ] Frontend can communicate with backend
- [ ] Database persists data across restarts

---

**Happy Dockerizing! 🐳**

