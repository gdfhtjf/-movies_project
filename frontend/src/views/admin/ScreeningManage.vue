<template>
  <div class="manage-page">
    <div class="page-header">
      <h3 class="page-title" style="border-bottom:none; margin-bottom:0">📅 场次管理</h3>
      <n-space>
        <n-button type="error" @click="addModalOpen = true">
          <template #icon><AddOutline /></template>
          添加场次
        </n-button>
      </n-space>
    </div>

    <div class="search-row">
      <n-input v-model:value="filters.movieKeyword" placeholder="搜索电影" clearable style="width:180px" @keyup.enter="doSearch">
        <template #prefix><n-icon><SearchOutline /></n-icon></template>
      </n-input>
      <n-input v-model:value="filters.hallNumber" placeholder="影厅号" clearable style="width:130px" @keyup.enter="doSearch">
        <template #prefix><n-icon><LocationOutline /></n-icon></template>
      </n-input>
      <n-button type="error" @click="doSearch">查询</n-button>
      <n-button @click="resetFilters">重置</n-button>
    </div>

    <div class="toolbar">
      <BatchBar :checked-keys="checkedKeys" @clear="checkedKeys = []">
        <template #actions>
          <n-button size="small" type="error" @click="batchDelete">批量删除</n-button>
        </template>
      </BatchBar>
      <div class="toolbar-right">
        <ColumnToggle :columns="allColumns" :active-keys="activeColKeys" @update:active-keys="onColChange" />
        <n-button size="small" quaternary @click="exportData">
          <template #icon><DownloadOutline /></template>
          导出CSV
        </n-button>
      </div>
    </div>

    <n-data-table
      :columns="visibleColumns"
      :data="sortedScreenings"
      :loading="loading"
      :row-key="(r) => r.id"
      :checked-row-keys="checkedKeys"
      :bordered="false"
      striped
      @update:checked-row-keys="onCheck"
      @update:sorter="handleSorterChange"
    />

    <div style="display:flex; justify-content:flex-end; margin-top:16px">
      <n-pagination
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :item-count="pagination.itemCount"
        :page-sizes="pagination.pageSizes"
        show-size-picker
        show-quick-jumper
        @update:page="fetchScreenings"
        @update:page-size="handlePageSizeChange"
      />
    </div>

    <n-modal v-model:show="addModalOpen" preset="card" title="添加场次" style="width:620px" :mask-closable="false">
      <n-form :model="addForm" label-placement="left" label-width="90">
        <n-grid :cols="2" :x-gap="12">
          <n-gi>
            <n-form-item label="电影">
              <n-select v-model:value="addForm.movieId" placeholder="选择电影" filterable :options="movieOptions" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="影厅号">
              <n-input v-model:value="addForm.hallNumber" placeholder="1号厅" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="开始时间">
              <n-date-picker v-model:value="addForm.startTime" type="datetime" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="结束时间">
              <n-date-picker v-model:value="addForm.endTime" type="datetime" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="票价(¥)">
              <n-input-number v-model:value="addForm.price" :min="0" :precision="2" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="座位数">
              <n-input-number v-model:value="addForm.totalSeats" :min="1" style="width:100%" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="场次状态" style="margin-top:8px">
          <n-select v-model:value="addForm.status" :options="statusOptions" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="addModalOpen = false">取消</n-button>
          <n-button type="error" :loading="addSubmitting" @click="submitAdd">添加</n-button>
        </n-space>
      </template>
    </n-modal>

    <n-modal v-model:show="editModalOpen" preset="card" title="编辑场次" style="width:620px" :mask-closable="false">
      <n-form :model="editForm" label-placement="left" label-width="90">
        <n-grid :cols="2" :x-gap="12">
          <n-gi>
            <n-form-item label="电影">
              <n-select v-model:value="editForm.movieId" filterable :options="movieOptions" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="影厅号">
              <n-input v-model:value="editForm.hallNumber" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="开始时间">
              <n-date-picker v-model:value="editForm.startTime" type="datetime" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="结束时间">
              <n-date-picker v-model:value="editForm.endTime" type="datetime" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="票价(¥)">
              <n-input-number v-model:value="editForm.price" :min="0" :precision="2" style="width:100%" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="座位数">
              <n-input-number v-model:value="editForm.totalSeats" :min="1" style="width:100%" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="场次状态" style="margin-top:8px">
          <n-select v-model:value="editForm.status" :options="statusOptions" />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="editModalOpen = false">取消</n-button>
          <n-button type="error" :loading="editSubmitting" @click="submitEdit">保存</n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, h } from 'vue'
