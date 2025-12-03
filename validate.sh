#!/bin/bash

echo "=========================================="
echo "  Smart Expense Tracker - Validation"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Counters
TOTAL_CHECKS=0
PASSED_CHECKS=0

check() {
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $1${NC}"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    else
        echo -e "${RED}❌ $1${NC}"
    fi
}

echo "🔍 Checking Prerequisites..."
echo ""

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo -e "${GREEN}✅ Java $JAVA_VERSION installed${NC}"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    else
        echo -e "${RED}❌ Java 17+ required, found Java $JAVA_VERSION${NC}"
    fi
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "${RED}❌ Java not found${NC}"
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | awk '{print $3}')
    echo -e "${GREEN}✅ Maven $MVN_VERSION installed${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "${RED}❌ Maven not found${NC}"
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

# Check Node.js
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo -e "${GREEN}✅ Node.js $NODE_VERSION installed${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "${RED}❌ Node.js not found${NC}"
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

# Check npm
if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm -v)
    echo -e "${GREEN}✅ npm $NPM_VERSION installed${NC}"
    PASSED_CHECKS=$((PASSED_CHECKS + 1))
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
else
    echo -e "${RED}❌ npm not found${NC}"
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
fi

echo ""
echo "🔍 Checking Project Structure..."
echo ""

# Check backend files
[ -f "backend/pom.xml" ] && check "backend/pom.xml exists" || check "backend/pom.xml missing"
[ -f "backend/src/main/resources/application.properties" ] && check "application.properties exists" || check "application.properties missing"
[ -f "backend/src/main/java/com/example/expensetracker/ExpenseTrackerApplication.java" ] && check "Main application class exists" || check "Main application class missing"

# Check backend controllers
[ -f "backend/src/main/java/com/example/expensetracker/controller/UploadController.java" ] && check "UploadController exists" || check "UploadController missing"
[ -f "backend/src/main/java/com/example/expensetracker/controller/TransactionController.java" ] && check "TransactionController exists" || check "TransactionController missing"

# Check backend services
[ -f "backend/src/main/java/com/example/expensetracker/service/ExcelParserService.java" ] && check "ExcelParserService exists" || check "ExcelParserService missing"
[ -f "backend/src/main/java/com/example/expensetracker/service/CategorizationService.java" ] && check "CategorizationService exists" || check "CategorizationService missing"
[ -f "backend/src/main/java/com/example/expensetracker/service/TransactionService.java" ] && check "TransactionService exists" || check "TransactionService missing"

# Check backend utils
[ -f "backend/src/main/java/com/example/expensetracker/util/CategoryRuleEngine.java" ] && check "CategoryRuleEngine exists" || check "CategoryRuleEngine missing"
[ -f "backend/src/main/java/com/example/expensetracker/util/DescriptionCleaner.java" ] && check "DescriptionCleaner exists" || check "DescriptionCleaner missing"

# Check frontend files
[ -f "frontend/package.json" ] && check "frontend/package.json exists" || check "frontend/package.json missing"
[ -f "frontend/vite.config.js" ] && check "vite.config.js exists" || check "vite.config.js missing"
[ -f "frontend/tailwind.config.js" ] && check "tailwind.config.js exists" || check "tailwind.config.js missing"
[ -f "frontend/src/App.jsx" ] && check "App.jsx exists" || check "App.jsx missing"
[ -f "frontend/src/main.jsx" ] && check "main.jsx exists" || check "main.jsx missing"

# Check frontend pages
[ -f "frontend/src/pages/DashboardPage.jsx" ] && check "DashboardPage exists" || check "DashboardPage missing"
[ -f "frontend/src/pages/UploadPage.jsx" ] && check "UploadPage exists" || check "UploadPage missing"
[ -f "frontend/src/pages/TransactionsPage.jsx" ] && check "TransactionsPage exists" || check "TransactionsPage missing"

# Check frontend components
[ -f "frontend/src/components/SummaryCards.jsx" ] && check "SummaryCards exists" || check "SummaryCards missing"
[ -f "frontend/src/components/CategoryPieChart.jsx" ] && check "CategoryPieChart exists" || check "CategoryPieChart missing"
[ -f "frontend/src/components/ExpenseBarChart.jsx" ] && check "ExpenseBarChart exists" || check "ExpenseBarChart missing"
[ -f "frontend/src/components/TransactionTable.jsx" ] && check "TransactionTable exists" || check "TransactionTable missing"

# Check frontend services
[ -f "frontend/src/services/api.js" ] && check "api.js exists" || check "api.js missing"
[ -f "frontend/src/services/transactionApi.js" ] && check "transactionApi.js exists" || check "transactionApi.js missing"

# Check documentation
[ -f "README.md" ] && check "README.md exists" || check "README.md missing"
[ -f "QUICKSTART.md" ] && check "QUICKSTART.md exists" || check "QUICKSTART.md missing"
[ -f "SAMPLE_DATA.md" ] && check "SAMPLE_DATA.md exists" || check "SAMPLE_DATA.md missing"
[ -f "start.sh" ] && check "start.sh exists" || check "start.sh missing"

echo ""
echo "=========================================="
echo "  Validation Summary"
echo "=========================================="
echo ""
echo -e "Total Checks: $TOTAL_CHECKS"
echo -e "${GREEN}Passed: $PASSED_CHECKS${NC}"
echo -e "${RED}Failed: $((TOTAL_CHECKS - PASSED_CHECKS))${NC}"
echo ""

if [ $PASSED_CHECKS -eq $TOTAL_CHECKS ]; then
    echo -e "${GREEN}🎉 All checks passed! Project is ready to run.${NC}"
    echo ""
    echo "Next steps:"
    echo "  1. Run: ./start.sh"
    echo "  2. Open: http://localhost:5173"
    echo "  3. Upload an Excel file and start tracking!"
else
    echo -e "${YELLOW}⚠️  Some checks failed. Please review the output above.${NC}"
fi

echo ""

