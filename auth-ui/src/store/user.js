import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 1. 定义状态：尝试从浏览器的本地存储中读取 token，如果没有就是空字符串
  const token = ref(localStorage.getItem('token') || '')

  // 2. 定义动作：保存 Token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken) // 存入本地存储，防刷新丢失
  }

  // 3. 定义动作：清除 Token (将来退出登录时用)
  const clearToken = () => {
    token.value = ''
    localStorage.removeItem('token')
  }

  return { token, setToken, clearToken }
})