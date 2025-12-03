import React from 'react'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts'

const IncomeExpenseTrendChart = ({ data, mode }) => {
  // Ensure data is an array
  const safeData = Array.isArray(data) ? data : [];

  /**
   * Format X-axis labels based on mode
   */
  const formatXAxis = (value) => {
    if (mode === 'monthly') {
      // Convert "2024-01" to "Jan"
      const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      const monthPart = value.split('-')[1]
      const monthIndex = parseInt(monthPart, 10) - 1
      return monthNames[monthIndex] || value
    } else {
      // Daily mode: Convert "2024-02-01" to "1 Feb"
      const date = new Date(value)
      const day = date.getDate()
      const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      const month = monthNames[date.getMonth()]
      return `${day} ${month}`
    }
  }

  /**
   * Format currency for tooltip
   */
  const formatCurrency = (value) => {
    return `₹${value.toLocaleString('en-IN', { maximumFractionDigits: 0 })}`
  }

  /**
   * Custom tooltip component
   */
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-white p-4 border border-gray-300 rounded-lg shadow-lg">
          <p className="font-semibold text-gray-800 mb-2">
            {mode === 'monthly' ? formatXAxis(label) : label}
          </p>
          {payload.map((entry, index) => (
            <p key={index} style={{ color: entry.color }} className="text-sm">
              {entry.name}: {formatCurrency(entry.value)}
            </p>
          ))}
        </div>
      )
    }
    return null
  }

  /**
   * Transform data for chart
   */
  const chartData = safeData.map(item => ({
    label: mode === 'monthly' ? item.month : item.date,
    income: item.income || 0,
    expenses: item.expenses || 0
  }))

  if (safeData.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">Income vs Expenses Trend</h3>
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
          <p className="text-yellow-800">No data available for the selected period.</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-xl font-semibold text-gray-800 mb-4">
        Income vs Expenses Trend {mode === 'daily' ? '(Daily)' : '(Monthly)'}
      </h3>

      <ResponsiveContainer width="100%" height={400}>
        <LineChart
          data={chartData}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
          <XAxis
            dataKey="label"
            tick={{ fontSize: 12 }}
            stroke="#6b7280"
            tickFormatter={formatXAxis}
          />
          <YAxis
            tick={{ fontSize: 12 }}
            stroke="#6b7280"
            tickFormatter={(value) => `₹${(value / 1000).toFixed(0)}k`}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend
            wrapperStyle={{ fontSize: '14px', paddingTop: '20px' }}
            iconType="line"
          />
          <Line
            type="monotone"
            dataKey="income"
            name="Income"
            stroke="#16a34a"
            strokeWidth={2}
            dot={{ r: 4 }}
            activeDot={{ r: 6 }}
          />
          <Line
            type="monotone"
            dataKey="expenses"
            name="Expenses"
            stroke="#dc2626"
            strokeWidth={2}
            dot={{ r: 4 }}
            activeDot={{ r: 6 }}
          />
        </LineChart>
      </ResponsiveContainer>

      <div className="mt-4 flex justify-center space-x-6 text-sm text-gray-600">
        <div className="flex items-center">
          <div className="w-4 h-1 bg-green-600 mr-2"></div>
          <span>Income</span>
        </div>
        <div className="flex items-center">
          <div className="w-4 h-1 bg-red-600 mr-2"></div>
          <span>Expenses</span>
        </div>
      </div>
    </div>
  )
}

export default IncomeExpenseTrendChart

