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

