# Smart Expense Tracker - MVP

A production-ready local-only web application for tracking and analyzing expenses from bank statement Excel files.

## 🚀 Features

- **Excel Upload**: Upload bank statements in .xls or .xlsx format
- **Automatic Categorization**: Smart categorization using keyword-based rules
- **Transaction Management**: View, filter, and search all transactions
- **Visual Analytics**: Interactive charts and graphs for expense analysis
- **Summary Dashboard**: Overview of income, expenses, and savings
- **Category Breakdown**: Detailed breakdown by spending categories

## 🛠️ Tech Stack

### Backend
- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (file-based)
- **Apache POI** (Excel parsing)
- **Maven**

### Frontend
- **React 18**
- **Vite**
- **Tailwind CSS**
- **React Router**
- **Axios**
- **Recharts**

## 📋 Prerequisites

- **Java 17 or higher** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **npm** (comes with Node.js)

## 🚀 Getting Started

### 1. Clone or Download the Project

```bash
cd /Users/p.raut/demoprojects/expensetracker
```

### 2. Start the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend server will start at `http://localhost:8080`

**H2 Console**: Access at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:~/expense-tracker-db`
- Username: `sa`
- Password: (leave blank)

### 3. Start the Frontend

Open a new terminal window:

```bash
cd frontend
npm install
npm run dev
```

The frontend will start at `http://localhost:5173` and automatically open in your browser.

## 📊 Excel File Format

Your bank statement Excel file should have the following columns (in order):

| Column | Type | Description | Example |
|--------|------|-------------|---------|
| Date | Date/String | Transaction date | 01-01-2025 or 2025-01-01 |
| Description | String | Transaction description | UPI/1234/Swiggy |
| Amount | Number | Transaction amount | -250 or 50000 |
| Type | String | DEBIT or CREDIT | DEBIT |
| Balance | Number | Account balance after transaction | 12000 |

### Sample Excel Data

```
Date        | Description              | Amount  | Type   | Balance
----------- | ------------------------ | ------- | ------ | --------
01-01-2025  | UPI/1234/Swiggy         | -250    | DEBIT  | 12000
01-01-2025  | Salary for December     | 50000   | CREDIT | 62000
02-01-2025  | POS 402912 DMART BLR    | -1500   | DEBIT  | 60500
02-01-2025  | UPI/5678/Uber           | -200    | DEBIT  | 60300
03-01-2025  | AMZ*Amazon Marketplace  | -2500   | DEBIT  | 57800
```

## 📁 Project Structure

### Backend (`/backend`)

```
backend/
├── src/main/java/com/example/expensetracker/
│   ├── ExpenseTrackerApplication.java     # Main application class
│   ├── config/
│   │   └── WebConfig.java                 # CORS configuration
│   ├── controller/
│   │   ├── UploadController.java          # File upload endpoint
│   │   └── TransactionController.java     # Transaction APIs
│   ├── dto/
│   │   ├── TransactionDto.java            # Transaction DTO
│   │   ├── SummaryDto.java                # Summary DTO
│   │   └── UploadResponseDto.java         # Upload response DTO
│   ├── exception/
│   │   └── GlobalExceptionHandler.java    # Exception handling
│   ├── model/
│   │   └── Transaction.java               # Transaction entity
│   ├── repository/
│   │   └── TransactionRepository.java     # JPA repository
│   ├── service/
│   │   ├── CategorizationService.java     # Categorization logic
│   │   ├── ExcelParserService.java        # Excel parsing
│   │   └── TransactionService.java        # Transaction service
│   └── util/
│       ├── CategoryRuleEngine.java        # Category rules
│       └── DescriptionCleaner.java        # Description cleanup
├── src/main/resources/
│   └── application.properties             # Application config
└── pom.xml                                # Maven dependencies
```

### Frontend (`/frontend`)

```
frontend/
├── src/
│   ├── components/
│   │   ├── CategoryPieChart.jsx           # Pie chart component
│   │   ├── ExpenseBarChart.jsx            # Bar chart component
│   │   ├── SummaryCards.jsx               # Summary cards
│   │   └── TransactionTable.jsx           # Transaction table
│   ├── pages/
│   │   ├── DashboardPage.jsx              # Dashboard page
│   │   ├── TransactionsPage.jsx           # Transactions page
│   │   └── UploadPage.jsx                 # Upload page
│   ├── services/
│   │   ├── api.js                         # Axios instance
│   │   └── transactionApi.js              # API calls
│   ├── App.jsx                            # Main app component
│   ├── main.jsx                           # Entry point
│   └── index.css                          # Tailwind styles
├── index.html
├── package.json
├── vite.config.js
├── tailwind.config.js
└── postcss.config.js
```

