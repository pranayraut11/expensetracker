import React, { useState, useEffect } from 'react'
import { getSummary } from '../services/transactionApi'
import { getBalanceSummary } from '../services/balanceSummaryApi'
import { getIncomeExpenseTrend } from '../services/incomeExpenseTrendApi'
import { getCategoryExpenses } from '../services/categoryExpenseApi'
import BalanceSummaryCard from '../components/BalanceSummaryCard'
import CategoryPieChart from '../components/CategoryPieChart'
import ExpenseBarChart from '../components/ExpenseBarChart'
import IncomeExpenseTrendChart from '../components/IncomeExpenseTrendChart'
import TrendFilter from '../components/TrendFilter'
import CategoryExpenseChart from '../components/CategoryExpenseChart'
import CategoryMonthSelector from '../components/CategoryMonthSelector'
import AverageCategoryWidget from '../components/AverageCategoryWidget'

const DashboardPage = () => {
  const [summary, setSummary] = useState(null)
  const [balanceSummary, setBalanceSummary] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Income vs Expense Trend states
  const [trendData, setTrendData] = useState([])
  const [selectedMonth, setSelectedMonth] = useState('all')
  const [trendLoading, setTrendLoading] = useState(false)
  const [trendMode, setTrendMode] = useState('monthly') // 'monthly' or 'daily'

  // Category Expense states
  const [categoryExpenseData, setCategoryExpenseData] = useState([])
  const [categorySelectedMonth, setCategorySelectedMonth] = useState(new Date().getMonth() + 1) // Current month
  const [categoryLoading, setCategoryLoading] = useState(false)

  const currentYear = new Date().getFullYear()

  useEffect(() => {
    fetchSummary()
    fetchBalanceSummary()
    fetchTrendData()
    fetchCategoryExpenses()
  }, [])

  useEffect(() => {
    fetchTrendData()
  }, [selectedMonth])

  useEffect(() => {
    fetchCategoryExpenses()
  }, [categorySelectedMonth])

  const fetchSummary = async () => {
    try {
      setLoading(true)
      const data = await getSummary()
      setSummary(data)
    } catch (err) {
      console.error('Error fetching summary:', err)
      setError('Failed to load summary data')
    } finally {
      setLoading(false)
    }
  }

  const fetchBalanceSummary = async () => {
    try {
      const summary = await fetchSummary()
      setBalanceSummary(summary)
    } catch (err) {
      console.error('Error fetching balance summary:', err)
      setBalanceSummary(null)
    }
  }

  const fetchTrendData = async () => {
    try {
      setTrendLoading(true)
      let data

      if (selectedMonth === 'all') {
        // Fetch monthly trend for entire year
        data = await getIncomeExpenseTrend(currentYear)
        setTrendMode('monthly')
      } else {
        // Fetch daily trend for specific month
        data = await getIncomeExpenseTrend(currentYear, selectedMonth)
        setTrendMode('daily')
      }

      setTrendData(data)
    } catch (err) {
      console.error('Error fetching trend data:', err)
      setTrendData([])
    } finally {
      setTrendLoading(false)
    }
  }

  const handleMonthChange = (month) => {
    setSelectedMonth(month)
  }

  const fetchCategoryExpenses = async () => {
    try {
      setCategoryLoading(true)
      const data = await getCategoryExpenses(currentYear, categorySelectedMonth)
      setCategoryExpenseData(data)
    } catch (err) {
      console.error('Error fetching category expenses:', err)
      setCategoryExpenseData([])
    } finally {
      setCategoryLoading(false)
    }
  }

  const handleCategoryMonthChange = (month) => {
    setCategorySelectedMonth(month)
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-xl text-gray-600">Loading dashboard...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <p className="text-red-800">{error}</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-3xl font-bold text-gray-800">Dashboard</h2>
        <button
          onClick={fetchSummary}
          className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition"
        >
          Refresh
        </button>
      </div>

      {summary && (
        <>
          {/* Balance Summary Section */}
          <div className="bg-white rounded-lg shadow-md p-6 border-t-4 border-indigo-500">
            <h3 className="text-lg font-semibold text-gray-800 mb-4">Balance Summary</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
              {/* Opening Balance */}
              <div className="bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-xs font-medium text-blue-700">Opening Balance</p>
                  <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                  </svg>
                </div>
                <p className="text-xl font-bold text-blue-900">₹{(summary.openingBalance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
              </div>

              {/* Total Income */}
              <div className="bg-gradient-to-br from-green-50 to-green-100 border border-green-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-xs font-medium text-green-700">Total Income</p>
                  <svg className="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 11l5-5m0 0l5 5m-5-5v12" />
                  </svg>
                </div>
                <p className="text-xl font-bold text-green-900">₹{(summary.totalIncome || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
              </div>

              {/* Total Expense */}
              <div className="bg-gradient-to-br from-red-50 to-red-100 border border-red-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-xs font-medium text-red-700">Total Expense</p>
                  <svg className="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 13l-5 5m0 0l-5-5m5 5V6" />
                  </svg>
                </div>
                <p className="text-xl font-bold text-red-900">₹{(summary.totalExpenses || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
              </div>

              {/* Surplus/Deficit */}
              <div className={`bg-gradient-to-br ${(summary.surplus || 0) >= 0 ? 'from-emerald-50 to-emerald-100 border-emerald-200' : 'from-orange-50 to-orange-100 border-orange-200'} border rounded-lg p-4`}>
                <div className="flex items-center justify-between mb-2">
                  <p className={`text-xs font-medium ${(summary.surplus || 0) >= 0 ? 'text-emerald-700' : 'text-orange-700'}`}>
                    {(summary.surplus || 0) >= 0 ? 'Surplus' : 'Deficit'}
                  </p>
                  <svg className={`w-5 h-5 ${(summary.surplus || 0) >= 0 ? 'text-emerald-600' : 'text-orange-600'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                </div>
                <p className={`text-xl font-bold ${(summary.surplus || 0) >= 0 ? 'text-emerald-900' : 'text-orange-900'}`}>
                  ₹{Math.abs(summary.surplus || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                </p>
              </div>

              {/* Closing Balance */}
              <div className="bg-gradient-to-br from-indigo-50 to-indigo-100 border border-indigo-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-2">
                  <p className="text-xs font-medium text-indigo-700">Closing Balance</p>
                  <svg className="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                  </svg>
                </div>
                <p className="text-xl font-bold text-indigo-900">₹{(summary.closingBalance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</p>
              </div>
            </div>
          </div>

          {/* Balance Summary Card (Opening/Closing Balance) */}
          {balanceSummary && (
            <BalanceSummaryCard summary={balanceSummary} />
          )}

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <CategoryPieChart categoryBreakdown={summary.categoryBreakdown} />
            <ExpenseBarChart categoryBreakdown={summary.categoryBreakdown} />
          </div>

          {/* Income vs Expense Trend Section */}
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <h3 className="text-xl font-semibold text-gray-800">
                Income vs Expenses Trend ({currentYear})
              </h3>
              <TrendFilter
                selectedMonth={selectedMonth}
                onMonthChange={handleMonthChange}
              />
            </div>

            {trendLoading ? (
              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex justify-center items-center h-64">
                  <div className="text-gray-600">Loading trend data...</div>
                </div>
              </div>
            ) : (
              <IncomeExpenseTrendChart
                data={trendData}
                mode={trendMode}
              />
            )}
          </div>

          {/* Category-wise Expense Section */}
          <div className="space-y-4">
            <div className="flex justify-between items-center">
              <h3 className="text-xl font-semibold text-gray-800">
                Category-wise Expenses ({currentYear})
              </h3>
              <CategoryMonthSelector
                selectedMonth={categorySelectedMonth}
                onMonthChange={handleCategoryMonthChange}
              />
            </div>

            {categoryLoading ? (
              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex justify-center items-center h-64">
                  <div className="text-gray-600">Loading category data...</div>
                </div>
              </div>
            ) : (
              <CategoryExpenseChart
                data={categoryExpenseData}
                selectedMonth={categorySelectedMonth}
              />
            )}
          </div>

          {/* Average Monthly Category Widget */}
          <div className="space-y-4">
            <AverageCategoryWidget />
          </div>
        </>
      )}

      {summary && summary.transactionCount === 0 && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-6 text-center">
          <p className="text-yellow-800 text-lg">
            No transactions found. Please upload a bank statement to get started.
          </p>
        </div>
      )}
    </div>
  )
}

export default DashboardPage

