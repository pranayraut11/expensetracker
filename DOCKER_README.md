# 🐳 Docker Setup - Complete Guide

## Overview

This document provides complete instructions for containerizing and deploying the Expense Tracker application using Docker.

---

## 📁 Files Created

### Docker Configuration
- `backend/Dockerfile` - Backend container configuration
- `frontend/Dockerfile` - Frontend container configuration  
- `docker-compose.yml` - Multi-container orchestration
- `frontend/nginx.conf` - Nginx reverse proxy configuration

### Scripts
- `setup-docker.sh` - Interactive Docker Hub setup
- `docker-build-push.sh` - Build and push to Docker Hub
- `docker-build-local.sh` - Quick local build
- `docker-manager.sh` - Interactive management menu

### Environment Files
- `.env.example` - Template for Docker Hub username
- `frontend/.env.development` - Development API configuration
- `frontend/.env.production` - Production API configuration

### Documentation
- `DOCKER_QUICKSTART.md` - Quick start guide
- `DOCKER_DEPLOYMENT.md` - Comprehensive deployment guide
- `DOCKER_SETUP_SUMMARY.md` - Feature summary

---

## 🚀 Quick Start

### Method 1: Interactive Manager (Recommended)

```bash
./docker-manager.sh
```

This launches an interactive menu with options to:
1. Build and run locally
2. Build and push to Docker Hub
3. Pull from Docker Hub and run
4. Stop containers
5. View logs
6. Clean up

### Method 2: Manual Steps

```bash
# Setup
./setup-docker.sh

# Build locally and run
docker-compose up -d

# Or build and push to Docker Hub
./docker-build-push.sh
```

---

## 📋 Prerequisites

- Docker Desktop installed and running
- Docker Hub account (for pushing images)
- Ports 80 and 8080 available

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  Frontend Container (nginx:alpine)              │
│  ┌──────────────────────────────────────────┐  │
│  │  React SPA (Vite Build)                  │  │
│  │  Port: 80                                │  │
│  │                                          │  │
│  │  nginx.conf:                             │  │
│  │   - Serves static files                  │  │
│  │   - Proxies /api → backend:8080          │  │
│  │   - Proxies /rules → backend:8080        │  │
│  └──────────────────────────────────────────┘  │
│                     │                           │
└─────────────────────┼───────────────────────────┘
                      │ HTTP
                      ↓
┌─────────────────────────────────────────────────┐
│                                                 │
│  Backend Container (eclipse-temurin:17-alpine) │
│  ┌──────────────────────────────────────────┐  │
│  │  Spring Boot Application                 │  │
│  │  Port: 8080                              │  │
│  │                                          │  │
│  │  Features:                               │  │
│  │   - REST API                             │  │
│  │   - Drools Rules Engine                  │  │
│  │   - Excel Parsing                        │  │
│  │   - Transaction Management               │  │
│  └──────────────────────────────────────────┘  │
│                     │                           │
│                     ↓                           │
│  ┌──────────────────────────────────────────┐  │
│  │  H2 Database (File-based)                │  │
│  │  Volume: backend-data                    │  │
│  │  Path: /app/data                         │  │
│  └──────────────────────────────────────────┘  │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 🔧 Configuration Details

### Backend Image

**Dockerfile highlights:**
- Multi-stage build (Maven + JRE)
- Base: `eclipse-temurin:17-jre-alpine`
- Size: ~250 MB
- Health check: `/actuator/health`

**Environment variables:**
```yaml
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/expense-tracker-db
JAVA_OPTS=-Xms256m -Xmx512m
```

### Frontend Image

**Dockerfile highlights:**
- Multi-stage build (Node + Nginx)
- Base: `nginx:alpine`
- Size: ~30 MB
- Health check: `/health`

**Nginx configuration:**
- Serves static files from `/usr/share/nginx/html`
- Proxies API calls to `backend:8080`
- Gzip compression enabled
- Security headers configured
- SPA routing support (all routes → index.html)

### Docker Compose

**Services:**
- `backend` - Spring Boot API
- `frontend` - React + Nginx

