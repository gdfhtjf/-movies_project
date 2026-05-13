<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>注册</h2>
      <n-form :model="form">
        <n-form-item label="用户名">
          <n-input v-model:value="form.name" placeholder="请输入用户名" size="large" />
        </n-form-item>
        <n-form-item label="密码">
          <n-input
v-model:value="form.password" type="password" placeholder="请输入密码" size="large"
            show-password-on="click" />
        </n-form-item>
        <n-form-item>
          <n-button type="error" size="large" :loading="loading" block @click="doRegister">注册</n-button>
        </n-form-item>
      </n-form>
      <p class="switch">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])
const auth = useAuthStore()
const router = useRouter()
const loading = ref(false)
const form = reactive({ name: '', password: '' })

async function doRegister() {
  if (!form.name || !form.password) { message.warning('请填写完整'); return }
  loading.value = true
  try {
    await auth.register(form.name, form.password)
    message.success('注册成功，请登录')
    router.push('/login')
  } catch { /* handled */ }
  loading.value = false
}
</script>

<style scoped>
.auth-page { display: flex; justify-content: center; align-items: center; min-height: 70vh; }
.auth-card { background: var(--color-bg-card); border: 1px solid var(--color-border); border-radius: 16px; padding: 40px; width: 400px; }
.auth-card h2 { text-align: center; font-family: var(--font-heading); color: var(--color-accent-gold); margin-bottom: 32px; }
.switch { text-align: center; margin-top: 16px; color: var(--color-text-muted); font-size: 0.9rem; }
</style>
