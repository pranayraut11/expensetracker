#!/bin/bash

echo "=========================================="
echo "  Smart Expense Tracker - Quick Start"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null
then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "❌ Maven is not installed. Please install Maven 3.6+."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null
then
    echo "❌ Node.js is not installed. Please install Node.js 18+."
    exit 1
fi

echo "✅ Prerequisites check passed!"
echo ""

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "Shutting down servers..."
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    exit 0
}

trap cleanup SIGINT SIGTERM

# Start Backend
echo "🚀 Starting Backend (Spring Boot)..."
cd backend
mvn clean install -DskipTests > /dev/null 2>&1
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

echo "⏳ Waiting for backend to start..."
sleep 10

# Check if backend started successfully
if ! curl -s http://localhost:8080/api/transactions/summary > /dev/null 2>&1; then
    echo "⚠️  Backend might still be starting... (check backend.log for details)"
else
    echo "✅ Backend started successfully at http://localhost:8080"
fi

# Start Frontend
echo ""
echo "🚀 Starting Frontend (React + Vite)..."
cd frontend

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install > /dev/null 2>&1
fi

npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo "⏳ Waiting for frontend to start..."
sleep 5
echo "✅ Frontend started successfully at http://localhost:5173"

echo ""
echo "=========================================="
echo "  Application is running!"
echo "=========================================="
echo ""
echo "🌐 Frontend: http://localhost:5173"
echo "🔧 Backend:  http://localhost:8080"
echo "💾 H2 Console: http://localhost:8080/h2-console"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: tail -f frontend.log"
echo ""
echo "Press Ctrl+C to stop all servers"
echo ""

# Wait for user to interrupt
wait