**Volumes:**
- `backend-data` - Persistent H2 database

**Networks:**
- `expense-tracker-network` - Bridge network for inter-container communication

**Health Checks:**
- Backend: Waits for `/actuator/health` to return 200
- Frontend: Depends on backend health

---

## 🌐 API Configuration

### Development Mode
When running `npm run dev`:
- API calls go to `http://localhost:8080`
- Configured in `frontend/.env.development`

### Production Mode (Docker)
When built with Docker:
- API calls use relative URLs (empty baseURL)
- Nginx proxies `/api` and `/rules` to backend
- Configured in `frontend/.env.production`

This is handled automatically in `frontend/src/services/api.js`:
```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
});
```

---

## 📊 Image Tags

When you push to Docker Hub:
- `latest` - Always points to most recent build
- `YYYYMMDD-HHMMSS` - Timestamp-based version

Example:
```
yourusername/expense-tracker-backend:latest
yourusername/expense-tracker-backend:20250101-143022
yourusername/expense-tracker-frontend:latest
yourusername/expense-tracker-frontend:20250101-143022
```

---

## 🛠️ Common Commands

### Start Everything
```bash
docker-compose up -d
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Stop Everything
```bash
docker-compose down
```

### Rebuild After Code Changes
```bash
docker-compose up -d --build
```

### Check Status
```bash
docker-compose ps
```

### Execute Commands in Container
```bash
# Access backend shell
docker exec -it expense-tracker-backend sh

# Access frontend shell
docker exec -it expense-tracker-frontend sh
```

### Remove Everything (Including Data)
```bash
docker-compose down -v
```

---

## 🐛 Troubleshooting

### Backend Won't Start

**Symptoms:** Backend container exits or restarts continuously

**Check:**
```bash
docker-compose logs backend
```

**Common causes:**
- Port 8080 already in use
- Insufficient memory
- Database initialization error

**Solutions:**
```bash
# Check if port is in use
lsof -i :8080

# Increase memory in docker-compose.yml
JAVA_OPTS=-Xms512m -Xmx1024m

# Clear and restart
docker-compose down -v
docker-compose up -d
```

### Frontend Can't Reach Backend

**Symptoms:** 502 Bad Gateway or API errors

**Check:**
```bash
# Verify backend is healthy
docker-compose ps

# Check nginx config
docker exec expense-tracker-frontend cat /etc/nginx/conf.d/default.conf

# Test backend from frontend container
docker exec expense-tracker-frontend wget -O- http://backend:8080/actuator/health
```

**Solutions:**
```bash
# Restart frontend
docker-compose restart frontend

# Rebuild with correct config
docker-compose up -d --build frontend
```

### Database Data Lost

**Check volume:**
```bash
docker volume ls
docker volume inspect expensetracker_backend-data
```

**Backup database:**
```bash
# Copy database file from container
docker cp expense-tracker-backend:/app/data/expense-tracker-db.mv.db ./backup.mv.db

# Restore database file
docker cp ./backup.mv.db expense-tracker-backend:/app/data/expense-tracker-db.mv.db
```

### Build Fails

**Clear Docker cache:**
```bash
docker system prune -a
docker volume prune
```

**Build with no cache:**
```bash
docker-compose build --no-cache
```

---

## 🚢 Production Deployment

### Option 1: Docker Hub

```bash
# Push images
./docker-build-push.sh

# On production server
docker-compose pull
docker-compose up -d
```

### Option 2: AWS ECR + ECS

```bash
# Create ECR repositories
aws ecr create-repository --repository-name expense-tracker-backend
aws ecr create-repository --repository-name expense-tracker-frontend

# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com

# Tag and push
docker tag expense-tracker-backend:latest <account>.dkr.ecr.us-east-1.amazonaws.com/expense-tracker-backend:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/expense-tracker-backend:latest

# Deploy to ECS (use AWS Console or CLI)
```

### Option 3: Google Cloud Run

```bash
# Build and push to GCR
gcloud builds submit --tag gcr.io/PROJECT-ID/expense-tracker-backend backend/
gcloud builds submit --tag gcr.io/PROJECT-ID/expense-tracker-frontend frontend/

