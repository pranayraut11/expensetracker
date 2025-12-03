# 🚀 Quick Start - Docker Setup

## Step 1: Setup Docker Hub Username

Run the setup script:
```bash
./setup-docker.sh
```

This will prompt you for your Docker Hub username and create a `.env` file.

## Step 2: Build and Push to Docker Hub

```bash
./docker-build-push.sh
```

This will:
- Login to Docker Hub
- Build backend and frontend images
- Tag them with `latest` and timestamp
- Push to your Docker Hub account

## Step 3: Run Locally

```bash
docker-compose up -d
```

Access:
- Frontend: http://localhost
- Backend: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console

## View Logs

```bash
docker-compose logs -f
```

## Stop

```bash
docker-compose down
```

---

**For detailed documentation, see [DOCKER_DEPLOYMENT.md](./DOCKER_DEPLOYMENT.md)**

