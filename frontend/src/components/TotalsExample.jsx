import React, { useState, useEffect } from 'react'
import { getTotals } from '../services/totalsApi'

/**
 * Example React component showing how to use the Totals API
 *
 * This component demonstrates:
 * - Fetching totals with filters
 * - Displaying total credit and debit
 * - Handling loading and error states
 */
const TotalsExample = () => {
  const [totals, setTotals] = useState({ totalCredit: 0, totalDebit: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Filters (optional)
  const [fromDate, setFromDate] = useState('')
  const [toDate, setToDate] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')

  useEffect(() => {
    fetchTotals()
  }, [fromDate, toDate, selectedCategory])

  const fetchTotals = async () => {
    try {
      setLoading(true)
      setError(null)

      // Call API with filters (null values are handled by the API)
      const data = await getTotals(
        fromDate || null,
        toDate || null,
        selectedCategory || null
      )

      setTotals(data)
    } catch (err) {
      console.error('Failed to fetch totals:', err)
      setError('Failed to load totals')
    } finally {
      setLoading(false)
    }
  }

  const formatCurrency = (value) => {
    return `₹${value.toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`
  }

  if (loading) {
    return (
      <div className="p-4 bg-white shadow rounded-xl">
        <p className="text-gray-600">Loading totals...</p>
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-4 bg-red-50 border border-red-200 rounded-xl">
        <p className="text-red-800">{error}</p>
      </div>
    )
  }

  return (
    <div className="space-y-4">
      {/* Filters (Optional) */}
      <div className="p-4 bg-white shadow rounded-xl">
        <h3 className="text-lg font-semibold mb-4">Filters</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              From Date
            </label>
            <input
              type="date"
              value={fromDate}
              onChange={(e) => setFromDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              To Date
            </label>
            <input
              type="date"
              value={toDate}
              onChange={(e) => setToDate(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Category
            </label>
            <input
              type="text"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              placeholder="e.g., Food"
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
            />
          </div>
        </div>
      </div>

      {/* Totals Display */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Total Credit */}
        <div className="p-6 bg-gradient-to-br from-green-50 to-green-100 border border-green-200 shadow rounded-xl">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-green-700 mb-1">
                Total Credit
              </p>
              <p className="text-3xl font-bold text-green-900">
                {formatCurrency(totals.totalCredit)}
              </p>
            </div>
            <div className="bg-green-200 rounded-full p-3">
              <svg
                className="w-8 h-8 text-green-700"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M7 11l5-5m0 0l5 5m-5-5v12"
                />
              </svg>
            </div>
          </div>
        </div>

        {/* Total Debit */}
        <div className="p-6 bg-gradient-to-br from-red-50 to-red-100 border border-red-200 shadow rounded-xl">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-red-700 mb-1">
                Total Debit
              </p>
              <p className="text-3xl font-bold text-red-900">
                {formatCurrency(totals.totalDebit)}
              </p>
            </div>
            <div className="bg-red-200 rounded-full p-3">
              <svg
                className="w-8 h-8 text-red-700"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M17 13l-5 5m0 0l-5-5m5 5V6"
                />
              </svg>
            </div>
          </div>
        </div>
      </div>

      {/* Info */}
      <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg">
        <p className="text-xs text-blue-800">
          💡 Note: Credit card payments are automatically excluded from these totals.
          Credit card transactions from your CC statement are included.
        </p>
      </div>
    </div>
  )
}

export default TotalsExample

