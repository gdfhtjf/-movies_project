<template>
  <div class="skeleton-card">
    <div class="sk-img shimmer"></div>
    <div class="sk-body">
      <div class="sk-line shimmer w-70"></div>
      <div class="sk-line shimmer w-50"></div>
      <div class="sk-line shimmer w-40"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  count: { type: Number, default: 1 },
})
</script>

<style scoped>
.skeleton-card {
  background: var(--color-bg-card); border: 1px solid var(--color-border);
  border-radius: 12px; overflow: hidden;
}

.sk-img { width: 100%; padding-bottom: 140%; }

.sk-body { padding: 14px 16px 18px; display: flex; flex-direction: column; gap: 10px; }

.sk-line { height: 14px; border-radius: 4px; }

.w-70 { width: 70%; }
.w-50 { width: 50%; }
.w-40 { width: 40%; }

.shimmer {
  background: linear-gradient(90deg, var(--color-bg-input) 25%, rgba(255,255,255,0.03) 50%, var(--color-bg-input) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s ease-in-out infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
</style>
