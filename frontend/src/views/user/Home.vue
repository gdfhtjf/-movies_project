<template>
  <div class="home">
    <!-- Hero 轮播区域 -->
    <div class="hero-banner" v-if="featuredMovies.length">
      <n-carousel
        v-model:current-index="currentSlide"
        :interval="5000"
        :autoplay="true"
        dot-type="line"
        :loop="true"
        :show-dot="true"
        :show-arrow="true"
        draggable
        class="hero-carousel"
      >
        <div
          v-for="(movie, idx) in featuredMovies"
          :key="movie.id"
          class="hero-slide"
        >
          <div class="hero-bg">
            <img
              :src="getHeroPoster(movie.title, movie.posterPath)"
              :alt="movie.title"
            />
            <div class="hero-overlay"></div>
          </div>
          
          <div class="hero-content-wrapper">
            <div class="hero-left">
              <div class="hero-poster-wrap">
                <img
                  :src="getMoviePoster(movie.title, movie.posterPath)"
                  :alt="movie.title"
                  class="hero-poster"
                />
                <div class="hot-badge">🔥 正在热映</div>
              </div>
            </div>
            
            <div class="hero-right">
              <h1 class="hero-title">{{ movie.title }}</h1>
              <div class="hero-meta-tags">
                <span class="meta-tag">{{ movie.genre || '暂无分类' }}</span>
                <span class="meta-tag">{{ movie.director || '知名导演' }}</span>
                <span class="meta-tag">{{ movie.duration || 120 }}分钟</span>
              </div>
              <p class="hero-desc" v-if="movie.description">
                {{ movie.description.length > 80 ? movie.description.slice(0, 80) + '...' : movie.description }}
              </p>
              <div class="hero-bottom">
                <div class="hero-price-wrap">
                  <span class="price-label">票价</span>
                  <span class="price-value">¥{{ Number(movie.price || 0).toFixed(0) }}</span>
                  <span class="price-suffix">起</span>
                </div>
                <n-button type="error" size="large" class="hero-cta" @click="$router.push(`/detail/${movie.id}`)">
                  <template #icon>
                    <n-icon><TicketOutline /></n-icon>
                  </template>
                  查看详细
                </n-button>
              </div>
            </div>
          </div>
        </div>
      </n-carousel>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 搜索和分类区域 -->
      <div class="section-header">
        <div class="header-left">
          <h2 class="section-title">🎬 发现好片</h2>
          <p class="section-subtitle">探索正在热映的精彩电影</p>
        </div>
        
        <div class="search-filter-wrap">
          <div class="search-bar">
            <n-input
              v-model:value="searchQuery"
              type="text"
              placeholder="搜索电影名称..."
              clearable
              size="large"
              @keydown.enter="doSearch"
              @clear="searchQuery = ''"
            >
              <template #prefix>
                <n-icon><SearchOutline /></n-icon>
              </template>
            </n-input>
          </div>
          
          <n-select
            v-model:value="sortBy"
            :options="sortOptions"
            size="large"
            style="width: 140px;"
          />
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="genre-tabs">
        <button
          v-for="g in genres"
          :key="g.value"
          :class="{ active: activeGenre === g.value }"
          class="genre-btn"
          @click="activeGenre = g.value"
        >
          <span class="genre-icon">{{ g.icon }}</span>
          <span class="genre-text">{{ g.label }}</span>
        </button>
      </div>

      <!-- 搜索结果提示 -->
      <div class="search-info" v-if="searchQuery">
        <span class="search-icon">🔍</span>
        搜索 <span class="search-highlight">{{ searchQuery }}</span>，找到 <span class="count-highlight">{{ displayedMovies.length }}</span> 部电影
      </div>

      <!-- 电影网格 -->
      <div v-if="displayedMovies.length" class="movie-grid">
        <div
          v-for="(movie, idx) in sortedMovies"
          :key="movie.id"
          class="movie-card"
          @click="$router.push(`/detail/${movie.id}`)"
          :style="{ animationDelay: `${idx * 0.05}s` }"
        >
          <div class="movie-card-img-wrap">
            <img
              :src="getMoviePoster(movie.title, movie.posterPath)"
              :alt="movie.title"
            />
            <div class="movie-card-overlay">
              <n-button type="error" size="small">
                <template #icon>
                  <n-icon><TicketOutline /></n-icon>
                </template>
                立即购票
              </n-button>
            </div>
            
            <!-- 热映标签 -->
            <div class="card-badge hot">🔥 热映</div>
            
            <!-- 评分标签 -->
            <div class="card-rating" v-if="movie.rating">
              <span class="star">⭐</span>
              <span class="rating-value">{{ movie.rating }}</span>
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
              <span class="card-cta">查看详情 →</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">{{ searchQuery ? '🔍' : '🎬' }}</div>
        <h3 class="empty-title">{{ searchQuery ? '未找到相关电影' : '暂无电影' }}</h3>
        <p class="empty-desc">{{ searchQuery ? '请尝试其他关键词' : '管理员尚未添加任何电影' }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { SearchOutline, TicketOutline } from '@vicons/ionicons5'
import api from '@/utils/api'
import { getMoviePoster, getHeroPoster } from '@/utils/poster'

const allMovies = ref([])
const activeGenre = ref('全部')
const currentSlide = ref(0)
const searchQuery = ref('')
const sortBy = ref('hot')

const genres = [
  { value: '全部', label: '全部', icon: '🎬' },
  { value: '动作', label: '动作', icon: '💥' },
  { value: '科幻', label: '科幻', icon: '🚀' },
  { value: '奇幻', label: '奇幻', icon: '✨' },
  { value: '剧情', label: '剧情', icon: '📖' },
  { value: '喜剧', label: '喜剧', icon: '😂' },
  { value: '犯罪', label: '犯罪', icon: '🔍' },
  { value: '冒险', label: '冒险', icon: '🗺️' },
  { value: '动画', label: '动画', icon: '🎨' },
  { value: '爱情', label: '爱情', icon: '💕' }
]

const sortOptions = [
  { label: '热度排序', value: 'hot' },
  { label: '价格最低', value: 'price_asc' },
  { label: '价格最高', value: 'price_desc' },
  { label: '时长排序', value: 'duration' }
]

const HERO_MOVIE_NAMES = ['我不是药神', '哪吒之魔童降世', '孤注一掷', '流浪地球3', '飞驰人生3']

const featuredMovies = computed(() => {
  const nameSet = new Set(HERO_MOVIE_NAMES)
  const picked = allMovies.value.filter(m => nameSet.has(m.title))
  return HERO_MOVIE_NAMES.map(name => picked.find(m => m.title === name)).filter(Boolean)
})

const filteredMovies = computed(() => {
  if (activeGenre.value === '全部') return allMovies.value
  return allMovies.value.filter(m =>
    m.genre && m.genre.includes(activeGenre.value)
  )
})

const displayedMovies = computed(() => {
  let result = filteredMovies.value
  if (searchQuery.value.trim()) {
    const q = searchQuery.value.trim().toLowerCase()
    result = result.filter(m => m.title && m.title.toLowerCase().includes(q))
  }
  return result
})

const sortedMovies = computed(() => {
  let movies = [...displayedMovies.value]
  if (sortBy.value === 'hot') {
    movies = movies.sort((a, b) => (b.rating || 0) - (a.rating || 0))
  } else if (sortBy.value === 'price_asc') {
    movies = movies.sort((a, b) => (a.price || 0) - (b.price || 0))
  } else if (sortBy.value === 'price_desc') {
    movies = movies.sort((a, b) => (b.price || 0) - (a.price || 0))
  } else if (sortBy.value === 'duration') {
    movies = movies.sort((a, b) => (b.duration || 0) - (a.duration || 0))
  }
  return movies
})

function doSearch() {
  // 搜索由 computed 自动响应
}

onMounted(async () => {
  try {
    const res = await api.get('/movies', { params: { page: 1, size: 50 } })
    allMovies.value = res.data?.records || []
  } catch { /* handled */ }
})
</script>

<style scoped>
.home {
  position: relative;
  background: #0f0f0f;
  z-index: 2;
  min-height: 100vh;
}

/* Hero Banner */
.hero-banner {
  position: relative;
  width: 100%;
  height: 620px;
  overflow: hidden;
}

.hero-carousel {
  position: absolute;
  inset: 0;
  z-index: 0;
  height: 100%;
}

.hero-carousel :deep(.n-carousel__arrow) {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.3s;
  z-index: 6;
}

.hero-carousel :deep(.n-carousel__arrow:hover) {
  background: rgba(229, 9, 20, 0.8);
  transform: scale(1.1);
}

.hero-carousel :deep(.n-carousel__dots) {
  bottom: 24px;
}

.hero-carousel :deep(.n-carousel__dot) {
  width: 40px;
  height: 4px;
  border-radius: 2px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s;
}

.hero-carousel :deep(.n-carousel__dot.is-active) {
  background: #e50914;
  width: 60px;
}

.hero-slide {
  position: relative;
  height: 620px;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-bg img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 20%;
  image-rendering: auto;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  z-index: 1;
  background: linear-gradient(
    90deg,
    rgba(15, 15, 15, 0.9) 0%,
    rgba(15, 15, 15, 0.7) 35%,
    rgba(15, 15, 15, 0.4) 55%,
    rgba(15, 15, 15, 0.8) 100%
  );
}

.hero-content-wrapper {
  position: relative;
  z-index: 3;
  height: 100%;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 60px;
}

.hero-left {
  flex-shrink: 0;
}

.hero-poster-wrap {
  position: relative;
  width: 320px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.6);
}

