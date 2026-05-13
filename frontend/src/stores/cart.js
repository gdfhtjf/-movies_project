import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/api'

export const useCartStore = defineStore('cart', () => {
  const selectedSeats = ref([])
  const screeningId = ref(null)
  const screening = ref(null)

  function toggleSeat(seatLabel) {
    const idx = selectedSeats.value.indexOf(seatLabel)
    if (idx > -1) selectedSeats.value.splice(idx, 1)
    else selectedSeats.value.push(seatLabel)
  }

  function clearSeats() { selectedSeats.value = [] }

  async function loadScreening(id) {
    const res = await api.get(`/screenings/${id}`)
    screening.value = res.data
    screeningId.value = id
  }

  async function submitOrder() {
    const res = await api.post('/orders', {
      screeningId: screeningId.value,
      seatNumbers: selectedSeats.value
    })
    clearSeats()
    return res.data
  }

  const totalPrice = computed(() => {
    if (!screening.value?.price) return 0
    return screening.value.price * selectedSeats.value.length
  })

  return { selectedSeats, screeningId, screening, toggleSeat, clearSeats, loadScreening, submitOrder, totalPrice }
})
