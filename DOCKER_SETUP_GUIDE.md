# Docker Setup Guide - Development vs Production

## Problem Fixed
The error `ECONNREFUSED ::1:8080` was caused by Vite dev server trying to connect to `localhost:8080`, which doesn't work in Docker containers. The backend is on a different container accessible via the service name `backend`.

## Solution
I've updated the configuration to use environment variables for the backend URL.

---

## Development Mode (with Vite dev server and hot reload)

### Files Used:
- `Dockerfile.dev` - Runs Vite dev server
- `docker-compose.dev.yml` - Development compose file
- `vite.config.js` - Uses `VITE_BACKEND_URL` environment variable

### Run Development:
```bash
# Stop any running containers
docker-compose down

# Start development environment
docker-compose -f docker-compose.dev.yml up --build

# Or run in detached mode
docker-compose -f docker-compose.dev.yml up -d --build
```

### Access:
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

### Features:
- Hot reload enabled
- Source code mounted as volume
- Changes reflect immediately without rebuild

---

## Production Mode (with nginx)

### Files Used:
- `Dockerfile` - Multi-stage build with nginx
- `docker-compose.yml` - Production compose file
- `nginx.conf.template` - Uses `BACKEND_URL` environment variable

### Build & Push Images:
```bash
# Make script executable
chmod +x docker-build-push.sh

# Build and push to Docker Hub
./docker-build-push.sh
```

### Run Production:
```bash
# Stop any running containers
docker-compose down

# Start production environment
docker-compose up -d
```

### Access:
- Frontend: http://localhost:80
- Backend: http://localhost:8080

### Features:
- Optimized production build
- Static files served by nginx
- Better performance

---

## Key Changes Made

### 1. vite.config.js
```javascript
const backendUrl = process.env.VITE_BACKEND_URL || 'http://localhost:8080';
```
Now reads backend URL from environment variable.

### 2. docker-compose.dev.yml
```yaml
environment:
  - VITE_BACKEND_URL=http://backend:8080
```
Sets backend URL to Docker service name.

### 3. docker-compose.yml (Production)
```yaml
environment:
  - BACKEND_URL=backend
  - VITE_BACKEND_URL=http://backend:8080
```
Both nginx and Vite can use the correct backend URL.

---

## Quick Commands

### Development:
```bash
# Start dev
docker-compose -f docker-compose.dev.yml up -d

# View logs
docker-compose -f docker-compose.dev.yml logs -f

# Stop
docker-compose -f docker-compose.dev.yml down
```

### Production:
```bash
# Build images
./docker-build-push.sh

# Start prod
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Rebuild Frontend Only:
```bash
# Dev
docker-compose -f docker-compose.dev.yml up -d --build frontend

# Prod
docker-compose up -d --build frontend
```

---

## Troubleshooting

### If you still see connection refused:
1. Make sure backend is running: `docker-compose ps`
2. Check backend logs: `docker-compose logs backend`
3. Verify network connectivity from frontend:
   ```bash
   docker exec expense-tracker-frontend wget -O- http://backend:8080/api/rules
   ```

### If using local development (not Docker):
Backend and frontend both run on localhost:
- Backend: http://localhost:8080
- Frontend: http://localhost:5173
- No changes needed - vite.config.js defaults to localhost

---

## Environment Variables Summary

| Variable | Used By | Purpose |
|----------|---------|---------|
| `VITE_BACKEND_URL` | Vite dev server | Proxy configuration |
| `BACKEND_URL` | nginx | Proxy pass in production |
| `DOCKER_USERNAME` | Build script | Docker Hub username |

---

## Current Status
✅ vite.config.js updated to use environment variable
✅ docker-compose.dev.yml created for development
✅ docker-compose.yml updated for production
✅ Dockerfile.dev created for dev mode
✅ Dockerfile already configured for production with nginx

**Next Step:** Run the appropriate docker-compose command based on your needs (dev or prod).
