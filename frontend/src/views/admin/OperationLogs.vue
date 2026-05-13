<template>
  <div class="manage-page">
    <div class="page-header">
      <h3 class="page-title">📋 操作日志</h3>
    </div>

    <n-card class="search-card">
      <n-form inline label-placement="left" label-width="auto">
        <n-form-item label="搜索">
          <n-input
            v-model:value="keyword"
            placeholder="按用户名或URI搜索"
            clearable
            @keyup.enter="refresh"
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
      :columns="columns"
      :data="logs"
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
        @update:page="fetchLogs"
        @update:page-size="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { SearchOutline } from '@vicons/ionicons5'
import api from '@/utils/api'
import { NTag } from 'naive-ui'

const logs = ref([])
const loading = ref(false)
const keyword = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 20,
  itemCount: 0,
  pageSizes: [10, 20, 50, 100],
})

const columns = [
  { title: '用户名', key: 'username', width: 120, ellipsis: { tooltip: true } },
  { title: 'HTTP方法', key: 'method', width: 90 },
  { title: 'URI', key: 'uri', width: 220, ellipsis: { tooltip: true } },
  { title: '操作类型', key: 'operationType', width: 80 },
  { title: 'IP', key: 'ip', width: 140 },
  { title: '参数数', key: 'argsCount', width: 80 },
  { title: '耗时(ms)', key: 'elapsedMs', width: 100 },
  {
    title: '状态',
    key: 'status',
    width: 90,
    render(row) {
      const isSuccess = row.status === '成功'
      return h(NTag, { type: isSuccess ? 'success' : 'error', size: 'small' }, () => row.status)
    },
  },
  { title: '时间', key: 'createTime', width: 170 },
]

function resetSearch() {
  keyword.value = ''
  refresh()
}

function refresh() {
  pagination.page = 1
  fetchLogs()
}

function handlePageSizeChange() {
  pagination.page = 1
  fetchLogs()
}

async function fetchLogs() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.pageSize }
    if (keyword.value) {
      params.keyword = keyword.value
    }
    const res = await api.get('/operation-logs', { params })
    const data = res.data || {}
    logs.value = data.records || []
    pagination.itemCount = data.total || 0
  } catch {
    logs.value = []
  }
  loading.value = false
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.manage-page {
  max-width: 1400px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #e0e0e0;
  border-bottom: 2px solid #e50914;
  display: inline-block;
  padding-bottom: 8px;
}

.search-card {
  margin-bottom: 16px;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
}
</style>
