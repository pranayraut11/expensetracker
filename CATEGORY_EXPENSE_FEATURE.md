# 📊 Category-wise Monthly Expenses Feature - Complete Documentation

## Overview
The Category-wise Monthly Expenses feature provides a visual breakdown of expenses by category for a selected month. Users can switch between **Bar Chart** and **Pie Chart** views to analyze their spending patterns.

---

## 🎯 Features

### Dynamic Month Selector
- Dropdown with all 12 months
- Default: Current month
- Updates chart when month changes

### Dual Chart Views
- **Bar Chart View** (Default): Shows categories on X-axis with expense totals
- **Pie Chart View**: Shows percentage distribution with custom legend
- Toggle button to switch between views

### Interactive Elements
- Tooltips showing exact amounts
- Total expenses summary at bottom
- Color-coded categories (12 distinct colors)
- Sorted by highest spending (descending)

---

## 🔧 Backend Implementation

### API Endpoint

```
GET /api/analytics/category-expenses
```

**Query Parameters:**
- `year` (required): The year (e.g., 2024)
- `month` (required): The month (1-12)

**Response Format:**
```json
[
  {
    "category": "Food",
    "total": 3200.50
  },
  {
    "category": "Shopping",
    "total": 5190.00
  },
  {
    "category": "Travel",
    "total": 760.00
  }
]
```

**Notes:**
- Returns only DEBIT (expense) transactions
- Sorted by total amount (highest first)
- Empty array if no expenses for the month

---

### Backend Files Created

#### 1. **CategoryExpenseDto.java**
```
Location: backend/src/main/java/.../dto/
Fields:
  - String category
  - Double total
```

#### 2. **CategoryExpenseService.java**
```
Location: backend/src/main/java/.../service/
Method:
  - getCategoryExpenses(int year, int month)
  
Logic:
  1. Calculate month boundaries using YearMonth
  2. Fetch transactions for the month
  3. Filter DEBIT transactions only
  4. Group by category and sum amounts
  5. Sort by total (descending)
  6. Return as List<CategoryExpenseDto>
```

#### 3. **IncomeExpenseTrendController.java** (Updated)
```
Location: backend/src/main/java/.../controller/
Added endpoint:
  - GET /analytics/category-expenses
  - Validates month range (1-12)
  - Returns category expense data
```

---

## 🎨 Frontend Implementation

### Frontend Files Created

#### 1. **categoryExpenseApi.js**
```
Location: frontend/src/services/
Function:
  - getCategoryExpenses(year, month)
```

#### 2. **CategoryMonthSelector.jsx**
```
Location: frontend/src/components/
Purpose: Month selector dropdown
Options: January - December
Default: Current month
```

#### 3. **CategoryExpenseChart.jsx**
```
Location: frontend/src/components/
Purpose: Dual-view chart component

Features:
  - Bar Chart with Recharts
  - Pie Chart with Recharts
  - Toggle between views
  - Custom tooltips
  - Percentage labels on pie
  - Color-coded legend
  - Total expenses summary
```

#### 4. **DashboardPage.jsx** (Updated)
```
Location: frontend/src/pages/
Changes:
  - Added category expense states
  - Added month selection state
  - Integrated CategoryMonthSelector
  - Integrated CategoryExpenseChart
  - Fetch data on component mount and month change
```

---

## 🚀 How to Use

### For End Users

1. **Navigate to Dashboard**
   - Scroll down to "Category-wise Expenses" section

2. **Select a Month**
   - Use the dropdown to select any month
   - Default shows current month
   - Chart updates automatically

3. **View Bar Chart (Default)**
   - Categories on X-axis
   - Expense amounts on Y-axis
   - Bars sorted by highest spending
   - Hover to see exact amounts

4. **Switch to Pie Chart**
   - Click "🥧 Pie View" button
   - See percentage distribution
   - Color-coded slices
   - Legend on the right with totals

5. **View Total**
   - Bottom of chart shows total expenses for the month

---

## 📝 API Usage Examples

### Test API
```bash
# Get category expenses for February 2024
curl "http://localhost:8080/api/analytics/category-expenses?year=2024&month=2"
```

**Response:**
```json
[
  { "category": "Shopping", "total": 5190.0 },
  { "category": "Food", "total": 3200.5 },
  { "category": "Travel", "total": 760.0 }
]
```

---

## 🎨 UI Design

### Chart Toggle
```
┌──────────────────────────────────────┐
│ Category-wise Expenses - February    │
│                                      │
│  [📊 Bar View]  [🥧 Pie View]       │
└──────────────────────────────────────┘
```

### Bar Chart View
```
┌────────────────────────────────────────┐
│  ₹6k ┤           ███                   │
│      │           ███                   │
│  ₹5k ┤           ███                   │
│      │           ███                   │
│  ₹4k ┤           ███      ███          │
│      │           ███      ███          │
│  ₹3k ┤           ███      ███          │
│      │           ███      ███   ███    │
│  ₹2k ┤           ███      ███   ███    │
│      │           ███      ███   ███    │
│  ₹1k ┤           ███      ███   ███    │
│      └───────────┴────────┴─────┴──── │
│        Shopping   Food   Travel        │
│                                        │
│  Total Expenses: ₹9,150.50            │
└────────────────────────────────────────┘
```

### Pie Chart View
```
┌────────────────────────────────────────┐
│         ╱──────╲                       │
│      ╱    56%    ╲    Legend:          │
│    │  Shopping   │   □ Shopping ₹5,190│
│     ╲           ╱    □ Food ₹3,200    │
│      │  35%   │      □ Travel ₹760     │
│       ╲  9%  ╱                         │
│        ╲──╱                            │
│                                        │
│  Total Expenses: ₹9,150.50            │
└────────────────────────────────────────┘
```

