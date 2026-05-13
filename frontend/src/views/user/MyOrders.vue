<template>
  <div class="orders-page">
    <h3 class="page-title">📦 我的订单</h3>

    <div v-if="orders.length" class="order-list">
      <n-card v-for="o in orders" :key="o.id" class="order-card">
        <div class="order-top">
          <div class="order-movie-info">
            <h4>
              <router-link :to="`/detail/${o.movieId}`">{{ o.movieTitle || '未知电影' }}</router-link>
            </h4>
            <p class="order-meta">
              <span>场次 #{{ o.screeningId }}</span>
              <span class="dot-sep">·</span>
              <span>{{ o.seatNumber }}</span>
              <span class="dot-sep">·</span>
              <span class="order-price">¥{{ o.totalPrice }}</span>
            </p>
          </div>
          <div class="order-status-area">
            <span class="status-badge" :class="statusClass(o.status)">
              <span class="status-dot"></span>
              {{ statusLabel(o.status) }}
            </span>
            <p class="order-time">{{ o.createTime?.replace('T', ' ') }}</p>
          </div>
        </div>

        <div class="order-timeline">
          <div
            v-for="(node, idx) in timelineNodes(o)"
            :key="node.key"
            class="tl-node"
            :class="{ active: node.active, first: idx === 0 }"
          >
            <div class="tl-icon">
              <span class="tl-icon-inner">{{ node.active ? '✓' : (idx + 1) }}</span>
            </div>
            <div class="tl-content">
              <span class="tl-label">{{ node.label }}</span>
              <span class="tl-time">{{ node.time || '—' }}</span>
            </div>
          </div>
        </div>

        <div v-if="o.status === 'CREATED' || o.status === 'PAID'" class="order-actions">
          <n-button v-if="o.status === 'CREATED'" size="small" type="error" @click="cancelOrder(o)">
            取消订单
          </n-button>
          <n-button v-if="o.status === 'CREATED'" size="small" type="warning" @click="payOrder(o)">
            去支付
          </n-button>
        </div>
      </n-card>
    </div>

    <div v-else class="empty-state">
      <div class="icon">📭</div>
      <h3>暂无订单记录</h3>
      <n-button type="error" @click="$router.push('/movies')">去看看电影</n-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])

const orders = ref([])

const STATUS_MAP = {
  CREATED: { label: '待支付', cls: 'status-pending', desc: '订单已创建，等待支付' },
  PAID: { label: '已支付', cls: 'status-paid', desc: '支付成功，等待出票' },
  TICKETED: { label: '已出票', cls: 'status-ticketed', desc: '已出票，请按时观影' },
  COMPLETED: { label: '已完成', cls: 'status-completed', desc: '观影完成' },
  CANCELLED: { label: '已取消', cls: 'status-cancelled', desc: '订单已取消' },
}

function statusLabel(status) {
  return STATUS_MAP[status]?.label || status || '未知'
}

function statusClass(status) {
  return STATUS_MAP[status]?.cls || 'status-pending'
}

function timelineNodes(order) {
  const s = order.status
  const nodes = [
    { key: 'created', label: '下单', time: order.createTime?.replace('T', ' ') || '', active: true },
    { key: 'paid', label: '支付', time: s === 'PAID' || s === 'TICKETED' || s === 'COMPLETED' ? (order.payTime || order.createTime?.replace('T', ' ')) : null, active: s === 'PAID' || s === 'TICKETED' || s === 'COMPLETED' },
    { key: 'ticketed', label: '出票', time: s === 'TICKETED' || s === 'COMPLETED' ? (order.ticketTime || order.createTime?.replace('T', ' ')) : null, active: s === 'TICKETED' || s === 'COMPLETED' },
    { key: 'completed', label: '完成', time: s === 'COMPLETED' ? (order.completeTime || order.createTime?.replace('T', ' ')) : null, active: s === 'COMPLETED' },
  ]

  if (s === 'CANCELLED') {
    nodes.forEach((n) => { n.active = n.key === 'created' })
    nodes.push({ key: 'cancelled', label: '已取消', time: order.cancelTime?.replace('T', ' ') || order.createTime?.replace('T', ' ') || '', active: true })
  }

  return nodes
}

async function cancelOrder(order) {
  dialog.warning({
    title: '取消订单',
    content: '确定要取消这个订单吗？取消后不可恢复。',
    positiveText: '确认取消',
    negativeText: '再想想',
    onPositiveClick: async () => {
      try {
        await api.put(`/orders/${order.id}/cancel`)
        message.success('订单已取消')
        fetchOrders()
      } catch { /* handled */ }
    },
  })
}

async function payOrder(order) {
  message.info('模拟支付中...')
  setTimeout(async () => {
    try {
      await api.put(`/orders/${order.id}/pay`)
      message.success('支付成功！')
      fetchOrders()
    } catch { /* handled */ }
  }, 800)
}

