import React, { createContext, useState, useEffect, useContext } from 'react'
import { getEnabledCategories } from '../services/categoryApi'

const CategoryContext = createContext()

/**
 * Category Provider - Manages global category state
 * Fetches enabled categories from DB and provides them to all components
 */
export const CategoryProvider = ({ children }) => {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetchCategories = async () => {
    try {
      setLoading(true)
      setError(null)
      console.log('CategoryContext - Fetching categories...')
      const data = await getEnabledCategories()
      console.log('CategoryContext - Received categories:', data)
      console.log('CategoryContext - Categories count:', data?.length)
      setCategories(data)
    } catch (err) {
      console.error('CategoryContext - Error fetching categories:', err)
      console.error('CategoryContext - Error details:', {
        message: err.message,
        response: err.response?.data,
        status: err.response?.status
      })
      setError('Failed to load categories')
      // Set empty array on error to prevent app breakage
      setCategories([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCategories()
  }, [])

  const refreshCategories = async () => {
    await fetchCategories()
  }

  const getCategoryByName = (name) => {
    return categories.find(cat => cat.name === name)
  }

  const getCategoryColor = (name) => {
    const category = getCategoryByName(name)
    return category?.color || '#6b7280' // Default gray
  }

  const getCategoryIcon = (name) => {
    const category = getCategoryByName(name)
    return category?.icon || 'circle' // Default icon
  }

  const value = {
    categories,
    loading,
    error,
    refreshCategories,
    getCategoryByName,
    getCategoryColor,
    getCategoryIcon
  }

  return (
    <CategoryContext.Provider value={value}>
      {children}
    </CategoryContext.Provider>
  )
}

/**
 * Hook to use categories in any component
 */
export const useCategories = () => {
  const context = useContext(CategoryContext)
  if (!context) {
    throw new Error('useCategories must be used within CategoryProvider')
  }
  return context
}

export default CategoryContext

