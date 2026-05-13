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
          <!-- 出口标记（顶部） -->
          <div v-if="topExits.length" class="exit-row top-exits">
            <span class="row-label"></span>
            <span v-for="(marker, mi) in topExitMarkers" :key="'te-'+mi"
              class="seat-slot" :class="{ 'slot-placeholder': !marker }">
              <span v-if="marker" class="exit-badge">{{ marker }}</span>
            </span>
          </div>

          <div v-for="(row, rowIdx) in rowsNumber" :key="row" class="seat-row" :class="{ 'row-aisle-gap': hasAisleAfterRow(row) }">
            <span class="row-label">{{ rowLabel(row) }}</span>
            <span
              v-for="(col, colIdx) in colsNumber"
              :key="`${row}-${col}`"
              class="seat-slot"
              :class="{ 'col-aisle-gap': hasAisleAfterCol(col) }"
            >
              <button
                v-if="!isExitSlot(row, col)"
                class="seat-btn"
                :class="seatClass(row, col)"
                :disabled="isOccupied(row, col)"
                :title="`${rowLabel(row)}${col}座`"
                @click="toggleSeat(row, col)"
              >
                {{ col }}
              </button>
              <span v-else class="exit-badge">{{ getExitLabel(row, col) }}</span>
            </span>
            <!-- 行尾出口 -->
            <span v-if="getRowEndExit(row)" class="exit-badge side-exit">{{ getRowEndExit(row) }}</span>
          </div>

          <!-- 出口标记（底部） -->
          <div v-if="bottomExits.length" class="exit-row bottom-exits">
            <span class="row-label"></span>
            <span v-for="(marker, mi) in bottomExitMarkers" :key="'be-'+mi"
              class="seat-slot" :class="{ 'slot-placeholder': !marker }">
              <span v-if="marker" class="exit-badge">{{ marker }}</span>
            </span>
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
const layoutData = ref(null)

const screeningId = computed(() => route.params.screeningId)

// 用于 v-for 迭代的数字数组
const rowsNumber = computed(() => Array.from({ length: rows.value }, (_, i) => i + 1))
const colsNumber = computed(() => Array.from({ length: cols.value }, (_, i) => i + 1))

// 走道间隙：哪些列/行之后需要额外间距
const aisleAfterCols = computed(() => {
  if (!layoutData.value?.aisles?.afterCols) return new Set()
  return new Set(layoutData.value.aisles.afterCols)
})

const aisleAfterRows = computed(() => {
  if (!layoutData.value?.aisles?.afterRows) return new Set()
  return new Set(layoutData.value.aisles.afterRows)
})

// 出口集合：Map<"row-col", label>
const exitMap = computed(() => {
  const map = new Map()
  if (!layoutData.value?.exits) return map
  for (const exit of layoutData.value.exits) {
    map.set(`${exit.row}-${exit.col}`, exit.label)
  }
  return map
})

// 顶/底出口（col 为 0 或超出范围，表示网格外）
const topExits = computed(() => {
  if (!layoutData.value?.exits) return []
  return layoutData.value.exits.filter(e => e.row === 0)
})
const bottomExits = computed(() => {
  if (!layoutData.value?.exits) return []
  return layoutData.value.exits.filter(e => e.row > rows.value)
})

// 构建顶/底部出口标记数组（与列对齐）
const topExitMarkers = computed(() => {
  const markers = new Array(cols.value).fill(null)
  for (const e of topExits.value) {
    const ci = e.col - 1
    if (ci >= 0 && ci < cols.value) markers[ci] = e.label
  }
  return markers
})
const bottomExitMarkers = computed(() => {
  const markers = new Array(cols.value).fill(null)
  for (const e of bottomExits.value) {
    const ci = e.col - 1
    if (ci >= 0 && ci < cols.value) markers[ci] = e.label
  }
  return markers
})

function hasAisleAfterCol(c) {
  return aisleAfterCols.value.has(c)
}

function hasAisleAfterRow(r) {
  return aisleAfterRows.value.has(r)
}

function isExitSlot(r, c) {
  return exitMap.value.has(`${r}-${c}`)
}

function getExitLabel(r, c) {
  return exitMap.value.get(`${r}-${c}`) || ''
}

function getRowEndExit(r) {
  return exitMap.value.get(`${r}-0`) || ''
}

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
  if (isExitSlot(r, c)) return ''
  if (isSelected(r, c)) return 'selected'
  if (isOccupied(r, c)) return 'sold'
  return 'available'
}

function toggleSeat(r, c) {
  if (isExitSlot(r, c)) return
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

async function loadHallLayout(hallNumber) {
  try {
    const res = await api.get('/hall-layouts/by-hall', { params: { hallNumber } })
    const layout = res.data
    rows.value = layout.rowsNum || 8
    cols.value = layout.colsNum || 12
    layoutData.value = layout.layoutJson ? JSON.parse(layout.layoutJson) : null
  } catch {
    rows.value = 8
    cols.value = 12
    layoutData.value = null
  }
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

    // 根据场次的 hallNumber 加载影厅布局
    if (screening.value.hallNumber) {
      await loadHallLayout(screening.value.hallNumber)
    }
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

.seat-row.row-aisle-gap { margin-bottom: 12px; }

.row-label {
  width: 36px; text-align: right; font-size: 0.72rem;
  color: #e0e0e0; margin-right: 6px; flex-shrink: 0;
}

/* 座位槽位容器 */
.seat-slot {
  width: 34px; height: 30px; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}

.seat-slot.col-aisle-gap { margin-right: 12px; }

.seat-btn {
  width: 34px; height: 30px; border-radius: 4px 4px 10px 10px;
  border: 1px solid #555;
  background: #222;
  color: #e0e0e0;
  text-align: center; font-size: 0.7rem; cursor: pointer;
  transition: all 0.18s ease; outline: none;
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

/* 出口标记 */
.exit-badge {
  font-size: 0.62rem; color: #81C784;
  background: rgba(129,199,132,0.12);
  border: 1px dashed #4CAF50;
  border-radius: 4px; padding: 2px 6px;
  white-space: nowrap; text-align: center;
  display: inline-block;
}

.side-exit {
  margin-left: 8px; flex-shrink: 0;
}

.exit-row {
  display: flex; align-items: center; gap: 5px;
  padding: 4px 0;
}

.exit-row .row-label { visibility: hidden; }

.slot-placeholder { visibility: hidden; }

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
  .seat-slot { width: 28px; height: 26px; }
  .seat-btn { width: 28px; height: 26px; font-size: 0.65rem; line-height: 26px; }
}
</style>
