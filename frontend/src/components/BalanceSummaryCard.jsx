import React from 'react'

const BalanceSummaryCard = ({ summary }) => {
  if (!summary) {
    return null
  }

  const {
    openingBalance = 0,
    closingBalance = 0,
    totalCredits = 0,
    totalDebits = 0,
    netFlow = 0,
    transactionsCount = 0
  } = summary

  const formatCurrency = (value) => {
    const absValue = Math.abs(value)
    return `₹${absValue.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
  }

  const isPositiveFlow = netFlow >= 0

  return (
    <div className="bg-white rounded-lg shadow-md p-6 border-l-4 border-indigo-500">
      <div className="mb-4">
        <h3 className="text-lg font-semibold text-gray-800 mb-1">Balance Summary</h3>
        <p className="text-sm text-gray-500">
          Based on {transactionsCount} transaction{transactionsCount !== 1 ? 's' : ''}
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {/* Opening Balance */}
        <div className="bg-blue-50 rounded-lg p-4 border border-blue-200">
          <p className="text-xs font-medium text-blue-700 mb-1">Opening Balance</p>
          <p className="text-xl font-bold text-blue-900">{formatCurrency(openingBalance)}</p>
        </div>

        {/* Total Credits */}
        <div className="bg-green-50 rounded-lg p-4 border border-green-200">
          <p className="text-xs font-medium text-green-700 mb-1">Total Credits</p>
          <p className="text-xl font-bold text-green-900">+{formatCurrency(totalCredits)}</p>
        </div>

        {/* Total Debits */}
        <div className="bg-red-50 rounded-lg p-4 border border-red-200">
          <p className="text-xs font-medium text-red-700 mb-1">Total Debits</p>
          <p className="text-xl font-bold text-red-900">-{formatCurrency(totalDebits)}</p>
        </div>

        {/* Closing Balance */}
        <div className="bg-purple-50 rounded-lg p-4 border border-purple-200">
          <p className="text-xs font-medium text-purple-700 mb-1">Closing Balance</p>
          <p className="text-xl font-bold text-purple-900">{formatCurrency(closingBalance)}</p>
        </div>

        {/* Net Flow */}
        <div className={`${isPositiveFlow ? 'bg-teal-50 border-teal-200' : 'bg-orange-50 border-orange-200'} rounded-lg p-4 border`}>
          <p className={`text-xs font-medium ${isPositiveFlow ? 'text-teal-700' : 'text-orange-700'} mb-1`}>
            Net Flow
          </p>
          <p className={`text-xl font-bold ${isPositiveFlow ? 'text-teal-900' : 'text-orange-900'}`}>
            {isPositiveFlow ? '+' : ''}{formatCurrency(netFlow)}
          </p>
          <p className="text-xs text-gray-600 mt-1">
            {isPositiveFlow ? '(Surplus)' : '(Deficit)'}
          </p>
        </div>
      </div>

      {/* Formula Explanation */}
      <div className="mt-4 pt-4 border-t border-gray-200">
        <p className="text-xs text-gray-500 text-center">
          Net Flow = Closing Balance - Opening Balance = {formatCurrency(closingBalance)} - {formatCurrency(openingBalance)} =
          <span className={`font-semibold ${isPositiveFlow ? 'text-teal-700' : 'text-orange-700'}`}>
            {' '}{isPositiveFlow ? '+' : ''}{formatCurrency(netFlow)}
          </span>
        </p>
      </div>
    </div>
  )
}

export default BalanceSummaryCard

