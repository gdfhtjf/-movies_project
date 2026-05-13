import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/utils/api'

export const useAdminStore = defineStore('admin', () => {
  const stats = ref({ movieCount: 0, screeningCount: 0, userCount: 0, orderCount: 0, paidOrderCount: 0 })
  const todayData = ref({ orderCount: 0, revenue: 0 })
  const orderTrend = ref([])
  const movieRevenue = ref([])

  async function fetchStats() {
    const res = await api.get('/admin/stats')
    stats.value = res.data || {}
  }

  async function fetchTodayData() {
    const res = await api.get('/admin/today-data')
    todayData.value = res.data || {}
  }

  async function fetchOrderTrend(days = 7) {
    const res = await api.get('/admin/order-trend', { params: { days } })
    orderTrend.value = res.data || []
  }

  async function fetchMovieRevenue() {
    const res = await api.get('/admin/movie-revenue')
    movieRevenue.value = res.data || []
  }

  return { stats, todayData, orderTrend, movieRevenue, fetchStats, fetchTodayData, fetchOrderTrend, fetchMovieRevenue }
})
