# API Prefix Migration - /api URL Structure

## Overview
All API endpoints now use the `/api` prefix for better organization and clarity.

## Changes Made

### Backend (Java Spring Boot Controllers)

All controllers have been updated to use `/api` prefix in their `@RequestMapping` annotation:

#### 1. **TransactionController** ✅
- **Before:** `@RequestMapping("/transactions")`
- **After:** `@RequestMapping("/api/transactions")`
- **Endpoints:**
  - `GET /api/transactions` - Get all transactions (paginated)
  - `GET /api/transactions/summary` - Get transaction summary
  - `PUT /api/transactions/{id}/category` - Update transaction category
  - `GET /api/transactions/balance-summary` - Get balance summary

#### 2. **UploadController** ✅
- **Already had:** `@RequestMapping("/api/upload")`
- **No change needed**
- **Endpoints:**
  - `POST /api/upload` - Upload bank statement

#### 3. **UploadProxyController** ✅
- **Before:** `@RequestMapping("/upload")`
- **After:** `@RequestMapping("/api/upload")`
- **Endpoints:**
  - `POST /api/upload` - Upload bank statement (proxy)
  - `GET /api/upload` - Returns method not allowed message

#### 4. **CreditCardStatementController** ✅
- **Before:** `@RequestMapping("/credit-card")`
- **After:** `@RequestMapping("/api/credit-card")`
- **Endpoints:**
  - `POST /api/credit-card/upload-xls` - Upload credit card statement

#### 5. **TotalsController** ✅
- **Before:** `@RequestMapping("/analytics")`
- **After:** `@RequestMapping("/api/analytics")`
- **Endpoints:**
  - `GET /api/analytics/totals` - Get total credit/debit amounts

#### 6. **IncomeExpenseTrendController** ✅
- **Before:** `@RequestMapping("/analytics")`
- **After:** `@RequestMapping("/api/analytics")`
- **Endpoints:**
  - `GET /api/analytics/income-expense-trend` - Get income vs expense trend
  - `GET /api/analytics/category-expenses` - Get category-wise expenses

#### 7. **TagController** ✅
- **Before:** `@RequestMapping("/tags")`
- **After:** `@RequestMapping("/api/tags")`
- **Endpoints:**
  - `GET /api/tags/top` - Get top tags
  - `GET /api/tags/search` - Search tags

#### 8. **RuleController** ✅
- **Already had:** `@RequestMapping("/api/rules")`
- **No change needed**
- **Endpoints:**
  - `GET /api/rules` - Get all rules
  - `POST /api/rules` - Create rule
  - `PUT /api/rules/{id}` - Update rule
  - `DELETE /api/rules/{id}` - Delete rule
  - `POST /api/rules/reload` - Reload rules
  - `GET /api/rules/export` - Export rules
  - `POST /api/rules/import` - Import rules

---

### Frontend (JavaScript API Services)

All API service files have been updated to use `/api` prefix:

#### 1. **transactionApi.js** ✅
```javascript
// Before → After
'/upload' → '/api/upload'
'/transactions/summary' → '/api/transactions/summary'
'/transactions' → '/api/transactions'
'/transactions/{id}/category' → '/api/transactions/{id}/category'
'/tags/search' → '/api/tags/search'
'/tags/top' → '/api/tags/top'
```

#### 2. **creditCardApi.js** ✅
```javascript
// Before → After
'/credit-card/upload-xls' → '/api/credit-card/upload-xls'
```

#### 3. **totalsApi.js** ✅
```javascript
// Before → After
'/analytics/totals' → '/api/analytics/totals'
```

#### 4. **categoryExpenseApi.js** ✅
```javascript
// Before → After
'/analytics/category-expenses' → '/api/analytics/category-expenses'
```

#### 5. **incomeExpenseTrendApi.js** ✅
```javascript
// Before → After
'/analytics/income-expense-trend' → '/api/analytics/income-expense-trend'
```

#### 6. **balanceSummaryApi.js** ✅
```javascript
// Before → After
'/transactions/balance-summary' → '/api/transactions/balance-summary'
```

#### 7. **ruleService.js** ✅
- **Already had `/api` prefix** - No changes needed

---

## Complete API Endpoint List

### Upload Endpoints
- `POST /api/upload` - Upload bank statement Excel file
- `POST /api/credit-card/upload-xls` - Upload credit card statement Excel file

### Transaction Endpoints
- `GET /api/transactions` - Get transactions (paginated, filtered, sorted)
- `GET /api/transactions/summary` - Get transaction summary
- `GET /api/transactions/balance-summary` - Get balance summary
- `PUT /api/transactions/{id}/category` - Update transaction category

### Analytics Endpoints
- `GET /api/analytics/totals` - Get total credit/debit amounts
- `GET /api/analytics/income-expense-trend` - Get income vs expense trend
- `GET /api/analytics/category-expenses` - Get category-wise expenses

### Tag Endpoints
- `GET /api/tags/top` - Get top transaction tags
- `GET /api/tags/search` - Search transaction tags

### Rule Endpoints
- `GET /api/rules` - Get all categorization rules
- `POST /api/rules` - Create a new rule
- `PUT /api/rules/{id}` - Update a rule
- `DELETE /api/rules/{id}` - Delete a rule
- `POST /api/rules/reload` - Reload rules from database
- `GET /api/rules/export` - Export rules as JSON
- `POST /api/rules/import` - Import rules from JSON

