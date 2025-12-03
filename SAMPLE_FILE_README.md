# 📊 Sample Bank Statement Excel File

## ✅ File Created: `sample_bank_statement.xlsx`

A ready-to-use Excel file with realistic bank transaction data for testing the Smart Expense Tracker application.

---

## 📁 File Details

- **Filename**: `sample_bank_statement.xlsx`
- **Location**: `/Users/p.raut/demoprojects/expensetracker/`
- **Size**: ~6.8 KB
- **Format**: Excel 2007+ (.xlsx)

---

## 📊 Transaction Summary

### Overview
- **Total Transactions**: 41
- **Date Range**: November 1-30, 2024
- **Starting Balance**: ₹0
- **Final Balance**: ₹-4,831

### Breakdown by Type
- **Income (CREDIT)**: 3 transactions
  - Salary: ₹50,000
  - Refund: ₹500
  - Interest: ₹250
  - **Total Income**: ₹50,750

- **Expenses (DEBIT)**: 38 transactions
  - **Total Expenses**: ₹55,581

### Expected Categories
The sample file includes transactions for all 13 categories:

1. **Income** (3 transactions)
   - Salary, Refunds, Interest

2. **Food** (8 transactions)
   - Swiggy, Zomato, KFC, Dominos, Pizza Hut, Starbucks

3. **Groceries** (6 transactions)
   - DMart, Reliance Fresh, BigBasket, More, Blinkit

4. **Shopping** (6 transactions)
   - Amazon, Flipkart, Myntra, Ajio

5. **Travel** (5 transactions)
   - Uber, Ola (multiple rides)

6. **Bills** (4 transactions)
   - Electricity, Airtel, Jio Fiber, Water

7. **Fuel** (2 transactions)
   - HPCL, BPCL petrol pumps

8. **Medical** (2 transactions)
   - Apollo Pharmacy, MedPlus

9. **Entertainment** (4 transactions)
   - Netflix, BookMyShow, Hotstar, YouTube Premium

10. **Rent** (1 transaction)
    - Monthly house rent

11. **Investment** (1 transaction)
    - Mutual Fund SIP

12. **Education** (1 transaction)
    - Udemy course

13. **Miscellaneous** (any uncategorized)

---

## 🎯 How to Use

### Option 1: Use the Generated File Directly

```bash
# The file is already created at:
/Users/p.raut/demoprojects/expensetracker/sample_bank_statement.xlsx

# Just upload it through the web interface!
```

### Option 2: Generate a New File

```bash
cd /Users/p.raut/demoprojects/expensetracker
python3 generate_sample_excel.py
```

This will create a fresh `sample_bank_statement.xlsx` file.

---

## 📝 Excel File Structure

### Columns

| Column | Name | Type | Example |
|--------|------|------|---------|
| A | Date | Date String | 01-11-2024 |
| B | Description | Text | UPI/1234/Swiggy |
| C | Amount | Number | -250 or 50000 |
| D | Type | Text | DEBIT or CREDIT |
| E | Balance | Number | 49750 |

### Sample Rows

```
Date        | Description                      | Amount  | Type   | Balance
01-11-2024  | Salary for November             | 50000   | CREDIT | 50000
02-11-2024  | UPI/1234/Swiggy/Food Delivery   | -250    | DEBIT  | 49750
02-11-2024  | POS 402912 DMART BLR IN         | -1500   | DEBIT  | 48250
03-11-2024  | UPI/5678/Uber/Morning Ride      | -200    | DEBIT  | 48050
...
```

---

## 🚀 Testing Steps

### 1. Start the Application

```bash
cd /Users/p.raut/demoprojects/expensetracker
./start.sh
```

### 2. Open Browser

Navigate to: http://localhost:5173

### 3. Upload the Sample File

1. Click **"Upload"** in the navigation menu
2. Click **"Choose File"** or drag-and-drop
3. Select `sample_bank_statement.xlsx`
4. Click **"Upload and Process"**

### 4. View Results

**Expected Upload Response:**
```json
{
  "rowsProcessed": 41,
  "rowsSaved": 41,
  "errors": 0
}
```

**Go to Dashboard to see:**
- Total Income: ₹50,750
- Total Expenses: ₹55,581
- Net Savings: -₹4,831 (spent more than earned)
- Transaction Count: 41

