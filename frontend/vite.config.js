import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Get backend URL from environment variable, default to localhost for local dev
const backendUrl = process.env.VITE_BACKEND_URL || 'http://localhost:8080';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: '0.0.0.0',
    open: true,
    proxy: {
      '/api': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/transactions': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/upload': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/credit-card': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/analytics': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/tags': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      },
      '/settings': {
        target: backendUrl,
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
