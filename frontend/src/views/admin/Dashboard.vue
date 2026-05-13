<template>
  <div class="dashboard">
    <vue-particles
      id="dash-particles"
      class="dash-particles"
      :options="dashParticleOptions"
    />

    <div class="dash-header">
      <h3 class="page-title" style="border-bottom:none; margin-bottom:0">📊 系统概览</h3>
      <n-space>
        <n-button size="small" quaternary @click="refreshAll" class="refresh-btn">
          <template #icon><RefreshOutline /></template>
          刷新数据
        </n-button>
      </n-space>
    </div>

    <n-grid :cols="4" :x-gap="16" responsive="screen" class="stat-grid">
      <n-gi :span="1">
        <StatCard icon="🎬" title="电影总数" :value="stats.movieCount ?? '—'" />
      </n-gi>
      <n-gi :span="1">
        <StatCard icon="📅" title="放映场次" :value="stats.screeningCount ?? '—'" />
      </n-gi>
      <n-gi :span="1">
        <StatCard icon="📦" title="订单总数" :value="stats.orderCount ?? '—'" />
      </n-gi>
      <n-gi :span="1">
        <StatCard icon="👤" title="用户总数" :value="stats.userCount ?? '—'" />
      </n-gi>
    </n-grid>

    <div class="quick-actions">
      <n-button type="error" @click="$router.push('/admin/movies/add')">
        <template #icon><AddOutline /></template>
        添加新电影
      </n-button>
      <n-button type="warning" @click="$router.push('/admin/screenings')">
        <template #icon><CalendarOutline /></template>
        管理放映场次
      </n-button>
      <n-button type="info" @click="$router.push('/admin/orders')">
        <template #icon><TicketOutline /></template>
        全站订单
      </n-button>
      <n-button @click="$router.push('/admin/movies/list')">
        <template #icon><ListOutline /></template>
        电影列表
      </n-button>
      <n-button @click="$router.push('/admin/posters')">
        <template #icon><ImageOutline /></template>
        海报管理
      </n-button>
    </div>

    <n-grid :cols="2" :x-gap="16" responsive="screen" class="chart-grid">
      <n-gi :span="1">
        <n-card class="chart-card" title="📈 订单趋势">
          <template #header-extra>
            <n-button-group size="tiny">
              <n-button
                :type="trendRange === 7 ? 'primary' : 'default'"
                @click="setTrendRange(7)"
              >
                7天
              </n-button>
              <n-button
                :type="trendRange === 30 ? 'primary' : 'default'"
                @click="setTrendRange(30)"
              >
                30天
              </n-button>
            </n-button-group>
          </template>
          <n-spin :show="chartLoading.trend">
            <div ref="trendChartRef" class="chart-box">
              <n-empty
                v-if="!chartLoading.trend && trendData.length === 0"
                description="暂无订单数据"
                style="height:100%; justify-content:center"
              />
            </div>
          </n-spin>
        </n-card>
      </n-gi>
      <n-gi :span="1">
        <n-card class="chart-card" title="🎯 电影票房 TOP5">
          <n-spin :show="chartLoading.revenue">
            <div ref="revenueChartRef" class="chart-box">
              <n-empty
                v-if="!chartLoading.revenue && revenueData.length === 0"
                description="暂无售票数据"
                style="height:100%; justify-content:center"
              />
            </div>
          </n-spin>
        </n-card>
      </n-gi>
    </n-grid>

    <n-grid :cols="3" :x-gap="16" class="realtime-grid">
      <n-gi :span="1">
        <n-card class="realtime-card">
          <div class="rt-header">💰 今日票房</div>
          <div class="rt-value">¥{{ todayRevenue }}</div>
        </n-card>
      </n-gi>
      <n-gi :span="1">
        <n-card class="realtime-card">
          <div class="rt-header">📋 今日订单</div>
          <div class="rt-value">{{ todayOrders }}</div>
        </n-card>
      </n-gi>
      <n-gi :span="1">
        <n-card class="realtime-card">
          <div class="rt-header">👥 用户总数</div>
          <div class="rt-value">{{ stats.userCount ?? '—' }}</div>
        </n-card>
      </n-gi>
    </n-grid>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useResizeObserver } from '@vueuse/core'
