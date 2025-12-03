# Quick Start Guide

## 🎯 Running the Application

### Option 1: Using the Start Script (Easiest)

```bash
cd /Users/p.raut/demoprojects/expensetracker
./start.sh
```

This will:
- Check prerequisites (Java, Maven, Node.js)
- Build and start the backend
- Install dependencies and start the frontend
- Open the application in your browser

To stop: Press `Ctrl+C`

### Option 2: Manual Start

#### Terminal 1 - Backend
```bash
cd /Users/p.raut/demoprojects/expensetracker/backend
mvn clean install
mvn spring-boot:run
```

#### Terminal 2 - Frontend
```bash
cd /Users/p.raut/demoprojects/expensetracker/frontend
npm install
npm run dev
```

## 📍 Access Points

- **Frontend App**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:~/expense-tracker-db`
  - Username: `sa`
  - Password: (leave blank)

## 🧪 Testing the Application

### Step 1: Create Sample Excel File

Create a file named `sample_transactions.xlsx` with this data:

| Date | Description | Amount | Type | Balance |
|------|-------------|--------|------|---------|
| 01-01-2025 | Salary for December | 50000 | CREDIT | 50000 |
| 02-01-2025 | UPI/1234/Swiggy | -250 | DEBIT | 49750 |
| 02-01-2025 | POS 402912 DMART BLR IN | -1500 | DEBIT | 48250 |
| 03-01-2025 | UPI/5678/Uber | -200 | DEBIT | 48050 |
| 03-01-2025 | AMZ*Amazon Marketplace | -2500 | DEBIT | 45550 |
| 05-01-2025 | ATM WDL HPCL PETROL | -2000 | DEBIT | 43550 |
| 10-01-2025 | Electricity Bill Payment | -1800 | DEBIT | 41750 |
| 13-01-2025 | House Rent Payment | -15000 | DEBIT | 26750 |

### Step 2: Upload File

1. Navigate to http://localhost:5173
2. Click "Upload" in the navigation
3. Select your Excel file
4. Click "Upload and Process"

### Step 3: View Results

1. Go to "Dashboard" to see:
   - Summary cards (Income, Expenses, Savings)
   - Pie chart of category breakdown
   - Bar chart of top expenses

2. Go to "Transactions" to see:
   - Complete transaction list
   - Filter by category, date range
   - All transactions with categories

## 🔧 Troubleshooting

### Backend won't start

```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill the process if needed
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Frontend won't start

```bash
# Clear node_modules and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Database issues

```bash
# Delete and recreate database
rm ~/expense-tracker-db.mv.db
# Restart backend - database will be recreated
```

### CORS errors

- Ensure backend is running on port 8080
- Check browser console for specific error
- Verify WebConfig.java has correct origins

## 📊 Expected Results

After uploading the sample data:

- **Total Income**: ₹50,000
- **Total Expenses**: ₹23,250
- **Net Savings**: ₹26,750
- **Transaction Count**: 8

Categories:
- Income: 1
- Food: 1
- Groceries: 1
- Shopping: 1
- Travel: 1
- Fuel: 1
- Bills: 1
- Rent: 1

## 🎓 Next Steps

1. **Add more transactions**: Upload your own bank statement
2. **Customize categories**: Edit `CategoryRuleEngine.java`
3. **Modify UI**: Update React components in `frontend/src`
4. **Add features**: Extend the API and add new pages

## 📝 Common Tasks

### View logs in real-time
```bash
# Backend logs
tail -f backend.log

# Frontend logs
tail -f frontend.log
```

### Clear all data
```bash
# Delete database file
rm ~/expense-tracker-db.mv.db

# Restart backend
```

### Check backend health
```bash
curl http://localhost:8080/api/transactions/summary
```

### Build for production

Backend:
```bash
cd backend
mvn clean package
java -jar target/expensetracker-1.0.0.jar
```

Frontend:
```bash
cd frontend
npm run build
# Dist folder will contain production build
```

## 💡 Tips

- Keep the first row as headers in Excel
- Date formats: DD-MM-YYYY, DD/MM/YYYY, or YYYY-MM-DD
- Type must be exactly "DEBIT" or "CREDIT"
- Amount can be negative or positive (Type column determines transaction type)
- Balance column is optional

## 🐛 Known Issues

None at this time. If you encounter issues, check:
1. Java version (must be 17+)
2. Node version (must be 18+)
3. Port availability (8080 and 5173)
4. File permissions (database file location)

---

**Happy Tracking! 💰**

