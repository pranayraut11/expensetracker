#!/bin/bash

# Smart Expense Tracker - Quick Start Script
# This script starts both backend and frontend servers

echo "================================================"
echo "  Smart Expense Tracker - Starting Application"
echo "================================================"
echo ""

# Check if we're in the right directory
if [ ! -f "start.sh" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Function to check if port is in use
check_port() {
    lsof -i:$1 > /dev/null 2>&1
    return $?
}

# Check if ports are available
if check_port 8080; then
    echo "⚠️  Warning: Port 8080 is already in use. Backend might fail to start."
    echo "   Kill the process using: lsof -ti:8080 | xargs kill -9"
fi

if check_port 5173; then
    echo "⚠️  Warning: Port 5173 is already in use. Frontend might fail to start."
    echo "   Kill the process using: lsof -ti:5173 | xargs kill -9"
fi

echo ""
echo "📦 Starting Backend (Spring Boot on port 8080)..."
echo "   Log file: backend.log"
cd backend
nohup mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "   Backend PID: $BACKEND_PID"
cd ..

echo ""
echo "⏳ Waiting for backend to start (20 seconds)..."
sleep 20

echo ""
echo "🎨 Starting Frontend (Vite on port 5173)..."
echo "   Log file: frontend.log"
cd frontend
nohup npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "   Frontend PID: $FRONTEND_PID"
cd ..

echo ""
echo "================================================"
echo "  ✅ Application Started Successfully!"
echo "================================================"
echo ""
echo "🌐 Frontend URL: http://localhost:5173"
echo "🔧 Backend API:  http://localhost:8080"
echo "💾 H2 Console:   http://localhost:8080/h2-console"
echo ""
echo "📊 View logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: tail -f frontend.log"
echo ""
echo "🛑 To stop the application:"
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "💡 Or save PIDs to a file:"
echo "echo \"$BACKEND_PID $FRONTEND_PID\" > .app_pids"
echo ""

