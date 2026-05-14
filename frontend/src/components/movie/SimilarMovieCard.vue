<template>
  <div 
    class="similar-movie-card"
    :class="{ 'card-loading': loading }"
    @click="handleClick"
  >
    <!-- 骨架屏加载状态 -->
    <div v-if="loading" class="card-skeleton">
      <div class="skeleton-poster"></div>
      <div class="skeleton-info">
        <div class="skeleton-title"></div>
        <div class="skeleton-meta"></div>
      </div>
    </div>

    <!-- 正常内容 -->
    <template v-else>
      <div class="card-poster">
        <img 
          :src="posterUrl" 
          :alt="movie.title"
          @error="handleImageError"
          loading="lazy"
        />
        <div class="poster-overlay">
          <span class="play-icon">▶</span>
        </div>
      </div>
      
      <div class="card-content">
        <h4 class="card-title">{{ movie.title }}</h4>
        
        <div v-if="movie.genre" class="card-genre">
          {{ movie.genre }}
        </div>

        <div class="card-footer">
          <span class="card-price">¥{{ movie.price }}</span>
          <n-button 
            type="error" 
            size="small" 
            text
            @click.stop="handleClick"
          >
            查看详情
          </n-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getMoviePoster, getMovieFallback } from '@/utils/poster'

const props = defineProps({
  movie: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const router = useRouter()

const posterUrl = computed(() => {
  return getMoviePoster(props.movie.title, props.movie.posterPath)
})

function handleImageError(e) {
  e.target.src = getMovieFallback(props.movie.title)
}

function handleClick() {
  if (props.loading || !props.movie?.id) return
  
  router.push(`/detail/${props.movie.id}`)
}

defineExpose({
  handleClick
})
</script>

<style scoped>
.similar-movie-card {
  background: #1c1c1c;
  border: 1px solid #333;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
}

.similar-movie-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(229, 9, 20, 0.2);
  border-color: #e50914;
}

/* 骨架屏样式 */
.card-skeleton {
  padding: 0;
}

.skeleton-poster {
  width: 100%;
  height: 280px;
  background: linear-gradient(90deg, #2a2a2a 25%, #3a3a3a 50%, #2a2a2a 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

.skeleton-info {
  padding: 12px;
}

.skeleton-title {
  width: 70%;
  height: 20px;
  background: linear-gradient(90deg, #2a2a2a 25%, #3a3a3a 50%, #2a2a2a 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 8px;
}

.skeleton-meta {
  width: 40%;
  height: 16px;
  background: linear-gradient(90deg, #2a2a2a 25%, #3a3a3a 50%, #2a2a2a 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
}

@keyframes skeleton-loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 正常卡片样式 */
.card-poster {
  position: relative;
  width: 100%;
  height: 280px;
  overflow: hidden;
}

.card-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.similar-movie-card:hover .card-poster img {
  transform: scale(1.1);
}

.poster-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.similar-movie-card:hover .poster-overlay {
  opacity: 1;
}

.play-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(229, 9, 20, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  box-shadow: 0 4px 20px rgba(229, 9, 20, 0.5);
}

.card-content {
  padding: 14px;
}

.card-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-genre {
  font-size: 12px;
  color: #ffb74d;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  font-size: 20px;
  font-weight: 700;
  color: #e50914;
}

/* 响应式 */
@media (max-width: 768px) {
  .card-poster {
    height: 220px;
  }
}
</style>
