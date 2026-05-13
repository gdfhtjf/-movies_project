import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAdmin = ref(false)
  const isLoggedIn = computed(() => user.value !== null)
  const fetched = ref(false)

  async function fetchMe() {
    try {
      const res = await api.get('/auth/me')
      user.value = res.data
      isAdmin.value = res.data?.role === 'admin'
    } catch (error) {
      if (error.response?.status === 401 || error.message === '未登录') {
        user.value = null
        isAdmin.value = false
      }
    } finally {
      fetched.value = true
    }
  }

  async function login(name, password, remember) {
    const params = new URLSearchParams()
    params.append('name', name)
    params.append('password', password)
    params.append('remember', remember ? 'true' : 'false')
    const res = await api.post('/auth/login', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    })
    user.value = res.data
    isAdmin.value = res.data?.role === 'admin'
    fetched.value = true
    message.success('登录成功')
    return res.data
  }

  async function register(name, password) {
    const params = new URLSearchParams()
    params.append('name', name)
    params.append('password', password)
    const res = await api.post('/auth/register', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    })
    message.success('注册成功')
    return res.data
  }

  async function logout() {
    await api.post('/auth/logout')
    user.value = null
    isAdmin.value = false
    fetched.value = false
    message.success('已退出')
  }

  return { user, isAdmin, isLoggedIn, fetched, fetchMe, login, register, logout }
})
