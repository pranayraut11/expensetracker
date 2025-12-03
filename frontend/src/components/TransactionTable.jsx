import React, { useState } from 'react'
import { updateTransactionCategory } from '../services/transactionApi'
import { CATEGORIES } from '../constants/categories'

const TransactionTable = ({ transactions, onCategoryChanged, onSort, sortField, sortDirection }) => {
  const [editingId, setEditingId] = useState(null)
  const [copiedId, setCopiedId] = useState(null)

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
    })
  }

  const getCategoryColor = (category) => {
    const colors = {
      Food: 'bg-orange-100 text-orange-800',
      Groceries: 'bg-green-100 text-green-800',
      Shopping: 'bg-purple-100 text-purple-800',
      Travel: 'bg-blue-100 text-blue-800',
      Income: 'bg-emerald-100 text-emerald-800',
      Bills: 'bg-yellow-100 text-yellow-800',
      Fuel: 'bg-red-100 text-red-800',
      Medical: 'bg-pink-100 text-pink-800',
      Rent: 'bg-indigo-100 text-indigo-800',
      Entertainment: 'bg-fuchsia-100 text-fuchsia-800',
      Insurance: 'bg-cyan-100 text-cyan-800',
      Investment: 'bg-teal-100 text-teal-800',
      Education: 'bg-violet-100 text-violet-800',
      Transfers: 'bg-slate-100 text-slate-800',
      'ATM Withdrawals': 'bg-stone-100 text-stone-800',
      Miscellaneous: 'bg-gray-100 text-gray-800',
    }
    return colors[category] || 'bg-gray-100 text-gray-800'
  }

  const handleCategoryChange = async (transaction, newCategory) => {
    try {
      const updated = await updateTransactionCategory(transaction.id, newCategory)
      setEditingId(null)
      if (onCategoryChanged) onCategoryChanged(updated)
    } catch (e) {
      console.error('Failed to update category', e)
      alert('Failed to update category')
    }
  }

  const handleCopyDescription = async (transaction) => {
    try {
      await navigator.clipboard.writeText(transaction.description)
      setCopiedId(transaction.id)
      setTimeout(() => setCopiedId(null), 2000) // Reset after 2 seconds
    } catch (err) {
      console.error('Failed to copy text:', err)
      alert('Failed to copy to clipboard')
    }
  }

  // Sortable column header component
  const SortableHeader = ({ field, label, align = 'left' }) => {
    const isActive = sortField === field
    const isAsc = sortDirection === 'asc'

    return (
      <th
        onClick={() => onSort && onSort(field)}
        className={`px-6 py-3 text-${align} text-xs font-medium uppercase tracking-wider cursor-pointer hover:bg-gray-100 transition-colors select-none ${
          isActive ? 'text-indigo-700 bg-indigo-50' : 'text-gray-500'
        }`}
      >
        <div className={`flex items-center gap-2 ${align === 'right' ? 'justify-end' : ''}`}>
          <span>{label}</span>
          {onSort && (
            <span className="flex flex-col">
              {isActive ? (
                // Show active arrow
                isAsc ? (
                  <svg className="w-4 h-4 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clipRule="evenodd" />
                  </svg>
                ) : (
                  <svg className="w-4 h-4 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z" clipRule="evenodd" />
                  </svg>
                )
              ) : (
                // Show inactive sorting indicator
                <svg className="w-4 h-4 text-gray-300" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M5 12a1 1 0 102 0V6.414l1.293 1.293a1 1 0 001.414-1.414l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L5 6.414V12zM15 8a1 1 0 10-2 0v5.586l-1.293-1.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L15 13.586V8z" />
                </svg>
              )}
            </span>
          )}
        </div>
      </th>
    )
  }

  if (!transactions || transactions.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow-md p-8 text-center">
        <p className="text-gray-500 text-lg">No transactions found</p>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <SortableHeader field="date" label="Date" />
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Description
              </th>
              <SortableHeader field="category" label="Category" />
              <SortableHeader field="type" label="Type" />
              <SortableHeader field="amount" label="Amount" align="right" />
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Balance
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {transactions.map((transaction) => (
              <tr key={transaction.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {formatDate(transaction.date)}
                </td>
                <td className="px-6 py-4 text-sm text-gray-900">
                  <div className="flex items-center gap-2 max-w-md">
                    <span className="truncate" title={transaction.description}>
                      {transaction.description}
                    </span>
                    {transaction.isCreditCardTransaction && (
                      <span className="flex-shrink-0 px-2 py-0.5 text-xs font-medium bg-purple-100 text-purple-800 rounded-full border border-purple-300">
                        💳 CC
                      </span>
                    )}
                    {transaction.isCreditCardPayment && (
                      <span className="flex-shrink-0 px-2 py-0.5 text-xs font-medium bg-gray-100 text-gray-600 rounded-full border border-gray-300">
                        💳 Payment
                      </span>
                    )}
                    <button
                      onClick={() => handleCopyDescription(transaction)}
                      className={`flex-shrink-0 p-1 rounded hover:bg-gray-100 transition-colors ${
                        copiedId === transaction.id ? 'text-green-600' : 'text-gray-400 hover:text-gray-600'
                      }`}
                      title={copiedId === transaction.id ? 'Copied!' : 'Copy description'}
                    >
                      {copiedId === transaction.id ? (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                        </svg>
                      ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M8 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" />
                          <path d="M6 3a2 2 0 00-2 2v11a2 2 0 002 2h8a2 2 0 002-2V5a2 2 0 00-2-2 3 3 0 01-3 3H9a3 3 0 01-3-3z" />
                        </svg>
                      )}
                    </button>
                  </div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  {editingId === transaction.id ? (
                    <select
                      className="border rounded px-2 py-1 text-sm"
                      value={transaction.category}
                      onChange={(e) => handleCategoryChange(transaction, e.target.value)}
                      onBlur={() => setEditingId(null)}
                      autoFocus
                    >
                      {CATEGORIES.map((c) => (
                        <option key={c} value={c}>{c}</option>
                      ))}
                    </select>
                  ) : (
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getCategoryColor(transaction.category)}`}>
                      {transaction.category}
                    </span>
                  )}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  <span
                    className={`px-2 py-1 rounded-full text-xs font-medium ${
                      transaction.type === 'CREDIT'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}
                  >
                    {transaction.type}
                  </span>
                </td>
                <td className={`px-6 py-4 whitespace-nowrap text-sm text-right font-medium ${
                  transaction.type === 'CREDIT' ? 'text-green-600' : 'text-red-600'
                }`}>
                  ₹{Math.abs(transaction.amount).toLocaleString('en-IN', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  })}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right text-gray-900">
                  ₹{transaction.balance?.toLocaleString('en-IN', {
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2,
                  }) || '—'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-right">
                  {editingId !== transaction.id && (
                    <button
                      onClick={() => setEditingId(transaction.id)}
                      className="text-indigo-600 hover:text-indigo-900"
                    >
                      Edit
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default TransactionTable

