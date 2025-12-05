import React, { useState, useEffect } from 'react'
import { CATEGORIES } from '../constants/categories'
import { getRules, createRule, updateRule } from '../services/ruleService'

const AddRuleModal = ({ isOpen, onClose, transaction, onRuleCreated }) => {
  const [selectedCategory, setSelectedCategory] = useState('')
  const [condition, setCondition] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (isOpen && transaction) {
      // Set default category to transaction's category
      setSelectedCategory(transaction.category || '')
      // Set default condition from transaction description
      setCondition(transaction.description?.toLowerCase().replace(/[^a-z0-9\s]/g, '').trim() || '')
    }
  }, [isOpen, transaction])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!selectedCategory || !condition) {
      setError('Please select a category and enter a condition')
      return
    }

    setLoading(true)
    setError(null)

    try {
      // Fetch all existing rules
      const allRules = await getRules()

      // Find if a rule exists for the selected category
      const existingRule = allRules.find(rule =>
        rule.categoryName === selectedCategory && rule.enabled
      )

      if (existingRule) {
        // Append condition to existing rule pattern with OR operator
        let newPattern
        const existingPattern = existingRule.pattern.trim()
        const newCondition = condition.trim()

        // Check if the condition already exists in the pattern
        const patternParts = existingPattern
          .replace(/^\(|\)$/g, '') // Remove outer brackets if they exist
          .split('|')
          .map(p => p.trim())

        if (patternParts.includes(newCondition)) {
          setError('This condition already exists in the rule')
          setLoading(false)
          return
        }

        // Check if existing pattern is wrapped in parentheses
        if (existingPattern.startsWith('(') && existingPattern.endsWith(')')) {
          // Remove the closing bracket, add the new condition, and close the bracket
          newPattern = existingPattern.slice(0, -1) + `|${newCondition})`
        } else if (existingPattern.includes('|')) {
          // Multiple conditions exist but not wrapped in brackets - wrap everything
          newPattern = `(${existingPattern}|${newCondition})`
        } else {
          // Single condition - create a bracketed group
          newPattern = `(${existingPattern}|${newCondition})`
        }

        const updatedRule = {
          ...existingRule,
          pattern: newPattern
        }

        await updateRule(existingRule.id, updatedRule)
      } else {
        // Create new rule
        const newRule = {
          ruleName: `${selectedCategory.replace(/[^a-zA-Z0-9]/g, '_')}_AutoRule_${Date.now()}`,
          categoryName: selectedCategory,
          pattern: condition,
          priority: 10,
          enabled: true,
          includeInTotals: true
        }

        await createRule(newRule)
      }

      // Notify parent and close
      if (onRuleCreated) {
        onRuleCreated()
      }
      handleClose()
    } catch (err) {
      console.error('Error creating/updating rule:', err)
      setError(err.response?.data?.message || 'Failed to save rule. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleClose = () => {
    setSelectedCategory('')
    setCondition('')
    setError(null)
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="px-6 py-4 border-b border-gray-200">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold text-gray-900">Add Rule from Transaction</h3>
            <button
              onClick={handleClose}
              className="text-gray-400 hover:text-gray-500"
            >
              <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {/* Transaction Description Display */}
          <div className="bg-gray-50 p-3 rounded-md">
            <p className="text-xs text-gray-500 mb-1">Transaction Description:</p>
            <p className="text-sm text-gray-900 font-medium">{transaction?.description}</p>
          </div>

          {/* Category Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Category <span className="text-red-500">*</span>
            </label>
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              required
              className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border"
            >
              <option value="">-- Select Category --</option>
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {cat}
                </option>
              ))}
            </select>
            <p className="mt-1 text-xs text-gray-500">
              If a rule exists for this category, the condition will be added to it
            </p>
          </div>

          {/* Condition Input */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Rule Condition <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={condition}
              onChange={(e) => setCondition(e.target.value)}
              required
              className="w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm px-3 py-2 border font-mono"
              placeholder="e.g., swiggy or (swiggy|zomato)"
            />
            <p className="mt-1 text-xs text-gray-500">
              Enter a regex pattern or keyword to match transactions
            </p>
          </div>

          {/* Error Display */}
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-md p-3">
              <p className="text-sm text-red-600">{error}</p>
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={handleClose}
              disabled={loading}
              className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Save Rule'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default AddRuleModal

