import api from './api'

export const uploadFile = async (formData) => {
  const response = await api.post('/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

export const getSummary = async () => {
  const response = await api.get('/transactions/summary')
  console.log('Summary response:', response.data)
  return response.data
}

/**
 * Get transactions with pagination, filtering, and sorting
 *
 * @param {Object} options - Query options
 * @param {number} options.page - Page number (0-based, default 0)
 * @param {number} options.size - Page size (default 20)
 * @param {string|string[]} options.sort - Sort field(s) and direction (default "date,desc")
 *                                Format: "field,direction" e.g. "date,desc", "amount,asc"
 *                                Or array for multi-sort: ["type,asc", "category,asc", "date,desc"]
 * @param {string} options.search - Search term for description
 * @param {string} options.category - Filter by category
 * @param {string} options.type - Filter by type (CREDIT or DEBIT)
 * @param {boolean} options.isCreditCardTransaction - Filter by credit card flag
 * @param {string} options.fromDate - Start date (yyyy-MM-dd)
 * @param {string} options.toDate - End date (yyyy-MM-dd)
 * @returns {Promise} PagedTransactionResponse with content, totalElements, totalPages, etc.
 */
export const getTransactions = async (options = {}) => {
  const params = {
    page: options.page ?? 0,
    size: options.size ?? 20,
    // Sort can be string or array - axios will handle both correctly
    sort: options.sort ?? 'date,desc',
  }

  // Add optional filters
  if (options.search) params.search = options.search
  if (options.category) params.category = options.category
  if (options.type) params.type = options.type
  if (options.isCreditCardTransaction !== undefined) {
    params.isCreditCardTransaction = options.isCreditCardTransaction
  }
  if (options.fromDate) params.fromDate = options.fromDate
  if (options.toDate) params.toDate = options.toDate

  // Use custom params serializer to handle array properly
  // We need to send: sort=type,desc (not sort=type&sort=desc)
  const config = {
    params,
    paramsSerializer: (params) => {
      const parts = []
      Object.keys(params).forEach(key => {
        const value = params[key]
        if (Array.isArray(value)) {
          // For array values, add each as separate param with same key
          value.forEach(v => {
            parts.push(`${key}=${encodeURIComponent(v)}`)
          })
        } else if (value !== undefined && value !== null) {
          parts.push(`${key}=${encodeURIComponent(value)}`)
        }
      })
      return parts.join('&')
    }
  }

  const response = await api.get('/transactions', config)
  return response.data
}

export const updateTransactionCategory = async (id, category) => {
  const response = await api.put(`/transactions/${id}/category`, category, {
    headers: { 'Content-Type': 'text/plain' }
  })
  return response.data
}

export const getTags = async (limit = 25, search = '') => {
  if (search) {
    // Use search endpoint when searching
    const response = await api.get('/tags/search', { params: { q: search } })
    return response.data
  } else {
    // Use top tags endpoint when not searching
    const response = await api.get('/tags/top', { params: { limit } })
    return response.data
  }
}
