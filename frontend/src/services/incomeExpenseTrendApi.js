import api from './api'

/**
 * Get income vs expense trend
 * @param {number} year - The year to fetch data for
 * @param {number|null} month - Optional month (1-12). If null, returns yearly monthly trend
 * @returns {Promise} Trend data (monthly or daily based on parameters)
 */
export const getIncomeExpenseTrend = async (year, month = null) => {
  try {
    const params = { year }
    if (month !== null && month !== 'all') {
      params.month = month
    }

    const response = await api.get('/analytics/income-expense-trend', { params })
    // Ensure we always return an array
    return Array.isArray(response.data) ? response.data : []
  } catch (error) {
    console.error('Error fetching income expense trend:', error)
    return [] // Return empty array on error
  }
}