**Charts will show:**
- Pie Chart: Category-wise expense breakdown
- Bar Chart: Top spending categories

**Go to Transactions to see:**
- Complete list of 41 transactions
- Filter by category (Food, Groceries, etc.)
- Filter by date range

---

## 🎨 Realistic Transaction Patterns

The sample file includes realistic transaction descriptions:

### UPI Transactions
```
UPI/1234/Swiggy/Food Delivery
UPI/5678/Uber/Morning Ride
UPI/9012/Zomato/Lunch Order
```

### POS (Point of Sale) Transactions
```
POS 402912 DMART BLR IN
POS 234567 KFC RESTAURANT
POS 567890 RELIANCE FRESH
```

### Online Shopping
```
AMZ*Amazon Marketplace
AMZ*Amazon Prime Membership
```

### Bill Payments
```
Electricity Bill Payment BSES
Airtel Prepaid Recharge
JIO Fiber Internet Bill
```

### ATM Withdrawals (Fuel)
```
ATM WDL HPCL PETROL PUMP BLR
ATM WDL BPCL PETROL PUMP
```

---

## 🔄 Regenerate Sample Data

If you want to modify the sample data:

1. Edit `generate_sample_excel.py`
2. Modify the `transactions` list
3. Run: `python3 generate_sample_excel.py`

Example modifications:
```python
# Add your own transaction
(15, "UPI/1234/YOUR_MERCHANT", -500, "DEBIT"),

# Change amounts
(1, "Salary for November", 75000, "CREDIT"),  # Higher salary

# Add more transactions for specific categories
(20, "POS 123456 PANTALOONS", -3000, "DEBIT"),  # Shopping
```

---

## 📊 Expected Dashboard Results

After uploading the sample file, you should see:

### Summary Cards
- **Total Income**: ₹50,750 (green card)
- **Total Expenses**: ₹55,581 (red card)
- **Net Savings**: -₹4,831 (orange card, negative)
- **Transactions**: 41 (blue card)

### Top Categories by Spending
1. Rent: ₹15,000
2. Investment: ₹5,000
3. Shopping: ~₹9,000+
4. Groceries: ~₹7,000+
5. Food: ~₹3,000+
6. Fuel: ₹3,800
7. Bills: ~₹3,800
8. Travel: ~₹1,700
9. Entertainment: ~₹1,700
10. Medical: ~₹1,500

---

## 💡 Tips

✅ **Realistic Data**: The sample includes typical monthly expenses
✅ **All Categories**: Covers all 13 expense categories
✅ **Proper Format**: Follows exact format expected by the parser
✅ **Mixed Patterns**: UPI, POS, online, bill payments
✅ **Balanced**: Mix of large and small transactions

---

## 🔧 Customization

### Create Your Own Test Data

Copy the sample and modify it:

```bash
# Make a copy
cp sample_bank_statement.xlsx my_test_data.xlsx

# Edit in Excel/Google Sheets
# - Change dates
# - Modify amounts
# - Add/remove transactions
# - Test edge cases
```

### Test Edge Cases

1. **Large file**: Copy-paste rows to create 500+ transactions
2. **Different dates**: Test various date formats
3. **Missing balance**: Leave balance column empty
4. **Special characters**: Test descriptions with emojis, etc.
5. **Large amounts**: Test with lakhs/crores

---

## 🎯 What You'll Learn

By uploading this sample file, you'll see how the app:

✅ Parses Excel files correctly
✅ Categorizes transactions automatically
✅ Handles UPI/POS transaction patterns
✅ Calculates running balance
✅ Generates visual reports
✅ Filters and searches transactions

---

## 🐛 Troubleshooting

**File won't upload:**
- Check file size (must be < 10MB)
- Verify .xlsx extension
- Ensure backend is running

**Wrong categories:**
- Check `CategoryRuleEngine.java` for keyword rules
- Descriptions are cleaned before categorization
- "Miscellaneous" means no keyword match found

**Missing transactions:**
- Check for empty rows in Excel
- Verify all required columns are present
- Check backend logs for parsing errors

---

## 📞 Need More Help?

- Review: `QUICKSTART.md`
- Check: `README.md`
- See: `SAMPLE_DATA.md` for format details

---

**Ready to test? Upload `sample_bank_statement.xlsx` and start tracking! 💰📊**

