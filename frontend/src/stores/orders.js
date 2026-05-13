import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/utils/api'

export const useOrderStore = defineStore('orders', () => {
  const list = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)

  async function fetchMyOrders(page = 1) {
    loading.value = true
    try {
      const res = await api.get('/orders', { params: { page, size: 10 } })
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
      currentPage.value = page
    } finally {
      loading.value = false
    }
  }

  async function cancelOrder(id) {
    await api.put(`/orders/${id}/cancel`)
    await fetchMyOrders(currentPage.value)
  }

  async function payOrder(id) {
    await api.post(`/payment/pay/${id}`)
    await fetchMyOrders(currentPage.value)
  }

  return { list, loading, total, currentPage, fetchMyOrders, cancelOrder, payOrder }
})
