# 📊 Income vs Expenses Trend Feature - Complete Documentation

## Overview
The Income vs Expenses Trend feature provides a dynamic visual chart showing income and expense patterns over time. Users can view either:
- **Monthly trend** for the entire year (default)
- **Daily trend** for a specific month (when month is selected)

---

## 🎯 Features

### Dynamic Month Selector
- Dropdown with options: "All Months", "January", "February", ... "December"
- Default: "All Months" (shows monthly trend for current year)
- When specific month selected: Shows daily trend for that month

### Interactive Charts
- **Green line**: Income (CREDIT transactions)
- **Red line**: Expenses (DEBIT transactions)
- Interactive tooltips with exact amounts
- Responsive design
- Auto-formatted currency (₹)

---

## 🔧 Backend Implementation

### API Endpoint

```
GET /api/analytics/income-expense-trend
```

**Query Parameters:**
- `year` (required): The year to fetch data for (e.g., 2024)
- `month` (optional): Specific month (1-12)

**Behavior:**
- If `month` is provided → Returns daily trend for that month
- If only `year` is provided → Returns monthly trend for the year

---

### Response Formats

#### Mode 1: Monthly Trend (year only)
```json
[
  {
    "month": "2024-01",
    "income": 45000.0,
    "expenses": 32000.0
  },
  {
    "month": "2024-02",
    "income": 42000.0,
    "expenses": 35000.0
  }
]
```

#### Mode 2: Daily Trend (year + month)
```json
[
  {
    "date": "2024-02-01",
    "income": 2000.0,
    "expenses": 1500.0
  },
  {
    "date": "2024-02-02",
    "income": 0.0,
    "expenses": 1200.0
  }
]
```

---

### Backend Files Created

#### 1. **IncomeExpenseMonthlyDto.java**
```
Location: backend/src/main/java/.../dto/
Fields:
  - String month (format: "2024-01")
  - Double income
  - Double expenses
```

#### 2. **IncomeExpenseDailyDto.java**
```
Location: backend/src/main/java/.../dto/
Fields:
  - String date (format: "2024-02-01")
  - Double income
  - Double expenses
```

#### 3. **IncomeExpenseTrendService.java**
```
Location: backend/src/main/java/.../service/
Methods:
  - getMonthlyTrend(int year)
  - getDailyTrend(int year, int month)
```

#### 4. **IncomeExpenseTrendController.java**
```
Location: backend/src/main/java/.../controller/
Endpoint:
  - GET /api/analytics/income-expense-trend
```

---

## 🎨 Frontend Implementation

### Frontend Files Created

#### 1. **incomeExpenseTrendApi.js**
```
Location: frontend/src/services/
Function:
  - getIncomeExpenseTrend(year, month)
```

#### 2. **TrendFilter.jsx**
```
Location: frontend/src/components/
Purpose: Month selector dropdown
Options: All Months, January - December
```

#### 3. **IncomeExpenseTrendChart.jsx**
```
Location: frontend/src/components/
Purpose: Recharts line chart component
Modes: monthly or daily
Lines: Income (green), Expenses (red)
```

#### 4. **DashboardPage.jsx (Updated)**
```
Location: frontend/src/pages/
Changes:
  - Added trend data state
  - Added month selection state
  - Integrated TrendFilter dropdown
  - Integrated IncomeExpenseTrendChart
```

---

## 🚀 How to Use

### For End Users

1. **Navigate to Dashboard**
   - The chart appears automatically below the category charts

2. **View Yearly Trend**
   - Default view shows monthly trend for current year
   - X-axis: Months (Jan, Feb, Mar...)
   - Y-axis: Amount in ₹

3. **View Monthly Trend**
   - Select a month from the dropdown (e.g., "February")
   - Chart updates to show daily trend for that month
   - X-axis: Days (1 Feb, 2 Feb, 3 Feb...)
   - Y-axis: Amount in ₹

4. **Interpret the Chart**
   - **Green line**: Your income for each period
   - **Red line**: Your expenses for each period
   - Hover over any point to see exact amounts

---

## 📝 API Usage Examples

### Test Monthly Trend
```bash
# Get monthly trend for 2024
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024"
```

### Test Daily Trend
```bash
# Get daily trend for February 2024
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024&month=2"
```

---

## 🎨 UI Design

### Month Selector Dropdown
```
Select Month: [All Months ▼]

Options:
- All Months
- January
- February
- March
- April
- May
- June
- July
- August
- September
- October
- November
- December
```

### Chart Display

**When "All Months" selected:**
```
Income vs Expenses Trend (Monthly)
┌────────────────────────────────────┐
│                                    │
│     ╱──── Income (Green)           │
│    ╱                               │
│   ╱   ╱──── Expenses (Red)         │
│  ╱   ╱                             │
│ ╱───╱──────────────────────        │
│                                    │
│ Jan Feb Mar Apr May Jun Jul Aug    │
└────────────────────────────────────┘
```

