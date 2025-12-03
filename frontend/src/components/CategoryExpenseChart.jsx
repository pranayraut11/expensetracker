import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts'

const CategoryExpenseChart = ({ data, selectedMonth }) => {
  const navigate = useNavigate()
  const [chartType, setChartType] = useState('bar') // 'bar' or 'pie'

  // Ensure data is an array
  const safeData = Array.isArray(data) ? data : [];

  // Color palette for categories
  const COLORS = [
    '#6366f1', // indigo
    '#ec4899', // pink
    '#f59e0b', // amber
    '#10b981', // emerald
    '#3b82f6', // blue
    '#8b5cf6', // violet
    '#f97316', // orange
    '#06b6d4', // cyan
    '#ef4444', // red
    '#84cc16', // lime
    '#14b8a6', // teal
    '#a855f7'  // purple
  ]

  /**
   * Get month name from number
   */
  const getMonthName = (monthNumber) => {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ]
    return monthNames[monthNumber - 1] || ''
  }

  /**
   * Handle click on category (bar or pie slice)
   */
  const handleCategoryClick = (category) => {
    const currentYear = new Date().getFullYear()

    // Calculate month date range
    const year = currentYear
    const month = selectedMonth
    const startDate = `${year}-${String(month).padStart(2, '0')}-01`

    // Calculate last day of month
    const lastDay = new Date(year, month, 0).getDate()
    const endDate = `${year}-${String(month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`

    // Navigate to transactions page with filters
    navigate(`/transactions?category=${encodeURIComponent(category)}&fromDate=${startDate}&toDate=${endDate}`)
  }

  /**
   * Format currency for tooltip
   */
  const formatCurrency = (value) => {
    return `₹${value.toLocaleString('en-IN', { maximumFractionDigits: 0 })}`
  }

  /**
   * Custom tooltip for bar chart
   */
  const CustomBarTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white p-4 border border-gray-300 rounded-lg shadow-lg">
          <p className="font-semibold text-gray-800">{payload[0].payload.category}</p>
          <p className="text-red-600">
            Total: {formatCurrency(Math.abs(payload[0].value))}
          </p>
        </div>
      )
    }
    return null
  }

  /**
   * Custom tooltip for pie chart
   */
  const CustomPieTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white p-4 border border-gray-300 rounded-lg shadow-lg">
          <p className="font-semibold text-gray-800">{payload[0].name}</p>
          <p className="text-red-600">
            Total: {formatCurrency(Math.abs(payload[0].value))}
          </p>
        </div>
      )
    }
    return null
  }

  /**
   * Custom label for pie chart
   */
  const renderCustomLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
    const RADIAN = Math.PI / 180
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5
    const x = cx + radius * Math.cos(-midAngle * RADIAN)
    const y = cy + radius * Math.sin(-midAngle * RADIAN)

    if (percent < 0.05) return null // Don't show label for small slices

    return (
      <text
        x={x}
        y={y}
        fill="white"
        textAnchor={x > cx ? 'start' : 'end'}
        dominantBaseline="central"
        className="text-xs font-semibold"
      >
        {`${(percent * 100).toFixed(0)}%`}
      </text>
    )
  }

  if (!safeData || safeData.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">
          Category-wise Expenses - {getMonthName(selectedMonth)}
        </h3>
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <p className="text-yellow-800">No expense data available for this month.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <div className="flex justify-between items-center mb-4">
        <div>
          <h3 className="text-xl font-semibold text-gray-800">
            Category-wise Expenses - {getMonthName(selectedMonth)}
          </h3>
          <p className="text-xs text-gray-500 mt-1">💡 Click on any category to view transactions</p>
        </div>

        {/* Chart Type Toggle */}
        <div className="flex gap-2 bg-gray-100 p-1 rounded-lg">
          <button
            onClick={() => setChartType('bar')}
            className={`px-4 py-2 rounded-md text-sm font-medium transition ${
              chartType === 'bar'
                ? 'bg-white text-indigo-600 shadow-sm'
                : 'text-gray-600 hover:text-gray-800'
            }`}
          >
            📊 Bar View
          </button>
          <button
            onClick={() => setChartType('pie')}
            className={`px-4 py-2 rounded-md text-sm font-medium transition ${
              chartType === 'pie'
                ? 'bg-white text-indigo-600 shadow-sm'
                : 'text-gray-600 hover:text-gray-800'
            }`}
          >
            🥧 Pie View
          </button>
        </div>
      </div>

      {/* Bar Chart View */}
      {chartType === 'bar' && (
        <ResponsiveContainer width="100%" height={350}>
          <BarChart
            data={safeData.map(item => ({ ...item, total: Math.abs(item.total) }))}
            margin={{ top: 5, right: 30, left: 20, bottom: 50 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis
              dataKey="category"
              tick={{ fontSize: 12 }}
              stroke="#6b7280"
              angle={-45}
              textAnchor="end"
              height={80}
            />
            <YAxis
              tick={{ fontSize: 12 }}
              stroke="#6b7280"
              tickFormatter={(value) => `₹${(Math.abs(value) / 1000).toFixed(0)}k`}
              domain={[0, 'auto']}
            />
            <Tooltip content={<CustomBarTooltip />} cursor={{ fill: 'rgba(239, 68, 68, 0.1)' }} />
            <Bar
              dataKey="total"
              fill="#ef4444"
              barSize={35}
              radius={[6, 6, 0, 0]}
              onClick={(data) => handleCategoryClick(data.category)}
              cursor="pointer"
            />
          </BarChart>
        </ResponsiveContainer>
      )}

      {/* Pie Chart View */}
      {chartType === 'pie' && (
        <div className="flex flex-col lg:flex-row items-center justify-center gap-8">
          <ResponsiveContainer width="100%" height={350}>
            <PieChart>
              <Pie
                data={safeData.map(item => ({ ...item, total: Math.abs(item.total) }))}
                dataKey="total"
                nameKey="category"
                cx="50%"
                cy="50%"
                outerRadius={120}
                label={renderCustomLabel}
                labelLine={false}
                onClick={(data) => handleCategoryClick(data.category)}
                cursor="pointer"
              >
                {safeData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip content={<CustomPieTooltip />} />
            </PieChart>
          </ResponsiveContainer>

          {/* Legend */}
          <div className="flex flex-col gap-2 max-w-xs">
            {safeData.map((entry, index) => (
              <div
                key={index}
                className="flex items-center gap-2 cursor-pointer hover:bg-gray-50 p-2 rounded transition"
                onClick={() => handleCategoryClick(entry.category)}
              >
                <div
                  className="w-4 h-4 rounded"
                  style={{ backgroundColor: COLORS[index % COLORS.length] }}
                ></div>
                <span className="text-sm text-gray-700 flex-1">{entry.category}</span>
                <span className="text-sm font-semibold text-gray-800">
                  {formatCurrency(Math.abs(entry.total))}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Summary */}
      <div className="mt-6 pt-4 border-t border-gray-200">
        <div className="flex justify-between items-center">
          <span className="text-sm font-medium text-gray-600">Total Expenses:</span>
          <span className="text-lg font-bold text-red-600">
            {formatCurrency(data.reduce((sum, item) => sum + Math.abs(item.total), 0))}
          </span>
        </div>
      </div>
    </div>
  )
}

export default CategoryExpenseChart

