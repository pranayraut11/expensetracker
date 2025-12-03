import React from 'react'
import { useNavigate } from 'react-router-dom'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts'

const ExpenseBarChart = ({ categoryBreakdown }) => {
  const navigate = useNavigate()

  const data = Object.entries(categoryBreakdown || {})
    .filter(([_, value]) => value > 0)
    .map(([name, value]) => ({
      category: name,
      amount: Math.abs(value),
    }))
    .sort((a, b) => b.amount - a.amount)
    .slice(0, 10) // Top 10 categories

  /**
   * Handle click on bar - navigate to transactions filtered by category
   */
  const handleBarClick = (data) => {
    const category = data.category
    // Navigate to transactions page with category filter
    navigate(`/transactions?category=${encodeURIComponent(category)}`)
  }

  if (data.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Top Expenses by Category</h3>
        <div className="h-64 flex items-center justify-center text-gray-500">
          No expense data available
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div>
        <h3 className="text-lg font-semibold text-gray-700 mb-1">Top Expenses by Category</h3>
        <p className="text-xs text-gray-500 mb-4">💡 Click on any category to view transactions</p>
      </div>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis
            dataKey="category"
            angle={-45}
            textAnchor="end"
            height={100}
            interval={0}
          />
          <YAxis />
          <Tooltip formatter={(value) => `₹${value.toFixed(2)}`} cursor={{ fill: 'rgba(59, 130, 246, 0.1)' }} />
          <Legend />
          <Bar
            dataKey="amount"
            fill="#3B82F6"
            name="Amount (₹)"
            onClick={handleBarClick}
            cursor="pointer"
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}

export default ExpenseBarChart