import api from '@/utils/api'
import { createDiscreteApi, NButton, NTag } from 'naive-ui'
import { AddOutline, DownloadOutline, SearchOutline, LocationOutline } from '@vicons/ionicons5'
import { exportToCSV } from '@/utils/exportCSV'
import BatchBar from '@/components/business/BatchBar.vue'
import ColumnToggle from '@/components/business/ColumnToggle.vue'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])

const screenings = ref([])
const loading = ref(false)
const checkedKeys = ref([])
const addModalOpen = ref(false)
const addSubmitting = ref(false)
const sortState = ref(null)

const sortedScreenings = computed(() => {
  const data = [...screenings.value]
  if (!sortState.value || !sortState.value.order) return data
  const { columnKey, order } = sortState.value
  return data.sort((a, b) => {
    const va = a[columnKey]
    const vb = b[columnKey]
    if (columnKey === 'price') {
      return order === 'ascend' ? (va ?? 0) - (vb ?? 0) : (vb ?? 0) - (va ?? 0)
    }
    if (columnKey === 'startTime') {
      return order === 'ascend'
        ? new Date(va || 0) - new Date(vb || 0)
        : new Date(vb || 0) - new Date(va || 0)
    }
    return order === 'ascend'
      ? String(va || '').localeCompare(String(vb || ''), 'zh')
      : String(vb || '').localeCompare(String(va || ''), 'zh')
  })
})

function handleSorterChange(sorter) {
  sortState.value = sorter
}
const editModalOpen = ref(false)
const editSubmitting = ref(false)
const editingRow = ref(null)
const movieOptions = ref([])

const filters = reactive({ movieKeyword: '', hallNumber: '' })

const statusOptions = [
  { label: '可售', value: 'AVAILABLE' },
  { label: '已满', value: 'FULL' },
  { label: '已取消', value: 'CANCELLED' },
  { label: '已结束', value: 'FINISHED' },
]

const STATUS_MAP = {
  AVAILABLE: { label: '可售', type: 'success' },
  FULL: { label: '已售罄', type: 'error' },
  CANCELLED: { label: '已取消', type: 'default' },
  FINISHED: { label: '已结束', type: 'info' },
}

const addForm = reactive({
  movieId: null, hallNumber: '', startTime: null, endTime: null,
  price: null, totalSeats: 100, status: 'AVAILABLE',
})

const editForm = reactive({
  movieId: null, hallNumber: '', startTime: null, endTime: null,
  price: null, totalSeats: 100, status: 'AVAILABLE',
})

const pagination = reactive({
  page: 1, pageSize: 10,
  pageSizes: [10, 20, 50, 100],
  itemCount: 0,
})

const allColumns = [
  { key: 'movieTitle', title: '电影', width: 160 },
  { key: 'hallNumber', title: '影厅', width: 90, sorter: true },
  { key: 'startTime', title: '开始时间', width: 170, sorter: true },
  { key: 'endTime', title: '结束时间', width: 170 },
  { key: 'price', title: '票价(¥)', width: 90, sorter: true },
  { key: 'status', title: '状态', width: 90 },
  { key: 'remainingSeats', title: '剩余/总', width: 100 },
  { key: 'actions', title: '操作', width: 140 },
]

const activeColKeys = ref(allColumns.map((c) => c.key))

const visibleColumns = computed(() =>
  allColumns
    .filter((c) => activeColKeys.value.includes(c.key))
    .map((col) => {
      if (col.key === 'startTime' || col.key === 'endTime') {
        col.render = (row) => (row[col.key] ? row[col.key].replace('T', ' ') : '')
      }
      if (col.key === 'price') {
        col.render = (row) => h('span', { class: 'price-tag' }, '¥' + Number(row.price || 0).toFixed(2))
      }
      if (col.key === 'status') {
        col.render = (row) => {
          const info = STATUS_MAP[row.status] || { label: row.status, type: 'default' }
          return h(NTag, { type: info.type, size: 'small', bordered: false }, () => info.label)
        }
      }
      if (col.key === 'remainingSeats') {
        col.render = (row) => {
          const ratio = row.totalSeats > 0 ? row.remainingSeats / row.totalSeats : 1
          const warn = ratio < 0.2
          return h('span', {
            style: { color: warn ? '#ff9f1c' : '#e0e0e0' }
          }, `${row.remainingSeats}/${row.totalSeats}`)
        }
      }
      if (col.key === 'actions') {
        col.render = (row) =>
          h('div', { style: 'display:flex;gap:6px' }, [
            h(NButton, { size: 'tiny', type: 'primary', text: true, onClick: () => editRow(row) }, { default: () => '编辑' }),
            h(NButton, { size: 'tiny', type: 'error', text: true, onClick: () => deleteRow(row) }, { default: () => '删除' }),
          ])
      }
      return col
    })
)

