import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/utils/api'

export const useMovieStore = defineStore('movies', () => {
  const list = ref([])
  const loading = ref(false)
  const total = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(12)

  async function fetchMovies(params = {}) {
    loading.value = true
    try {
      const res = await api.get('/movies', {
        params: { page: currentPage.value, size: pageSize.value, ...params }
      })
      list.value = res.data?.records || []
      total.value = res.data?.total || 0
    } finally {
      loading.value = false
    }
  }

  return { list, loading, total, currentPage, pageSize, fetchMovies }
})