import * as echarts from 'echarts'
import api from '@/utils/api'
import StatCard from '@/components/common/StatCard.vue'
import {
  AddOutline, CalendarOutline, TicketOutline, ListOutline,
  ImageOutline, RefreshOutline,
} from '@vicons/ionicons5'

const stats = ref({})
const trendRange = ref(7)
const trendChartRef = ref(null)
const revenueChartRef = ref(null)
const todayRevenue = ref('0.00')
const todayOrders = ref(0)
const trendData = ref([])
const revenueData = ref([])
const chartLoading = ref({ trend: false, revenue: false })

let trendChart = null
let revenueChart = null

const dashParticleOptions = {
  fullScreen: false,
  fpsLimit: 30,
  pauseOnBlur: true,
  particles: {
    number: { value: 20, density: { enable: true, area: 1200 } },
    color: { value: '#999999' },
    shape: { type: 'circle' },
    opacity: { value: 0.1, random: true },
    size: { value: { min: 1, max: 2 }, random: true },
    move: {
      enable: true,
      speed: { min: 0.3, max: 0.5 },
      direction: 'none',
      random: true,
      straight: false,
      outModes: 'out',
    },
  },
  interactivity: {
    events: {
      onHover: { enable: false },
      onClick: { enable: false },
    },
  },
  detectRetina: false,
}

function setTrendRange(days) {
  trendRange.value = days
  fetchTrendData()
}

function buildTrendChart() {
  if (!trendChart) return
  const data = trendData.value
  if (!data.length) {
    trendChart.clear()
    return
  }

  const dates = data.map(d => d.date)
  const counts = data.map(d => d.count)
  const amounts = data.map(d => d.amount)

  trendChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 50, right: 60, bottom: 40, left: 60 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#555', width: 2 } },
      axisLabel: {
        color: '#b0b0b0',
        fontSize: 12,
        fontWeight: 500,
        margin: 12
      },
      axisTick: { show: false },
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        nameTextStyle: {
          color: '#e50914',
          fontSize: 13,
          fontWeight: 600,
          padding: [0, 0, 0, 10]
        },
        axisLine: { show: false },
        axisLabel: {
          color: '#b0b0b0',
          fontSize: 12,
          fontWeight: 500
        },
        splitLine: {
          lineStyle: {
            color: '#333',
            type: 'dashed',
            width: 1
          }
        },
      },
      {
        type: 'value',
        name: '金额(¥)',
        nameTextStyle: {
          color: '#ff9f1c',
          fontSize: 13,
          fontWeight: 600,
          padding: [0, 0, 0, 10]
        },
        axisLine: { show: false },
        axisLabel: {
          color: '#b0b0b0',
          fontSize: 12,
          fontWeight: 500
        },
        splitLine: { show: false },
      },
    ],
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(20, 20, 20, 0.98)',
      borderColor: '#e50914',
      borderWidth: 1,
      textStyle: {
        color: '#fff',
        fontSize: 13,
        fontWeight: 500
      },
      padding: [14, 18],
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(229, 9, 20, 0.1)'
        }
      }
    },
    legend: {
      top: 10,
      data: ['订单数', '金额(¥)'],
      textStyle: {
        color: '#e0e0e0',
        fontSize: 13,
        fontWeight: 600
      },
      itemWidth: 14,
      itemHeight: 14,
      itemGap: 25
    },
    series: [
      {
        name: '订单数',
        type: 'line',
        data: counts,
        smooth: 0.4,
        symbol: 'circle',
        symbolSize: 10,
        showSymbol: true,
        lineStyle: {
          color: '#e50914',
          width: 3,
          shadowColor: 'rgba(229, 9, 20, 0.4)',
          shadowBlur: 10
        },
        itemStyle: {
          color: '#e50914',
          borderColor: '#fff',
          borderWidth: 2,
          shadowColor: 'rgba(229, 9, 20, 0.5)',
          shadowBlur: 6
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(229,9,20,0.35)' },
            { offset: 0.5, color: 'rgba(229,9,20,0.15)' },
            { offset: 1, color: 'rgba(229,9,20,0.02)' },
          ]),
        },
        label: {
          show: true,
          position: 'top',
          color: '#e50914',
          fontSize: 11,
          fontWeight: 600,
          backgroundColor: 'rgba(229,9,20,0.1)',
          padding: [2, 6],
          borderRadius: 4
        }
      },
      {
        name: '金额(¥)',
        type: 'bar',
        yAxisIndex: 1,
        data: amounts,
        barWidth: 18,
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#ff9f1c' },
            { offset: 0.5, color: '#ff7b00' },
            { offset: 1, color: '#e55d00' },
          ]),
          shadowColor: 'rgba(255, 159, 28, 0.4)',
          shadowBlur: 8
        },
        label: {
          show: true,
          position: 'top',
          color: '#ff9f1c',
          fontSize: 11,
          fontWeight: 600,
          formatter: (v) => v.value > 0 ? '¥' + Number(v.value).toFixed(0) : ''
        }
      },
    ],
  })
}

