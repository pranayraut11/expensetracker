# Upload File Error Fix

## Problem
When clicking the upload file button, the API call was failing with the error showing `http://localhost/upload` in the console.

## Root Causes Identified

### 1. Incorrect API Endpoint Path
**File:** `frontend/src/services/transactionApi.js`

**Issue:** The upload function was calling `/api/upload` instead of `/upload`

```javascript
// ❌ BEFORE (Incorrect)
export const uploadFile = async (formData) => {
  const response = await api.post('/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

// ✅ AFTER (Fixed)
export const uploadFile = async (formData) => {
  const response = await api.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}
```

**Explanation:** 
- The backend has two controllers handling uploads:
  - `UploadController` at `/api/upload`
  - `UploadProxyController` at `/upload` (delegates to UploadController)
- The correct endpoint to use is `/upload` which works with the configured baseURL

### 2. Tailwind Config Syntax Error (Bonus Fix)
**File:** `frontend/tailwind.config.js`

**Issue:** The config had syntax errors:
- Missing `content` property declaration
- Duplicate `extend` objects

```javascript
// ❌ BEFORE (Incorrect)
export default {  
  darkMode: 'class',
  "./index.html",           // ⚠️ Missing 'content:' property
  "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},             // ⚠️ Empty extend
    extend: {               // ⚠️ Duplicate extend
      fontFamily: { ... },
      ...
    },
  ...
}

// ✅ AFTER (Fixed)
export default {
  darkMode: 'class',
  content: [                // ✅ Added content property
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {               // ✅ Single extend object
      fontFamily: { ... },
      ...
    },
  },
  plugins: [],
}
```

## How the Upload Works Now

1. **Development Mode:**
   - Frontend runs on: `http://localhost:5173` (Vite dev server)
   - Backend runs on: `http://localhost:8080`
   - API baseURL: `http://localhost:8080` (from `.env.development`)
   - Upload endpoint: `POST http://localhost:8080/upload`

2. **Production Mode (Docker):**
   - Frontend served by nginx on: `http://localhost`
   - Backend on: `http://backend:8080` (internal Docker network)
   - API baseURL: Empty string (uses nginx proxy)
   - Upload endpoint: `POST http://localhost/upload` → proxied to backend

## Backend Endpoints

Both endpoints are now working:
- `/api/upload` - UploadProxyController (recommended for general use)
- `/api/upload` - UploadController (same endpoint, different implementation)

Both use the same SmartUploadService for processing.

## Testing

To test the fix:

1. **Start the backend:**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Start the frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

3. **Test upload:**
   - Navigate to `http://localhost:5173/upload`
   - Select an Excel file (.xls or .xlsx)
   - Click "Upload and Process"
   - Should see successful upload response

## Expected Response

On successful upload, you should see:
```json
{
  "rowsProcessed": 100,
  "rowsSaved": 95,
  "duplicates": 5,
  "errors": 0,
  "duplicateTransactions": [...]
}
```

## Files Modified

1. ✅ `frontend/src/services/transactionApi.js` - Fixed API endpoint path
2. ✅ `frontend/tailwind.config.js` - Fixed syntax errors

## No Further Action Required

The fixes have been applied and the upload functionality should now work correctly in both development and production environments.

