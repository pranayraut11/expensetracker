import React from 'react'

const TrendFilter = ({ selectedMonth, onMonthChange }) => {
  const monthOptions = [
    { label: 'All Months', value: 'all' },
    { label: 'January', value: 1 },
    { label: 'February', value: 2 },
    { label: 'March', value: 3 },
    { label: 'April', value: 4 },
    { label: 'May', value: 5 },
    { label: 'June', value: 6 },
    { label: 'July', value: 7 },
    { label: 'August', value: 8 },
    { label: 'September', value: 9 },
    { label: 'October', value: 10 },
    { label: 'November', value: 11 },
    { label: 'December', value: 12 }
  ]

  const handleChange = (e) => {
    const value = e.target.value
    onMonthChange(value === 'all' ? 'all' : parseInt(value))
  }

  return (
    <div className="flex items-center gap-3">
      <label htmlFor="month-selector" className="text-sm font-medium text-gray-700">
        Select Month:
      </label>
      <select
        id="month-selector"
        value={selectedMonth}
        onChange={handleChange}
        className="border border-gray-300 p-2 rounded-md bg-white shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 text-sm"
      >
        {monthOptions.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  )
}

export default TrendFilter

