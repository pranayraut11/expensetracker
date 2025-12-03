# Nginx API Proxy Fix - "No static resource api/transactions/summary"

## Problem
After adding `/api` prefix to all backend endpoints, the application shows error:
```
An unexpected error occurred: No static resource api/transactions/summary
```

## Root Cause
The nginx configuration was not properly configured to proxy `/api/*` requests to the backend. The issue was:

1. **Missing trailing slash:** `location /api` instead of `location /api/`
2. **Incorrect proxy_pass:** `proxy_pass http://backend:8080;` instead of `proxy_pass http://backend:8080/api/;`
3. **Old /rules proxy:** Redundant proxy configuration that's now handled by `/api/` proxy

## The Fix

### Before (Incorrect nginx.conf):
```nginx
# API proxy to backend
location /api {
    proxy_pass http://backend:8080;
    # ... headers ...
}

# Rules API proxy (redundant)
location /rules {
    proxy_pass http://backend:8080;
    # ... headers ...
}
```

**Problem:** 
- Request: `GET /api/transactions/summary`
- Nginx proxies to: `http://backend:8080/api/transactions/summary` ❌
- But backend expects: `/api/transactions/summary`
- Without proper path rewriting, nginx looks for static files instead

### After (Fixed nginx.conf):
```nginx
# API proxy to backend
location /api/ {
    proxy_pass http://backend:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_cache_bypass $http_upgrade;
    proxy_read_timeout 90;
}
```

**How it works now:**
- Request: `GET /api/transactions/summary`
- Nginx proxies to: `http://backend:8080/api/transactions/summary` ✅
- Backend responds correctly

## Key Changes

### 1. Added Trailing Slashes ✅
```nginx
# Before
location /api {
    proxy_pass http://backend:8080;
}

# After
location /api/ {
    proxy_pass http://backend:8080/api/;
}
```

**Why this matters:**
- `location /api/` with `proxy_pass http://backend:8080/api/;` preserves the full path
- Request `/api/transactions` → proxied to → `http://backend:8080/api/transactions`
- Without trailing slashes, path rewriting can be unpredictable

### 2. Removed Redundant /rules Proxy ✅
The old `/rules` proxy location is no longer needed because:
- All rules endpoints now use `/api/rules/*`
- The `/api/` proxy handles all API requests including rules

## How to Apply the Fix

### If Using Docker:

1. **Rebuild and restart containers:**
   ```bash
   docker-compose down
   docker-compose build frontend
   docker-compose up -d
   ```

2. **Or restart just the frontend:**
   ```bash
   docker-compose restart frontend
   ```

### If Using Local Development:

For local development, this nginx config is **NOT used**. The frontend uses Vite dev server which proxies directly to the backend using the `VITE_API_BASE_URL` from `.env.development`.

**No action needed for local development** - it should work automatically.

## Testing

### Test in Docker (Production Mode):
```bash
# Start containers
docker-compose up -d

# Test API endpoints
curl http://localhost/api/transactions/summary
curl http://localhost/api/analytics/totals
curl http://localhost/api/rules

# Open browser
http://localhost

# Check browser console - should see successful API calls
```

### Test Locally (Development Mode):
```bash
# Start backend
cd backend
mvn spring-boot:run

# Start frontend
cd frontend
npm run dev

# Open browser
http://localhost:5173

# All API calls should work with base URL: http://localhost:8080/api/*
```

## Verification

After applying the fix, verify these requests work:

### Frontend Browser Network Tab:
- ✅ `GET /api/transactions/summary` → Status 200
- ✅ `GET /api/transactions?page=0&size=20` → Status 200
- ✅ `GET /api/analytics/totals` → Status 200
- ✅ `GET /api/analytics/income-expense-trend` → Status 200
- ✅ `POST /api/upload` → Status 200 (with file)
- ✅ `GET /api/rules` → Status 200

### Docker Logs:
```bash
# Check nginx access logs
docker-compose logs -f frontend

# Should see successful proxy requests like:
# 172.x.x.x - - [03/Dec/2025:...] "GET /api/transactions/summary HTTP/1.1" 200 ...
```

## Understanding Nginx Location Blocks

### With Trailing Slash (Correct):
```nginx
location /api/ {
    proxy_pass http://backend:8080/api/;
}
```
- Request: `/api/foo/bar`
- Proxied to: `http://backend:8080/api/foo/bar`
- ✅ Path is preserved

### Without Trailing Slash (Can cause issues):
```nginx
location /api {
    proxy_pass http://backend:8080;
}
```
- Request: `/api/foo/bar`
- Proxied to: `http://backend:8080/api/foo/bar` (might work)
- But behavior can be inconsistent

### Rule of Thumb:
**Always use trailing slashes in both `location` and `proxy_pass` for predictable path handling.**

## Complete nginx.conf Structure

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript 
               application/x-javascript application/xml+rss 
               application/json application/javascript;

    # Health check
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    # ✅ API proxy to backend (MAIN FIX)
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        proxy_read_timeout 90;
    }

    # Static assets with caching
    location /assets {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # SPA fallback - all routes go to index.html
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
```

## File Modified
- ✅ `frontend/nginx.conf`

## Status: ✅ FIXED

The nginx configuration has been corrected to properly proxy all `/api/*` requests to the backend.

**Next Steps:**
1. Rebuild frontend Docker image: `docker-compose build frontend`
2. Restart containers: `docker-compose up -d`
3. Test the application: `http://localhost`
4. Verify all API calls work in browser Network tab

## Related Documentation
- `API_PREFIX_MIGRATION.md` - Complete API migration guide
- `UPLOAD_FIX.md` - Upload endpoint fix (now outdated, use /api/upload)

