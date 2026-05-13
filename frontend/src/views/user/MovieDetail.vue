<template>
  <div class="movie-detail">
    <n-spin :show="loading" size="large">
      <!-- 错误状态 -->
      <div v-if="error" class="empty-state error-state">
        <div class="icon">⚠️</div>
        <h3>加载失败</h3>
        <p>{{ error }}</p>
        <n-button type="error" @click="retry">重新加载</n-button>
      </div>

      <!-- 电影不存在 -->
      <div v-else-if="!movie && !loading" class="empty-state">
        <div class="icon">😕</div>
        <h3>电影不存在</h3>
      </div>

      <!-- 正常内容 -->
      <template v-else-if="movie">
        <!-- 头部信息 -->
        <div class="detail-hero">
          <div class="detail-poster">
            <img :src="getMoviePoster(movie.title, movie.posterPath)" :alt="movie.title" />
            <div class="poster-actions">
              <button class="fav-btn" :class="{ active: favorited }" @click="toggleFav">
                <span class="fav-icon">{{ favorited ? '❤️' : '🤍' }}</span>
                <span>{{ favorited ? '已收藏' : '收藏' }}</span>
              </button>
            </div>
          </div>
          <div class="detail-info">
            <h1>{{ movie.title }}</h1>
            <div class="tags">
              <n-tag v-if="movie.genre" type="error">{{ movie.genre }}</n-tag>
              <n-tag v-if="movie.duration">{{ movie.duration }}分钟</n-tag>
              <n-tag v-if="movie.releaseDate" type="info">{{ formatDate(movie.releaseDate) }}</n-tag>
            </div>
            <div class="meta-item"><strong>导演：</strong>{{ movie.director || '暂无信息' }}</div>
            <div v-if="movie.cast" class="meta-item"><strong>主演：</strong>{{ movie.cast }}</div>
            <div class="price-tag">¥{{ movie.price }}</div>
            <p v-if="movie.description" class="desc">{{ movie.description }}</p>
          </div>
        </div>

        <!-- 放映场次 -->
        <div class="screenings-section">
          <h3 class="section-title">📅 放映场次</h3>
          <div v-if="screenings.length" class="screening-list">
            <div v-for="s in screenings" :key="s.id" class="screening-item">
              <div class="sc-left">
                <div class="sc-date-row">
                  <span class="sc-date">{{ formatScreeningDate(s.showDate || s.startTime) || '日期待定' }}</span>
                  <span class="sc-status-tag" :class="(s.remainingSeats || 0) <= 0 ? 'soldout' : 'on'">
                    {{ (s.remainingSeats || 0) <= 0 ? '售罄' : '放映中' }}
                  </span>
                </div>
                <div class="sc-info-row">
                  <div class="sc-info-item">
                    <span class="sc-label">时间</span>
                    <span class="sc-value time-range">
                      {{ formatShortTime(s.startTime) || '--' }} ~ {{ formatShortTime(s.endTime) || '--' }}
                    </span>
                  </div>
                  <div class="sc-divider"></div>
                  <div class="sc-info-item">
                    <span class="sc-label">影厅</span>
                    <span class="sc-value hall-name">{{ s.hallNumber || '待定' }}</span>
                  </div>
                  <div class="sc-divider"></div>
                  <div class="sc-info-item">
                    <span class="sc-label">票价</span>
                    <span class="sc-value price-val">¥{{ s.price || movie.price || '--' }}</span>
                  </div>
                  <div class="sc-divider"></div>
                  <div class="sc-info-item">
                    <span class="sc-label">余座</span>
                    <span class="sc-value seat-count" :class="{ low: (s.remainingSeats || 0) > 0 && (s.remainingSeats || 0) <= (s.totalSeats || 1) * 0.2 }">
                      {{ s.remainingSeats ?? '--' }}/{{ s.totalSeats ?? '--' }}
                    </span>
                  </div>
                </div>
              </div>
              <n-button type="error" size="large" :disabled="(s.remainingSeats || 0) <= 0" @click="$router.push(`/seat/${s.id}`)">
                {{ (s.remainingSeats || 0) <= 0 ? '已售罄' : '选座购票' }}
              </n-button>
            </div>
          </div>
          <div v-else class="empty-screenings">
            <span class="empty-icon">🎬</span>
            <p>该电影暂无排期，请稍后再来</p>
          </div>
        </div>

        <!-- 预告片 -->
        <div v-if="movie.trailerPath" class="trailer-section">
          <h3 class="section-title">🎬 预告片</h3>
          <div class="video-wrap" @click="togglePlay">
            <video
              ref="videoRef"
              :src="`/api/files/download?file=${movie.trailerPath}`"
              class="trailer-video"
              controls
              preload="metadata"
            >
              您的浏览器不支持视频播放
            </video>
            <div v-if="!playing" class="play-overlay">
              <span class="big-play">▶</span>
            </div>
          </div>
        </div>

        <!-- 相似推荐 - 重构后的版本 -->
        <div class="similar-section">
          <h3 class="section-title">🎯 相似推荐</h3>
          
          <!-- 加载状态 -->
          <div v-if="similarLoading" class="similar-grid">
            <SimilarMovieCard v-for="i in 4" :key="`skeleton-${i}`" :loading="true" />
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="similarError" class="similar-error">
            <div class="error-content">
              <span class="error-icon">😟</span>
              <p>加载推荐失败</p>
              <n-button size="small" @click="loadSimilarMovies">重试</n-button>
            </div>
          </div>
          
          <!-- 正常内容 -->
          <div v-else-if="similarMovies.length" class="similar-grid">
            <SimilarMovieCard 
              v-for="m in similarMovies" 
              :key="m.id" 
              :movie="m"
            />
          </div>
          
          <!-- 无推荐内容 -->
          <div v-else class="similar-empty">
            <span class="empty-icon">🎬</span>
            <p>暂无相似推荐</p>
          </div>
        </div>

        <!-- 短评 -->
        <div class="reviews-section">
          <h3 class="section-title">💬 短评 ({{ reviewList.length }})</h3>
          <div class="review-form">
            <n-input
              v-model:value="reviewText"
              type="textarea"
              :rows="3"
              placeholder="写下你的观影感受..."
              maxlength="500"
              show-count
            />
            <div class="review-form-footer">
              <n-checkbox v-model:checked="isAnonymous">匿名发表</n-checkbox>
              <n-button type="error" :disabled="!reviewText.trim()" @click="submitReview">发表短评</n-button>
            </div>
          </div>
          <div v-if="reviewList.length" class="review-list">
            <div v-for="r in reviewList" :key="r.id" class="review-item">
              <div class="review-header">
                <span class="review-user">{{ r.userName }}</span>
                <span class="review-time">{{ formatTimeFromTs(r.time) }}</span>
              </div>
              <p class="review-text">{{ r.text }}</p>
            </div>
          </div>
          <div v-else class="review-empty">暂无短评，来抢沙发吧</div>
        </div>
      </template>
    </n-spin>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import { useReview } from '@/composables/useReview'