---

## URL Structure

### Development Mode
- **Frontend:** `http://localhost:5173`
- **Backend:** `http://localhost:8080`
- **API Base URL:** `http://localhost:8080` (from .env.development)
- **Example:** `http://localhost:8080/api/transactions`

### Production Mode (Docker)
- **Frontend:** `http://localhost` (nginx)
- **Backend:** `http://backend:8080` (internal)
- **API Base URL:** Empty string (nginx proxy)
- **Example:** `http://localhost/api/transactions` → proxied to `http://backend:8080/api/transactions`

---

## Environment Configuration

### Frontend - `.env.development`
```env
VITE_API_BASE_URL=http://localhost:8080
```

### Frontend - `.env.production`
```env
VITE_API_BASE_URL=
```

### Axios Configuration - `api.js`
```javascript
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "",
});
```

This configuration automatically handles:
- **Development:** Prepends `http://localhost:8080` to all API calls
- **Production:** Uses relative URLs (empty baseURL) which nginx proxies to backend

---

## Nginx Configuration (Production)

Your nginx configuration should proxy `/api/*` requests to the backend:

```nginx
location /api/ {
    proxy_pass http://backend:8080/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

---

## Testing

### Test Backend Changes
```bash
# Start backend
cd backend
mvn spring-boot:run

# Test endpoints (examples)
curl http://localhost:8080/api/transactions
curl http://localhost:8080/api/analytics/totals
curl http://localhost:8080/api/rules
```

### Test Frontend Changes
```bash
# Start frontend
cd frontend
npm run dev

# Open browser
http://localhost:5173

# All API calls should now use /api prefix
# Check browser Network tab to verify endpoints
```

### Test Upload
1. Navigate to `/upload` page
2. Upload a bank statement Excel file
3. Request should go to: `http://localhost:8080/api/upload`
4. Check console for success/error messages

---

## Migration Checklist

### Backend ✅
- [x] TransactionController → `/api/transactions`
- [x] UploadController → `/api/upload` (already done)
- [x] UploadProxyController → `/api/upload`
- [x] CreditCardStatementController → `/api/credit-card`
- [x] TotalsController → `/api/analytics`
- [x] IncomeExpenseTrendController → `/api/analytics`
- [x] TagController → `/api/tags`
- [x] RuleController → `/api/rules` (already done)

### Frontend ✅
- [x] transactionApi.js → Updated all endpoints
- [x] creditCardApi.js → Updated upload endpoint
- [x] totalsApi.js → Updated analytics endpoint
- [x] categoryExpenseApi.js → Updated analytics endpoint
- [x] incomeExpenseTrendApi.js → Updated analytics endpoint
- [x] balanceSummaryApi.js → Updated transactions endpoint
- [x] ruleService.js → (already had /api prefix)

### Documentation ✅
- [x] Created this comprehensive migration guide
- [x] Updated UPLOAD_FIX.md references
- [x] No breaking changes - all endpoints updated consistently

---

## Benefits of /api Prefix

1. **Clear Separation:** Distinguishes API endpoints from static content
2. **Easier Proxy Configuration:** Nginx can easily proxy all `/api/*` to backend
3. **Better Organization:** Groups all API endpoints under a common path
4. **Future-Proof:** Allows for API versioning (e.g., `/api/v2/...`)
5. **Industry Standard:** Follows common REST API conventions

---

## Rollback (If Needed)

If you need to rollback these changes:

1. **Backend:** Change all `@RequestMapping("/api/...")` back to `@RequestMapping("/...")`
2. **Frontend:** Remove `/api` prefix from all service files
3. **Restart:** Restart both backend and frontend services

---

## Files Modified

### Backend (8 files)
1. `backend/src/main/java/com/example/expensetracker/controller/TransactionController.java`
2. `backend/src/main/java/com/example/expensetracker/controller/UploadProxyController.java`
3. `backend/src/main/java/com/example/expensetracker/controller/CreditCardStatementController.java`
4. `backend/src/main/java/com/example/expensetracker/controller/TotalsController.java`
5. `backend/src/main/java/com/example/expensetracker/controller/IncomeExpenseTrendController.java`
6. `backend/src/main/java/com/example/expensetracker/controller/TagController.java`
7. `backend/src/main/java/com/example/expensetracker/controller/UploadController.java` (no change)
8. `backend/src/main/java/com/example/expensetracker/controller/RuleController.java` (no change)

### Frontend (6 files)
1. `frontend/src/services/transactionApi.js`
2. `frontend/src/services/creditCardApi.js`
3. `frontend/src/services/totalsApi.js`
4. `frontend/src/services/categoryExpenseApi.js`
5. `frontend/src/services/incomeExpenseTrendApi.js`
6. `frontend/src/services/balanceSummaryApi.js`
7. `frontend/src/services/ruleService.js` (no change - already had /api)

---

## Status: ✅ COMPLETE

All API endpoints now use the `/api` prefix in both backend and frontend!

**Next Steps:**
1. Restart your backend: `mvn spring-boot:run`
2. Restart your frontend: `npm run dev`
3. Test all features to ensure everything works
4. For production, update nginx config to proxy `/api/*` to backend

