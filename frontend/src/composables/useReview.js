import { useStorage } from '@vueuse/core'
import { computed } from 'vue'

/**
 * 电影短评功能 composable
 * 基于 localStorage 按电影 ID 存储评论数据，支持发表和查询
 *
 * @param {Ref<string|number>} movieIdRef - 当前电影 ID 的 ref
 * @returns {Object} 短评操作接口
 * @returns {ComputedRef<Array>} movieReviews - 按时间倒序排列的短评列表
 * @returns {(text: string, userName?: string) => void} addReview - 发表短评
 * @returns {(reviewId: number) => void} deleteReview - 删除短评
 *
 * @example
 * const { movieReviews, addReview } = useReview(movieId)
 * addReview('太好看了！', '张三')
 */
const STORAGE_KEY = 'movie-reviews'
const reviews = useStorage(STORAGE_KEY, {})

export function useReview(movieIdRef) {
  const movieReviews = computed(() => {
    const movieId = movieIdRef.value
    const list = reviews.value[movieId] || []
    return list.sort((a, b) => b.time - a.time)
  })

  function addReview(text, userName = '匿名用户') {
    const movieId = movieIdRef.value
    if (!reviews.value[movieId]) {
      reviews.value[movieId] = []
    }
    reviews.value[movieId].push({
      id: Date.now(),
      text,
      userName,
      time: Date.now(),
    })
  }

  function deleteReview(reviewId) {
    const movieId = movieIdRef.value
    if (!reviews.value[movieId]) return
    reviews.value[movieId] = reviews.value[movieId].filter((r) => r.id !== reviewId)
  }

  return { movieReviews, addReview, deleteReview }
}
