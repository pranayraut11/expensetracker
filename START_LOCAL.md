# 🚀 Quick Start - Local Development

## ✅ Everything is Fixed and Ready!

### Run These Commands:

#### Terminal 1 - Start Backend:
```bash
cd /Users/p.raut/demoprojects/expensetracker/backend
mvn spring-boot:run
```

Wait for: `Started ExpenseTrackerApplication` message

---

#### Terminal 2 - Start Frontend:
```bash
cd /Users/p.raut/demoprojects/expensetracker/frontend
npm run dev
```

Wait for: `Local: http://localhost:5173/` message

---

#### Browser:
Open: **http://localhost:5173**

---

## That's It! 🎉

Your app should now work perfectly with:
- ✅ Backend running on port 8080
- ✅ Frontend running on port 5173
- ✅ Vite proxying all `/api/*` requests to backend
- ✅ No CORS errors
- ✅ No "static resource" errors

---

## What Changed:

1. **Vite Config** - Added proxy for `/api` → `http://localhost:8080`
2. **Environment** - Set empty base URL (Vite handles routing)

---

## Troubleshooting:

### If Backend Won't Start:
```bash
# Kill any process on port 8080
lsof -ti:8080 | xargs kill -9

# Try again
cd backend
mvn spring-boot:run
```

### If Frontend Won't Start:
```bash
# Kill any process on port 5173
lsof -ti:5173 | xargs kill -9

# Clear cache and restart
rm -rf node_modules/.vite
npm run dev
```

### If API Calls Fail:
1. Check backend is running: `curl http://localhost:8080/api/transactions/summary`
2. Check frontend dev server is running
3. Check browser console for errors
4. Restart both backend and frontend

---

**For more details:** See `LOCAL_DEV_SETUP.md`

