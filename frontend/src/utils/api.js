import axios from 'axios'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true,
})

api.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      if (!response.config?.url?.includes('/auth/me')) {
        message.error(data.message || '请求失败')
      }
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  (error) => {
    const backendMsg = error.response?.data?.message
    const displayMsg = backendMsg || error.message || '网络错误'
    if (error.response?.status === 401) {
      if (!error.config?.url?.includes('/auth/me')) {
        message.error('请先登录')
      }
    } else if (error.response?.status === 403) {
      message.error('无操作权限')
    } else {
      message.error(displayMsg)
    }
    return Promise.reject(error)
  }
)

export default api