import { useSimilarMovies } from '@/composables/useSimilarMovies'
import { useAuthStore } from '@/stores/auth'
import { createDiscreteApi } from 'naive-ui'
import SimilarMovieCard from '@/components/movie/SimilarMovieCard.vue'
import { getMoviePoster } from '@/utils/poster'

const { message } = createDiscreteApi(['message'])

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

// 核心状态
const movie = ref(null)
const screenings = ref([])
const loading = ref(true)
const error = ref('')
const reviewText = ref('')
const videoRef = ref(null)
const playing = ref(false)
const favorited = ref(false)
const isAnonymous = ref(true)

// 计算属性
const movieId = computed(() => Number(route.params.id))

// 引入功能模块
const { movieReviews, addReview } = useReview(movieId)
const { 
  similarMovies, 
  loading: similarLoading, 
  error: similarError, 
  fetchSimilarMovies 
} = useSimilarMovies()

const reviewList = computed(() => movieReviews.value)

// 监听路由变化，重新加载数据
watch(() => route.params.id, (newId) => {
  if (newId) {
    loadData()
    reviewText.value = ''
  }
})

// 检查收藏状态
async function checkFavorite() {
  try {
    const res = await api.get(`/favorites/${movieId.value}/check`)
    favorited.value = res.data
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏
async function toggleFav() {
  try {
    if (favorited.value) {
      await api.delete(`/favorites/${movieId.value}`)
      favorited.value = false
      message.success('已取消收藏')
    } else {
      await api.post(`/favorites/${movieId.value}`)
      favorited.value = true
      message.success('已收藏')
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    message.error('操作失败')
  }
}

function togglePlay() {
  const vid = videoRef.value
  if (!vid) return
  if (vid.paused) { 
    vid.play()
    playing.value = true 
  } else { 
    vid.pause()
    playing.value = false 
  }
}

function submitReview() {
  if (!reviewText.value.trim()) return
  const userName = isAnonymous.value ? '匿名用户' : (auth.user?.name || '用户')
  addReview(reviewText.value.trim(), userName)
  reviewText.value = ''
  message.success('短评发表成功')
}

// 日期格式化方法
function formatScreeningDate(d) {
  if (!d) return ''
  const date = new Date(d)
  const today = new Date()
  const tomorrow = new Date(today)
  tomorrow.setDate(tomorrow.getDate() + 1)
  if (date.toDateString() === today.toDateString()) return '今天'
  if (date.toDateString() === tomorrow.toDateString()) return '明天'
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

function formatShortTime(t) {
  if (!t) return ''
  const d = new Date(t)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function formatDate(d) {
  if (!d) return ''
  const date = new Date(d)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function formatTimeFromTs(ts) {
  if (!ts) return ''
  const d = new Date(ts)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 加载相似电影
async function loadSimilarMovies() {
  if (!movie.value) return
  
  console.log('[MovieDetail] 开始加载相似电影...')
  await fetchSimilarMovies(movieId.value, movie.value, 4)
}

// 主数据加载
async function loadData() {
  const id = route.params.id
  loading.value = true
  error.value = ''

  try {
    const [mRes, sRes] = await Promise.all([
      api.get(`/movies/${id}`),
      api.get('/screenings', { params: { movieId: id } })
    ])
    
    movie.value = mRes.data
    const rawScreenings = sRes.data?.records || sRes.data || []
    screenings.value = Array.isArray(rawScreenings) ? rawScreenings : []
    
    // 检查收藏状态
    await checkFavorite()
    
    // 电影详情加载成功后，再加载相似推荐
    if (movie.value) {
      await loadSimilarMovies()
    }
  } catch (err) {
    error.value = err?.response?.data?.message || err?.message || '加载电影详情失败，请稍后重试'
    message.error(error.value)
  } finally {
    loading.value = false
  }
}

function retry() {
  error.value = ''
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.movie-detail { 
  max-width: 1100px; 
  margin: 0 auto; 
  padding: 32px 24px; 
  color: #ffffff; 
}

.detail-hero { 
  display: flex; 
  gap: 40px; 
  margin-bottom: 48px; 
}

.detail-poster { 
  flex-shrink: 0; 
}

.detail-poster img { 
  width: 320px; 
  border-radius: 12px; 
  display: block; 
}

.poster-actions { 
  margin-top: 12px; 
}

.fav-btn {
  display: flex; 
  align-items: center; 
  gap: 6px;
  width: 100%; 
  padding: 10px; 
  border-radius: 8px;
  border: 1px solid #555;
  background: #1c1c1c; 
  color: #ffffff;
  cursor: pointer; 
  font-size: 0.9rem; 
  transition: all 0.25s; 
  outline: none;
}

.fav-btn:hover { 
  border-color: #e50914; 
}

.fav-btn.active { 
  border-color: #e50914; 
  background: rgba(230,57,70,0.1); 
}

.fav-icon { 
  font-size: 1.1rem; 
  transition: transform 0.3s; 
}

.fav-btn:hover .fav-icon { 
  transform: scale(1.2); 
}

.detail-info { 
  color: #ffffff; 
}

.detail-info h1 { 
  font-size: 2.2rem; 
  color: #ffb74d; 
  margin-bottom: 16px; 
}

.tags { 
  display: flex; 
  gap: 8px; 
  margin-bottom: 16px; 
  flex-wrap: wrap; 
}

.meta-item { 
  color: #ffffff; 
  margin: 8px 0; 
  font-size: 0.95rem; 
}

.meta-item strong { 
  color: #e0e0e0; 
}

.price-tag { 
  font-weight: 700; 
  font-size: 1.6rem; 
  color: #e50914; 
  margin: 16px 0; 
}

.desc { 
  color: #e0e0e0; 
  line-height: 1.8; 
  margin-top: 16px; 
}

/* ===== 预告片 ===== */
.trailer-section { 
  margin-bottom: 48px; 
}

.video-wrap {
  position: relative; 
  border-radius: 12px; 
  overflow: hidden;
  background: #000; 
  cursor: pointer;
}

.trailer-video { 
  width: 100%; 
  display: block; 
  border-radius: 12px; 
}

.play-overlay {
  position: absolute; 
  inset: 0;
  display: flex; 
  align-items: center; 
  justify-content: center;
  background: rgba(0,0,0,0.3);
}

.big-play {
  width: 72px; 
  height: 72px; 
  border-radius: 50%;
  background: #e50914;
  display: flex; 
  align-items: center; 
  justify-content: center;
  font-size: 1.4rem; 
  color: #fff;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.45);
  transition: transform 0.2s;
}

.play-overlay:hover .big-play { 
  transform: scale(1.1); 
}

/* ===== 场次 ===== */
.screenings-section { 
  margin-bottom: 48px; 
}

.screening-list { 
  display: flex; 
  flex-direction: column; 
  gap: 16px; 
}

.screening-item {
  display: flex; 
  justify-content: space-between; 
  align-items: center;
  background: #1c1c1c; 
  border: 1px solid #444;
  padding: 20px 28px; 
  border-radius: 12px; 
  gap: 20px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.screening-item:hover { 
  border-color: #e50914; 
  box-shadow: 0 0 20px rgba(229,9,20,0.08); 
}

.sc-left { 
  flex: 1; 
  min-width: 0; 
}

.sc-date-row { 
  display: flex; 
  align-items: center; 
  gap: 8px; 
  margin-bottom: 14px; 
}

.sc-date { 
  color: #ffffff; 
  font-size: 1rem; 
  font-weight: 600; 
}

.sc-status-tag {
  padding: 2px 10px; 
  border-radius: 4px; 
  font-size: 0.72rem; 
  font-weight: 600;
}

.sc-status-tag.on { 
  background: rgba(129,199,132,0.18); 
  color: #81C784; 
}

.sc-status-tag.soldout { 
  background: rgba(239,83,80,0.18); 
  color: #EF5350; 
}

.sc-info-row { 
  display: flex; 
  align-items: center; 
  flex-wrap: wrap; 
}

.sc-info-item { 
  display: flex; 
  align-items: center; 
  gap: 6px; 
}

.sc-label { 
  color: #e0e0e0; 
  font-size: 0.78rem; 
  white-space: nowrap; 
}

.sc-value { 
  color: #ffffff; 
  font-size: 0.92rem; 
  font-weight: 500; 
  white-space: nowrap; 
}

.time-range { 
  color: #90CAF9; 
}

.hall-name { 
  color: #ffb74d; 
}

.price-val { 
  color: #e50914; 
  font-weight: 700; 
}

.seat-count { 
  color: #81C784; 
}

.seat-count.low { 
  color: #FFB74D; 
}

.sc-divider {
  width: 1px; 
  height: 18px; 
  background: #444; 
  flex-shrink: 0;
  margin: 0 16px;
}

.empty-screenings {
  text-align: center; 
  padding: 48px 20px;
  background: #1c1c1c; 
  border: 1px solid #444; 
  border-radius: 12px;
}

.empty-screenings .empty-icon { 
  font-size: 2.5rem; 
  display: block; 
  margin-bottom: 12px; 
}

.empty-screenings p { 
  color: #e0e0e0; 
  font-size: 0.95rem; 
  margin: 0; 
}

/* ===== 相似推荐 - 重构后样式 ===== */
.similar-section { 
  margin-bottom: 48px; 
  position: relative; 
  z-index: 1; 
}

.section-title { 
  font-size: 1.5rem; 
  color: #ffb74d; 
  margin-bottom: 20px; 
}

.similar-grid { 
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 18px; 
}

.similar-error {
  background: #1c1c1c;
  border: 1px solid #444;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

.error-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.error-icon {
  font-size: 3rem;
}

.error-content p {
  color: #e0e0e0;
  margin: 0;
}

.similar-empty {
  background: #1c1c1c;
  border: 1px solid #444;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
}

.similar-empty .empty-icon {
  font-size: 2.5rem;
  display: block;
  margin-bottom: 12px;
}

.similar-empty p {
  color: #e0e0e0;
  margin: 0;
}

/* ===== 短评 ===== */
.reviews-section { 
  margin-bottom: 48px; 
}

.review-form { 
  margin-bottom: 24px; 
}

.review-form-footer {
  display: flex; 
  justify-content: flex-end; 
  align-items: center;
  gap: 12px; 
  margin-top: 10px;
}

.count-hint { 
  color: #e0e0e0; 
  font-size: 0.8rem; 
}

.review-item {
  background: #1c1c1c; 
  border: 1px solid #555;
  border-radius: 10px; 
  padding: 14px 18px; 
  margin-bottom: 12px;
}

.review-header { 
  display: flex; 
  justify-content: space-between; 
  margin-bottom: 8px; 
}

.review-user { 
  font-weight: 600; 
  color: #ffb74d; 
  font-size: 0.9rem; 
}

.review-time { 
  color: #e0e0e0; 
  font-size: 0.75rem; 
}

.review-text { 
  color: #ffffff; 
  line-height: 1.6; 
  font-size: 0.92rem; 
}

.review-empty { 
  text-align: center; 
  color: #e0e0e0; 
  padding: 24px; 
}

/* ===== 空/错误状态 ===== */
.empty-state { 
  text-align: center; 
  padding: 80px 20px; 
}

.empty-state .icon { 
  font-size: 4rem; 
  margin-bottom: 16px; 
}

.empty-state h3 { 
  font-size: 1.2rem; 
  color: #ffffff; 
  margin-bottom: 8px; 
}

.error-state p { 
  color: #e0e0e0; 
  margin-bottom: 16px; 
  max-width: 400px; 
  word-break: break-word; 
  margin-left: auto; 
  margin-right: auto; 
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .detail-hero { 
    flex-direction: column; 
  }
  
  .detail-poster img { 
    width: 100%; 
    max-width: 280px; 
  }
  
  .detail-info h1 { 
    font-size: 1.6rem; 
  }
  
  .screening-item { 
    flex-direction: column; 
    gap: 16px; 
    align-items: stretch; 
  }
  
  .sc-info-row { 
    gap: 8px 16px; 
  }
  
  .sc-divider { 
    display: none; 
  }
  
  .similar-grid { 
    grid-template-columns: repeat(2, 1fr); 
  }
}

@media (max-width: 480px) {
  .similar-grid { 
    grid-template-columns: 1fr; 
  }
}
</style>