.hero-poster {
  width: 100%;
  aspect-ratio: 2/3;
  object-fit: cover;
  display: block;
}

.hot-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #e50914, #ff4d4d);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(229, 9, 20, 0.4);
}

.hero-right {
  flex: 1;
  max-width: 600px;
}

.hero-title {
  font-family: var(--font-heading);
  font-size: 52px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 16px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.8);
  letter-spacing: 1px;
  line-height: 1.2;
}

.hero-meta-tags {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.meta-tag {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

.hero-desc {
  color: rgba(255, 255, 255, 0.8);
  font-size: 16px;
  line-height: 1.7;
  margin-bottom: 32px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.hero-bottom {
  display: flex;
  align-items: center;
  gap: 24px;
}

.hero-price-wrap {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.price-label {
  color: rgba(255, 255, 255, 0.6);
  font-size: 14px;
}

.price-value {
  color: #ff9f1c;
  font-size: 44px;
  font-weight: 800;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);
}

.price-suffix {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
}

.hero-cta {
  padding: 14px 48px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(229, 9, 20, 0.4);
  transition: all 0.3s;
}

.hero-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(229, 9, 20, 0.5);
}

/* Main Content */
.main-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 48px 24px;
  position: relative;
  z-index: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 24px;
}

.header-left {
  flex: 1;
  min-width: 0;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 8px 0;
}

.section-subtitle {
  color: rgba(255, 255, 255, 0.5);
  font-size: 14px;
  margin: 0;
}

.search-filter-wrap {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-bar {
  width: 320px;
}

.search-bar :deep(.n-input) {
  --n-height: 44px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  transition: all 0.3s;
}

.search-bar :deep(.n-input:hover) {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(229, 9, 20, 0.3);
}

.search-bar :deep(.n-input-focus) {
  background: rgba(255, 255, 255, 0.08);
  border-color: #e50914;
  box-shadow: 0 0 0 3px rgba(229, 9, 20, 0.15);
}

/* Genre Tabs */
.genre-tabs {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 32px;
}

.genre-btn {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.75);
  padding: 10px 18px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.25s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.genre-btn:hover {
  border-color: rgba(229, 9, 20, 0.5);
  color: #fff;
  background: rgba(229, 9, 20, 0.08);
  transform: translateY(-1px);
}

.genre-btn:active {
  transform: scale(0.98);
}

.genre-btn.active {
  background: linear-gradient(135deg, #e50914, #c40812);
  border-color: transparent;
  color: #fff;
  box-shadow: 0 6px 16px rgba(229, 9, 20, 0.35);
}

.genre-icon {
  font-size: 16px;
}

/* Search Info */
.search-info {
  background: rgba(255, 159, 28, 0.1);
  border: 1px solid rgba(255, 159, 28, 0.2);
  padding: 12px 20px;
  border-radius: 12px;
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
}

.search-icon {
  font-size: 18px;
}

.search-highlight,
.count-highlight {
  color: #ff9f1c;
  font-weight: 600;
}

/* Movie Grid */
.movie-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 24px;
}

.movie-card {
  cursor: pointer;
  border-radius: 16px;
  overflow: hidden;
  background: #1a1a1a;
  border: 1px solid rgba(255, 255, 255, 0.06);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0;
  animation: fadeInUp 0.5s forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.movie-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 16px 48px rgba(229, 9, 20, 0.2), 0 8px 24px rgba(0, 0, 0, 0.3);
  border-color: rgba(229, 9, 20, 0.3);
  background: linear-gradient(180deg, #1a1a1a 0%, #161616 100%);
}

.movie-card:active {
  transform: translateY(-6px) scale(0.99);
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

.card-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #e50914, #ff4d4d);
  color: white;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  z-index: 2;
  box-shadow: 0 4px 10px rgba(229, 9, 20, 0.35);
}

.card-rating {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  color: white;
  padding: 5px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.star {
  font-size: 14px;
}

.rating-value {
  color: #ff9f1c;
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

.card-cta {
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
  transition: color 0.2s;
}

.movie-card:hover .card-cta {
  color: #e50914;
}

/* Empty State */
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
  margin: 0;
}

/* Responsive */
@media (max-width: 1200px) {
  .hero-banner,
  .hero-slide {
    height: 540px;
  }
  
  .movie-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }
  
  .hero-content-wrapper {
    gap: 48px;
  }
  
  .hero-poster-wrap {
    width: 280px;
  }
  
  .hero-title {
    font-size: 42px;
  }
  
  .price-value {
    font-size: 38px;
  }
}

@media (max-width: 992px) {
  .hero-banner,
  .hero-slide {
    height: 420px;
  }
  
  .hero-content-wrapper {
    flex-direction: column;
    text-align: center;
    padding-top: 80px;
  }
  
  .hero-poster-wrap {
    width: 160px;
  }
  
  .hero-right {
    max-width: 100%;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .hero-meta-tags {
    justify-content: center;
  }
  
  .hero-bottom {
    justify-content: center;
  }
  
  .movie-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .search-filter-wrap {
    width: 100%;
  }
  
  .search-bar {
    flex: 1;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .hero-banner,
  .hero-slide {
    height: 380px;
  }
  
  .hero-poster-wrap {
    width: 140px;
  }
  
  .hero-title {
    font-size: 24px;
  }
  
  .price-value {
    font-size: 28px;
  }
  
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .genre-tabs {
    gap: 8px;
  }
  
  .genre-btn {
    padding: 8px 14px;
    font-size: 13px;
  }
  
  .main-content {
    padding: 32px 16px;
  }
}

@media (max-width: 576px) {
  .hero-banner,
  .hero-slide {
    height: 340px;
  }
  
  .search-filter-wrap {
    flex-direction: column;
  }
  
  .search-bar {
    width: 100%;
  }
  
  .search-filter-wrap .n-select {
    width: 100% !important;
  }
  
  .section-title {
    font-size: 22px;
  }
  
  .movie-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-cta {
    padding: 12px 36px;
  }
}
</style>
