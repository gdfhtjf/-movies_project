<template>
  <div class="seat-page">
    <n-spin :show="loading">
      <div v-if="screening && movie" class="seat-header">
        <h2>{{ movie.title }}</h2>
        <div class="header-meta">
          <span><strong>影厅：</strong>{{ screening.hallNumber }}</span>
          <span><strong>时间：</strong>{{ formatTime(screening.showTime) }}</span>
          <span><strong>票价：</strong><span class="price-text">¥{{ movie.price }}</span></span>
        </div>
      </div>

      <div class="screen-box">
        <div class="screen-label">银 幕</div>
      </div>

      <div class="legend-enhanced">
        <div class="legend-item">
          <span class="seat-demo available"></span>
          <span class="legend-desc">可选座位</span>
        </div>
        <div class="legend-item">
          <span class="seat-demo sold"></span>
          <span class="legend-desc">已售出</span>
        </div>
        <div class="legend-item">
          <span class="seat-demo selected"></span>
          <span class="legend-desc">已选座位</span>
        </div>
        <div class="legend-item">
          <span class="seat-demo hover"></span>
          <span class="legend-desc">悬停预览</span>
        </div>
      </div>

      <div v-if="screening" class="seat-grid-wrap">
        <div class="seat-grid">
          <div v-for="row in rows" :key="row" class="seat-row">
            <span class="row-label">{{ rowLabel(row) }}</span>
            <button
              v-for="col in cols"
              :key="`${row}-${col}`"
              class="seat-btn"
              :class="seatClass(row, col)"
              :disabled="isOccupied(row, col)"
              :title="`${rowLabel(row)}${col}座`"
              @click="toggleSeat(row, col)"
            >
              {{ col }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="selectedSeats.length" class="order-summary">
        <div class="summary-info">
          <div class="summary-seats">
            <span class="summary-label">已选座位</span>
            <span class="summary-value">{{ selectedSeats.join('、') }}</span>
          </div>
          <div class="summary-total">
            <span class="summary-label">合计</span>
            <span class="summary-price">¥{{ totalPrice }}</span>
          </div>
        </div>
        <n-button-group>
          <n-button size="large" @click="clearAll">清空重选</n-button>
          <n-button type="error" size="large" :loading="submitting" @click="submitOrder">
            确认下单 · ¥{{ totalPrice }}
          </n-button>
        </n-button-group>
      </div>

      <div v-else-if="!screening && !loading" class="empty-state">
        <div class="icon">🎫</div>
        <h3>场次信息加载失败</h3>
      </div>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useIntervalFn } from '@vueuse/core'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

const route = useRoute()
const router = useRouter()
const screening = ref(null)
const movie = ref(null)
const selectedSeats = ref([])
const occupiedSeats = ref([])
const loading = ref(true)
const submitting = ref(false)
const rows = ref(8)
const cols = ref(12)

const screeningId = computed(() => route.params.screeningId)

function rowLabel(r) {
  return `${r}排`
}

function isSelected(r, c) {
  return selectedSeats.value.includes(`${rowLabel(r)}${c}座`)
}

function isOccupied(r, c) {
  return occupiedSeats.value.includes(`${rowLabel(r)}${c}座`)
}

function seatClass(r, c) {
  if (isSelected(r, c)) return 'selected'
  if (isOccupied(r, c)) return 'sold'
  return 'available'
}

function toggleSeat(r, c) {
  const label = `${rowLabel(r)}${c}座`
  if (occupiedSeats.value.includes(label)) return
  const idx = selectedSeats.value.indexOf(label)
  if (idx > -1) {
    selectedSeats.value.splice(idx, 1)
  } else {
    selectedSeats.value.push(label)
  }
}

function clearAll() {
  selectedSeats.value = []
}

const totalPrice = computed(() => {
  if (!movie.value) return '0.00'
  return (movie.value.price * selectedSeats.value.length).toFixed(2)
})

async function fetchSeats() {
  try {
    const res = await api.get(`/orders/screening/${screeningId.value}/seats`)
    occupiedSeats.value = res.data || []
  } catch { /* silent */ }
}

const { pause, resume } = useIntervalFn(fetchSeats, 8000, { immediate: false })

async function submitOrder() {
  if (!selectedSeats.value.length) {
    message.warning('请至少选择一个座位')
    return
  }
  submitting.value = true
  try {
    await api.post('/orders', {
      screeningId: screening.value.id,
      seatNumbers: selectedSeats.value,
    })
    message.success('下单成功！')
    router.push('/orders')
  } catch { /* handled */ }
  submitting.value = false
}

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ')
}