async function fetchOrders() {
  try {
    const res = await api.get('/orders')
    orders.value = res.data?.records || []
  } catch { /* handled */ }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-page { 
  max-width: 900px; 
  margin: 0 auto; 
  padding: 32px 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #ffffff 0%, #ff9f1c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  border: none;
  margin-bottom: 32px;
  padding: 0;
}

.order-card {
  margin-bottom: 24px;
  border: 1px solid #2d2d2d;
  border-radius: 16px;
  background: linear-gradient(145deg, #1a1a1a 0%, #161616 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(229, 9, 20, 0.1);
  border-color: rgba(229, 9, 20, 0.3);
}

/* ===== 顶部信息 ===== */
.order-top { 
  display: flex; 
  justify-content: space-between; 
  align-items: flex-start; 
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #2a2a2a;
}

.order-movie-info h4, .order-movie-info h4 a {
  color: #ffffff; 
  margin-bottom: 10px; 
  font-size: 1.15rem;
  font-weight: 700;
  transition: color 0.2s;
}

.order-movie-info h4 a:hover {
  color: #e50914;
}

.order-meta { 
  color: #999999; 
  font-size: 0.88rem; 
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.dot-sep { 
  margin: 0; 
  color: #444;
}

.order-price { 
  color: #ff9f1c; 
  font-weight: 800; 
  font-size: 1.05rem;
  background: linear-gradient(135deg, #ff9f1c 0%, #ffd54f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.order-status-area { 
  text-align: right; 
  flex-shrink: 0;
}

.status-badge {
  display: inline-flex; 
  align-items: center; 
  gap: 8px;
  padding: 6px 16px; 
  border-radius: 24px; 
  font-size: 0.85rem; 
  font-weight: 700;
  letter-spacing: 0.3px;
}

.status-dot { 
  width: 8px; 
  height: 8px; 
  border-radius: 50%; 
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}

.status-pending { 
  background: rgba(255, 183, 77, 0.15); 
  border: 1px solid rgba(255, 183, 77, 0.4); 
  color: #ffb74d; 
}
.status-pending .status-dot { 
  background: #ffb74d; 
  box-shadow: 0 0 8px rgba(255, 183, 77, 0.5);
}

.status-paid { 
  background: rgba(144, 202, 249, 0.15); 
  border: 1px solid rgba(144, 202, 249, 0.4); 
  color: #90CAF9; 
}
.status-paid .status-dot { 
  background: #90CAF9;
  box-shadow: 0 0 8px rgba(144, 202, 249, 0.5);
}

.status-ticketed { 
  background: rgba(129, 199, 132, 0.15); 
  border: 1px solid rgba(129, 199, 132, 0.4); 
  color: #81C784; 
}
.status-ticketed .status-dot { 
  background: #81C784;
  box-shadow: 0 0 8px rgba(129, 199, 132, 0.5);
}

.status-completed { 
  background: rgba(129, 199, 132, 0.15); 
  border: 1px solid rgba(129, 199, 132, 0.4); 
  color: #81C784; 
}
.status-completed .status-dot { 
  background: #81C784;
  box-shadow: 0 0 8px rgba(129, 199, 132, 0.5);
}

.status-cancelled { 
  background: rgba(158, 158, 158, 0.15); 
  border: 1px solid rgba(158, 158, 158, 0.4); 
  color: #9e9e9e; 
}
.status-cancelled .status-dot { 
  background: #9e9e9e;
  animation: none;
}

.order-time { 
  color: #777; 
  font-size: 0.8rem; 
  margin-top: 6px;
  font-weight: 500;
}

/* ===== 时间轴 ===== */
.order-timeline { 
  display: flex; 
  gap: 0; 
  padding: 16px 0 12px;
}

.tl-node {
  flex: 1; 
  display: flex; 
  flex-direction: column; 
  align-items: center;
  position: relative; 
  text-align: center;
}

.tl-node::before {
  content: ''; 
  position: absolute; 
  top: 16px; 
  left: 0; 
  right: 50%;
  height: 3px; 
  background: #333;
  border-radius: 2px;
}

.tl-node::after {
  content: ''; 
  position: absolute; 
  top: 16px; 
  left: 50%; 
  right: 0;
  height: 3px; 
  background: #333;
  border-radius: 2px;
}

.tl-node.first::before { display: none; }

.tl-node.active::before,
.tl-node.active::after {
  background: linear-gradient(90deg, #81C784 0%, #A5D6A7 100%);
}

.tl-icon {
  width: 36px; 
  height: 36px; 
  border-radius: 50%;
  background: #252525; 
  border: 3px solid #3a3a3a;
  display: flex; 
  align-items: center; 
  justify-content: center;
  z-index: 1; 
  margin-bottom: 10px; 
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.tl-icon-inner { 
  font-size: 0.85rem; 
  color: #666;
  font-weight: 800;
}

.tl-node.active .tl-icon {
  background: linear-gradient(135deg, #81C784 0%, #66BB6A 100%);
  border-color: #81C784;
  box-shadow: 0 4px 16px rgba(129, 199, 132, 0.4);
}

.tl-node.active .tl-icon-inner {
  color: #ffffff;
}

.tl-content { 
  display: flex; 
  flex-direction: column; 
}

.tl-label { 
  font-size: 0.85rem; 
  color: #888; 
  margin-bottom: 4px;
  font-weight: 600;
  transition: color 0.3s;
}
.tl-time { 
  font-size: 0.75rem; 
  color: #666;
  font-weight: 500;
}

.tl-node.active .tl-label { 
  color: #81C784; 
  font-weight: 700;
}
.tl-node.active .tl-time { 
  color: #e0e0e0;
}

/* ===== 操作按钮 ===== */
.order-actions { 
  margin-top: 18px; 
  display: flex; 
  gap: 12px; 
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #252525;
}

.empty-state {
  padding: 100px 20px;
}

.empty-state .icon {
  font-size: 5rem;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 1.3rem;
  margin-bottom: 16px;
  color: #e0e0e0;
}

@media (max-width: 768px) {
  .orders-page {
    padding: 24px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .order-top { 
    flex-direction: column; 
    gap: 12px; 
  }
  
  .order-status-area { 
    text-align: left; 
  }
  
  .order-timeline { 
    flex-wrap: wrap; 
    gap: 16px; 
  }
  
  .tl-node::before, .tl-node::after { display: none; }
  
  .tl-node { 
    flex: 0 0 auto; 
    flex-direction: row; 
    gap: 12px;
    align-items: center;
  }
  
  .tl-icon {
    margin-bottom: 0;
  }
  
  .tl-content {
    align-items: flex-start;
  }
}
</style>
