# Multi-Column Sorting Feature for Transactions

## Overview
Implemented multi-column sorting functionality that allows users to sort transactions by multiple columns in sequence (e.g., first by type, then by category, then by date).

## Features

### 🎯 Key Capabilities
- **Single-column sort**: Click on any column header to sort by that column
- **Multi-column sort**: Ctrl/Cmd + Click on column headers to add columns to the sort order
- **Visual indicators**: 
  - Active sort columns highlighted with blue background
  - Sort direction shown with up/down arrows
  - Sort order numbers displayed (1, 2, 3...) for multi-column sorting
- **Sort order management**:
  - Remove individual columns from sort by clicking the × button
  - Clear all sorting to reset to default (date descending)
  - Visual display of current sort order above the table

### 📋 How It Works

#### Single Column Sorting
1. Click on any column header (Date, Category, Type, Amount)
2. First click: Sort descending
3. Second click: Sort ascending
4. Continues toggling between asc/desc

#### Multi-Column Sorting
1. Click on first column header (e.g., Type)
2. Hold Ctrl (Windows/Linux) or Cmd (Mac) and click another column header (e.g., Category)
3. Continue adding more columns with Ctrl/Cmd + Click
4. The sort order numbers (1, 2, 3...) show the priority of each column

#### Example Use Case
Sort all transactions:
1. First by **Type** (Credit/Debit)
2. Then by **Category** within each type
3. Then by **Date** within each category

Result: All credit transactions appear first (sorted by category and date), followed by all debit transactions (sorted by category and date).

## Changes Made

### Backend Changes

#### 1. TransactionController.java
**File**: `backend/src/main/java/com/example/expensetracker/controller/TransactionController.java`

- Updated `getTransactions` endpoint to accept multiple `sort` parameters
- Changed `sort` parameter from `String` to `List<String>`
- Supports format: `sort=type,asc&sort=category,asc&sort=date,desc`

**Before**:
```java
@RequestParam(defaultValue = "date,desc") String sort
```

**After**:
```java
@RequestParam(defaultValue = "date,desc") List<String> sort
```

#### 2. TransactionService.java
**File**: `backend/src/main/java/com/example/expensetracker/service/TransactionService.java`

- Updated `getTransactionsPageable` method to accept `List<String[]> sortParams`
- Builds multi-column `Sort` object using `Sort.by(orders)`
- Maintains backward compatibility with single-column sorting

**Key Code**:
```java
if (sortParams != null && sortParams.size() > 1) {
    // Multiple sort columns
    List<Sort.Order> orders = new java.util.ArrayList<>();
    for (String[] sortParam : sortParams) {
        String field = mapSortField(sortParam[0]);
        String direction = sortParam.length > 1 ? sortParam[1] : "desc";
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) 
            ? Sort.Direction.ASC : Sort.Direction.DESC;
        orders.add(new Sort.Order(dir, field));
    }
    sort = Sort.by(orders);
}
```

### Frontend Changes

#### 3. TransactionsPage.jsx
**File**: `frontend/src/pages/TransactionsPage.jsx`

**Changed**:
- Replaced `sortField` and `sortDirection` state with `sortColumns` array
- Added `handleSort` function to support Ctrl/Cmd+Click for multi-column sorting
- Added `getSortInfo` helper function to get sort info for each column
- Added visual sort order indicator component
- Updated `fetchTransactions` to send multiple sort parameters

**State Change**:
```javascript
// Before
const [sortField, setSortField] = useState('date')
const [sortDirection, setSortDirection] = useState('desc')

// After
const [sortColumns, setSortColumns] = useState([{ field: 'date', direction: 'desc' }])
```

**Sort Handler**:
```javascript
const handleSort = (field, ctrlKey = false) => {
  if (ctrlKey) {
    // Multi-column mode: add/remove/toggle
    // ...
  } else {
    // Single-column mode: replace current sort
    // ...
  }
}
```

#### 4. TransactionTable.jsx
**File**: `frontend/src/components/TransactionTable.jsx`

**Changed**:
- Updated `SortableHeader` component to show sort order numbers
- Added support for Ctrl/Cmd+Click detection
- Highlights active sort columns with blue background
- Shows order number badge for multi-column sorting
- Added tooltip: "Click to sort. Ctrl/Cmd + Click to add to sort order"

**Props Change**:
```javascript
// Before
const TransactionTable = ({ ..., sortField, sortDirection })

// After
const TransactionTable = ({ ..., getSortInfo })
```

**Visual Enhancements**:
- Active columns have blue background (`bg-blue-50`)
- Sort order number displayed in blue badge
- Direction arrow (↑/↓) shows sort direction

## User Interface

