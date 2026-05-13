<template>
  <div class="filter-bar">
    <n-space align="center" :wrap="true">
      <slot name="filters" />
      <n-button size="small" type="primary" @click="$emit('search')">
        <template #icon><SearchOutline /></template>
        查询
      </n-button>
      <n-button size="small" type="info" @click="$emit('reset')">重置</n-button>
      <n-button size="small" type="default" @click="$emit('toggleAdvanced')">
        {{ advancedOpen ? '收起' : '高级' }}
      </n-button>
    </n-space>
    <n-collapse-transition :show="advancedOpen">
      <div class="advanced-area">
        <slot name="advanced" />
      </div>
    </n-collapse-transition>
  </div>
</template>

<script setup>
import { SearchOutline } from '@vicons/ionicons5'

defineProps({
  advancedOpen: { type: Boolean, default: false },
})

defineEmits(['search', 'reset', 'toggleAdvanced'])
</script>

<style scoped>
.filter-bar { margin-bottom: 16px; }
.advanced-area { padding: 12px 0; border-top: 1px solid var(--color-border); margin-top: 8px; }
</style>
