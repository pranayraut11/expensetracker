# 🚀 Quick Start Guide - Salary Cycle Feature

## Prerequisites
- Java 17+ installed
- Maven installed
- Node.js and npm installed
- MySQL/PostgreSQL database running

---

## 🏃 Quick Start (5 Steps)

### Step 1: Start the Backend
```bash
cd backend
mvn spring-boot:run
```

Wait for: `Started ExpenseTrackerApplication`

---

### Step 2: Start the Frontend
```bash
cd frontend
npm install  # Only needed first time
npm run dev
```

Frontend will open at: `http://localhost:5173`

---

### Step 3: Upload Bank Statement with Salary
1. Go to **Upload** page
2. Upload Excel file containing transactions
3. **Important**: Salary transactions must have:
   - Type: CREDIT
   - Category: Income (categorized by rules or manually)
   - Description containing: SALARY, PAYROLL, NEFT SALARY, etc.

Example transaction:
```
Date: 2025-01-05
Description: NEFT SALARY JAN 2025
Amount: 50000
Type: CREDIT
```

---

### Step 4: View Salary Cycles

#### Option A: Dashboard
1. Go to **Dashboard**
2. In "Date Range Selection" section:
   - Select "Salary Cycle" from dropdown
   - Choose a cycle (e.g., "Jan Salary Cycle (5 Jan – 4 Feb)")
3. View cycle-specific metrics:
   - Total Income
   - Total Expenses
   - Net Savings

#### Option B: Transactions
1. Go to **Transactions**
2. In Filters section:
   - Select "Salary Cycle" mode
   - Choose a cycle
3. View filtered transactions for that cycle

---

### Step 5: Verify (Optional)

Check salary cycles via API:
```bash
# List all cycles
curl http://localhost:8080/api/salary-cycles | python3 -m json.tool

# Get totals for cycle ID 1
curl http://localhost:8080/api/salary-cycles/1/totals | python3 -m json.tool
```

---

## 🛠️ CLI Tool (Optional)

For advanced management:
```bash
./salary-cycle-manager.sh
```

Options:
1. Detect Salary Cycles
2. Refresh Salary Cycles
3. Update Last Cycle End Date
4. List All Salary Cycles
5. Get Cycle Totals

---

## 🧪 Test Scenario

### Sample Data to Test:

**Salary Transaction:**
```
Date: 2025-01-05
Description: NEFT SALARY JANUARY
Amount: 50000
Type: CREDIT
Category: Income
```

**Regular Expenses:**
```
Date: 2025-01-10
Description: SWIGGY FOOD ORDER
Amount: 500
Type: DEBIT
Category: Food & Dining
```

**Expected Result:**
- **Salary Cycle Created**: "Jan Salary Cycle (5 Jan – 4 Feb)"
- **Total Income**: ₹50,000
- **Total Expense**: ₹500
- **Net Savings**: ₹49,500

---

## 📊 Where to See the Feature

### Dashboard Page
- **Date Range Selection** box at top
- Toggle: Calendar Month / Salary Cycle
- Shows cycle info and totals

### Transactions Page
- **Filters** section
- Same toggle and cycle selector
- Filters transactions to cycle dates

---

## 🐛 Troubleshooting

### Problem: No salary cycles detected

**Solution:**
1. Check transaction has Category = "Income"
2. Check description has keywords: SALARY, PAYROLL, etc.
3. Manually trigger detection:
   ```bash
   curl -X POST http://localhost:8080/api/salary-cycles/detect
   ```

### Problem: Wrong dates in cycle

**Solution:**
```bash
curl -X POST http://localhost:8080/api/salary-cycles/refresh
```

### Problem: API not responding

**Solution:**
1. Check backend is running: `http://localhost:8080/actuator/health`
2. Check logs in terminal
3. Verify database connection

---

## 💡 Tips

1. **Upload Multiple Months**: Upload 6-12 months of data to see multiple cycles
2. **Refresh Regularly**: Use "update-last" endpoint to keep current cycle current
3. **Compare Cycles**: Switch between cycles to see spending patterns
4. **Use with Filters**: Combine with category filters for deeper insights

---

## 📱 UI Flow

```
Dashboard/Transactions Page
    ↓
[Date Range Selection]
    ↓
Select: "Salary Cycle" ← Toggle here
    ↓
[Dropdown appears with cycles]
    ↓
Select: "Jan Salary Cycle (5 Jan – 4 Feb)"
    ↓
✨ All data updates to show cycle-specific info
```

---

## 🎯 Success Indicators

You'll know it's working when you see:

✅ Salary cycles appear in dropdown  
✅ Cycle label shows dates (e.g., "5 Jan – 4 Feb")  
✅ Blue info banner shows selected date range  
✅ Totals update when switching cycles  
✅ Transactions filter to cycle dates  

---

## 📞 Need Help?

Refer to:
- `SALARY_CYCLE_FEATURE.md` - Complete documentation
- `IMPLEMENTATION_SUMMARY.md` - What was implemented
- Backend logs - Check for "Created salary cycle" messages
- Browser console - Check for API errors

---

**Ready to Start?** → Go to Step 1! 🚀

