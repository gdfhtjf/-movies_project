<template>
  <n-popover trigger="click" placement="bottom-end">
    <template #trigger>
      <n-button size="small" quaternary>
        <template #icon><SettingsOutline /></template>
        列设置
      </n-button>
    </template>
    <div class="col-toggle-panel">
      <n-checkbox-group :value="activeKeys" @update:value="onChange">
        <div v-for="col in columns" :key="col.key" class="col-check-item">
          <n-checkbox :value="col.key" :label="col.title || col.key" />
        </div>
      </n-checkbox-group>
    </div>
  </n-popover>
</template>

<script setup>
import { SettingsOutline } from '@vicons/ionicons5'

const emit = defineEmits(['update:activeKeys'])

defineProps({
  columns: { type: Array, required: true },
  activeKeys: { type: Array, required: true },
})

function onChange(keys) {
  emit('update:activeKeys', keys)
}
</script>

<style scoped>
.col-toggle-panel { min-width: 160px; padding: 4px 0; }
.col-check-item { padding: 4px 8px; }
</style>
