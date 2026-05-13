<template>
  <div class="movie-list-page">
    <div class="section-container">
      <h2 class="section-title">🍿 全部电影</h2>

      <div class="genre-tabs">
        <button
          v-for="g in genres"
          :key="g"
          :class="{ active: activeGenre === g }"
          class="genre-btn"
          @click="activeGenre = g; page = 1; fetchMovies()"
        >
          {{ g }}
        </button>
      </div>

      <div v-if="movies.length" class="movie-grid">
        <n-card
          v-for="movie in movies"
          :key="movie.id"
          class="movie-card"
          hoverable
          @click="$router.push(`/detail/${movie.id}`)"
        >
          <div class="movie-card-img-wrap">
            <img
              :src="getMoviePoster(movie.title, movie.posterPath)"
              :alt="movie.title"
            />
            <div class="movie-card-overlay">
              <n-button type="error" size="small">立即购票</n-button>
            </div>
          </div>
          <div class="movie-card-body">
            <h4 class="movie-card-title">{{ movie.title }}</h4>
            <p class="movie-card-genre">{{ movie.genre }}</p>
            <p class="movie-card-price">¥{{ Number(movie.price || 0).toFixed(0) }}</p>
          </div>
        </n-card>
      </div>

      <div v-else-if="!loading" class="empty-state">
        <div class="icon">🎬</div>
        <h3>暂无电影</h3>
        <p>当前分类下没有找到电影</p>
      </div>

      <n-spin v-if="loading" style="display:flex; justify-content:center; padding:40px" />

      <div v-if="totalPages > 1" style="display:flex; justify-content:center; margin-top:32px">
        <n-pagination
          v-model:page="page"
          :page-size="pageSize"
          :item-count="total"
          show-quick-jumper
          @update:page="fetchMovies"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'
import { getMoviePoster } from '@/utils/poster'

const movies = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalPages = ref(1)
const activeGenre = ref('全部')

const genres = ['全部', '动作', '科幻', '奇幻', '剧情', '喜剧', '犯罪', '冒险', '动画', '爱情']

async function fetchMovies() {
  loading.value = true
  try {
    const params = { page: page.value, size: pageSize.value }
    if (activeGenre.value !== '全部') params.genre = activeGenre.value
    const res = await api.get('/movies', { params })
    movies.value = res.data?.records || []
    total.value = res.data?.total || 0
    totalPages.value = res.data?.pages || 1
  } catch { /* handled */ }
  loading.value = false
}

onMounted(fetchMovies)
</script>

<style scoped>
.movie-list-page {
  min-height: calc(100vh - 64px);
  background: #0f0f0f;
}

.section-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

.section-title {
  font-family: var(--font-heading);
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 20px;
}

.genre-tabs {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 32px;
}

.genre-btn {
    background: none;
    border: 1px solid #555;
    color: #e0e0e0;
    padding: 6px 16px;
    border-radius: 20px;
    font-size: 0.85rem;
    cursor: pointer;
    transition: all 0.2s;
  }

.genre-btn:hover { border-color: #e50914; color: #e50914; background: #1a1a1a; }
.genre-btn:active { transform: scale(0.95); }

.genre-btn.active {
  background: #e50914;
  border-color: #e50914;
  color: #fff;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.movie-card {
  cursor: pointer;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  overflow: hidden;
  background: #1a1a1a;
  transition: transform 0.3s, box-shadow 0.3s;
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(229, 9, 20, 0.15);
}

.movie-card-img-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 2/3;
  overflow: hidden;
}

.movie-card-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.movie-card:hover .movie-card-img-wrap img {
  transform: scale(1.05);
}

.movie-card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-card:hover .movie-card-overlay { opacity: 1; }

.movie-card-body { padding: 12px 14px; }

.movie-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.movie-card-genre {
    color: #e0e0e0;
    font-size: 12px;
    margin-bottom: 6px;
  }

.movie-card-price {
  color: #ff9f1c;
  font-size: 16px;
  font-weight: 700;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-state .icon { font-size: 4rem; margin-bottom: 16px; }
  .empty-state h3 { font-size: 1.2rem; color: #e0e0e0; }
  .empty-state p { color: #e0e0e0; }

@media (max-width: 992px) {
  .movie-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 576px) {
  .movie-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
