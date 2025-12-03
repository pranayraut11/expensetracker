import api from './api'

/**
 * Get balance summary for a date range
 * @param {string} fromDate - Start date (YYYY-MM-DD) or null for all time
 * @param {string} toDate - End date (YYYY-MM-DD) or null for current date
 * @returns {Promise} Balance summary data
 */
export const getBalanceSummary = async (fromDate = null, toDate = null) => {
  const params = {}

  if (fromDate) {
    params.from = fromDate
  }

  if (toDate) {
    params.to = toDate
  }

  const response = await api.get('/transactions/balance-summary', { params })
  return response.data
}

