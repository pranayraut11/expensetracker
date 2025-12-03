# Sample Bank Statement Data

This document provides sample data you can use to test the Smart Expense Tracker application.

## Creating a Sample Excel File

Use Microsoft Excel, Google Sheets, or LibreOffice Calc to create a file with the following data:

### Sheet 1: Bank Statement

**Column Headers (Row 1):**
```
Date | Description | Amount | Type | Balance
```

**Sample Data (Rows 2-21):**

```
01-01-2025 | Salary for December | 50000 | CREDIT | 50000
02-01-2025 | UPI/1234/Swiggy/Food | -250 | DEBIT | 49750
02-01-2025 | POS 402912 DMART BLR IN | -1500 | DEBIT | 48250
03-01-2025 | UPI/5678/Uber/Ride | -200 | DEBIT | 48050
03-01-2025 | AMZ*Amazon Marketplace | -2500 | DEBIT | 45550
04-01-2025 | UPI/9012/Zomato | -450 | DEBIT | 45100
05-01-2025 | ATM WDL HPCL PETROL PUMP | -2000 | DEBIT | 43100
06-01-2025 | UPI/3456/Netflix Subscription | -799 | DEBIT | 42301
07-01-2025 | NEFT CR REFUND FROM FLIPKART | 500 | CREDIT | 42801
08-01-2025 | POS 567890 RELIANCE FRESH | -1200 | DEBIT | 41601
09-01-2025 | UPI/7890/OLA CABS | -150 | DEBIT | 41451
10-01-2025 | Electricity Bill Payment | -1800 | DEBIT | 39651
11-01-2025 | UPI/2345/BIGBASKET | -2500 | DEBIT | 37151
12-01-2025 | POS 123456 KFC RESTAURANT | -650 | DEBIT | 36501
13-01-2025 | House Rent Payment | -15000 | DEBIT | 21501
14-01-2025 | UPI/6789/APOLLO PHARMACY | -850 | DEBIT | 20651
15-01-2025 | UPI/4567/BOOKMYSHOW | -500 | DEBIT | 20151
16-01-2025 | Airtel Prepaid Recharge | -599 | DEBIT | 19552
17-01-2025 | UPI/8901/MYNTRA FASHION | -1999 | DEBIT | 17553
18-01-2025 | IMPS CR Cashback Credit | 100 | CREDIT | 17653
```

## Alternative: CSV Format

If you prefer CSV, create a file named `sample_transactions.csv`:

```csv
Date,Description,Amount,Type,Balance
01-01-2025,Salary for December,50000,CREDIT,50000
02-01-2025,UPI/1234/Swiggy/Food,-250,DEBIT,49750
02-01-2025,POS 402912 DMART BLR IN,-1500,DEBIT,48250
03-01-2025,UPI/5678/Uber/Ride,-200,DEBIT,48050
03-01-2025,AMZ*Amazon Marketplace,-2500,DEBIT,45550
04-01-2025,UPI/9012/Zomato,-450,DEBIT,45100
05-01-2025,ATM WDL HPCL PETROL PUMP,-2000,DEBIT,43100
06-01-2025,UPI/3456/Netflix Subscription,-799,DEBIT,42301
07-01-2025,NEFT CR REFUND FROM FLIPKART,500,CREDIT,42801
08-01-2025,POS 567890 RELIANCE FRESH,-1200,DEBIT,41601
09-01-2025,UPI/7890/OLA CABS,-150,DEBIT,41451
10-01-2025,Electricity Bill Payment,-1800,DEBIT,39651
11-01-2025,UPI/2345/BIGBASKET,-2500,DEBIT,37151
12-01-2025,POS 123456 KFC RESTAURANT,-650,DEBIT,36501
13-01-2025,House Rent Payment,-15000,DEBIT,21501
14-01-2025,UPI/6789/APOLLO PHARMACY,-850,DEBIT,20651
15-01-2025,UPI/4567/BOOKMYSHOW,-500,DEBIT,20151
16-01-2025,Airtel Prepaid Recharge,-599,DEBIT,19552
17-01-2025,UPI/8901/MYNTRA FASHION,-1999,DEBIT,17553
18-01-2025,IMPS CR Cashback Credit,100,CREDIT,17653
```

Then save as Excel format (.xlsx) from your spreadsheet application.

## Expected Categorization

When you upload the sample data, transactions should be categorized as:

- **Income**: Salary, Refund, Cashback (3 transactions)
- **Food**: Swiggy, Zomato, KFC (3 transactions)
- **Groceries**: DMart, Reliance Fresh, BigBasket (3 transactions)
- **Shopping**: Amazon, Myntra (2 transactions)
- **Travel**: Uber, Ola (2 transactions)
- **Fuel**: HPCL Petrol Pump (1 transaction)
- **Entertainment**: Netflix, BookMyShow (2 transactions)
- **Bills**: Electricity, Airtel Recharge (2 transactions)
- **Medical**: Apollo Pharmacy (1 transaction)
- **Rent**: House Rent (1 transaction)

## Summary Statistics

- **Total Income**: ₹50,600
- **Total Expenses**: ₹32,947
- **Net Savings**: ₹17,653
- **Transaction Count**: 20

## Tips for Testing

1. Save the data as an Excel file (.xlsx or .xls)
2. Ensure the first row contains headers exactly as shown
3. Date format can be DD-MM-YYYY, DD/MM/YYYY, or YYYY-MM-DD
4. Amount can be negative for debits or positive (rely on Type column)
5. Type must be either "DEBIT" or "CREDIT"
6. Balance column is optional but recommended

## Generating More Data

To test with more data, you can:

1. Duplicate existing rows and change dates
2. Add more merchant names (the app will categorize them)
3. Mix different date formats to test parser robustness
4. Add some malformed rows to test error handling

## Real Bank Statement

When using your actual bank statement:

1. Export transactions from your bank's website
2. Convert to Excel format if needed
3. Ensure column order matches: Date, Description, Amount, Type, Balance
4. Remove any header/footer rows from the bank
5. Keep only the transaction data rows

