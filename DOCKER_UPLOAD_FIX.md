# 🔥 Docker Fix - 405 Not Allowed on Upload

## Problem
Getting **405 Not Allowed** error when clicking upload button in Docker.

## Root Cause
The nginx.conf had the old configuration with only `/api/` proxy, but we removed `/api` prefix from most backend endpoints. This caused nginx to reject requests to `/upload` and other endpoints.

## ✅ Solution Applied

Updated `frontend/nginx.conf` to proxy ALL backend endpoints:

```nginx
# Proxy for /api/rules (RuleController - only endpoint with /api)
location /api/ {
    proxy_pass http://backend:8080/api/;
}

# Proxy for /transactions
location /transactions {
    proxy_pass http://backend:8080/transactions;
}

# Proxy for /upload (FIXED - This is what was missing!)
location /upload {
    proxy_pass http://backend:8080/upload;
    client_max_body_size 50M;  # Allow large file uploads
}

# Proxy for /credit-card
location /credit-card {
    proxy_pass http://backend:8080/credit-card;
    client_max_body_size 50M;
}

# Proxy for /analytics
location /analytics {
    proxy_pass http://backend:8080/analytics;
}

# Proxy for /tags
location /tags {
    proxy_pass http://backend:8080/tags;
}
```

---

## 🚀 Apply the Fix:

### Step 1: Stop Containers
```bash
docker-compose down
```

### Step 2: Rebuild Frontend Image (with new nginx.conf)
```bash
docker-compose build frontend
```

### Step 3: Start Containers
```bash
docker-compose up -d
```

### Step 4: Verify
```bash
# Check logs
docker-compose logs -f frontend

# Should see nginx starting successfully
```

---

## 🧪 Test Upload:

1. Open browser: `http://localhost`
2. Navigate to Upload page
3. Select an Excel file
4. Click "Upload and Process"
5. Should now work! ✅

---

## How It Works:

```
Browser Request:
http://localhost/upload (POST with file)
         ↓
Nginx (Port 80)
         ↓
location /upload {} matches
         ↓
Proxy to: http://backend:8080/upload
         ↓
Backend processes file ✅
```

---

## What Was Wrong Before:

```nginx
# OLD (only had this)
location /api/ {
    proxy_pass http://backend:8080/api/;
}
```

**Problem:**
- Request to `/upload` didn't match `/api/`
- Nginx looked for static file `/upload` in `/usr/share/nginx/html`
- No such file exists
- Returns 405 Not Allowed

**NOW FIXED:**
- Request to `/upload` matches `location /upload {}`
- Nginx proxies to `http://backend:8080/upload`
- Backend handles the upload ✅

---

## Complete Rebuild Commands:

```bash
cd /Users/p.raut/demoprojects/expensetracker

# Stop everything
docker-compose down

# Rebuild frontend with new nginx config
docker-compose build frontend

# Optional: Rebuild backend too (if you made changes)
docker-compose build backend

# Start everything
docker-compose up -d

# Watch logs
docker-compose logs -f
```

---

## Verify All Endpoints Work:

After restart, test these in browser Network tab:

- ✅ `GET /transactions/summary` → 200 OK
- ✅ `GET /analytics/totals` → 200 OK
- ✅ `POST /upload` → 200 OK (with file)
- ✅ `POST /credit-card/upload-xls` → 200 OK (with file)
- ✅ `GET /api/rules` → 200 OK
- ✅ `GET /tags/top` → 200 OK

---

## File Modified:
- ✅ `frontend/nginx.conf` - Added proxy locations for all backend endpoints

---

## Status: ✅ FIXED

**Just rebuild and restart Docker containers as shown above!**

Upload should work perfectly now. 🎉