**When specific month selected (e.g., "February"):**
```
Income vs Expenses Trend (Daily)
┌────────────────────────────────────┐
│                                    │
│     ╱──── Income (Green)           │
│    ╱                               │
│   ╱   ╱──── Expenses (Red)         │
│  ╱   ╱                             │
│ ╱───╱──────────────────────        │
│                                    │
│ 1  5  10  15  20  25  28 Feb       │
└────────────────────────────────────┘
```

---

## 🔧 Technical Details

### Backend Logic

**Monthly Trend Calculation:**
1. Fetch all transactions for the year
2. Group by YearMonth (e.g., "2024-01")
3. Sum CREDIT amounts → income
4. Sum DEBIT amounts → expenses
5. Return list sorted chronologically

**Daily Trend Calculation:**
1. Fetch all transactions for the specific month
2. Group by date
3. Include all days in the month (even with no transactions)
4. Sum CREDIT amounts → income
5. Sum DEBIT amounts → expenses
6. Return list for all days of the month

---

### Frontend Logic

**State Management:**
```javascript
const [trendData, setTrendData] = useState([])
const [selectedMonth, setSelectedMonth] = useState('all')
const [trendMode, setTrendMode] = useState('monthly')
const currentYear = new Date().getFullYear()
```

**Data Fetching:**
- On component mount: Fetch monthly trend for current year
- On month change: Fetch appropriate trend based on selection
  - "all" → Monthly trend
  - Specific month → Daily trend

**Chart Rendering:**
- X-axis formatter changes based on mode
  - Monthly: "2024-01" → "Jan"
  - Daily: "2024-02-01" → "1 Feb"
- Y-axis: Currency format (₹25k, ₹50k, etc.)
- Tooltip: Full currency format (₹45,000)

---

## ✅ Verification

### Backend Tests
```bash
# Start backend
cd backend && mvn spring-boot:run

# Test API
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024"
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024&month=2"
```

### Frontend Tests
```bash
# Start frontend
cd frontend && npm run dev

# Open browser
open http://localhost:5173

# Navigate to Dashboard
# Try selecting different months from dropdown
```

---

## 📊 Sample Data Flow

```
User selects "February" from dropdown
        ↓
handleMonthChange(2)
        ↓
setSelectedMonth(2)
        ↓
useEffect triggered
        ↓
fetchTrendData()
        ↓
getIncomeExpenseTrend(2024, 2)
        ↓
GET /api/analytics/income-expense-trend?year=2024&month=2
        ↓
Backend: getDailyTrend(2024, 2)
        ↓
Returns daily data for February
        ↓
Frontend: setTrendData(data)
        ↓
Chart re-renders with daily view
```

---

## 🎨 Styling

### Colors
- **Income Line**: `#16a34a` (green-600)
- **Expenses Line**: `#dc2626` (red-600)
- **Grid Lines**: `#e5e7eb` (gray-200)
- **Axis Text**: `#6b7280` (gray-500)

### Chart Dimensions
- Height: 400px
- Width: 100% (responsive)
- Margins: top: 5, right: 30, left: 20, bottom: 5

### Dropdown Styling
- Border: gray-300
- Padding: 8px
- Border radius: 6px
- Background: white
- Shadow: sm
- Focus ring: indigo-500

---

## 🐛 Error Handling

### Backend
- Invalid month (< 1 or > 12): Returns 400 Bad Request
- No data for period: Returns empty array

### Frontend
- API errors: Caught and logged, empty array displayed
- No data: Shows "No data available" message
- Loading state: Shows spinner while fetching

---

## 🔮 Future Enhancements

Potential improvements:
1. **Year Selector**: Allow selecting different years
2. **Comparison Mode**: Compare two months side by side
3. **Export**: Download chart as image or data as CSV
4. **Annotations**: Mark significant events on the chart
5. **Zoom**: Allow zooming into specific date ranges
6. **Savings Line**: Add third line showing net savings
7. **Budget Lines**: Add target budget overlay

---

## 📁 File Summary

### Backend (4 files)
✅ IncomeExpenseMonthlyDto.java - Monthly data DTO  
✅ IncomeExpenseDailyDto.java - Daily data DTO  
✅ IncomeExpenseTrendService.java - Business logic  
✅ IncomeExpenseTrendController.java - REST endpoint  

### Frontend (4 files)
✅ incomeExpenseTrendApi.js - API service  
✅ TrendFilter.jsx - Month dropdown  
✅ IncomeExpenseTrendChart.jsx - Chart component  
✅ DashboardPage.jsx - Integration (updated)  

**Total:** 8 files (7 new + 1 updated)

---

## ✅ Status

| Component | Status |
|-----------|--------|
| Backend Compilation | ✅ PASSED |
| Frontend Build | ✅ PASSED |
| API Endpoint | ✅ WORKING |
| Month Dropdown | ✅ WORKING |
| Chart Rendering | ✅ WORKING |
| Mode Switching | ✅ WORKING |
| Error Handling | ✅ IMPLEMENTED |
| Documentation | ✅ COMPLETE |

---

**🎉 Feature Complete!**

The Income vs Expenses Trend feature is ready to use with full month selector functionality!

---

*Created: November 30, 2025*  
*Status: Production Ready ✅*

