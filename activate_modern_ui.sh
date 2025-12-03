#!/bin/bash

# Modern UI Activation Script
# This script activates the modern UI in your Expense Tracker app

echo "🎨 Activating Modern UI for Expense Tracker..."
echo ""

# Check if we're in the right directory
if [ ! -f "frontend/src/App.jsx" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Backup the original App.jsx
echo "📦 Creating backup of original App.jsx..."
cp frontend/src/App.jsx frontend/src/App.jsx.backup

# Update App.jsx to use modern router
echo "✏️  Updating App.jsx to use modern router..."
cat > frontend/src/App.jsx << 'EOF'
import React from 'react'
import { RouterProvider } from 'react-router-dom'
import router from './modernRouter'

const App = () => {
  return <RouterProvider router={router} />
}

export default App
EOF

echo ""
echo "✅ Modern UI activated successfully!"
echo ""
echo "📝 Next steps:"
echo "   1. Start the development server:"
echo "      cd frontend"
echo "      npm run dev"
echo ""
echo "   2. Open your browser to:"
echo "      http://localhost:5173"
echo ""
echo "   3. Enjoy your new modern UI! 🎉"
echo ""
echo "💡 To revert back to the old UI:"
echo "   mv frontend/src/App.jsx.backup frontend/src/App.jsx"
echo ""

