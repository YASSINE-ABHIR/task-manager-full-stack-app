
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'path'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  
  // Dev-server settings
  server: {
    host: 'localhost',    
    port: 3000,       
    strictPort: true, 
  },

  preview: {
    port: 3000,
    strictPort: true,
  },
})
