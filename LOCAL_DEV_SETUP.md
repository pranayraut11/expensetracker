# Local Development Setup - Fixed!

## ✅ Problem Fixed for Local Development

When running locally (not in Docker), the nginx.conf is **NOT used**. Instead, Vite dev server handles the frontend.

## What I Changed:

### 1. **Vite Configuration** - Added Proxy
**File:** `frontend/vite.config.js`

```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: true,
    proxy: {                    // ✅ ADDED THIS
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
```

**Why:** This tells Vite to forward all `/api/*` requests to your backend at `http://localhost:8080`

### 2. **Environment Variable** - Set Empty Base URL
**File:** `frontend/.env.development`

```env
# Empty base URL - Vite proxy handles /api/* routing
VITE_API_BASE_URL=
```

**Why:** When base URL is empty, axios makes requests to the same origin (Vite dev server), which then proxies to backend.

## How It Works Now:

```
Browser Request:
http://localhost:5173/api/transactions/summary
         ↓
Vite Dev Server (Port 5173)
         ↓ (proxy)
Backend (Port 8080)
/api/transactions/summary
```

## How to Run Locally:

### Step 1: Start Backend
```bash
cd /Users/p.raut/demoprojects/expensetracker/backend
mvn spring-boot:run
```

**Wait for:**
```
Started ExpenseTrackerApplication in X.XXX seconds
```

### Step 2: Start Frontend (in a new terminal)
```bash
cd /Users/p.raut/demoprojects/expensetracker/frontend

# Install dependencies if needed
npm install

# Start dev server
npm run dev
```

**Should see:**
```
VITE v5.x.x  ready in XXX ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

### Step 3: Open Browser
Navigate to: `http://localhost:5173`

## Verify Everything Works:

### ✅ Check Backend is Running:
```bash
curl http://localhost:8080/api/transactions/summary
```
Should return JSON response.

### ✅ Check Frontend:
1. Open `http://localhost:5173` in browser
2. Open DevTools → Network tab
3. Navigate to Dashboard
4. Should see successful API calls to `/api/*`

### ✅ Example Network Requests:
- `GET http://localhost:5173/api/transactions/summary` → 200 OK
- `GET http://localhost:5173/api/analytics/totals` → 200 OK
- `POST http://localhost:5173/api/upload` → 200 OK

**Note:** All requests go through Vite proxy at `:5173`, which forwards to backend at `:8080`

## Troubleshooting:

### Error: "Failed to fetch"
**Problem:** Backend is not running
**Solution:** 
```bash
cd backend
mvn spring-boot:run
```

### Error: "CORS error" or "No Access-Control-Allow-Origin"
**Problem:** CORS configuration issue
**Solution:** The CORS is already configured correctly in `backend/src/main/java/com/example/expensetracker/config/WebConfig.java`

Restart backend:
```bash
cd backend
mvn clean spring-boot:run
```

### Error: "Cannot GET /api/..."
**Problem:** Vite proxy not working
**Solution:** 
1. Stop frontend: `Ctrl+C`
2. Clear Vite cache: `rm -rf node_modules/.vite`
3. Restart: `npm run dev`

### Port Already in Use:
**Backend (8080):**
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9
```

**Frontend (5173):**
```bash
# Find and kill process
lsof -ti:5173 | xargs kill -9
```

## Files Modified:

1. ✅ `frontend/vite.config.js` - Added proxy configuration
2. ✅ `frontend/.env.development` - Set empty base URL

## Complete Development Workflow:

```bash
# Terminal 1 - Backend
cd /Users/p.raut/demoprojects/expensetracker/backend
mvn spring-boot:run

# Terminal 2 - Frontend  
cd /Users/p.raut/demoprojects/expensetracker/frontend
npm run dev

# Browser
# Open: http://localhost:5173
```

## Architecture:

```
┌─────────────────────────────────────────┐
│  Browser (http://localhost:5173)        │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│  Vite Dev Server (Port 5173)            │
│  - Serves React app                     │
│  - Proxies /api/* to :8080              │
└────────────────┬────────────────────────┘
                 │ /api/* requests
                 ▼
┌─────────────────────────────────────────┐
│  Spring Boot Backend (Port 8080)        │
│  - Handles /api/* endpoints             │
│  - CORS enabled for :5173               │
└─────────────────────────────────────────┘
```

## No Docker Needed!

When running locally, you **DO NOT** need Docker. Just run:
1. Backend with Maven
2. Frontend with npm

The nginx.conf is only used when you build and deploy with Docker.

---

**Status: ✅ READY FOR LOCAL DEVELOPMENT**

Just run the commands above and you're good to go! 🚀

