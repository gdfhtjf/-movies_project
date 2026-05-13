<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>忘记密码</h2>

      <!-- 第一步：输入用户名 -->
      <template v-if="step === 1">
        <p class="step-hint">请输入您的用户名，我们将为您生成密码重置链接。</p>
        <n-form>
          <n-form-item label="用户名">
            <n-input
              v-model:value="username"
              placeholder="请输入用户名"
              size="large"
              @keyup.enter="sendResetRequest"
            />
          </n-form-item>
          <n-form-item>
            <n-button type="error" size="large" :loading="sending" block @click="sendResetRequest">
              发送重置请求
            </n-button>
          </n-form-item>
        </n-form>
      </template>

      <!-- 第二步：输入 token 和新密码 -->
      <template v-if="step === 2">
        <p class="step-hint">
          重置令牌已生成。请输入收到的令牌和新密码。
        </p>
        <n-form>
          <n-form-item label="重置令牌">
            <n-input
              v-model:value="token"
              placeholder="请输入重置令牌"
              size="large"
            />
          </n-form-item>
          <n-form-item label="新密码">
            <n-input
              v-model:value="newPassword"
              type="password"
              placeholder="请输入新密码"
              size="large"
              show-password-on="click"
              @keyup.enter="resetPassword"
            />
          </n-form-item>
          <n-form-item>
            <n-button type="error" size="large" :loading="resetting" block @click="resetPassword">
              重置密码
            </n-button>
          </n-form-item>
        </n-form>
      </template>

      <!-- 完成状态 -->
      <template v-if="step === 3">
        <div class="done-state">
          <span class="done-icon">✅</span>
          <p>密码重置成功！</p>
          <n-button type="error" size="large" block @click="$router.push('/login')">
            返回登录
          </n-button>
        </div>
      </template>

      <p v-if="step !== 3" class="switch">
        <router-link to="/login">返回登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
const router = useRouter()

const step = ref(1)
const username = ref('')
const token = ref('')
const newPassword = ref('')
const sending = ref(false)
const resetting = ref(false)

async function sendResetRequest() {
  if (!username.value.trim()) {
    message.warning('请输入用户名')
    return
  }

  sending.value = true
  try {
    const res = await api.post('/auth/forgot-password', {
      name: username.value.trim()
    })
    // 如果后端返回了 token，自动填充（开发模式下简化流程）
    if (res.data?.token) {
      token.value = res.data.token
    }
    message.success('如果账户存在，重置链接已发送')
    step.value = 2
  } catch { /* handled */ }
  sending.value = false
}

async function resetPassword() {
  if (!token.value.trim()) {
    message.warning('请输入重置令牌')
    return
  }
  if (!newPassword.value) {
    message.warning('请输入新密码')
    return
  }
  if (newPassword.value.length < 6) {
    message.warning('密码长度不能少于6位')
    return
  }

  resetting.value = true
  try {
    await api.post('/auth/reset-password', {
      token: token.value.trim(),
      newPassword: newPassword.value
    })
    message.success('密码重置成功，请使用新密码登录')
    step.value = 3
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch { /* handled */ }
  resetting.value = false
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.auth-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  padding: 40px;
  width: 420px;
}

.auth-card h2 {
  text-align: center;
  font-family: var(--font-heading);
  color: var(--color-accent-gold);
  margin-bottom: 24px;
}

.step-hint {
  color: var(--color-text-muted);
  font-size: 0.9rem;
  margin-bottom: 20px;
  text-align: center;
  line-height: 1.6;
}

.switch {
  text-align: center;
  margin-top: 16px;
  color: var(--color-text-muted);
  font-size: 0.9rem;
}

.done-state {
  text-align: center;
  padding: 24px 0;
}

.done-icon {
  font-size: 3rem;
  display: block;
  margin-bottom: 16px;
}

.done-state p {
  color: #ffffff;
  font-size: 1.1rem;
  margin-bottom: 24px;
}
</style>
