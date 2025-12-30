# Salary Cycle Feature - Implementation Guide

## 📋 Overview

The Salary Cycle feature allows you to track expenses and income based on **salary credit dates** instead of calendar months (1st-30th). This provides a more accurate view of your monthly financial status aligned with your actual income cycle.

## 🎯 Business Logic

### What is a Salary Cycle?

A **Salary Cycle** is defined as:
- **Start Date**: Date when salary is credited
- **End Date**: One day before the next salary credit date

**Example:**
- Salary credited on: 5th January 2025
- Next salary credited on: 5th February 2025
- **Salary Cycle**: 5 Jan 2025 to 4 Feb 2025

All income, expenses, and savings calculations are based on this cycle.

---

## 🏗️ Architecture

### Backend Components

#### 1. **Entity: SalaryCycle**
**File**: `backend/src/main/java/com/example/expensetracker/model/SalaryCycle.java`

Stores salary cycle information:
- `id`: Unique identifier
- `startDate`: Salary credit date
- `endDate`: Day before next salary
- `salaryAmount`: Amount credited
- `salaryTransactionId`: Reference to transaction

#### 2. **Repository: SalaryCycleRepository**
**File**: `backend/src/main/java/com/example/expensetracker/repository/SalaryCycleRepository.java`

Database operations:
- Find all cycles (ordered by date)
- Find cycle by transaction ID
- Find next cycle after a date

#### 3. **Utility: SalaryTransactionDetector**
**File**: `backend/src/main/java/com/example/expensetracker/util/SalaryTransactionDetector.java`

**Detection Rules:**
A transaction is considered salary if:
- Type = `CREDIT`
- Category = `Income`
- Description matches: `SALARY`, `PAYROLL`, `NEFT SALARY`, `MONTHLY SALARY`, `SAL CREDIT`, etc.

#### 4. **Service: SalaryCycleService**
**File**: `backend/src/main/java/com/example/expensetracker/service/SalaryCycleService.java`

**Key Methods:**
- `detectAndCreateSalaryCycles()`: Automatically detect and create cycles
- `getAllSalaryCycles()`: Get all cycles
- `calculateSalaryCycleTotals(cycleId)`: Calculate totals for a cycle
- `refreshSalaryCycles()`: Re-detect and recreate cycles

#### 5. **Controller: SalaryCycleController**
**File**: `backend/src/main/java/com/example/expensetracker/controller/SalaryCycleController.java`

**API Endpoints:**

```
GET /api/salary-cycles
```
Returns all salary cycles

**Response:**
```json
[
  {
    "cycleId": 1,
    "label": "Jan Salary Cycle (5 Jan – 4 Feb)",
    "startDate": "2025-01-05",
    "endDate": "2025-02-04",
    "salaryAmount": 50000.0
  }
]
```

---

```
GET /api/salary-cycles/{cycleId}/totals
```
Returns financial totals for a specific cycle

**Response:**
```json
{
  "totalCredit": 52000,
  "totalDebit": 43000,
  "netSavings": 9000,
  "salaryAmount": 50000
}
```

---

```
POST /api/salary-cycles/detect
```
Manually trigger salary cycle detection

---

```
POST /api/salary-cycles/refresh
```
Delete and recreate all salary cycles

---

### Frontend Components

#### 1. **Service: salaryCycleApi.js**
**File**: `frontend/src/services/salaryCycleApi.js`

API client functions:
- `getSalaryCycles()`: Fetch all cycles
- `getSalaryCycleById(cycleId)`: Fetch specific cycle
- `getSalaryCycleTotals(cycleId)`: Fetch cycle totals
- `detectSalaryCycles()`: Trigger detection
- `refreshSalaryCycles()`: Refresh all cycles

#### 2. **Component: SalaryCycleSelector**
**File**: `frontend/src/components/SalaryCycleSelector.jsx`

**Props:**
- `onCycleChange`: Callback when cycle changes
- `onModeChange`: Callback when mode changes (calendar/salary)
- `selectedMode`: Current mode ('calendar' or 'salary')
- `selectedCycleId`: Currently selected cycle ID

**Features:**
- Toggle between Calendar Month and Salary Cycle
- Dropdown to select specific salary cycle
- Auto-selects most recent cycle
- Shows message if no cycles found

#### 3. **Updated Pages**

**DashboardPage.jsx**
- Integrated SalaryCycleSelector
- Shows totals based on selected cycle
- Displays cycle date range and salary amount
- All charts respect salary cycle dates

**TransactionsPage.jsx**
- Integrated SalaryCycleSelector
- Filters transactions by salary cycle dates
- Shows cycle info banner when selected
- Totals calculated for cycle period

