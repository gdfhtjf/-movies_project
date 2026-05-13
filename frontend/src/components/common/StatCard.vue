<template>
  <div class="stat-card">
    <div class="stat-icon-wrapper">
      <span class="stat-icon">{{ icon }}</span>
    </div>
    <div class="stat-body">
      <span class="stat-title">{{ title }}</span>
      <span class="stat-value">{{ value }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '📊' },
  title: { type: String, default: '统计项' },
  value: { type: [String, Number], default: '—' },
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #1a1a1a;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(229, 9, 20, 0.08);
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  font-size: 1.4rem;
  line-height: 1;
}

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-title {
  font-size: 14px;
  font-weight: 400;
  color: #e0e0e0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  line-height: 1.2;
}
</style>