function onCheck(keys) { checkedKeys.value = keys }

function handlePageSizeChange(sz) {
  pagination.pageSize = sz
  pagination.page = 1
  fetchScreenings()
}

function formatDateForApi(d) {
  if (!d) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function fetchScreenings() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.pageSize }
    if (filters.movieKeyword) params.keyword = filters.movieKeyword
    if (filters.hallNumber) params.hallNumber = filters.hallNumber
    const res = await api.get('/screenings', { params })
    screenings.value = res.data?.records || []
    pagination.itemCount = res.data?.total || 0
  } catch { /* handled */ }
  loading.value = false
}

async function fetchMovieOptions() {
  try {
    const res = await api.get('/movies', { params: { page: 1, size: 100 } })
    movieOptions.value = (res.data?.records || []).map((m) => ({
      label: m.title, value: m.id,
    }))
  } catch { /* handled */ }
}

function doSearch() { pagination.page = 1; fetchScreenings() }
function resetFilters() { Object.assign(filters, { movieKeyword: '', hallNumber: '' }); doSearch() }

async function submitAdd() {
  if (!addForm.movieId || !addForm.hallNumber || !addForm.startTime || !addForm.endTime) {
    message.warning('请填写电影、影厅号、开始时间、结束时间'); return
  }
  addSubmitting.value = true
  try {
    await api.post('/screenings', {
      movieId: addForm.movieId, hallNumber: addForm.hallNumber,
      startTime: formatDateForApi(addForm.startTime),
      endTime: formatDateForApi(addForm.endTime),
      price: addForm.price, totalSeats: addForm.totalSeats,
      status: addForm.status,
    })
    message.success('添加成功')
    addModalOpen.value = false
    Object.assign(addForm, { movieId: null, hallNumber: '', startTime: null, endTime: null, price: null, totalSeats: 100, status: 'AVAILABLE' })
    fetchScreenings()
  } catch { /* handled */ }
  addSubmitting.value = false
}

function editRow(row) {
  editingRow.value = row
  Object.assign(editForm, {
    movieId: row.movieId, hallNumber: row.hallNumber,
    startTime: row.startTime ? new Date(row.startTime) : null,
    endTime: row.endTime ? new Date(row.endTime) : null,
    price: row.price, totalSeats: row.totalSeats, status: row.status || 'AVAILABLE',
  })
  editModalOpen.value = true
}

async function submitEdit() {
  editSubmitting.value = true
  try {
    await api.put(`/screenings/${editingRow.value.id}`, {
      movieId: editForm.movieId, hallNumber: editForm.hallNumber,
      startTime: formatDateForApi(editForm.startTime),
      endTime: formatDateForApi(editForm.endTime),
      price: editForm.price, totalSeats: editForm.totalSeats,
      status: editForm.status,
    })
    message.success('更新成功')
    editModalOpen.value = false
    fetchScreenings()
  } catch { /* handled */ }
  editSubmitting.value = false
}

async function deleteRow(row) {
  dialog.warning({
    title: '删除场次',
    content: '确定要删除这个场次吗？',
    positiveText: '删除', negativeText: '取消',
    onPositiveClick: async () => {
      try { await api.delete(`/screenings/${row.id}`); message.success('已删除'); fetchScreenings() } catch { /* handled */ }
    },
  })
}

async function batchDelete() {
  if (!checkedKeys.value.length) { message.warning('请先选择场次'); return }
  dialog.warning({
    title: '批量删除',
    content: `确定要删除选中的 ${checkedKeys.value.length} 个场次吗？`,
    positiveText: '删除', negativeText: '取消',
    onPositiveClick: async () => {
      for (const id of checkedKeys.value) { try { await api.delete(`/screenings/${id}`) } catch { /* continue */ } }
      message.success('批量删除完成'); checkedKeys.value = []; fetchScreenings()
    },
  })
}

function exportData() {
  if (!screenings.value.length) { message.warning('没有数据可导出'); return }
  exportToCSV('场次列表', allColumns.filter((c) => c.key !== 'actions'), screenings.value)
  message.success('导出成功')
}

function onColChange(keys) { activeColKeys.value = keys }

onMounted(() => { fetchScreenings(); fetchMovieOptions() })
</script>

<style scoped>
.manage-page { max-width: 1280px; }

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-row {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}

.toolbar { margin-bottom: 12px; }

.toolbar-right {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-bottom: 8px;
}
</style>
