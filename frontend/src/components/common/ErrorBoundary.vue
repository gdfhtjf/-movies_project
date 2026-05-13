<template>
  <div v-if="hasError" class="error-boundary">
    <div class="error-boundary-content">
      <n-icon size="48" color="var(--color-error, #d03050)">
        <AlertCircleOutline />
      </n-icon>
      <h2>页面出现错误</h2>
      <p class="error-message">{{ error?.message || '未知错误' }}</p>
      <n-button type="primary" @click="resetError">重试</n-button>
    </div>
  </div>
  <slot v-else />
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'
import { NIcon, NButton } from 'naive-ui'
import { AlertCircleOutline } from '@vicons/ionicons5'

const hasError = ref(false)
const error = ref(null)

onErrorCaptured((err, instance, info) => {
  console.error('[ErrorBoundary] 捕获到错误:', err, info)
  error.value = err
  hasError.value = true
  return false // 阻止错误继续传播
})

function resetError() {
  hasError.value = false
  error.value = null
}

defineExpose({ resetError })
</script>

<style scoped>
.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 2rem;
}
.error-boundary-content {
  text-align: center;
  max-width: 400px;
}
.error-boundary-content h2 {
  margin: 1rem 0 0.5rem;
  font-size: 1.25rem;
  color: var(--color-text, #fff);
}
.error-message {
  color: var(--color-text-muted, #999);
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  word-break: break-all;
}
</style>
