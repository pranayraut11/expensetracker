# 📊 Income vs Expenses Trend - Quick Reference

## One-Page Guide

### 🎯 What It Does
- Shows **monthly trend** for the entire year (default)
- Shows **daily trend** for a selected month
- Green line = Income, Red line = Expenses
- Interactive dropdown to switch between views

---

### 🚀 Quick Start

**Backend:**
```bash
cd backend && mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend && npm run dev
```

**Open:** http://localhost:5173

---

### 📊 UI Components

**Month Selector Dropdown:**
```
Select Month: [All Months ▼]
- All Months (default) → Shows Jan-Dec monthly trend
- January → Shows daily trend for January
- February → Shows daily trend for February
... and so on
```

**Chart:**
- Green line: Income
- Red line: Expenses
- Hover: See exact amounts
- Auto-adjusts based on selection

---

### 🔌 API Usage

**Get Monthly Trend:**
```bash
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024"

Response:
[
  { "month": "2024-01", "income": 45000, "expenses": 32000 },
  { "month": "2024-02", "income": 42000, "expenses": 35000 }
]
```

**Get Daily Trend:**
```bash
curl "http://localhost:8080/api/analytics/income-expense-trend?year=2024&month=2"

Response:
[
  { "date": "2024-02-01", "income": 2000, "expenses": 1500 },
  { "date": "2024-02-02", "income": 0, "expenses": 1200 }
]
```

---

### 📁 Files Created

**Backend (4 files):**
1. `IncomeExpenseMonthlyDto.java`
2. `IncomeExpenseDailyDto.java`
3. `IncomeExpenseTrendService.java`
4. `IncomeExpenseTrendController.java`

**Frontend (4 files):**
1. `incomeExpenseTrendApi.js`
2. `TrendFilter.jsx`
3. `IncomeExpenseTrendChart.jsx`
4. `DashboardPage.jsx` (updated)

---

### 🎨 How It Looks

**Dashboard Layout:**
```
┌─────────────────────────────────────────────┐
│ Dashboard                        [Refresh]  │
├─────────────────────────────────────────────┤
│                                             │
│ [Summary Cards: Income, Expenses, Savings]  │
│                                             │
│ [Pie Chart]        [Bar Chart]              │
│                                             │
│ Income vs Expenses Trend (2024)             │
│                   Select Month: [All ▼]     │
│ ┌─────────────────────────────────────────┐ │
│ │        Income (Green)                   │ │
│ │        Expenses (Red)                   │ │
│ │  Chart shows trend based on selection   │ │
│ └─────────────────────────────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

---

### 🔄 Mode Switching

| Selection | Mode | X-Axis | Data Points |
|-----------|------|--------|-------------|
| All Months | Monthly | Jan, Feb, ... | 12 months |
| January | Daily | 1, 2, 3, ... | 31 days |
| February | Daily | 1, 2, 3, ... | 28/29 days |
| ... | ... | ... | ... |

---

### 💡 Use Cases

**Scenario 1: View Yearly Overview**
- Select: "All Months"
- See: How income/expenses changed month by month
- Use: Identify high-expense months

**Scenario 2: Deep Dive into a Month**
- Select: "February"
- See: Day-by-day income and expenses
- Use: Find specific dates with high spending

**Scenario 3: Compare Patterns**
- Switch between months
- Compare daily patterns
- Identify spending habits

---

### ✅ Checklist

Backend:
- [x] DTOs created
- [x] Service implemented
- [x] Controller with endpoint
- [x] Compiles successfully

Frontend:
- [x] API service created
- [x] Dropdown component
- [x] Chart component
- [x] Dashboard integration
- [x] Builds successfully

Testing:
- [x] Backend API tested
- [x] Frontend renders
- [x] Dropdown works
- [x] Chart updates dynamically

---

### 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Chart shows "No data" | Upload transactions first |
| Dropdown not changing chart | Check browser console for errors |
| API returns empty array | Ensure transactions exist for that period |
| Chart not loading | Verify backend is running on port 8080 |

---

### 📊 Data Format

**Monthly (Backend → Frontend):**
```
Backend: { month: "2024-01", income: 45000, expenses: 32000 }
Frontend Chart: { label: "2024-01", income: 45000, expenses: 32000 }
X-axis Display: "Jan"
```

**Daily (Backend → Frontend):**
```
Backend: { date: "2024-02-01", income: 2000, expenses: 1500 }
Frontend Chart: { label: "2024-02-01", income: 2000, expenses: 1500 }
X-axis Display: "1 Feb"
```

---

### 🎨 Color Scheme

```
Income Line:    #16a34a (green-600)
Expenses Line:  #dc2626 (red-600)
Grid:           #e5e7eb (gray-200)
Axis Text:      #6b7280 (gray-500)
```

---

### 🚦 Quick Test

```bash
# 1. Start services
cd backend && mvn spring-boot:run &
cd frontend && npm run dev &

# 2. Open browser
open http://localhost:5173

# 3. Go to Dashboard

# 4. Test dropdown:
- Select "All Months" → See monthly trend
- Select "February" → See daily trend for Feb
- Select "March" → See daily trend for Mar

# 5. Verify:
- Chart updates smoothly
- X-axis labels change
- Tooltips show correct data
```

---

### 📞 Need More Info?

See: `INCOME_EXPENSE_TREND_FEATURE.md` for complete documentation

---

**Status:** ✅ Ready to use!  
**Date:** Nov 30, 2025

---

**Print this for quick reference!** 📄

