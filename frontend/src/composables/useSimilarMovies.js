import { ref, computed } from 'vue'
import api from '@/utils/api'

/**
 * 相似电影推荐组合式函数
 * 提供基于多维度的电影推荐功能
 */
export function useSimilarMovies() {
  const similarMovies = ref([])
  const loading = ref(false)
  const error = ref(null)
  const allMoviesCache = ref([])

  /**
   * 计算电影相似度分数
   * @param {Object} currentMovie - 当前电影
   * @param {Object} candidateMovie - 候选电影
   * @returns {number} 相似度分数
   */
  function calculateSimilarityScore(currentMovie, candidateMovie) {
    let score = 0

    // 1. 类型匹配 (权重最高: 40分)
    if (currentMovie.genre && candidateMovie.genre) {
      const currentGenres = currentMovie.genre.split(/[,，/]/).map(g => g.trim().toLowerCase())
      const candidateGenres = candidateMovie.genre.split(/[,，/]/).map(g => g.trim().toLowerCase())
      
      const matchedGenres = currentGenres.filter(g => 
        candidateGenres.some(cg => cg.includes(g) || g.includes(cg))
      )
      
      if (matchedGenres.length > 0) {
        score += Math.min(40, matchedGenres.length * 15)
      }
    }

    // 2. 导演匹配 (权重: 25分)
    if (currentMovie.director && candidateMovie.director) {
      if (currentMovie.director === candidateMovie.director) {
        score += 25
      }
    }

    // 3. 演员匹配 (权重: 20分)
    if (currentMovie.cast && candidateMovie.cast) {
      const currentCast = currentMovie.cast.split(/[,，]/).map(c => c.trim())
      const candidateCast = candidateMovie.cast.split(/[,，]/).map(c => c.trim())
      
      const matchedCast = currentCast.filter(c => candidateCast.includes(c))
      if (matchedCast.length > 0) {
        score += Math.min(20, matchedCast.length * 8)
      }
    }

    // 4. 价格相近 (权重: 10分)
    if (currentMovie.price && candidateMovie.price) {
      const priceDiff = Math.abs(currentMovie.price - candidateMovie.price)
      if (priceDiff < 10) score += 10
      else if (priceDiff < 20) score += 5
    }

    // 5. 上映时间相近 (权重: 5分)
    if (currentMovie.releaseDate && candidateMovie.releaseDate) {
      try {
        const currentYear = new Date(currentMovie.releaseDate).getFullYear()
        const candidateYear = new Date(candidateMovie.releaseDate).getFullYear()
        const yearDiff = Math.abs(currentYear - candidateYear)
        if (yearDiff <= 1) score += 5
        else if (yearDiff <= 3) score += 2
      } catch (e) {
        // 忽略日期解析错误
      }
    }

    return score
  }

  /**
   * 获取相似电影推荐
   * @param {number} currentMovieId - 当前电影ID
   * @param {Object} currentMovie - 当前电影对象
   * @param {number} limit - 返回数量限制
   */
  async function fetchSimilarMovies(currentMovieId, currentMovie, limit = 4) {
    if (!currentMovie) {
      console.warn('[useSimilarMovies] 当前电影信息为空')
      return []
    }

    loading.value = true
    error.value = null

    try {
      // 如果已有缓存，直接使用
      let moviesToProcess = allMoviesCache.value
      
      // 如果没有缓存或缓存为空，重新获取
      if (!moviesToProcess || moviesToProcess.length === 0) {
        const response = await api.get('/movies', { params: { page: 1, size: 100 } })
        moviesToProcess = response.data?.records || response.data || []
        allMoviesCache.value = moviesToProcess
        console.log('[useSimilarMovies] 已获取电影列表，共', moviesToProcess.length, '部')
      }

      // 计算每部电影的相似度并排序
      const scoredMovies = moviesToProcess
        .filter(m => m.id !== currentMovieId) // 排除当前电影
        .map(movie => ({
          ...movie,
          similarityScore: calculateSimilarityScore(currentMovie, movie)
        }))
        .filter(m => m.similarityScore > 0) // 只保留有相似度的电影
        .sort((a, b) => b.similarityScore - a.similarityScore) // 按相似度降序排列
        .slice(0, limit)

      similarMovies.value = scoredMovies
      console.log('[useSimilarMovies] 找到相似电影:', scoredMovies)
      
      return scoredMovies
    } catch (err) {
      error.value = err.message || '获取相似电影失败'
      console.error('[useSimilarMovies] 获取相似电影失败:', err)
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 清除缓存
   */
  function clearCache() {
    allMoviesCache.value = []
    similarMovies.value = []
  }

  return {
    similarMovies,
    loading,
    error,
    fetchSimilarMovies,
    clearCache
  }
}

export default useSimilarMovies