### Month Selector
```
Select Month: [November ▼]

Options:
  January
  February
  March
  April
  May
  June
  July
  August
  September
  October
  November ✓
  December
```

---

## 🔧 Technical Details

### Backend Logic

**Month Boundary Calculation:**
```java
YearMonth yearMonth = YearMonth.of(year, month);
LocalDate startOfMonth = yearMonth.atDay(1);
LocalDate endOfMonth = yearMonth.atEndOfMonth();
```

**Filtering and Grouping:**
```java
transactions.stream()
  .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
  .collect(Collectors.groupingBy(
    Transaction::getCategory,
    Collectors.summingDouble(Transaction::getAmount)
  ))
```

**Sorting:**
```java
.sorted(Comparator.comparing(CategoryExpenseDto::getTotal).reversed())
```

---

### Frontend Logic

**State Management:**
```javascript
const [categoryExpenseData, setCategoryExpenseData] = useState([])
const [categorySelectedMonth, setCategorySelectedMonth] = useState(new Date().getMonth() + 1)
const [categoryLoading, setCategoryLoading] = useState(false)
const [chartType, setChartType] = useState('bar') // 'bar' or 'pie'
```

**Data Fetching:**
```javascript
useEffect(() => {
  fetchCategoryExpenses()
}, [categorySelectedMonth])
```

**Chart Type Toggle:**
```javascript
<button onClick={() => setChartType('bar')}>Bar View</button>
<button onClick={() => setChartType('pie')}>Pie View</button>
```

---

## 🎨 Color Palette

12 distinct colors for categories:
```javascript
const COLORS = [
  '#6366f1', // indigo
  '#ec4899', // pink
  '#f59e0b', // amber
  '#10b981', // emerald
  '#3b82f6', // blue
  '#8b5cf6', // violet
  '#f97316', // orange
  '#06b6d4', // cyan
  '#ef4444', // red
  '#84cc16', // lime
  '#14b8a6', // teal
  '#a855f7'  // purple
]
```

---

## 📊 Chart Specifications

### Bar Chart
- **Bar Size**: 35px
- **Border Radius**: Top corners rounded (6px)
- **Fill Color**: #6366f1 (indigo)
- **X-axis**: Category names (angled -45° for long names)
- **Y-axis**: Formatted as ₹Xk (e.g., ₹3k)
- **Tooltip**: Shows category and exact amount

### Pie Chart
- **Outer Radius**: 120px
- **Labels**: Show percentage (hidden if < 5%)
- **Label Color**: White text
- **Legend**: Custom legend with colors and amounts
- **Tooltip**: Shows category and exact amount

---

## 🐛 Error Handling

### Backend
- Invalid month (< 1 or > 12): Returns 400 Bad Request
- No data for month: Returns empty array

### Frontend
- API errors: Caught and logged, empty array displayed
- No data: Shows "No expense data available" message
- Loading state: Shows spinner while fetching

---

## 📈 Sample Insights

### Question 1: "Where do I spend the most?"
**Answer:** Look at the highest bar or largest pie slice

### Question 2: "What percentage goes to food?"
**Answer:** Switch to Pie View, see the percentage

### Question 3: "Did my shopping expenses decrease?"
**Answer:** Switch months and compare Shopping category

### Question 4: "How many categories do I spend on?"
**Answer:** Count the number of bars/slices

---

## ✅ Verification

### Backend Tests
```bash
# Start backend
cd backend && mvn spring-boot:run

# Test API
curl "http://localhost:8080/api/analytics/category-expenses?year=2024&month=11"
```

### Frontend Tests
```bash
# Start frontend
cd frontend && npm run dev

# Open browser
open http://localhost:5173

# Navigate to Dashboard
# Scroll to "Category-wise Expenses"
# Try selecting different months
# Toggle between Bar and Pie views
```

---

## 🔮 Future Enhancements

Potential improvements:
1. **Year Selector**: Allow selecting different years
2. **Comparison Mode**: Compare two months side by side
3. **Drill-down**: Click category to see transactions
4. **Budget Overlay**: Show budget line on bar chart
5. **Export**: Download chart as image
6. **Top N Filter**: Show only top 5 categories
7. **Animated Transitions**: Smooth animations when switching months

---

## 📁 File Summary

### Backend (3 files)
✅ CategoryExpenseDto.java - Data structure  
✅ CategoryExpenseService.java - Business logic  
✅ IncomeExpenseTrendController.java - API endpoint (updated)  

### Frontend (4 files)
✅ categoryExpenseApi.js - API service  
✅ CategoryMonthSelector.jsx - Month dropdown  
✅ CategoryExpenseChart.jsx - Dual-view chart  
✅ DashboardPage.jsx - Integration (updated)  

**Total:** 7 files (6 new + 1 updated)

---

## ✅ Status

| Component | Status |
|-----------|--------|
| Backend Compilation | ✅ PASSED |
| Frontend Build | ✅ PASSED |
| API Endpoint | ✅ WORKING |
| Month Dropdown | ✅ WORKING |
| Bar Chart | ✅ WORKING |
| Pie Chart | ✅ WORKING |
| Chart Toggle | ✅ WORKING |
| Error Handling | ✅ IMPLEMENTED |
| Documentation | ✅ COMPLETE |

---

**🎉 Feature Complete!**

The Category-wise Monthly Expenses feature is ready to use with dual chart views!

---

*Created: November 30, 2025*  
*Status: Production Ready ✅*

