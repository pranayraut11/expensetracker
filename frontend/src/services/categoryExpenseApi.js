import api from './api'

/**
 * Get category-wise expenses for a specific month
 * @param {number} year - The year
 * @param {number} month - The month (1-12)
 * @returns {Promise} Category expense data
 */
export const getCategoryExpenses = async (year, month) => {
  try {
    const response = await api.get('/analytics/category-expenses', {
      params: { year, month }
    })
    // Ensure we always return an array
    return Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('Error fetching category expenses:', error)
    return [] // Return empty array on error
  }
}

/**
 * Get category-wise expenses for a date range with optional filters
 * @param {string} fromDate - Start date (YYYY-MM-DD)
 * @param {string} toDate - End date (YYYY-MM-DD)
 * @param {string} search - Optional search term
 * @param {string} category - Optional category filter
 * @returns {Promise} Category expense data mapped as {categoryName: amount}
 */
export const getCategoryExpensesByDateRange = async (fromDate, toDate, search = '', category = '') => {
  try {
    const params = {}
    if (fromDate) params.fromDate = fromDate
    if (toDate) params.toDate = toDate
    if (search) params.search = search
    if (category) params.category = category

    const response = await api.get('/analytics/category-expenses-range', { params })

    // Response is an array of { category, total }
    // Convert to object format { categoryName: amount }
    if (Array.isArray(response.data)) {
      const result = {}
      response.data.forEach(item => {
        result[item.category] = item.total
      })
      console.log('Category expenses data:', result)
      return result
    }
    return response.data || {}
  } catch (error) {
    console.error('Error fetching category expenses by date range:', error)
    return {} // Return empty object on error
  }
}
