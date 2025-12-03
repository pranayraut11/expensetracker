import React from 'react'
import { useNavigate } from 'react-router-dom'
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts'

const CategoryPieChart = ({ categoryBreakdown }) => {
  const navigate = useNavigate()

  const COLORS = [
    '#3B82F6', '#EF4444', '#10B981', '#F59E0B', '#8B5CF6',
    '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
    '#14B8A6', '#EAB308', '#A855F7', '#22D3EE'
  ]

  const data = Object.entries(categoryBreakdown || {})
    .filter(([_, value]) => value > 0)
    .map(([name, value]) => ({
      name,
      value: Math.abs(value),
    }))
    .sort((a, b) => b.value - a.value)

  /**
   * Handle click on pie slice - navigate to transactions filtered by category
   */
  const handleSliceClick = (data) => {
    const category = data.name
    // Navigate to transactions page with category filter
    navigate(`/transactions?category=${encodeURIComponent(category)}`)
  }

  if (data.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Category Breakdown</h3>
        <div className="h-64 flex items-center justify-center text-gray-500">
          No expense data available
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      <div>
        <h3 className="text-lg font-semibold text-gray-700 mb-1">Category Breakdown</h3>
        <p className="text-xs text-gray-500 mb-4">💡 Click on any category to view transactions</p>
      </div>
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
            onClick={handleSliceClick}
            cursor="pointer"
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip formatter={(value) => `₹${value.toFixed(2)}`} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}

export default CategoryPieChart