function buildRevenueChart() {
  if (!revenueChart) return
  const data = revenueData.value
  if (!data.length) {
    revenueChart.clear()
    return
  }

  const names = data.map(d => d.movieTitle)
  const values = data.map(d => d.revenue)
  
  // 为每个条形生成渐变颜色（从深到浅）
  const colors = [
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#e50914' }, { offset: 1, color: '#ff3b3b' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ff4757' }, { offset: 1, color: '#ff6b7a' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ff6b81' }, { offset: 1, color: '#ff919e' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ff9f1c' }, { offset: 1, color: '#ffbe61' }]),
    new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#ffc312' }, { offset: 1, color: '#ffd866' }])
  ]

  revenueChart.setOption({
    backgroundColor: 'transparent',
    grid: { top: 25, right: 80, bottom: 30, left: 120 },
    yAxis: {
      type: 'category',
      data: names,
      axisLine: { show: false },
      axisLabel: {
        color: '#e0e0e0',
        fontSize: 13,
        fontWeight: 500,
        margin: 15,
        overflow: 'truncate',
        width: 100
      },
      axisTick: { show: false },
      inverse: true,
    },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: {
        color: '#b0b0b0',
        fontSize: 12,
        fontWeight: 500,
        formatter: (v) => (v >= 10000 ? (v / 10000).toFixed(1) + '万' : v),
      },
      splitLine: {
        lineStyle: {
          color: '#333',
          type: 'dashed',
          width: 1
        }
      },
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(20, 20, 20, 0.98)',
      borderColor: '#ff9f1c',
      borderWidth: 1,
      textStyle: {
        color: '#fff',
        fontSize: 13,
        fontWeight: 500
      },
      padding: [14, 18],
      formatter: (params) => {
        const p = Array.isArray(params) ? params[0] : params
        return `<span style="color:#ff9f1c;font-weight:600">${p.name}</span><br/><span style="color:#e0e0e0">票房: </span><span style="color:#ff9f1c;font-weight:600;font-size:14px">¥${Number(p.value).toFixed(2)}</span>`
      },
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(255, 159, 28, 0.1)'
        }
      }
    },
    series: [{
      type: 'bar',
      data: values.map((v, i) => ({
        value: v,
        itemStyle: {
          borderRadius: [0, 10, 10, 0],
          color: colors[i % colors.length],
          shadowColor: 'rgba(229, 9, 20, 0.3)',
          shadowBlur: 10,
          shadowOffsetX: 3
        }
      })),
      barWidth: 26,
      label: {
        show: true,
        position: 'right',
        color: '#ff9f1c',
        fontSize: 12,
        fontWeight: 600,
        distance: 8,
        formatter: (p) => (p.value > 0 ? '¥' + Number(p.value).toFixed(0) : ''),
      },
      emphasis: {
        itemStyle: {
          shadowColor: 'rgba(229, 9, 20, 0.5)',
          shadowBlur: 15,
          shadowOffsetX: 5
        },
        scale: true,
        scaleSize: 5
      }
    }],
    animationEasing: 'elasticOut',
    animationDelay: (idx) => idx * 100
  })
}

