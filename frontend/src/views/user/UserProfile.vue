<template>
  <div class="profile-page">
    <h3 class="page-title">👤 个人设置</h3>

    <n-card class="profile-card">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerUpload">
          <n-avatar
            :size="100"
            :src="avatarUrl || undefined"
            round
            class="profile-avatar"
          >
            <span v-if="!avatarUrl" class="avatar-placeholder">👤</span>
          </n-avatar>
          <div class="avatar-overlay">
            <span class="upload-icon">📷</span>
          </div>
        </div>
        <input
          ref="fileInputRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
        <p class="avatar-hint">点击更换头像</p>
      </div>

      <!-- 表单 -->
      <n-form ref="formRef" :model="form" label-placement="left" label-width="80">
        <n-form-item label="用户名">
          <n-input v-model:value="form.name" placeholder="请输入用户名" size="large" />
        </n-form-item>
        <n-form-item label="邮箱">
          <n-input v-model:value="form.email" placeholder="请输入邮箱" size="large" />
        </n-form-item>
        <n-form-item label="手机号">
          <n-input v-model:value="form.phone" placeholder="请输入手机号" size="large" />
        </n-form-item>
        <n-form-item>
          <n-button type="error" size="large" :loading="saving" block @click="saveProfile">保存修改</n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { useAuthStore } from '@/stores/auth'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
const auth = useAuthStore()
const router = useRouter()
const saving = ref(false)
const fileInputRef = ref(null)
const avatarUrl = ref('')

const form = reactive({
  name: '',
  email: '',
  phone: ''
})

function triggerUpload() {
  fileInputRef.value?.click()
}

async function handleAvatarChange(e) {
  const file = e.target.files?.[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await api.post('/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    avatarUrl.value = res.data?.url || `/uploads/${res.data}`
    message.success('头像上传成功')
  } catch { /* handled */ }
  // reset input
  if (fileInputRef.value) fileInputRef.value.value = ''
}

async function saveProfile() {
  if (!form.name.trim()) {
    message.warning('用户名不能为空')
    return
  }

  saving.value = true
  try {
    const userId = auth.user?.id
    await api.put(`/users/${userId}/profile`, {
      name: form.name.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
      avatar: avatarUrl.value || undefined
    })
    message.success('资料保存成功')
    await auth.fetchMe()
    router.push('/')
  } catch { /* handled */ }
  saving.value = false
}

onMounted(() => {
  if (auth.user) {
    form.name = auth.user.name || ''
    form.email = auth.user.email || ''
    form.phone = auth.user.phone || ''
    avatarUrl.value = auth.user.avatar || ''
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 800;
  color: var(--color-accent-gold);
  border: none;
  margin-bottom: 32px;
  padding: 0;
}

.profile-card {
  border: 1px solid #2d2d2d;
  border-radius: 16px;
  background: linear-gradient(145deg, #1a1a1a 0%, #161616 100%);
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #2a2a2a;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  transition: transform 0.2s;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.profile-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder {
  font-size: 2.5rem;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.upload-icon {
  font-size: 1.5rem;
}

.avatar-hint {
  color: #e0e0e0;
  font-size: 0.85rem;
  margin-top: 10px;
}
</style>