## 🎯 API Endpoints

### Upload Excel File
```
POST /api/upload
Content-Type: multipart/form-data
Parameter: file (Excel file)

Response:
{
  "rowsProcessed": 120,
  "rowsSaved": 118,
  "errors": 2
}
```

### Get Transactions
```
GET /api/transactions
Query Parameters (optional):
  - category: Filter by category
  - fromDate: Start date (yyyy-MM-dd)
  - toDate: End date (yyyy-MM-dd)

Response: Array of TransactionDto
```

### Get Summary
```
GET /api/transactions/summary

Response:
{
  "totalIncome": 55000,
  "totalExpenses": 32000,
  "categoryBreakdown": {
    "Food": 4500,
    "Shopping": 8000,
    "Travel": 2000
  },
  "transactionCount": 120
}
```

## 📊 Categories

The application automatically categorizes transactions into:

- **Food** - Swiggy, Zomato, restaurants, etc.
- **Groceries** - DMart, BigBasket, supermarkets, etc.
- **Shopping** - Amazon, Flipkart, Myntra, etc.
- **Travel** - Uber, Ola, IRCTC, flights, etc.
- **Income** - Salary, credits, refunds, etc.
- **Bills** - Electricity, water, mobile recharge, etc.
- **Fuel** - Petrol, diesel, gas stations, etc.
- **Medical** - Hospitals, pharmacies, clinics, etc.
- **Rent** - House rent, lease payments, etc.
- **Entertainment** - Netflix, Prime Video, movies, etc.
- **Insurance** - Policy premiums, etc.
- **Investment** - Mutual funds, stocks, etc.
- **Education** - Courses, books, tuition, etc.
- **Miscellaneous** - Uncategorized transactions

## 🧪 Testing

### Creating Sample Data

You can create a sample Excel file using Microsoft Excel, Google Sheets, or LibreOffice Calc with the format mentioned above.

### Manual Testing Steps

1. Start both backend and frontend servers
2. Navigate to `http://localhost:5173`
3. Click "Upload" in the navigation
4. Upload your Excel file
5. View the dashboard for visual analytics
6. Browse transactions with filters

## 🔧 Configuration

### Backend Configuration (`application.properties`)

```properties
# Server port
server.port=8080

# Database location
spring.datasource.url=jdbc:h2:file:~/expense-tracker-db

# File upload size
spring.servlet.multipart.max-file-size=10MB
```

### Frontend Configuration (`vite.config.js`)

```javascript
server: {
  port: 5173,
  open: true
}
```

### API Base URL (`src/services/api.js`)

```javascript
baseURL: 'http://localhost:8080/api'
```

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database locked:**
```bash
# Stop all running instances and delete the database file
rm ~/expense-tracker-db.mv.db
```

### Frontend Issues

**Port 5173 already in use:**
```bash
# Vite will automatically use the next available port
# Or specify a different port in vite.config.js
```

**CORS errors:**
- Ensure backend is running
- Check WebConfig.java has correct origins

## 📝 Notes

- The database file is stored at `~/expense-tracker-db.mv.db`
- All data is stored locally on your machine
- No internet connection required after initial setup
- Excel files up to 10MB are supported

## 🎨 UI Screenshots

The application features:
- **Dashboard**: Summary cards, pie chart, and bar chart
- **Upload Page**: Drag-and-drop file upload with format reference
- **Transactions Page**: Filterable table with all transactions

## 🔐 Security

- No authentication required (local-only application)
- CORS enabled only for localhost
- File size limits enforced
- Input validation on all endpoints

## 📄 License

This is a demo project for educational purposes.

## 👨‍💻 Development

### Adding New Categories

Edit `CategoryRuleEngine.java`:
```java
categoryKeywords.put("NewCategory", Arrays.asList(
    "keyword1", "keyword2", "keyword3"
));
```

### Modifying Excel Format

Update `ExcelParserService.java` to match your bank's format.

---

**Happy Expense Tracking! 💰📊**