function refreshAll() {
  fetchTrendData()
  fetchRevenueData()
  fetchTodayData()
  fetchStats()
}

async function fetchTrendData() {
  chartLoading.value.trend = true
  try {
    const res = await api.get('/admin/order-trend', { params: { days: trendRange.value } })
    trendData.value = res.data || []
    buildTrendChart()
  } catch {
    trendData.value = []
  }
  chartLoading.value.trend = false
}

async function fetchRevenueData() {
  chartLoading.value.revenue = true
  try {
    const res = await api.get('/admin/movie-revenue')
    revenueData.value = res.data || []
    buildRevenueChart()
  } catch {
    revenueData.value = []
  }
  chartLoading.value.revenue = false
}

async function fetchStats() {
  try {
    const res = await api.get('/admin/stats')
    stats.value = res.data || {}
  } catch { /* handled */ }
}

async function fetchTodayData() {
  try {
    const res = await api.get('/admin/today-data')
    const data = res.data || {}
    todayRevenue.value = (data.revenue != null ? Number(data.revenue) : 0).toFixed(2)
    todayOrders.value = data.orderCount || 0
  } catch {
    todayRevenue.value = '0.00'
    todayOrders.value = 0
  }
}

function initCharts() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value, 'dark')
    if (trendData.value.length) buildTrendChart()
  }
  if (revenueChartRef.value) {
    revenueChart = echarts.init(revenueChartRef.value, 'dark')
    if (revenueData.value.length) buildRevenueChart()
  }
}

useResizeObserver(trendChartRef, () => trendChart?.resize())
useResizeObserver(revenueChartRef, () => revenueChart?.resize())

onMounted(async () => {
  await Promise.all([fetchStats(), fetchTodayData(), fetchTrendData(), fetchRevenueData()])
  await nextTick()
  initCharts()
})

onUnmounted(() => {
  trendChart?.dispose()
  revenueChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  position: relative;
  max-width: 1280px;
}

.dash-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.dash-header {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.refresh-btn {
  transition: transform 0.3s;
}

.refresh-btn:active {
  transform: rotate(180deg) scale(0.9);
}

.stat-grid {
  position: relative;
  z-index: 2;
  margin-bottom: 24px;
}

.quick-actions {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  padding: 16px;
  background: #1a1a1a;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
}

.chart-grid {
  position: relative;
  z-index: 2;
  margin-bottom: 24px;
}

.chart-card {
  border: 1px solid #2d2d2d;
  border-radius: 8px;
}

.chart-box {
  width: 100%;
  height: 320px;
}

.realtime-grid {
  position: relative;
  z-index: 2;
  margin-bottom: 24px;
}

.realtime-card {
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  text-align: center;
  transition: transform 0.2s;
}

.realtime-card:hover {
  transform: translateY(-2px);
}

.rt-header {
  color: #e0e0e0;
  font-size: 14px;
  margin-bottom: 8px;
  font-weight: 500;
}

.rt-value {
  font-size: 32px;
  font-weight: 700;
  color: #ff9f1c;
}

@media (max-width: 992px) {
  .stat-grid :deep(.n-grid) { grid-template-columns: repeat(2, 1fr) !important; }
  .chart-grid :deep(.n-grid) { grid-template-columns: 1fr !important; }
  .realtime-grid :deep(.n-grid) { grid-template-columns: repeat(3, 1fr) !important; }
}

@media (max-width: 768px) {
  .quick-actions { justify-content: center; }
  .rt-value { font-size: 24px; }
  .dash-particles { display: none; }
}

@media (max-width: 480px) {
  .dash-particles { display: none; }
}
</style>
