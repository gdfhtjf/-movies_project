<template>
  <div class="lazy-img-wrap" :style="{ aspectRatio: aspectRatio || '2/3' }">
    <img
      v-if="loaded"
      :src="realSrc"
      :alt="alt"
      class="lazy-img"
      :class="{ loaded: loaded }"
      @error="onError"
    />
    <div v-else class="lazy-placeholder">
      <div class="shimmer"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'

const props = defineProps({
  src: { type: String, required: true },
  alt: { type: String, default: '' },
  aspectRatio: { type: String, default: '2/3' },
  fallback: { type: String, default: '' },
})

const imgRef = ref(null)
const loaded = ref(false)
const realSrc = ref('')
const hasIntersected = ref(false)

const { stop } = useIntersectionObserver(
  imgRef,
  ([{ isIntersecting }]) => {
    if (isIntersecting && !hasIntersected.value) {
      hasIntersected.value = true
      realSrc.value = props.src
      stop()
    }
  },
  { threshold: 0, rootMargin: '300px' }
)

watch(realSrc, (url) => {
  if (!url) return
  const img = new Image()
  img.onload = () => {
    loaded.value = true
  }
  img.onerror = () => {
    if (props.fallback && realSrc.value !== props.fallback) {
      realSrc.value = props.fallback
      const fb = new Image()
      fb.onload = () => { loaded.value = true }
      fb.src = props.fallback
    } else {
      // 如果没有fallback，直接显示原图让浏览器处理
      loaded.value = true
    }
  }
  img.src = url
})

function onError() {
  if (props.fallback && realSrc.value !== props.fallback) {
    realSrc.value = props.fallback
  }
}
</script>

<style scoped>
.lazy-img-wrap {
  position: relative;
  overflow: hidden;
  background: #1a1a1a;
}

.lazy-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.lazy-img.loaded {
  opacity: 1;
}

.lazy-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.shimmer {
  width: 40%;
  height: 60%;
  background: linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.06) 50%, transparent 100%);
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}
</style>
