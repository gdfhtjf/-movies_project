<template>
  <div class="manage-page">
    <div class="page-header">
      <h3 class="page-title">📦 订单管理</h3>
    </div>

    <n-card class="search-card">
      <n-form inline label-placement="left" label-width="auto" :model="searchForm">
        <n-form-item label="关键词">
          <n-input
            v-model:value="searchForm.keyword"
            placeholder="搜索订单号、电影名、用户名"
            clearable
            @keyup.enter="refresh"
          />
        </n-form-item>
        <n-form-item label="状态">
          <n-select
            v-model:value="searchForm.status"
            :options="statusOptions"
            placeholder="全部状态"
            clearable
          />
        </n-form-item>
        <n-form-item>
          <n-space>
            <n-button type="primary" @click="refresh">
              <template #icon><SearchOutline /></template>
              搜索
            </n-button>
            <n-button @click="resetSearch">重置</n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>

    <n-data-table
      :columns="visibleColumns"
      :data="orders"
      :loading="loading"
      :row-key="(r) => r.id"
      :bordered="false"
      striped
    />

    <div style="display:flex; justify-content:flex-end; margin-top:16px">
      <n-pagination
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :item-count="pagination.itemCount"
        :page-sizes="pagination.pageSizes"
        show-size-picker
        show-quick-jumper
        @update:page="fetchOrders"
        @update:page-size="handlePageSizeChange"
      />
    </div>

    <n-modal
      v-model:show="showStatusModal"
      preset="dialog"
      title="修改订单状态"
      positive-text="确定"
      negative-text="取消"
      @positive-click="handleUpdateStatus"
    >
      <n-form :model="statusForm" label-placement="left" label-width="80">
        <n-form-item label="当前状态">
          <n-tag :type="getStatusInfo(currentOrder?.status).type">
            {{ getStatusInfo(currentOrder?.status).label }}
          </n-tag>
        </n-form-item>
        <n-form-item label="新状态" required>
          <n-select
            v-model:value="statusForm.newStatus"
            :options="statusOptions.filter((o) => o.value !== '')"
            placeholder="请选择新状态"
          />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, h } from 'vue'
import { SearchOutline } from '@vicons/ionicons5'
import api from '@/utils/api'
import { createDiscreteApi, NButton, NTag } from 'naive-ui'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])

const orders = ref([])
const loading = ref(false)
const showStatusModal = ref(false)
const currentOrder = ref(null)

const searchForm = reactive({
  keyword: '',
  status: ''
})

const statusForm = reactive({
  newStatus: null
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  pageSizes: [10, 20, 50, 100],
  itemCount: 0
})

const STATUS_MAP = {
  paid: { label: '已支付', type: 'success' },
  TICKETED: { label: '已出票', type: 'success' },
  COMPLETED: { label: '已完成', type: 'info' },
  pending: { label: '待支付', type: 'warning' },
  cancelled: { label: '已取消', type: 'default' },
  CANCELLED: { label: '已取消', type: 'default' },
  REFUNDED: { label: '已退款', type: 'default' }
}

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待支付', value: 'pending' },
  { label: '已支付', value: 'paid' },
  { label: '已出票', value: 'TICKETED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已取消', value: 'CANCELLED' },
  { label: '已退款', value: 'REFUNDED' }
]

function getStatusInfo(status) {
  return STATUS_MAP[status] || { label: status, type: 'default' }
}

const visibleColumns = computed(() => [
  { key: 'id', title: '订单号', width: 80 },
  { key: 'movieTitle', title: '电影名称', width: 160 },
  { key: 'userName', title: '用户', width: 100 },
  { key: 'seatNumber', title: '座位', width: 120 },
  { key: 'showTime', title: '放映时间', width: 170, render: (row) => row.showTime ? row.showTime.replace('T', ' ').substring(0, 16) : '' },
  { key: 'totalPrice', title: '金额', width: 100, render: (row) => h('span', { style: { color: '#ff9f1c', fontWeight: '700' } }, '¥' + Number(row.totalPrice || 0).toFixed(2)) },
  { key: 'status', title: '状态', width: 100, render: (row) => { const info = getStatusInfo(row.status); return h(NTag, { type: info.type, size: 'small', bordered: false }, () => info.label) } },
  { key: 'createTime', title: '下单时间', width: 170, render: (row) => row.createTime ? row.createTime.replace('T', ' ').substring(0, 16) : '' },
  { title: '操作', key: 'actions', width: 180, fixed: 'right', render: (row) => h('div', { style: { display: 'flex', gap: '6px' } }, [
    h(NButton, { size: 'small', type: 'info', text: true, onClick: () => openStatusModal(row) }, () => '修改状态'),
    h(NButton, { size: 'small', type: 'error', text: true, onClick: () => handleDelete(row) }, () => '删除')
  ]) }
])

function openStatusModal(row) {
  currentOrder.value = row
  statusForm.newStatus = null
  showStatusModal.value = true
}

async function handleUpdateStatus() {
  if (!statusForm.newStatus) {
    message.warning('请选择新状态')
    return false
  }
  try {
    await api.put('/orders/' + currentOrder.value.id, { status: statusForm.newStatus })
    message.success('状态更新成功')
    showStatusModal.value = false
    fetchOrders()
    return true
  } catch {
    return false
  }
}

function handleDelete(row) {
  dialog.warning({
    title: '确认删除',
    content: '确定要删除订单 #' + row.id + ' 吗？此操作不可恢复。',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await api.delete('/orders/' + row.id)
        message.success('删除成功')
        fetchOrders()
      } catch {}
    }
  })
}

function handlePageSizeChange(sz) {
  pagination.pageSize = sz
  pagination.page = 1
  fetchOrders()
}

async function fetchOrders() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.status) params.status = searchForm.status
    const res = await api.get('/orders/admin', { params })
    orders.value = res.data?.records || []
    pagination.itemCount = res.data?.total || 0
  } catch {}
  loading.value = false
}

function refresh() {
  pagination.page = 1
  fetchOrders()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.status = ''
  refresh()
}

onMounted(fetchOrders)
</script>

<style scoped>
.manage-page { max-width: 1280px; }
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.search-card {
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  margin-bottom: 24px;
}
</style>