onMounted(async () => {
  const id = route.params.screeningId
  try {
    const [scRes, occRes] = await Promise.all([
      api.get(`/screenings/${id}`),
      api.get(`/orders/screening/${id}/seats`),
    ])
    screening.value = scRes.data
    occupiedSeats.value = occRes.data || []

    const mRes = await api.get(`/movies/${screening.value.movieId}`)
    movie.value = mRes.data

    rows.value = Math.min(Math.ceil(screening.value.totalSeats / cols.value), 12)
    cols.value = Math.min(cols.value, Math.ceil(screening.value.totalSeats / rows.value))
  } catch {
    message.error('加载场次失败')
  }
  loading.value = false
  resume()
})

onUnmounted(() => {
  pause()
})
</script>

<style scoped>
.seat-page {
  max-width: 920px; margin: 0 auto; padding: 32px 24px 160px 24px;
  min-height: 80vh;
  color: #ffffff;
}

.seat-header { text-align: center; margin-bottom: 28px; }

.seat-header h2 {
  font-family: var(--font-heading); font-size: 1.8rem;
  color: #ffb74d; margin-bottom: 10px;
}

.header-meta {
  display: flex; gap: 24px; justify-content: center;
  color: #e0e0e0; font-size: 0.9rem; flex-wrap: wrap;
}

.price-text { color: #e50914; font-weight: 700; }

/* ===== 银幕 ===== */
.screen-box { text-align: center; margin: 12px 0 36px; }
.screen-label {
  display: inline-block; background: #1c1c1c;
  color: #e0e0e0; padding: 10px 80px;
  border-radius: 0 0 50px 50px; letter-spacing: 8px;
  font-size: 0.9rem; border: 1px solid #555;
  text-shadow: 0 0 12px rgba(244,162,97,0.3);
}

/* ===== 图例 ===== */
.legend-enhanced {
  display: flex; gap: 28px; justify-content: center;
  margin-bottom: 32px; flex-wrap: wrap;
}

.legend-item { display: flex; align-items: center; gap: 8px; }

.seat-demo {
  width: 24px; height: 22px; border-radius: 4px 4px 8px 8px;
  border: 1px solid; flex-shrink: 0;
}

.seat-demo.available { background: #222; border-color: #555; }
.seat-demo.sold { background: #333; border-color: #555; }
.seat-demo.selected { background: #e50914; border-color: #e50914; }
.seat-demo.hover { background: #3a5068; border-color: #ffb74d; }

.legend-desc { color: #e0e0e0; font-size: 0.82rem; }

/* ===== 座位网格 ===== */
.seat-grid-wrap {
  display: flex; justify-content: center;
  overflow-x: auto; padding-bottom: 16px;
}

.seat-grid { display: flex; flex-direction: column; gap: 6px; }

.seat-row { display: flex; align-items: center; gap: 5px; }

.row-label {
  width: 36px; text-align: right; font-size: 0.72rem;
  color: #e0e0e0; margin-right: 6px; flex-shrink: 0;
}

.seat-btn {
  width: 34px; height: 30px; border-radius: 4px 4px 10px 10px;
  border: 1px solid #555;
  background: #222;
  color: #e0e0e0;
  text-align: center; font-size: 0.7rem; cursor: pointer;
  transition: all 0.18s ease; outline: none; flex-shrink: 0;
  padding: 0; line-height: 30px;
}

.seat-btn.available:hover {
  background: #3a5068; border-color: #ffb74d;
  color: #ffb74d; transform: scale(1.08);
}

.seat-btn.selected {
  background: #e50914; border-color: #e50914;
  color: #fff; box-shadow: 0 0 8px rgba(230,57,70,0.4);
}

.seat-btn.sold {
  background: #444; border-color: #666; color: #999;
  cursor: not-allowed; opacity: 0.6;
}

/* ===== 下单栏 ===== */
.order-summary {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #1c1c1c;
  border-top: 2px solid #555;
  padding: 16px 28px; display: flex; align-items: center;
  justify-content: space-between; z-index: 30; gap: 16px;
  flex-wrap: wrap;
}

.summary-info { display: flex; gap: 32px; align-items: center; }

.summary-label { font-size: 0.8rem; color: #e0e0e0; margin-right: 8px; }
.summary-value { color: #ffb74d; font-weight: 600; font-size: 0.9rem; }
.summary-price { color: #e50914; font-size: 1.5rem; font-weight: 700; }

@media (max-width: 768px) {
  .order-summary { flex-direction: column; align-items: stretch; padding: 12px 16px; }
  .summary-info { justify-content: space-between; }
  .seat-btn { width: 28px; height: 26px; font-size: 0.65rem; line-height: 26px; }
}
</style>
