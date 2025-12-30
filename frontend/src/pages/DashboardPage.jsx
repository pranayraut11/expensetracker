import React, { useState, useEffect } from 'react'
import { getSummary } from '../services/transactionApi'
import { getBalanceSummary } from '../services/balanceSummaryApi'
import { getIncomeExpenseTrend } from '../services/incomeExpenseTrendApi'
import { getCategoryExpenses } from '../services/categoryExpenseApi'
import { getSalaryCycleTotals } from '../services/salaryCycleApi'
import { getTotals } from '../services/totalsApi'
import ExpenseBarChart from '../components/ExpenseBarChart'
import IncomeExpenseTrendChart from '../components/IncomeExpenseTrendChart'
import TrendFilter from '../components/TrendFilter'
import CategoryExpenseChart from '../components/CategoryExpenseChart'
import CategoryMonthSelector from '../components/CategoryMonthSelector'
import AverageCategoryWidget from '../components/AverageCategoryWidget'
import SalaryCycleSelector from '../components/SalaryCycleSelector'

const DashboardPage = () => {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [summary, setSummary] = useState(null)

  // Balance Summary state
  const [balanceSummary, setBalanceSummary] = useState(null)

  // Income vs Expense Trend states
  const [trendData, setTrendData] = useState([])
  const [selectedMonth, setSelectedMonth] = useState('all')
  const [trendLoading, setTrendLoading] = useState(false)
  const [trendMode, setTrendMode] = useState('monthly') // 'monthly' or 'daily'

  // Category Expense states
  const [categoryExpenseData, setCategoryExpenseData] = useState([])
  const [categorySelectedMonth, setCategorySelectedMonth] = useState('all')
  const [categoryLoading, setCategoryLoading] = useState(false)

  // Salary Cycle states
  const [monthMode, setMonthMode] = useState('calendar') // 'calendar' or 'salary'
  const [selectedSalaryCycle, setSelectedSalaryCycle] = useState(null)
  const [salaryCycleTotals, setSalaryCycleTotals] = useState(null)

  const currentYear = new Date().getFullYear()

  // Initial data fetch
  useEffect(() => {
    fetchSummary()
    fetchBalanceSummary()
    fetchTrendData()
    fetchCategoryExpenses()
  }, [])

  // Fetch trend data when month changes
  useEffect(() => {
    fetchTrendData()
  }, [selectedMonth])

  // Fetch category expenses when month changes
  useEffect(() => {
    fetchCategoryExpenses()
  }, [categorySelectedMonth])

  // Fetch salary cycle totals when salary cycle is selected
  useEffect(() => {
    if (monthMode === 'salary' && selectedSalaryCycle) {
      fetchSalaryCycleTotals()
    }
  }, [selectedSalaryCycle, monthMode])

  const fetchSummary = async () => {
    try {
      setLoading(true)

      // If in salary mode and cycle selected, use cycle dates
      if (monthMode === 'salary' && selectedSalaryCycle) {
        // Format dates to YYYY-MM-DD format - handle both ISO strings and date objects
        const formatDate = (dateString) => {
          if (!dateString) return ''
          // If already in YYYY-MM-DD format, return as is
          if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
            return dateString
          }
          // Parse the date string properly
          const date = new Date(dateString)
          // Use UTC to avoid timezone issues
          const year = date.getUTCFullYear()
          const month = String(date.getUTCMonth() + 1).padStart(2, '0')
          const day = String(date.getUTCDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }

        const fromDate = formatDate(selectedSalaryCycle.startDate)
        const toDate = formatDate(selectedSalaryCycle.endDate)

        console.log('Salary cycle dates:', { startDate: selectedSalaryCycle.startDate, endDate: selectedSalaryCycle.endDate, formatted: { fromDate, toDate } })

        // Fetch totals for salary cycle
        const totals = await getTotals(fromDate, toDate, null, null)

        // Create summary object similar to getSummary response
        const data = {
          totalIncome: totals.totalCredit,
          totalExpenses: totals.totalDebit,
          surplus: totals.totalCredit - totals.totalDebit,
          openingBalance: 0, // Not applicable for salary cycle
          closingBalance: 0,  // Not applicable for salary cycle
          transactionCount: 0,
          categoryBreakdown: []
        }
        setSummary(data)
      } else {
        // Normal calendar mode
        const data = await getSummary()
        setSummary(data)
      }
    } catch (err) {
      console.error('Error fetching summary:', err)
      setError('Failed to load summary data')
    } finally {
      setLoading(false)
    }
  }

  const fetchBalanceSummary = async () => {
    try {
      const data = await getBalanceSummary()
      setBalanceSummary(data)
    } catch (err) {
      console.error('Error fetching balance summary:', err)
    }
  }

  const fetchTrendData = async () => {
    try {
      setTrendLoading(true)
      let data

      if (selectedMonth === 'all') {
        data = await getIncomeExpenseTrend(currentYear)
      } else {
        data = await getIncomeExpenseTrend(currentYear, parseInt(selectedMonth))
      }

      setTrendData(data)
    } catch (err) {
      console.error('Error fetching trend data:', err)
      setTrendData([])
    } finally {
      setTrendLoading(false)
    }
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

  const fetchSalaryCycleTotals = async () => {
    if (!selectedSalaryCycle) return

    try {
      const totals = await getSalaryCycleTotals(selectedSalaryCycle.cycleId)
      setSalaryCycleTotals(totals)
    } catch (err) {
      console.error('Error fetching salary cycle totals:', err)
    }
  }

  const handleMonthChange = (month) => {
    setSelectedMonth(month)
  }

  const handleCategoryMonthChange = (month) => {
    setCategorySelectedMonth(month)
  }

  const handleMonthModeChange = (mode) => {
    setMonthMode(mode)

    if (mode === 'calendar') {
      // Reset to normal calendar mode
      setSelectedSalaryCycle(null)
      setSalaryCycleTotals(null)
      fetchSummary() // Refresh with calendar data
    }
  }

  const handleSalaryCycleChange = (cycle) => {
    setSelectedSalaryCycle(cycle)

    if (cycle) {
      // Fetch data for the selected salary cycle
      fetchSummary()
    }
  }

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="text-2xl text-gray-600">Loading dashboard...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4 m-6">
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

      {/* Salary Cycle Selector */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-800 mb-4">Date Range Selection</h3>
        <SalaryCycleSelector
          onCycleChange={handleSalaryCycleChange}
          onModeChange={handleMonthModeChange}
          selectedMode={monthMode}
          selectedCycleId={selectedSalaryCycle?.cycleId}
        />

        {/* Show salary cycle info when selected */}
        {monthMode === 'salary' && selectedSalaryCycle && (
          <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <div className="flex items-center gap-2">
              <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p className="text-sm text-blue-800">
                Showing data from <strong>{new Date(selectedSalaryCycle.startDate).toLocaleDateString()}</strong> to <strong>{new Date(selectedSalaryCycle.endDate).toLocaleDateString()}</strong>
              </p>
            </div>
            {salaryCycleTotals && (
              <div className="mt-2 text-sm text-blue-700">
                <p>Salary Amount: <strong>₹{salaryCycleTotals.salaryAmount?.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</strong></p>
                <p>Net Savings: <strong>₹{salaryCycleTotals.netSavings?.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</strong></p>
              </div>
            )}
          </div>
        )}
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

          {/* Average Category Widget */}
          <AverageCategoryWidget />

          {/* Income vs Expense Trend */}
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
              <CategoryExpenseChart data={categoryExpenseData} />
            )}
          </div>

          {/* Top 5 Expenses Bar Chart */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-4">Top Expenses</h3>
            <ExpenseBarChart data={summary.categoryBreakdown || []} />
          </div>
        </>
      )}
    </div>
  )
}

export default DashboardPage

