import api from './api'

/**
 * Get all categories (enabled and disabled)
 */
export const getAllCategories = async () => {
  const response = await api.get('/api/categories')
  return response.data
}

/**
 * Get only enabled categories
 */
export const getEnabledCategories = async () => {
  console.log('categoryApi - Requesting enabled categories from /api/categories/enabled')
  try {
    const response = await api.get('/api/categories/enabled')
    console.log('categoryApi - Response:', response)
    console.log('categoryApi - Response data:', response.data)
    return response.data
  } catch (error) {
    console.error('categoryApi - Error fetching enabled categories:', error)
    console.error('categoryApi - Error details:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      url: error.config?.url
    })
    throw error
  }
}

/**
 * Get category by ID
 */
export const getCategoryById = async (id) => {
  const response = await api.get(`/api/categories/${id}`)
  return response.data
}

/**
 * Create new category
 */
export const createCategory = async (categoryData) => {
  const response = await api.post('/api/categories', categoryData)
  return response.data
}

/**
 * Update existing category
 */
export const updateCategory = async (id, categoryData) => {
  const response = await api.put(`/api/categories/${id}`, categoryData)
  return response.data
}

/**
 * Enable a category
 */
export const enableCategory = async (id) => {
  const response = await api.patch(`/api/categories/${id}/enable`)
  return response.data
}

/**
 * Disable a category
 */
export const disableCategory = async (id) => {
  const response = await api.patch(`/api/categories/${id}/disable`)
  return response.data
}

/**
 * Delete a category
 * WARNING: This permanently deletes the category
 */
export const deleteCategory = async (id) => {
  const response = await api.delete(`/api/categories/${id}`)
  return response.data
}

/**
 * Initialize default categories
 */
export const initializeCategories = async () => {
  const response = await api.post('/api/categories/initialize')
  return response.data
}

