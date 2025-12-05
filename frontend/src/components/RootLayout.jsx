import React from 'react';
import { Link, Outlet } from 'react-router-dom';

export default function RootLayout() {
  return (
    <div className="font-sans min-h-screen bg-gray-100">
      <nav className="bg-white shadow-lg">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <h1 className="text-2xl font-bold text-indigo-600">💰 Expense Tracker</h1>
            </div>
            <div className="flex space-x-4">
              <Link to="/" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Dashboard</Link>
              <Link to="/upload" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Upload Bank</Link>
              <Link to="/upload-credit-card" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Upload CC</Link>
              <Link to="/transactions" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Transactions</Link>
              <Link to="/rules" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Rules</Link>
              <Link to="/settings" className="px-4 py-2 rounded-md text-sm font-medium text-gray-700 hover:bg-indigo-50 hover:text-indigo-600 transition">Settings</Link>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <Outlet />
      </main>
    </div>
  );
}

