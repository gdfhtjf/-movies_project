<template>
  <div v-if="checkedKeys.length" class="batch-bar">
    <span class="batch-count">已选 {{ checkedKeys.length }} 项</span>
    <n-space>
      <slot name="actions" />
      <n-button size="small" @click="$emit('clear')">取消选择</n-button>
    </n-space>
  </div>
</template>

<script setup>
defineProps({
  checkedKeys: { type: Array, default: () => [] },
})

defineEmits(['clear'])
</script>

<style scoped>
.batch-bar {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 16px; background: rgba(244,162,97,0.08);
  border: 1px solid rgba(244,162,97,0.2); border-radius: 8px;
  margin-bottom: 16px;
}

.batch-count { font-weight: 600; color: var(--color-accent-gold); font-size: 0.9rem; }
</style>
