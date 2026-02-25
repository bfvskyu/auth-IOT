import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 只要是发往 /api 打头的请求，Vite 都会偷偷帮你转发给后端的 8080 端口
      '/api': {
        target: 'http://localhost:8080', 
        changeOrigin: true
      }
    }
  }
})