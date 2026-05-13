import { useStorage } from '@vueuse/core'

/**
 * 电影收藏功能 composable
 * 基于 localStorage 持久化收藏列表，支持跨页面状态共享
 *
 * @returns {Object} 收藏操作接口
 * @returns {Ref<number[]>} favorites - 收藏的电影 ID 数组
 * @returns {(movieId: number) => boolean} isFavorited - 判断电影是否已收藏
 * @returns {(movieId: number) => boolean} toggle - 切换收藏状态，返回操作后的状态
 * @returns {() => number[]} getFavorites - 获取收藏列表浅拷贝
 *
 * @example
 * const { isFavorited, toggle } = useFavorite()
 * const fav = isFavorited(movie.id)   // true | false
 * toggle(movie.id)                     // 切换收藏
 */
const STORAGE_KEY = 'movie-favorites'

const favorites = useStorage(STORAGE_KEY, [])

export function useFavorite() {
  function isFavorited(movieId) {
    return favorites.value.includes(movieId)
  }

  function toggle(movieId) {
    const idx = favorites.value.indexOf(movieId)
    if (idx > -1) {
      favorites.value.splice(idx, 1)
      return false
    } else {
      favorites.value.push(movieId)
      return true
    }
  }

  function getFavorites() {
    return [...favorites.value]
  }

  return { favorites, isFavorited, toggle, getFavorites }
}
