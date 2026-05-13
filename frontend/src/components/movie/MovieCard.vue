<template>
  <div class="movie-card" @click="handleClick" role="button" :aria-label="`查看 ${movie.title} 详情`" tabindex="0" @keydown.enter="handleClick">
    <div class="card-poster">
      <img :src="posterUrl" :alt="`${movie.title} 海报`" loading="lazy" @error="handleImgError" />
    </div>
    <div class="card-info">
      <h4 class="card-title">{{ movie.title }}</h4>
      <div class="card-meta">
        <span class="card-genre" v-if="movie.genre">{{ movie.genre }}</span>
        <span class="card-duration" v-if="movie.duration">{{ movie.duration }}分钟</span>
      </div>
      <div class="card-footer">
        <span class="card-price">¥{{ movie.price }}</span>
        <span class="card-action">查看详情</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { getMoviePoster } from '@/utils/poster'

const props = defineProps({
  movie: { type: Object, required: true }
})

const router = useRouter()

const posterUrl = computed(() => getMoviePoster(props.movie.title, props.movie.posterPath))

function handleClick() {
  router.push(`/detail/${props.movie.id}`)
}

function handleImgError(e) {
  e.target.src = 'https://picsum.photos/400/600?random'
}
</script>

<style scoped>
.movie-card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s var(--ease-out-quart), box-shadow 0.25s var(--ease-out-quart), border-color 0.25s var(--ease-out-quart);
}

.movie-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--color-accent);
}

.movie-card:focus-visible {
  outline: 2px solid var(--color-accent);
  outline-offset: 2px;
}

.card-poster {
  width: 100%;
  height: 280px;
  overflow: hidden;
  position: relative;
}

.card-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s var(--ease-out-quart);
}

.movie-card:hover .card-poster img {
  transform: scale(1.05);
}

.card-info {
  padding: var(--space-md);
}

.card-title {
  margin: 0 0 var(--space-sm) 0;
  font-size: var(--text-lg);
  color: var(--color-text);
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  gap: var(--space-sm);
  margin-bottom: 10px;
}

.card-genre, .card-duration {
  font-size: var(--text-xs);
  color: var(--color-text-muted);
  padding: 2px 8px;
  background: rgba(255,255,255,0.06);
  border-radius: 4px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  font-size: var(--text-lg);
  font-weight: 700;
  color: var(--color-accent);
}

.card-action {
  font-size: var(--text-sm);
  color: var(--color-accent-gold);
}
</style>
