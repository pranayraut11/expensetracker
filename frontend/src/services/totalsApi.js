import api from './api'

/**
 * Get total credit and debit amounts with optional filters
 *
 * IMPORTANT: This API excludes credit card payments automatically
 *
 * @param {string} from - Start date (optional, format: yyyy-MM-dd)
 * @param {string} to - End date (optional, format: yyyy-MM-dd)
 * @param {string} category - Category filter (optional)
 * @param {string} search - Description search term (optional)
 * @returns {Promise} Response with { totalCredit, totalDebit }
 */
export const getTotals = async (from = null, to = null, category = null, search = null) => {
  const params = {}

  if (from) params.from = from
  if (to) params.to = to
  if (category) params.category = category
  if (search) params.search = search

  const response = await api.get('/analytics/totals', { params })
  return response.data
}

