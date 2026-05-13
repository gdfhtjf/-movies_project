<template>
  <div class="favorites-page">
    <div class="page-header">
      <h2 class="page-title">💖 我的收藏</h2>
    </div>

    <div v-if="loading" class="loading-container">
      <n-spin size="large" />
    </div>

    <div v-else-if="movies.length === 0" class="empty-state">
      <div class="empty-icon">🎬</div>
      <h3 class="empty-title">暂无收藏</h3>
      <p class="empty-desc">快去发现喜欢的电影并收藏吧！</p>
      <n-button type="primary" @click="$router.push('/')">去发现</n-button>
    </div>

    <div v-else class="movie-grid">
      <div
        v-for="(movie, index) in movies"
        :key="movie.id"
        class="movie-card"
        @click="goToDetail(movie.id)"
      >
        <div class="movie-card-img-wrap">
          <img
            :src="getMoviePoster(movie.title, movie.posterPath)"
            :alt="movie.title"
          />
          <div class="movie-card-overlay">
            <n-button type="primary" size="small">
              查看详情
            </n-button>
          </div>
        </div>
        <div class="movie-card-body">
          <h4 class="movie-card-title">{{ movie.title }}</h4>
          <div class="card-meta">
            <span class="card-genre">{{ movie.genre || '未分类' }}</span>
            <span class="card-dot">·</span>
            <span class="card-duration">{{ movie.duration || 120 }}分钟</span>
          </div>
          <div class="card-bottom">
            <span class="card-price">
              <span class="price-currency">¥</span>
              <span class="price-num">{{ Number(movie.price || 0).toFixed(0) }}</span>
            </span>
            <n-button
              type="error"
              size="small"
              text
              @click.stop="removeFromFavorites(movie.id)"
            >
              取消收藏
            </n-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="movies.length > 0" class="pagination-wrap">
      <n-pagination
        v-model:page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :item-count="pagination.itemCount"
        :page-sizes="[12, 24, 48]"
        show-size-picker
        show-quick-jumper
        @update:page="loadFavorites"
        @update:page-size="loadFavorites"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/api'
import { getMoviePoster } from '@/utils/poster'
import { useMessage } from 'naive-ui'

const router = useRouter()
const message = useMessage()

const movies = ref([])
const loading = ref(false)

const pagination = reactive({
  page: 1,
  pageSize: 12,
  itemCount: 0
})

async function loadFavorites() {
  loading.value = true
  try {
    const res = await api.get('/favorites', {
      params: {
        page: pagination.page,
        size: pagination.pageSize
      }
    })
    movies.value = res.data?.records || []
    pagination.itemCount = res.data?.total || 0
  } catch (error) {
    console.error('加载收藏失败:', error)
    message.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

async function removeFromFavorites(movieId) {
  try {
    await api.delete('/favorites/' + movieId)
    message.success('已取消收藏')
    await loadFavorites()
  } catch (error) {
    console.error('取消收藏失败:', error)
    message.error('取消收藏失败')
  }
}

function goToDetail(movieId) {
  router.push('/detail/' + movieId)
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorites-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(135deg, #ffffff 0%, #ff6b9d 50%, #e50914 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 0;
}

.empty-state {
  text-align: center;
  padding: 100px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.7;
}

.empty-title {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.empty-desc {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  margin: 0 0 24px 0;
}

.movie-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  background: #1a1a1a;
  border: 1px solid rgba(255, 255, 255, 0.06);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.movie-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(229, 9, 20, 0.2), 0 8px 24px rgba(0, 0, 0, 0.3);
  border-color: rgba(229, 9, 20, 0.3);
}

.movie-card:active {
  transform: translateY(-4px) scale(0.99);
}

.movie-card-img-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 2 / 3;
  overflow: hidden;
}

.movie-card-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.movie-card:hover .movie-card-img-wrap img {
  transform: scale(1.08);
}

.movie-card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.7) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.movie-card:hover .movie-card-overlay {
  opacity: 1;
}

.movie-card-body {
  padding: 14px 16px 16px;
}

.movie-card-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 12px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
}

.card-genre {
  color: rgba(255, 255, 255, 0.55);
}

.card-dot {
  color: rgba(255, 255, 255, 0.2);
}

.card-duration {
  color: rgba(255, 255, 255, 0.45);
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-currency {
  color: #ff9f1c;
  font-size: 13px;
  font-weight: 600;
}

.price-num {
  color: #ff9f1c;
  font-size: 22px;
  font-weight: 800;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 32px;
}

@media (max-width: 1200px) {
  .movie-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }
}

@media (max-width: 992px) {
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .favorites-page {
    padding: 24px 16px;
  }
  .movie-grid {
    gap: 16px;
  }
}
</style>
