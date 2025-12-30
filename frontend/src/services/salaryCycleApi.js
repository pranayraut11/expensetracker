import api from './api'

/**
 * Get all salary cycles
 *
 * @returns {Promise} List of salary cycles
 */
export const getSalaryCycles = async () => {
  const response = await api.get('/api/salary-cycles')
  return response.data
}

/**
 * Get salary cycle by ID
 *
 * @param {number} cycleId - Salary cycle ID
 * @returns {Promise} Salary cycle details
 */
export const getSalaryCycleById = async (cycleId) => {
  const response = await api.get(`/api/salary-cycles/${cycleId}`)
  return response.data
}

/**
 * Get totals for a salary cycle
 *
 * @param {number} cycleId - Salary cycle ID
 * @returns {Promise} Response with { totalCredit, totalDebit, netSavings, salaryAmount }
 */
export const getSalaryCycleTotals = async (cycleId) => {
  const response = await api.get(`/api/salary-cycles/${cycleId}/totals`)
  return response.data
}

/**
 * Detect and create salary cycles from existing transactions
 *
 * @returns {Promise} Success message
 */
export const detectSalaryCycles = async () => {
  const response = await api.post('/api/salary-cycles/detect')
  return response.data
}

/**
 * Refresh salary cycles (delete and re-create)
 *
 * @returns {Promise} Success message
 */
export const refreshSalaryCycles = async () => {
  const response = await api.post('/api/salary-cycles/refresh')
  return response.data
}

/**
 * Update last salary cycle end date
 *
 * @returns {Promise} Success message
 */
export const updateLastSalaryCycle = async () => {
  const response = await api.post('/api/salary-cycles/update-last')
  return response.data
}