# Deploy
gcloud run deploy expense-tracker-backend --image gcr.io/PROJECT-ID/expense-tracker-backend --platform managed
gcloud run deploy expense-tracker-frontend --image gcr.io/PROJECT-ID/expense-tracker-frontend --platform managed
```

### Option 4: DigitalOcean App Platform

```bash
# Push to Docker Hub first
./docker-build-push.sh

# Use DigitalOcean Console to:
# 1. Create new app
# 2. Select Docker Hub
# 3. Enter image: yourusername/expense-tracker-backend:latest
# 4. Configure environment variables
# 5. Deploy
```

---

## 🔐 Security Recommendations

### For Production

1. **Use HTTPS**
   - Add SSL certificates
   - Configure nginx for HTTPS
   - Redirect HTTP to HTTPS

2. **Environment Variables**
   - Use Docker secrets or cloud-native secrets management
   - Don't commit `.env` file

3. **Database**
   - Use PostgreSQL or MySQL instead of H2
   - Configure proper authentication
   - Enable encryption at rest

4. **Network**
   - Use private networks
   - Implement rate limiting
   - Add WAF (Web Application Firewall)

5. **Images**
   - Scan for vulnerabilities
   - Keep base images updated
   - Use specific versions (not `latest`)

6. **Monitoring**
   - Add logging aggregation
   - Set up health monitoring
   - Configure alerts

---

## 📈 Performance Optimization

### Backend

```yaml
# Increase JVM heap
JAVA_OPTS=-Xms512m -Xmx1024m

# Enable GC logging
JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -Xlog:gc*:file=/app/logs/gc.log
```

### Frontend

```nginx
# Enable HTTP/2
listen 443 ssl http2;

# Optimize caching
location /assets {
    expires 1y;
    add_header Cache-Control "public, immutable";
}

# Enable brotli compression (in addition to gzip)
brotli on;
brotli_types text/plain text/css application/json application/javascript;
```

### Database

```properties
# Connection pooling
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5

# Query optimization
spring.jpa.properties.hibernate.jdbc.batch_size=50
```

---

## 📚 Additional Resources

- [DOCKER_QUICKSTART.md](./DOCKER_QUICKSTART.md) - Get started quickly
- [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md) - Detailed deployment guide
- [DOCKER_SETUP_SUMMARY.md](./DOCKER_SETUP_SUMMARY.md) - Feature summary

---

## ✅ Deployment Checklist

### Before First Deployment

- [ ] Docker Desktop installed and running
- [ ] Docker Hub account created
- [ ] `.env` file created with Docker Hub username
- [ ] Ports 80 and 8080 available
- [ ] Code committed to version control

### Building Images

- [ ] Backend builds without errors
- [ ] Frontend builds without errors
- [ ] Images tagged correctly
- [ ] Health checks pass

### Testing Locally

- [ ] `docker-compose up -d` succeeds
- [ ] Frontend accessible at http://localhost
- [ ] Backend API responds at http://localhost:8080
- [ ] Can upload Excel file
- [ ] Transactions display correctly
- [ ] Rules engine works
- [ ] Database persists after restart

### Pushing to Docker Hub

- [ ] Logged in to Docker Hub
- [ ] Images pushed successfully
- [ ] Can pull images from another machine

### Production Deployment

- [ ] SSL certificates configured
- [ ] Environment variables set
- [ ] Database backup strategy in place
- [ ] Monitoring configured
- [ ] Logs accessible
- [ ] Health checks enabled
- [ ] DNS configured correctly

---

## 🎉 Success!

Your Expense Tracker is now fully containerized and ready for deployment!

**What you can do now:**

1. **Run Locally:** `./docker-manager.sh` → Option 1
2. **Deploy to Cloud:** `./docker-build-push.sh` → Deploy on any platform
3. **Share with Team:** Push to Docker Hub → Team can pull and run
4. **Scale:** Use Kubernetes or cloud container services

For any issues, check the troubleshooting section or view logs with `docker-compose logs -f`.

**Happy containerizing! 🐳**

