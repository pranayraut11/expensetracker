import api from './api'

/**
 * Upload credit card statement XLS file
 */
export const uploadCreditCardStatement = async (formData) => {
  const response = await api.post('/credit-card/upload-xls', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