---

## 🔄 Automatic Detection Flow

### When Transactions are Uploaded:

1. **User uploads bank statement** → `TransactionService.saveAllWithDuplicateCheck()`
2. **After saving transactions** → `SalaryCycleService.detectAndCreateSalaryCycles()`
3. **Service scans all income transactions** → Filters by salary keywords
4. **Creates salary cycles** → Start date = salary date, End date = next salary - 1 day
5. **Last cycle** → End date = current date (auto-updates)

### Salary Detection Example:

```
Transaction:
- Date: 2025-01-05
- Type: CREDIT
- Amount: 50000
- Description: "NEFT SALARY JAN 2025"
- Category: Income

✅ DETECTED AS SALARY
```

---

## 💡 Usage Guide

### For Users:

#### Step 1: Upload Bank Statement
Upload your bank statement containing salary transactions.

#### Step 2: View Dashboard
- Go to **Dashboard**
- You'll see a "Date Range Selection" section
- Choose "Salary Cycle" from the dropdown

#### Step 3: Select Cycle
- A dropdown will appear with all detected salary cycles
- Example: "Jan Salary Cycle (5 Jan – 4 Feb)"
- Select the cycle you want to view

#### Step 4: View Data
- All totals, charts, and metrics update to show data for that cycle
- Income/Expense/Savings calculated for the cycle period

#### Step 5: View Transactions
- Go to **Transactions** page
- Use the same Salary Cycle selector
- All transactions filtered to the cycle period

### Manual Actions:

**Refresh Cycles:**
```bash
POST /api/salary-cycles/refresh
```
Use this if you've added/modified salary transactions.

**Update Last Cycle:**
```bash
POST /api/salary-cycles/update-last
```
Updates the end date of the most recent cycle to today.

---

## 📊 Calculation Rules

### What's Included:
✅ Salary credit transaction (included in total credit)  
✅ All other income in the cycle  
✅ All expenses (debit transactions)  
✅ Credit card purchases (from CC statement)  

### What's Excluded:
❌ Credit card payments (from bank statement)  
❌ Transfers (where `includeInTotals = false`)  

### Formula:
```
Net Savings = Total Credit - Total Debit
```

Where:
- **Total Credit** = All CREDIT transactions with `includeInTotals = true` (excludes CC transactions)
- **Total Debit** = All DEBIT transactions with `includeInTotals = true` (includes CC purchases)

---

## 🛠️ Developer Notes

### Adding New Salary Keywords:

Edit `SalaryTransactionDetector.java`:
```java
private static final Pattern SALARY_PATTERN = Pattern.compile(
    ".*(SALARY|PAYROLL|YOUR_NEW_KEYWORD).*",
    Pattern.CASE_INSENSITIVE
);
```

### Database Schema:

The `salary_cycles` table is automatically created by JPA:
```sql
CREATE TABLE salary_cycles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    salary_amount DOUBLE NOT NULL,
    salary_transaction_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### Testing:

1. **Upload sample transactions** with salary credits
2. **Check logs** for "Created salary cycle" messages
3. **Call** `GET /api/salary-cycles` to verify cycles created
4. **Test totals** with `GET /api/salary-cycles/{id}/totals`

---

## 🐛 Troubleshooting

### Issue: No Salary Cycles Detected

**Solution:**
1. Verify transactions have category = "Income"
2. Check description contains salary keywords
3. Run manual detection: `POST /api/salary-cycles/detect`

### Issue: Wrong Dates in Cycle

**Solution:**
1. Check salary transaction dates are correct
2. Refresh cycles: `POST /api/salary-cycles/refresh`

### Issue: Totals Don't Match

**Solution:**
1. Verify `includeInTotals` flag on transactions
2. Check credit card payment exclusion
3. Verify date range of cycle

---

## 📈 Future Enhancements

- [ ] Support multiple salary sources
- [ ] Handle variable salary dates (e.g., last working day)
- [ ] Cycle comparison (Month vs Month)
- [ ] Budget allocation per cycle
- [ ] Savings rate calculation
- [ ] Predictive analytics

---

## 📝 Summary

The Salary Cycle feature provides:
- ✅ Automatic detection of salary transactions
- ✅ Dynamic cycle creation based on actual salary dates
- ✅ Accurate financial tracking aligned with income
- ✅ Seamless UI integration with calendar month toggle
- ✅ Real-time totals and analytics per cycle

Perfect for users who want to understand their spending patterns relative to when they get paid!

---

**Version**: 1.0.0  
**Last Updated**: December 28, 2025

