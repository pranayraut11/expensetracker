# 🔄 Migration Guide - Existing Data to Salary Cycles

If you already have transactions in your database and want to enable salary cycle tracking, follow this guide.

---

## 📋 Pre-Migration Checklist

- [ ] Backend is running
- [ ] Database has existing transactions
- [ ] At least one salary transaction exists with:
  - Type = CREDIT
  - Category = Income
  - Description contains salary keywords

---

## 🚀 Migration Steps

### Step 1: Verify Your Data

Check if you have salary transactions:

```sql
-- Run in your database
SELECT * FROM transactions 
WHERE type = 'CREDIT' 
  AND category = 'Income' 
  AND LOWER(description) LIKE '%salary%'
ORDER BY date DESC;
```

If you see results → You're ready to proceed!

---

### Step 2: Ensure Transactions Are Categorized

Salary transactions **must** have `category = 'Income'`.

If not categorized:
1. Go to Rules page
2. Create a rule for salary:
   ```
   Category: Income
   Condition: (SALARY|PAYROLL|NEFT SALARY|MONTHLY SALARY)
   ```
3. Re-categorize existing transactions

Or manually update:
```sql
UPDATE transactions 
SET category = 'Income' 
WHERE type = 'CREDIT' 
  AND LOWER(description) LIKE '%salary%';
```

---

### Step 3: Deploy New Code

**Option A: Using Git**
```bash
# Pull latest code
git pull origin main

# Rebuild backend
cd backend
mvn clean package -DskipTests

# Restart backend
mvn spring-boot:run
```

**Option B: Manual**
- Copy new Java files to your project
- Rebuild and restart

---

### Step 4: Create Salary Cycles Table

The table will be **auto-created** by JPA on startup!

Check logs for:
```
Hibernate: create table salary_cycles (...)
```

Or verify manually:
```sql
SHOW TABLES LIKE 'salary_cycles';
```

---

### Step 5: Trigger Detection

**Option A: Automatic (Recommended)**

Upload any new transaction → Detection runs automatically

**Option B: Manual API Call**

```bash
curl -X POST http://localhost:8080/api/salary-cycles/detect
```

**Option C: Using CLI Tool**

```bash
./salary-cycle-manager.sh
# Select option 1: Detect Salary Cycles
```

---

### Step 6: Verify Cycles Created

```bash
# List all cycles
curl http://localhost:8080/api/salary-cycles | python3 -m json.tool
```

Expected output:
```json
[
  {
    "cycleId": 1,
    "label": "Dec Salary Cycle (5 Dec – 4 Jan)",
    "startDate": "2024-12-05",
    "endDate": "2025-01-04",
    "salaryAmount": 50000.0
  },
  {
    "cycleId": 2,
    "label": "Jan Salary Cycle (5 Jan – 28 Dec)",
    "startDate": "2025-01-05",
    "endDate": "2025-12-28",
    "salaryAmount": 50000.0
  }
]
```

---

### Step 7: Test in UI

1. Go to Dashboard
2. Select "Salary Cycle" mode
3. Choose a cycle
4. Verify totals are correct

---

## 🔧 Manual Detection (If Needed)

If automatic detection didn't work:

### Check Logs
Look for:
```
INFO  SalaryCycleService - Starting salary cycle detection...
INFO  SalaryCycleService - Found X salary transactions
INFO  SalaryCycleService - Created salary cycle: 2025-01-05 to 2025-02-04 (Salary: 50000.0)
```

### Debug Detection

Test the detector manually:
```java
// In your code or test
SalaryTransactionDetector detector = new SalaryTransactionDetector();

Transaction t = new Transaction();
t.setType("CREDIT");
t.setCategory("Income");
t.setDescription("NEFT SALARY JAN 2025");
t.setAmount(50000.0);

boolean isSalary = detector.isSalaryTransaction(t);
System.out.println("Is Salary: " + isSalary); // Should be true
```

---

## 🛠️ Common Issues & Fixes

### Issue 1: No Cycles Created

**Symptoms:**
- API returns empty array: `[]`
- No cycles in dropdown

