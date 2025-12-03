# 🐳 Docker Setup - Complete Package

## 📚 Documentation Index

This folder contains everything you need to containerize and deploy the Expense Tracker application.

### 🚀 Start Here

1. **[DOCKER_QUICKSTART.md](./DOCKER_QUICKSTART.md)** ⭐
   - 3-minute quick start guide
   - Fastest way to get running

2. **[DOCKER_README.md](./DOCKER_README.md)** 📖
   - Complete Docker setup guide
   - Architecture overview
   - Troubleshooting
   - Production deployment

3. **[DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md)** 🚢
   - Detailed deployment scenarios
   - Cloud platform guides
   - Best practices

4. **[DOCKER_SETUP_SUMMARY.md](./DOCKER_SETUP_SUMMARY.md)** 📊
   - What was created
   - Feature summary
   - Quick reference

---

## 🛠️ Scripts

### Interactive Manager (Recommended)
```bash
./docker-manager.sh
```
One-stop interactive menu for all Docker operations.

### Individual Scripts

- **`setup-docker.sh`** - Configure Docker Hub username
- **`docker-build-push.sh`** - Build and push to Docker Hub
- **`docker-build-local.sh`** - Quick local build

---

## 📁 Docker Files

### Configuration
- `backend/Dockerfile` - Backend container
- `frontend/Dockerfile` - Frontend container
- `docker-compose.yml` - Multi-container orchestration
- `frontend/nginx.conf` - Nginx configuration

### Environment
- `.env.example` - Template
- `frontend/.env.development` - Dev config
- `frontend/.env.production` - Prod config

---

## ⚡ Quick Commands

```bash
# First time setup
./setup-docker.sh

# Build and run locally
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Build and push to Docker Hub
./docker-build-push.sh
```

---

## 🎯 What's Included

✅ **Multi-stage Docker builds** - Optimized image sizes
✅ **Health checks** - Both frontend and backend
✅ **Persistent storage** - H2 database survives restarts
✅ **Auto-scaling ready** - Works with Kubernetes
✅ **Production ready** - Security headers, gzip, caching
✅ **Easy deployment** - Works on any cloud platform
✅ **Development friendly** - Hot reload in dev mode
✅ **Comprehensive docs** - Everything explained

---

## 🌐 Access Points

| Service | Development | Docker |
|---------|-------------|--------|
| Frontend | http://localhost:5173 | http://localhost |
| Backend | http://localhost:8080 | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console | http://localhost:8080/h2-console |

---

## 📦 Docker Images

After building and pushing:
- `yourusername/expense-tracker-backend:latest`
- `yourusername/expense-tracker-backend:YYYYMMDD-HHMMSS`
- `yourusername/expense-tracker-frontend:latest`
- `yourusername/expense-tracker-frontend:YYYYMMDD-HHMMSS`

---

## 🔍 Choose Your Path

### Path 1: Quick Local Test
```bash
./docker-manager.sh
# Select option 1
```
**Result:** Running locally in 2 minutes

### Path 2: Deploy to Production
```bash
./setup-docker.sh
./docker-build-push.sh
# Deploy on cloud platform
```
**Result:** Images on Docker Hub, ready for deployment

### Path 3: Development Mode
```bash
cd backend && mvn spring-boot:run
cd frontend && npm run dev
```
**Result:** Hot reload development environment

---

## ✅ Pre-flight Checklist

Before you start:
- [ ] Docker Desktop installed and running
- [ ] Docker Hub account (for pushing images)
- [ ] Ports 80 and 8080 available
- [ ] At least 2GB free disk space

---

## 🆘 Need Help?

1. **Quick issues:** Check [DOCKER_README.md](./DOCKER_README.md) troubleshooting
2. **Deployment:** See [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md)
3. **Logs:** Run `docker-compose logs -f`

---

## 🎓 Learning Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Reference](https://docs.docker.com/compose/)
- [Spring Boot + Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [React + Docker Best Practices](https://mherman.org/blog/dockerizing-a-react-app/)

---

**Ready to containerize? Run `./docker-manager.sh` to get started! 🚀**

