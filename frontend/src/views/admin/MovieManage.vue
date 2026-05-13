<template>
  <div class="manage-page">
    <div class="page-header">
      <h3 class="page-title" style="border-bottom:none; margin-bottom:0">🎬 电影管理</h3>
      <n-space>
        <n-button type="error" @click="router.push('/admin/movies/add')">
          <template #icon><AddOutline /></template>
          添加电影
        </n-button>
      </n-space>
    </div>

    <div class="search-row">
      <n-input
        v-model:value="filters.search"
        placeholder="搜索电影名称..."
        clearable
        style="width:300px"
        @keyup.enter="doSearch"
      >
        <template #prefix>
          <n-icon><SearchOutline /></n-icon>
        </template>
      </n-input>
      <n-button type="error" @click="doSearch">
        <template #icon><SearchOutline /></template>
        查询
      </n-button>
      <n-button @click="resetSearch">重置</n-button>
    </div>

    <div class="toolbar">
      <BatchBar :checked-keys="checkedKeys" @clear="checkedKeys = []">
        <template #actions>
          <n-button size="small" type="error" @click="batchDelete">批量删除</n-button>
        </template>
      </BatchBar>
      <div class="toolbar-right">
        <ColumnToggle
          :columns="allColumns"
          :active-keys="activeColKeys"
          @update:active-keys="onColChange"
        />
        <n-button size="small" quaternary @click="exportData">
          <template #icon><DownloadOutline /></template>
          导出CSV
        </n-button>
      </div>
    </div>

    <n-data-table
      :columns="visibleColumns"
      :data="sortedMovies"
      :loading="loading"
      :row-key="(r) => r.id"
      :checked-row-keys="checkedKeys"
      :row-props="rowProps"
      :bordered="false"
      :single-line="false"
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
        @update:page="fetchMovies"
        @update:page-size="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { createDiscreteApi, NButton, NTag } from 'naive-ui'
import { AddOutline, DownloadOutline, SearchOutline } from '@vicons/ionicons5'
import { exportToCSV } from '@/utils/exportCSV'
import BatchBar from '@/components/business/BatchBar.vue'
import ColumnToggle from '@/components/business/ColumnToggle.vue'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])
const router = useRouter()

const movies = ref([])
const loading = ref(false)
const checkedKeys = ref([])
const sortState = ref(null)

const sortedMovies = computed(() => {
  const data = [...movies.value]
  if (!sortState.value || !sortState.value.order) return data
  const { columnKey, order } = sortState.value
  return data.sort((a, b) => {
    const va = a[columnKey]
    const vb = b[columnKey]
    if (columnKey === 'price') {
      return order === 'ascend' ? (va ?? 0) - (vb ?? 0) : (vb ?? 0) - (va ?? 0)
    }
    return order === 'ascend'
      ? String(va || '').localeCompare(String(vb || ''), 'zh')
      : String(vb || '').localeCompare(String(va || ''), 'zh')
  })
})

function handleSorterChange(sorter) {
  sortState.value = sorter
}

const filters = reactive({ search: '' })

const pagination = reactive({
  page: 1, pageSize: 10,
  pageSizes: [10, 20, 50, 100],
  itemCount: 0,
})

const allColumns = [
  { key: 'title', title: '电影名称', width: 160, sorter: true },
  { key: 'director', title: '导演', width: 100 },
  { key: 'genre', title: '类型', width: 120 },
  { key: 'price', title: '票价(¥)', width: 90, sorter: true },
  { key: 'duration', title: '时长(分)', width: 90 },
  { key: 'releaseDate', title: '上映日期', width: 110 },
  { key: 'stock', title: '库存', width: 70 },
  { key: 'actions', title: '操作', width: 140 },
]

const activeColKeys = ref(allColumns.map((c) => c.key))

const visibleColumns = computed(() =>
  allColumns
    .filter((c) => activeColKeys.value.includes(c.key))
    .map((col) => {
      if (col.key === 'price') {
        col.render = (row) => '¥' + (row.price != null ? Number(row.price).toFixed(2) : '—')
      }
      if (col.key === 'genre') {
        col.render = (row) => {
          if (!row.genre) return ''
          const tags = row.genre.split(',')
          return h('span', { style: 'display:flex;gap:4px;flex-wrap:wrap' },
            tags.map(t => h(NTag, {
              size: 'small', type: 'info', bordered: false,
            }, () => t.trim()))
          )
        }
      }
      if (col.key === 'actions') {
        col.render = (row) =>
          h('div', { style: 'display:flex;gap:6px' }, [
            h(NButton, {
              size: 'tiny', type: 'primary', text: true,
              onClick: () => router.push(`/admin/movies/edit/${row.id}`)
            }, { default: () => '编辑' }),
            h(NButton, {
              size: 'tiny', type: 'error', text: true,
              onClick: () => deleteRow(row)
            }, { default: () => '删除' }),
          ])
      }
      return col
    })
)

function rowProps(row) {
  return { style: row.id % 2 === 0 ? 'background:#1a1a1a' : '' }
}

function onCheck(keys) { checkedKeys.value = keys }

function handlePageSizeChange(sz) {
  pagination.pageSize = sz
  pagination.page = 1
  fetchMovies()
}

async function fetchMovies() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.pageSize }
    if (filters.search) params.keyword = filters.search
    const res = await api.get('/movies', { params })
    movies.value = res.data?.records || []
    pagination.itemCount = res.data?.total || 0
  } catch { /* handled */ }
  loading.value = false
}

function doSearch() { pagination.page = 1; fetchMovies() }
function resetSearch() { filters.search = ''; doSearch() }

function onColChange(keys) { activeColKeys.value = keys }

async function deleteRow(row) {
  dialog.warning({
    title: '删除电影',
    content: `确定要删除《${row.title}》吗？删除后将无法恢复。`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await api.delete(`/movies/${row.id}`)
        message.success('已删除')
        fetchMovies()
      } catch { /* handled */ }
    },
  })
}

async function batchDelete() {
  if (!checkedKeys.value.length) { message.warning('请先选择电影'); return }
  dialog.warning({
    title: '批量删除',
    content: `确定要删除选中的 ${checkedKeys.value.length} 部电影吗？`,
    positiveText: '删除',
    negativeText: '取消',
    onPositiveClick: async () => {
      for (const id of checkedKeys.value) {
        try { await api.delete(`/movies/${id}`) } catch { /* continue */ }
      }
      message.success('批量删除完成')
      checkedKeys.value = []
      fetchMovies()
    },
  })
}

function exportData() {
  if (!movies.value.length) { message.warning('没有数据可导出'); return }
  exportToCSV('电影列表', allColumns.filter(c => c.key !== 'actions'), movies.value)
  message.success('导出成功')
}

onMounted(fetchMovies)
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

.toolbar {
  margin-bottom: 12px;
}

.toolbar-right {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-bottom: 8px;
}
</style>