**Diagnosis:**
```bash
# Check if salary transactions exist
curl "http://localhost:8080/transactions?category=Income&type=CREDIT"
```

**Fix:**
1. Ensure transactions have correct category
2. Check description has salary keywords
3. Run detection again:
   ```bash
   curl -X POST http://localhost:8080/api/salary-cycles/refresh
   ```

---

### Issue 2: Wrong Cycle Dates

**Symptoms:**
- Cycle end date is today for all cycles
- Cycles overlap

**Fix:**
```bash
# Refresh cycles (deletes and recreates)
curl -X POST http://localhost:8080/api/salary-cycles/refresh
```

---

### Issue 3: Duplicate Cycles

**Symptoms:**
- Multiple cycles for same month
- Detection ran multiple times

**Fix:**
```bash
# Refresh clears duplicates
curl -X POST http://localhost:8080/api/salary-cycles/refresh
```

---

### Issue 4: Missing Last Month

**Symptoms:**
- Current month cycle not showing

**Cause:** Last cycle end date needs update

**Fix:**
```bash
curl -X POST http://localhost:8080/api/salary-cycles/update-last
```

---

## 📊 Validation Queries

### Check Salary Cycles Table
```sql
SELECT * FROM salary_cycles ORDER BY start_date DESC;
```

### Check Totals for a Cycle
```sql
-- Replace dates with your cycle dates
SELECT 
  SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END) as total_credit,
  SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE 0 END) as total_debit
FROM transactions
WHERE date BETWEEN '2025-01-05' AND '2025-02-04'
  AND include_in_totals = true;
```

### Check Salary Transactions
```sql
SELECT 
  id,
  date,
  description,
  amount,
  type,
  category
FROM transactions
WHERE type = 'CREDIT'
  AND category = 'Income'
  AND (
    LOWER(description) LIKE '%salary%'
    OR LOWER(description) LIKE '%payroll%'
  )
ORDER BY date DESC;
```

---

## 🎯 Expected Results After Migration

✅ `salary_cycles` table exists  
✅ Cycles visible in API: `/api/salary-cycles`  
✅ Each salary transaction has a cycle  
✅ Cycle dates are sequential (no gaps)  
✅ Last cycle end date is today  
✅ Totals calculate correctly  
✅ UI shows cycles in dropdown  

---

## 📈 Performance Notes

- Detection scans all income transactions: **Fast for < 10K transactions**
- Runs async after transaction upload: **No user impact**
- Cycles cached in database: **Instant retrieval**
- Refresh takes 1-2 seconds: **OK to run periodically**

---

## 🔄 Ongoing Maintenance

### Daily
- Nothing! Auto-detects new salary transactions

### Monthly (Optional)
```bash
# Update last cycle to include current date
curl -X POST http://localhost:8080/api/salary-cycles/update-last
```

### When Rules Change
```bash
# If you recategorize transactions
curl -X POST http://localhost:8080/api/salary-cycles/refresh
```

---

## 🎓 Best Practices

1. **Initial Setup**: Run detection once after deploying
2. **Regular Use**: Let auto-detection handle new uploads
3. **Data Changes**: Use refresh if you modify historical transactions
4. **End of Month**: Use update-last to extend current cycle

---

## 📞 Rollback (If Needed)

If you want to disable the feature:

```sql
-- Drop the table
DROP TABLE IF EXISTS salary_cycles;
```

Then:
- Remove SalaryCycleSelector from UI
- Comment out detection call in TransactionService

The app will work normally without salary cycles.

---

## ✅ Migration Checklist

- [ ] Verified salary transactions exist
- [ ] Transactions have category = "Income"
- [ ] Deployed new code
- [ ] Table created automatically
- [ ] Ran detection (manual or auto)
- [ ] Verified cycles in API response
- [ ] Tested in Dashboard UI
- [ ] Tested in Transactions UI
- [ ] Verified totals are accurate

---

**Migration Complete!** 🎉

Your existing data is now organized into salary cycles!

---

**Need Help?**
- Check logs: `backend/logs/spring-boot-logger.log`
- Test APIs: Use Postman or curl
- Validate data: Run SQL queries above
- Read docs: `SALARY_CYCLE_FEATURE.md`