### Sort Order Indicator
Above the transaction table, a visual indicator shows:
```
Sort Order: 1. type ↓  2. category ↑  3. date ↓  [Clear all]
(Ctrl/Cmd + Click column headers to add to sort)
```

- Each active sort column shown as a pill/badge
- Order number (1, 2, 3...)
- Column name
- Direction arrow (↑ ascending, ↓ descending)
- Remove button (×) for individual columns
- "Clear all" button to reset to default

### Column Headers
- **Hover**: Light gray background
- **Active sort**: Light blue background
- **Sort indicator**: Up/down arrow in indigo color
- **Multi-sort badge**: Small circular badge with order number
- **Tooltip**: Instructions on how to use multi-column sort

## API Examples

### Single Column Sort
```
GET /transactions?sort=date,desc
```

### Multi-Column Sort
```
GET /transactions?sort=type,asc&sort=category,asc&sort=date,desc
```

This will sort:
1. First by type (ascending)
2. Then by category (ascending) 
3. Then by date (descending)

## Testing

### Backend Compilation
```bash
cd /Users/p.raut/expensetracker_2/backend
mvn clean compile -DskipTests
```
✅ **Result**: BUILD SUCCESS

### Test Scenarios
1. **Single sort**: Click "Type" → Transactions sorted by type
2. **Toggle direction**: Click "Type" again → Direction toggles
3. **Multi-sort**: Ctrl+Click "Type", then Ctrl+Click "Category" → Sorted by type, then category
4. **Remove column**: Click × on a sort badge → That column removed from sort
5. **Clear all**: Click "Clear all" → Back to default (date desc)

## Browser Compatibility
- **Ctrl+Click**: Works on Windows and Linux
- **Cmd+Click**: Works on macOS
- Both are detected via `e.ctrlKey || e.metaKey`

## Benefits
✅ More powerful data analysis  
✅ Better transaction organization  
✅ Intuitive UI with visual feedback  
✅ Maintains backward compatibility  
✅ Flexible sorting combinations  
✅ Easy to add/remove sort columns  

## Troubleshooting

### Sorting Not Working?

If clicking on column headers doesn't sort the transactions, follow these steps:

#### 1. Check Browser Console
Open browser Developer Tools (F12) and check the Console tab for:
- `handleSort called with field:` - Should appear when clicking headers
- `Fetching transactions with sort:` - Should show the sort parameters
- Any error messages

#### 2. Verify Backend is Running
Ensure the backend Spring Boot application is running and accessible:
```bash
# Check if backend is responding
curl http://localhost:8080/transactions?page=0&size=10&sort=date,desc
```

#### 3. Rebuild Backend
If you made changes, rebuild the backend:
```bash
cd /Users/p.raut/expensetracker_2/backend
mvn clean package -DskipTests
```

#### 4. Restart Backend
Stop and restart the backend application to ensure changes are loaded:
```bash
# If using Docker
docker-compose restart backend

# If running directly
java -jar target/expensetracker-1.0.0.jar
```

#### 5. Clear Browser Cache
Sometimes cached JavaScript can cause issues:
- Hard refresh: Ctrl+Shift+R (Windows/Linux) or Cmd+Shift+R (Mac)
- Or clear browser cache and reload

#### 6. Check Network Tab
In Developer Tools, go to Network tab:
- Click on a column header
- Look for the `/transactions` request
- Check if `sort` parameter is being sent correctly
- Example: `sort=type,asc&sort=category,asc`

#### 7. Verify Database Connection
Ensure the backend can connect to the database and fetch transactions.

### Common Issues

**Issue**: Clicking doesn't do anything
- **Solution**: Check if `onSort` and `getSortInfo` props are passed to TransactionTable
- **Check**: Console should show `handleSort called with field:` when clicking

**Issue**: Sort works but data doesn't change
- **Solution**: Backend might not be processing the sort parameter
- **Check**: Network tab should show sort parameters in the request URL

**Issue**: Multi-sort (Ctrl+Click) doesn't work
- **Solution**: Ensure you're holding Ctrl (Windows/Linux) or Cmd (Mac) when clicking
- **Check**: Console log should show `ctrlKey: true`

**Issue**: Sort indicator doesn't update
- **Solution**: The `getSortInfo` function might not be working
- **Check**: Verify `sortColumns` state is updating correctly

### Debug Mode

The current implementation includes console logging for debugging. Open browser console to see:
```
handleSort called with field: type ctrlKey: false
Current sortColumns: [{field: 'date', direction: 'desc'}]
Setting new sort columns (single new): [{field: 'type', direction: 'desc'}]
Fetching transactions with sort: ['type,desc']
Received transactions: {content: Array(20), totalElements: 150, ...}
```

## Date Implemented
December 29, 2025

