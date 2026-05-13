<template>
  <div class="movie-card" @click="handleClick">
    <div class="card-poster">
      <img :src="posterUrl" :alt="movie.title" @error="handleImgError" />
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
  console.log('点击了电影卡片，ID:', props.movie.id, '标题:', props.movie.title)
  router.push(`/detail/${props.movie.id}`)
}

function handleImgError(e) {
  e.target.src = 'https://picsum.photos/400/600?random'
}
</script>

<style scoped>
.movie-card {
  background: #1c1c1c;
  border: 1px solid #333;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.movie-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
  border-color: #e50914;
}

.card-poster {
  width: 100%;
  height: 280px;
  overflow: hidden;
}

.card-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-info {
  padding: 12px;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #fff;
  font-weight: 600;
}

.card-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.card-genre, .card-duration {
  font-size: 12px;
  color: #999;
  padding: 2px 8px;
  background: #2a2a2a;
  border-radius: 4px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  font-size: 18px;
  font-weight: 700;
  color: #e50914;
}

.card-action {
  font-size: 14px;
  color: #ffb74d;
}
</style>
