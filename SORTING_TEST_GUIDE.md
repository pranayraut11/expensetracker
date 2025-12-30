# 🧪 COMPLETE SORTING TEST GUIDE

## 🚀 Before Testing

### 1. Restart Backend (REQUIRED!)
```bash
# Stop backend (Ctrl+C)
cd /Users/p.raut/expensetracker_2/backend
java -jar target/expensetracker-1.0.0.jar
```

### 2. Hard Refresh Browser
`Ctrl+Shift+R` (Windows) or `Cmd+Shift+R` (Mac)

### 3. Open Developer Tools
Press `F12` and open both:
- **Console** tab (for frontend logs)
- **Network** tab (to see API requests)

## 🧪 Test Sequence

### Test 1: Click "Type" Column

**First Click (Descending):**

**Frontend Console Should Show:**
```
handleSort called with field: type ctrlKey: false
Current sortColumns: [{field: 'date', direction: 'desc'}]
Setting new sort columns (single new): [{field: 'type', direction: 'desc'}]
Fetching transactions with sort: ['type,desc']
```

**Network Tab Should Show:**
```
Request URL: http://localhost:8080/transactions?page=0&size=20&sort=type,desc
```

**Backend Console Should Show:**
```
=== SORT DEBUG START ===
Raw sort params received: [type,desc]
sortParams.size(): 1
sortParams[0]: length=2, values=[type, desc]
Parsed sortField: type
Parsed sortDirection: desc
Direction after equalsIgnoreCase check: DESC
=== SORT DEBUG END ===
```

**Expected Result:** DEBIT transactions appear first

---

**Second Click (Ascending):**

**Frontend Console Should Show:**
```
handleSort called with field: type ctrlKey: false
Current sortColumns: [{field: 'type', direction: 'desc'}]
Setting new sort columns (single toggle): [{field: 'type', direction: 'asc'}]
Fetching transactions with sort: ['type,asc']
```

**Network Tab Should Show:**
```
Request URL: http://localhost:8080/transactions?page=0&size=20&sort=type,asc
```

**Backend Console Should Show:**
```
=== SORT DEBUG START ===
Raw sort params received: [type,asc]
sortParams.size(): 1
sortParams[0]: length=2, values=[type, asc]
Parsed sortField: type
Parsed sortDirection: asc
Direction after equalsIgnoreCase check: ASC
=== SORT DEBUG END ===

DEBUG Single-sort: sortDirection=asc, trimmed=asc
DEBUG Single-sort: Resolved to Sort.Direction.ASC
```

**Expected Result:** CREDIT transactions appear first ⭐

---

### Test 2: Click "Amount" Column

**First Click (Descending):**
- URL: `sort=amount,desc`
- Result: Highest amounts first (e.g., ₹50,000 before ₹100)

**Second Click (Ascending):**
- URL: `sort=amount,asc`
- Result: Lowest amounts first (e.g., ₹100 before ₹50,000) ⭐

---

### Test 3: Click "Date" Column

**First Click (Descending):**
- URL: `sort=date,desc`
- Result: Newest dates first (2025-12-29 before 2025-01-01)

**Second Click (Ascending):**
- URL: `sort=date,asc`
- Result: Oldest dates first (2025-01-01 before 2025-12-29) ⭐

## 🔍 Diagnosis Guide

### Scenario A: Frontend Logs Show Wrong Direction

**If console shows:**
```
Setting new sort columns (single toggle): [{field: 'type', direction: 'desc'}]
```
**When it should show 'asc'**

**Problem:** Frontend toggle logic is broken
**Check:** The `handleSort` function in TransactionsPage.jsx

### Scenario B: Frontend Correct, URL Wrong

**If console shows 'asc' but URL shows 'desc'**

**Problem:** `fetchTransactions` not building URL correctly
**Check:** The `sortParams` mapping in `fetchTransactions`

### Scenario C: Frontend & URL Correct, Backend Receives Wrong

**If URL shows `sort=type,asc` but backend logs show `[type,desc]`**

**Problem:** Axios or proxy issue
**Solution:** Check proxy configuration in vite.config.js

### Scenario D: Everything Correct, Data Doesn't Change

**If backend logs show:**
```
Parsed sortDirection: asc
Direction after equalsIgnoreCase check: ASC
DEBUG Single-sort: Resolved to Sort.Direction.ASC
```

**But data stays in descending order**

**Problem:** Database query issue or transaction data issue
**Check:**
1. Look at actual SQL query in backend logs (if available)
2. Check if all transactions have the same value for that field
3. Check database column type

### Scenario E: Backend Shows DESC When It Should Be ASC

**If backend logs show:**
```
Parsed sortDirection: asc
Direction after equalsIgnoreCase check: DESC  ← WRONG
```

**Problem:** The equalsIgnoreCase logic is failing
**Possible Causes:**
1. Extra whitespace that `.trim()` doesn't catch (e.g., line breaks, tabs)
2. Different character encoding
3. Hidden unicode characters

## 📋 What to Share With Me

After testing, please share:

### 1. Frontend Console Output
Copy the full output from clicking twice on the same column

### 2. Network Tab Request
Copy the full Request URL from the Network tab

### 3. Backend Console Output
Copy the full debug output (the === SORT DEBUG === section)

### 4. What Actually Happened
- Did data change order?
- What order did you see? (First few transaction types/amounts/dates)

## 🎯 Quick Test Commands

### Test URL Directly
Open these URLs in your browser to test backend directly:

**Descending:**
```
http://localhost:8080/transactions?page=0&size=10&sort=type,desc
```

**Ascending:**
```
http://localhost:8080/transactions?page=0&size=10&sort=type,asc
```

Compare the results - are they different?

### Test with cURL
```bash
# Descending
curl "http://localhost:8080/transactions?page=0&size=10&sort=type,desc" | jq '.content[].type'

# Ascending  
curl "http://localhost:8080/transactions?page=0&size=10&sort=type,asc" | jq '.content[].type'
```

The output should be in different order.

---

**Follow these steps and share the outputs so I can pinpoint the exact issue!** 🔍

