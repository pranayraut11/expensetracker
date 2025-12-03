import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getTransactions } from '../services/transactionApi'
import { getTotals } from '../services/totalsApi'
import TransactionTable from '../components/TransactionTable'
import MonthYearSelector from '../components/MonthYearSelector'

// Category constants
const CATEGORIES = [
  'Income',
  'Food & Dining',
  'Groceries',
  'Shopping',
  'Travel',
  'Bills & Utilities',
  'Medical & Health',
  'Personal Care',
  'Subscriptions',
  'Loans & EMIs',
  'Transfers',
  'Fees & Charges',
  'Donations',
  'Business',
  'Fuel',
  'Medical',
  'Housing / Rent',
  'Entertainment',
  'Insurance',
  'Investment',
  'Education',
  'Pets',
  'Vehicle/Transportation',
  'Credit Card Payment',
  'Miscellaneous',
]

const TransactionsPage = () => {
  const [searchParams] = useSearchParams()

  // State
  const [transactions, setTransactions] = useState([])
  const [totals, setTotals] = useState({ totalCredit: 0, totalDebit: 0 })
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Pagination state
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(20)
  const [totalElements, setTotalElements] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // Sorting state
  const [sortField, setSortField] = useState('date')
  const [sortDirection, setSortDirection] = useState('desc')

  // Month and Year selection state
  const [selectedMonth, setSelectedMonth] = useState(null)
  const [selectedYear, setSelectedYear] = useState(null)

  // Initialize filters from URL parameters
  const [filters, setFilters] = useState({
    category: searchParams.get('category') || '',
    fromDate: searchParams.get('fromDate') || '',
    toDate: searchParams.get('toDate') || '',
    search: searchParams.get('search') || '',
  })

  // Fetch transactions on component mount and when dependencies change
  useEffect(() => {
    fetchTransactions()
  }, [page, pageSize, sortField, sortDirection])

  const fetchTransactions = async () => {
    try {
      setLoading(true)
      const sort = `${sortField},${sortDirection}`

      const response = await getTransactions({
        page,
        size: pageSize,
        sort,
        ...filters
      })

      // Update state with paginated data
      setTransactions(response.content || [])
      setTotalElements(response.totalElements || 0)
      setTotalPages(response.totalPages || 0)

      // Fetch totals with same filters
      await fetchTotals()
    } catch (err) {
      console.error('Error fetching transactions:', err)
      setError('Failed to load transactions')
    } finally {
      setLoading(false)
    }
  }

  const fetchTotals = async () => {
    try {
      const data = await getTotals(
        filters.fromDate || null,
        filters.toDate || null,
        filters.category || null,
        filters.search || null
      )
      setTotals(data)
    } catch (err) {
      console.error('Error fetching totals:', err)
      setTotals({ totalCredit: 0, totalDebit: 0 })
    }
  }

  const handleFilterChange = (e) => {
    const { name, value } = e.target
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleApplyFilters = () => {
    setPage(0) // Reset to first page when filters change
    fetchTransactions()
  }

  const handleClearFilters = () => {
    setFilters({
      category: '',
      fromDate: '',
      toDate: '',
      search: '',
    })
    setSelectedMonth(null)
    setSelectedYear(null)
    setPage(0)
    setTimeout(() => {
      fetchTransactions()
    }, 0)
  }

  const handleMonthChange = (month) => {
    setSelectedMonth(month)
    applyMonthYearFilter(month, selectedYear)
  }

  const handleYearChange = (year) => {
    setSelectedYear(year)
    applyMonthYearFilter(selectedMonth, year)
  }

  const applyMonthYearFilter = (month, year) => {
    if (year && month) {
      // Calculate start and end dates for the selected month/year
      const startDate = `${year}-${String(month).padStart(2, '0')}-01`
      const lastDay = new Date(year, month, 0).getDate()
      const endDate = `${year}-${String(month).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`

      setFilters(prev => ({
        ...prev,
        fromDate: startDate,
        toDate: endDate
      }))

      setPage(0)
      setTimeout(() => {
        fetchTransactions()
      }, 0)
    } else if (year && !month) {
      // Only year selected - filter for entire year
      const startDate = `${year}-01-01`
      const endDate = `${year}-12-31`

      setFilters(prev => ({
        ...prev,
        fromDate: startDate,
        toDate: endDate
      }))

      setPage(0)
      setTimeout(() => {
        fetchTransactions()
      }, 0)
    } else {
      // No month/year selected - clear date filters
      setFilters(prev => ({
        ...prev,
        fromDate: '',
        toDate: ''
      }))

      setPage(0)
      setTimeout(() => {
        fetchTransactions()
      }, 0)
    }
  }

  // Pagination handlers
  const handlePageChange = (newPage) => {
    setPage(newPage)
  }

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize)
    setPage(0) // Reset to first page when changing page size
  }

  // Sorting handler
  const handleSort = (field) => {
    if (sortField === field) {
      // Toggle direction if same field
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      // New field, default to desc
      setSortField(field)
      setSortDirection('desc')
    }
    setPage(0)
  }

  const formatCurrency = (value) => {
    return `₹${value.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
  }

  return (
    <div className="space-y-6">
      <h2 className="text-3xl font-bold text-gray-800">Transactions</h2>

      {/* Filters */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h3 className="text-lg font-semibold text-gray-700 mb-4">Filters</h3>

        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Search */}
            <div className="lg:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Search Description
              </label>
              <input
                type="text"
                name="search"
                value={filters.search}
                onChange={handleFilterChange}
                placeholder="e.g. uber, starbucks..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Category
              </label>
              <select
                name="category"
                value={filters.category}
                onChange={handleFilterChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">All Categories</option>
                {CATEGORIES.map((category) => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-end">
              <div className="w-full space-y-2 md:space-y-0 md:flex md:space-x-2">
                <button
                  onClick={handleApplyFilters}
                  className="w-full md:flex-1 bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 transition"
                >
                  Apply
                </button>
                <button
                  onClick={handleClearFilters}
                  className="w-full md:flex-1 bg-gray-200 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-300 transition"
                >
                  Clear
                </button>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            {/* Month and Year Selector - takes 2 columns */}
            <div className="lg:col-span-2">
              <MonthYearSelector
                selectedMonth={selectedMonth}
                selectedYear={selectedYear}
                onMonthChange={handleMonthChange}
                onYearChange={handleYearChange}
              />
            </div>

            {/* From Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                From Date
              </label>
              <input
                type="date"
                name="fromDate"
                value={filters.fromDate}
                onChange={handleFilterChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            {/* To Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                To Date
              </label>
              <input
                type="date"
                name="toDate"
                value={filters.toDate}
                onChange={handleFilterChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Transactions Table */}
      {loading ? (
        <div className="flex justify-center items-center h-64">
          <div className="text-xl text-gray-600">Loading transactions...</div>
        </div>
      ) : error ? (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <p className="text-red-800">{error}</p>
        </div>
      ) : (
        <>
          {/* Total Debit and Credit Summary */}
          {transactions.length > 0 && (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Total Credit */}
              <div className="bg-gradient-to-br from-green-50 to-green-100 border border-green-200 rounded-lg shadow-md p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-green-700 mb-1">Total Credit</p>
                    <p className="text-2xl font-bold text-green-900">{formatCurrency(totals.totalCredit)}</p>
                  </div>
                  <div className="bg-green-200 rounded-full p-3">
                    <svg className="w-8 h-8 text-green-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 11l5-5m0 0l5 5m-5-5v12" />
                    </svg>
                  </div>
                </div>
              </div>

              {/* Total Debit */}
              <div className="bg-gradient-to-br from-red-50 to-red-100 border border-red-200 rounded-lg shadow-md p-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-red-700 mb-1">Total Debit</p>
                    <p className="text-2xl font-bold text-red-900">{formatCurrency(totals.totalDebit)}</p>
                  </div>
                  <div className="bg-red-200 rounded-full p-3">
                    <svg className="w-8 h-8 text-red-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 13l-5 5m0 0l-5-5m5 5V6" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Transaction Count and Pagination Info */}
          {transactions.length > 0 && (
            <div className="bg-white rounded-lg shadow-md p-4 border-l-4 border-indigo-500 flex justify-between items-center">
              <p className="text-sm text-gray-600">
                Showing <span className="font-bold text-indigo-600">{page * pageSize + 1}</span> to{' '}
                <span className="font-bold text-indigo-600">
                  {Math.min((page + 1) * pageSize, totalElements)}
                </span>{' '}
                of <span className="font-bold text-indigo-600">{totalElements}</span> transactions
              </p>

              {/* Page Size Selector */}
              <div className="flex items-center gap-2">
                <label className="text-sm text-gray-600">Per page:</label>
                <select
                  value={pageSize}
                  onChange={(e) => handlePageSizeChange(Number(e.target.value))}
                  className="px-2 py-1 border border-gray-300 rounded-md text-sm"
                >
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                </select>
              </div>
            </div>
          )}

          <TransactionTable
            transactions={transactions}
            onCategoryChanged={() => fetchTransactions()}
            onSort={handleSort}
            sortField={sortField}
            sortDirection={sortDirection}
          />

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="bg-white rounded-lg shadow-md p-4 flex justify-center items-center gap-2">
              {/* First Page */}
              <button
                onClick={() => handlePageChange(0)}
                disabled={page === 0}
                className="px-3 py-2 text-sm font-medium rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                First
              </button>

              {/* Previous Page */}
              <button
                onClick={() => handlePageChange(page - 1)}
                disabled={page === 0}
                className="px-3 py-2 text-sm font-medium rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Previous
              </button>

              {/* Page Numbers */}
              <div className="flex gap-1">
                {[...Array(totalPages)].map((_, index) => {
                  // Show first page, last page, current page, and pages around current
                  if (
                    index === 0 ||
                    index === totalPages - 1 ||
                    (index >= page - 2 && index <= page + 2)
                  ) {
                    return (
                      <button
                        key={index}
                        onClick={() => handlePageChange(index)}
                        className={`px-3 py-2 text-sm font-medium rounded-md ${
                          page === index
                            ? 'bg-indigo-600 text-white'
                            : 'hover:bg-gray-100'
                        }`}
                      >
                        {index + 1}
                      </button>
                    )
                  } else if (index === page - 3 || index === page + 3) {
                    return <span key={index} className="px-2 py-2">...</span>
                  }
                  return null
                })}
              </div>

              {/* Next Page */}
              <button
                onClick={() => handlePageChange(page + 1)}
                disabled={page >= totalPages - 1}
                className="px-3 py-2 text-sm font-medium rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Next
              </button>

              {/* Last Page */}
              <button
                onClick={() => handlePageChange(totalPages - 1)}
                disabled={page >= totalPages - 1}
                className="px-3 py-2 text-sm font-medium rounded-md disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-100"
              >
                Last
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default TransactionsPage

