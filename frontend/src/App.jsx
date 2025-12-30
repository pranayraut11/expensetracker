import React from 'react'
import { RouterProvider } from 'react-router-dom'
import router from './router'
import { CategoryProvider } from './context/CategoryContext'

const App = () => {
  return (
    <CategoryProvider>
      <RouterProvider router={router} />
    </CategoryProvider>
  )
}

export default App
