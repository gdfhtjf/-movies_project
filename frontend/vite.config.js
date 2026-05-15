import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig(({ mode }) => ({
  base: '/',
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  build: {
    target: 'es2020',
    cssCodeSplit: true,
    chunkSizeWarningLimit: 600,
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-naive': ['naive-ui'],
          'vendor-echarts': ['echarts'],
          'vendor-vueuse': ['@vueuse/core', '@vueuse/motion'],
          'vendor-icons': ['@vicons/ionicons5'],
        },
      },
    },
  },
}))
