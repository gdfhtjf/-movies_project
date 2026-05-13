<template>
  <div class="user-manage">
    <div class="page-header">
      <h3 class="page-title">👥 用户管理</h3>
      <n-space>
        <n-button type="primary" @click="showCreateModal">
          <template #icon><AddOutline /></template>
          添加用户
        </n-button>
        <n-button quaternary @click="refresh">
          <template #icon><RefreshOutline /></template>
          刷新
        </n-button>
      </n-space>
    </div>

    <n-card class="search-card">
      <n-form inline label-placement="left" label-width="auto" :model="searchForm">
        <n-form-item label="关键词">
          <n-input
            v-model:value="searchForm.keyword"
            placeholder="搜索用户名"
            clearable
            @keyup.enter="refresh"
          />
        </n-form-item>
        <n-form-item label="角色">
          <n-select
            v-model:value="searchForm.role"
            placeholder="全部角色"
            :options="roleOptions"
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

    <n-card class="table-card">
      <n-spin :show="loading">
        <n-data-table
          :columns="columns"
          :data="tableData"
          :pagination="pagination"
          :row-key="(r) => r.id"
          :loading="loading"
        />
      </n-spin>
    </n-card>

    <n-modal
      v-model:show="showModal"
      preset="card"
      :title="isEdit ? '编辑用户' : '添加用户'"
      :style="{ width: '500px' }"
      :bordered="false"
    >
      <n-form
        ref="formRef"
        :model="formModel"
        :rules="formRules"
        label-placement="left"
        label-width="80"
        :show-label="true"
      >
        <n-form-item label="用户名" path="name">
          <n-input
            v-model:value="formModel.name"
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </n-form-item>
        <n-form-item v-if="!isEdit" label="密码" path="password">
          <n-input
            v-model:value="formModel.password"
            type="password"
            show-password-on="click"
            placeholder="请输入密码"
          />
        </n-form-item>
        <n-form-item label="角色" path="role">
          <n-select
            v-model:value="formModel.role"
            :options="roleOptions"
            placeholder="请选择角色"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showModal = false">取消</n-button>
          <n-button type="primary" :loading="saving" @click="handleSubmit">
            {{ isEdit ? '保存' : '创建' }}
          </n-button>
        </n-space>
      </template>
    </n-modal>

    <n-modal
      v-model:show="showResetPwdModal"
      preset="dialog"
      title="重置密码"
      positive-text="确认"
      negative-text="取消"
      @positive-click="handleResetPwd"
    >
      <n-form :model="pwdModel" label-placement="left" label-width="80" :show-label="true">
        <n-form-item label="新密码">
          <n-input
            v-model:value="pwdModel.newPassword"
            type="password"
            show-password-on="click"
            placeholder="请输入新密码"
          />
        </n-form-item>
      </n-form>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, h } from 'vue'
import { useRouter } from 'vue-router'
import { AddOutline, RefreshOutline, SearchOutline } from '@vicons/ionicons5'
import { createDiscreteApi, NTag, NButton } from 'naive-ui'
import api from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])
const router = useRouter()
const auth = useAuthStore()

const loading = ref(false)
const saving = ref(false)
const showModal = ref(false)
const showResetPwdModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const resetPwdId = ref(null)

const searchForm = reactive({
  keyword: '',
  role: null,
})

const formRef = ref(null)
const formModel = reactive({
  name: '',
  password: '',
  role: 'user',
})

const pwdModel = reactive({
  newPassword: '',
})

const formRules = {
  name: { required: true, message: '请输入用户名' },
  password: { required: true, message: '请输入密码' },
  role: { required: true, message: '请选择角色' },
}

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  onChange: (page) => { pagination.page = page; fetchData() },
  onUpdatePageSize: (size) => { pagination.page = 1; pagination.pageSize = size; fetchData() },
})

const tableData = ref([])

const roleOptions = [
  { label: '普通用户', value: 'user' },
  { label: '管理员', value: 'admin' },
]

const columns = computed(() => [
  { title: 'ID', key: 'id', width: 80 },
  { title: '用户名', key: 'name', minWidth: 150 },
  { 
    title: '角色', 
    key: 'role', 
    width: 120,
    render: (row) => h(NTag, { type: row.role === 'admin' ? 'error' : 'info', size: 'small' },
      { default: () => row.role === 'admin' ? '管理员' : '普通用户' }
    ),
  },
  { title: '注册时间', key: 'createTime', minWidth: 180 },
  {
    title: '操作', key: 'actions', width: 220, fixed: 'right',
    render: (row) =>
      h('div', { style: 'display:flex;gap:6px' }, [
        h(NButton, {
          size: 'tiny', type: 'info', text: true,
          onClick: () => showEditModal(row)
        }, { default: () => '编辑' }),
        h(NButton, {
          size: 'tiny', type: 'warning', text: true,
          onClick: () => openResetPwdModal(row)
        }, { default: () => '重置密码' }),
        h(NButton, {
          size: 'tiny', type: 'error', text: true,
          onClick: () => handleDelete(row)
        }, { default: () => '删除' }),
      ]),
  },
])

async function fetchData() {
  loading.value = true
  try {
    const params = { page: pagination.page, size: pagination.pageSize }
    if (searchForm.keyword) params.keyword = searchForm.keyword
    const res = await api.get('/users', { params })
    const pageData = res.data || {}
    tableData.value = pageData.records || []
    pagination.itemCount = pageData.total || 0
  } catch (e) {
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function refresh() {
  pagination.page = 1
  fetchData()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.role = null
  refresh()
}

function showCreateModal() {
  isEdit.value = false
  editId.value = null
  Object.assign(formModel, { name: '', password: '', role: 'user' })
  showModal.value = true
}

function showEditModal(row) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(formModel, { name: row.name, password: '', role: row.role })
  showModal.value = true
}

function openResetPwdModal(row) {
  resetPwdId.value = row.id
  pwdModel.newPassword = ''
  showResetPwdModal.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await api.put(`/users/${editId.value}`, { name: formModel.name, role: formModel.role })
      message.success('更新成功')
    } else {
      const formData = new URLSearchParams()
      formData.append('name', formModel.name)
      formData.append('password', formModel.password)
      formData.append('role', formModel.role)
      await api.post('/auth/register', formData, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      })
      message.success('创建成功')
    }
    showModal.value = false
    fetchData()
  } catch (e) {
    message.error(e.response?.data?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

async function handleResetPwd() {
  if (!pwdModel.newPassword) {
    message.warning('请输入新密码')
    return false
  }
  try {
    await api.put(`/users/${resetPwdId.value}/admin-reset-password`, {
      newPassword: pwdModel.newPassword,
    })
    message.success('密码重置成功')
    return true
  } catch (e) {
    message.error(e.response?.data?.message || '重置密码失败')
    return false
  }
}

function handleDelete(row) {
  if (row.role === 'admin' && auth.user?.id === row.id) {
    message.warning('不能删除自己')
    return
  }
  dialog.warning({
    title: '确认删除',
    content: `确定要删除用户"${row.name}"吗？此操作不可恢复。`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await api.delete(`/users/${row.id}`)
        message.success('删除成功')
        fetchData()
      } catch (e) {
        message.error(e.response?.data?.message || '删除失败')
      }
    },
  })
}

fetchData()
</script>

<style scoped>
.user-manage {
  max-width: 1200px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 1.5rem;
  color: #ff9f1c;
  margin: 0;
}

.search-card, .table-card {
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  margin-bottom: 16px;
  background: #1a1a1a;
}
</style>
