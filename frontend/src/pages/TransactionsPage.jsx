import React, { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getTransactions } from '../services/transactionApi'
import { getTotals } from '../services/totalsApi'
import TransactionTable from '../components/TransactionTable'
import MonthYearSelector from '../components/MonthYearSelector'
import SalaryCycleSelector from '../components/SalaryCycleSelector'
import { useCategories } from '../context/CategoryContext'

const TransactionsPage = () => {
  const { categories, loading: categoriesLoading } = useCategories()

  // Salary cycle state
  const [monthMode, setMonthMode] = useState('calendar') // 'calendar' or 'salary'
  const [selectedSalaryCycle, setSelectedSalaryCycle] = useState(null)

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

  // Sorting state - now supports multiple columns
  const [sortColumns, setSortColumns] = useState([{ field: 'date', direction: 'desc' }])

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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, pageSize, JSON.stringify(sortColumns), JSON.stringify(filters)])

  const fetchTransactions = async () => {
    try {
      setLoading(true)

      // Build sort parameter - support multiple columns
      // Use pipe (|) as delimiter instead of comma to avoid Spring Boot auto-splitting
      const sortParams = sortColumns.map(col => `${col.field}|${col.direction}`)

      console.log('Fetching transactions with filters:', filters)
      console.log('Fetching transactions with sort:', sortParams)

      const response = await getTransactions({
        page,
        size: pageSize,
        sort: sortParams,
        ...filters
      })

      console.log('Received transactions:', response)

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
    // fetchTransactions will be called automatically by useEffect
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
    // fetchTransactions will be called automatically by useEffect
  }

  const handleMonthChange = (month) => {
    setSelectedMonth(month)
    if (month && selectedYear) {
      // Both month and year are selected
      const fromDate = new Date(selectedYear, month - 1, 1)
      const toDate = new Date(selectedYear, month, 0)

      setFilters(prev => ({
        ...prev,
        fromDate: fromDate.toISOString().split('T')[0],
        toDate: toDate.toISOString().split('T')[0]
      }))

      setPage(0)
      // fetchTransactions will be called automatically by useEffect
    } else {
      // No month/year selected - clear date filters
      setFilters(prev => ({
        ...prev,
        fromDate: '',
        toDate: ''
      }))

      setPage(0)
      // fetchTransactions will be called automatically by useEffect
    }
  }

  const handleYearChange = (year) => {
    setSelectedYear(year)
    if (selectedMonth && year) {
      // Both month and year are selected
      const fromDate = new Date(year, selectedMonth - 1, 1)
      const toDate = new Date(year, selectedMonth, 0)

      setFilters(prev => ({
        ...prev,
        fromDate: fromDate.toISOString().split('T')[0],
        toDate: toDate.toISOString().split('T')[0]
      }))

      setPage(0)
      // fetchTransactions will be called automatically by useEffect
    }
  }

  // Pagination handlers
  const handlePageChange = (newPage) => {
    setPage(newPage)
  }

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize)
    setPage(0)
  }

  const handleMonthModeChange = (mode) => {
    setMonthMode(mode)

    if (mode === 'calendar') {
      // Reset to calendar mode
      setSelectedSalaryCycle(null)
      // Clear date filters
      setFilters(prev => ({
        ...prev,
        fromDate: '',
        toDate: ''
      }))
      setPage(0)
      // fetchTransactions will be called automatically by useEffect
    }
  }

  const handleSalaryCycleChange = (cycle) => {
    setSelectedSalaryCycle(cycle)

    if (cycle) {
      // Format dates to YYYY-MM-DD format - handle both ISO strings and date objects
      const formatDate = (dateString) => {
        if (!dateString) return ''
        // If already in YYYY-MM-DD format, return as is
        if (typeof dateString === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(dateString)) {
          return dateString
        }
        // Parse the date string properly using UTC to avoid timezone issues
        const date = new Date(dateString)
        const year = date.getUTCFullYear()
        const month = String(date.getUTCMonth() + 1).padStart(2, '0')
        const day = String(date.getUTCDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }

      const fromDate = formatDate(cycle.startDate)
      const toDate = formatDate(cycle.endDate)

      console.log('Salary cycle dates:', { startDate: cycle.startDate, endDate: cycle.endDate, formatted: { fromDate, toDate } })

      // Apply salary cycle date range
      setFilters(prev => ({
        ...prev,
        fromDate,
        toDate
      }))
      setPage(0)
      // fetchTransactions will be called automatically by useEffect
    }
  }

  // Sorting handler - supports multi-column sorting
  const handleSort = (field, ctrlKey = false) => {
    console.log('handleSort called with field:', field, 'ctrlKey:', ctrlKey)
    console.log('Current sortColumns:', sortColumns)

    if (ctrlKey) {
      // Multi-column sort mode (Ctrl/Cmd + Click)
      const existingIndex = sortColumns.findIndex(col => col.field === field)

      if (existingIndex >= 0) {
        // Column already in sort list - toggle direction or remove
        const current = sortColumns[existingIndex]
        if (current.direction === 'desc') {
          // Change to ascending
          const newSortColumns = [...sortColumns]
          newSortColumns[existingIndex] = { field, direction: 'asc' }
          console.log('Setting new sort columns (toggle to asc):', newSortColumns)
          setSortColumns(newSortColumns)
        } else {
          // Remove from sort list (if not the only column)
          if (sortColumns.length > 1) {
            const filtered = sortColumns.filter((_, i) => i !== existingIndex)
            console.log('Setting new sort columns (removed):', filtered)
            setSortColumns(filtered)
          } else {
            // If it's the only column, just toggle direction
            console.log('Setting new sort columns (toggle to desc):', [{ field, direction: 'desc' }])
            setSortColumns([{ field, direction: 'desc' }])
          }
        }
      } else {
        // Add new column to sort list
        const newSortColumns = [...sortColumns, { field, direction: 'desc' }]
        console.log('Setting new sort columns (add new):', newSortColumns)
        setSortColumns(newSortColumns)
      }
    } else {
      // Single column sort mode (normal click)
      const currentColumn = sortColumns.find(col => col.field === field)

      if (currentColumn) {
        // Toggle direction if same field
        const newDirection = currentColumn.direction === 'asc' ? 'desc' : 'asc'
        console.log('Setting new sort columns (single toggle):', [{ field, direction: newDirection }])
        setSortColumns([{ field, direction: newDirection }])
      } else {
        // New field, default to desc
        console.log('Setting new sort columns (single new):', [{ field, direction: 'desc' }])
        setSortColumns([{ field, direction: 'desc' }])
      }
    }
    setPage(0)
  }

  // Helper function to get sort info for a field
  const getSortInfo = (field) => {
    const index = sortColumns.findIndex(col => col.field === field)
    if (index >= 0) {
      return {
        isActive: true,
        direction: sortColumns[index].direction,
        order: sortColumns.length > 1 ? index + 1 : null
      }
    }
    return { isActive: false, direction: null, order: null }
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
                disabled={categoriesLoading}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                <option value="">All Categories</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.name}>
                    {cat.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-end">
              <div className="w-full space-y-2 md:space-y-0 md:flex md:space-x-2">
                <button
                  onClick={handleApplyFilters}
                  disabled={categoriesLoading}
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

            {/* Month and Year Selector - takes 2 columns */}
            <div className="lg:col-span-2">
              <MonthYearSelector
                selectedMonth={selectedMonth}
                selectedYear={selectedYear}
                onMonthChange={handleMonthChange}
                onYearChange={handleYearChange}
              />
            </div>
          </div>

          {/* Salary Cycle Selector */}
          <div className="pt-4 border-t border-gray-200">
            <SalaryCycleSelector
              onModeChange={handleMonthModeChange}
              selectedMode={monthMode}
              selectedCycleId={selectedSalaryCycle?.cycleId}
              onCycleChange={handleSalaryCycleChange}
            />

            {/* Show salary cycle info when selected */}
            {monthMode === 'salary' && selectedSalaryCycle && (
              <div className="mt-4 p-4 bg-blue-50 border border-blue-200 rounded-lg">
                <div className="flex items-center gap-2">
                  <svg className="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <p className="text-sm text-blue-800">
                    Showing transactions from <strong>{new Date(selectedSalaryCycle.startDate).toLocaleDateString()}</strong> to <strong>{new Date(selectedSalaryCycle.endDate).toLocaleDateString()}</strong>
                  </p>
                </div>
              </div>
            )}
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
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 17l-5-5m0 0l5-5m-5 5h12" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Results info and page size selector */}
          {transactions.length > 0 && (
            <div className="bg-white rounded-lg shadow-md p-4 flex justify-between items-center">
              <p className="text-sm text-gray-600">
                Showing <span className="font-bold text-indigo-600">{page * pageSize + 1}</span> to <span className="font-bold text-indigo-600">{Math.min((page + 1) * pageSize, totalElements)}</span> of <span className="font-bold text-indigo-600">{totalElements}</span> transactions
              </p>
              <div className="flex items-center gap-2">
                <label className="text-sm text-gray-600">Show:</label>
                <select
                  value={pageSize}
                  onChange={(e) => handlePageSizeChange(Number(e.target.value))}
                  className="border border-gray-300 rounded-md px-2 py-1 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                </select>
              </div>
            </div>
          )}

          {/* Sort Order Indicator */}
          {sortColumns.length > 0 && (
            <div className="bg-white rounded-lg shadow-md p-4">
              <div className="flex items-center gap-2 flex-wrap">
                <span className="text-sm font-medium text-gray-700">Sort Order:</span>
                {sortColumns.map((col, index) => (
                  <div key={col.field} className="flex items-center gap-1 px-3 py-1 bg-indigo-100 text-indigo-800 rounded-full text-sm">
                    <span className="font-semibold">{index + 1}.</span>
                    <span className="capitalize">{col.field}</span>
                    <span className="text-xs">
                      {col.direction === 'asc' ? '↑' : '↓'}
                    </span>
                    {sortColumns.length > 1 && (
                      <button
                        onClick={() => {
                          const newColumns = sortColumns.filter((_, i) => i !== index)
                          setSortColumns(newColumns.length > 0 ? newColumns : [{ field: 'date', direction: 'desc' }])
                        }}
                        className="ml-1 text-indigo-600 hover:text-indigo-800"
                        title="Remove from sort"
                      >
                        ×
                      </button>
                    )}
                  </div>
                ))}
                {sortColumns.length > 1 && (
                  <button
                    onClick={() => setSortColumns([{ field: 'date', direction: 'desc' }])}
                    className="px-3 py-1 text-sm text-gray-600 hover:text-gray-800 underline"
                  >
                    Clear all
                  </button>
                )}
                <span className="text-xs text-gray-500 italic">
                  (Ctrl/Cmd + Click column headers to add to sort)
                </span>
              </div>
            </div>
          )}

          <TransactionTable
            transactions={transactions}
            onCategoryChanged={() => fetchTransactions()}
            onSort={handleSort}
            getSortInfo={getSortInfo}
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

