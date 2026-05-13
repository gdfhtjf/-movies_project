import { ref } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'

/**
 * 滚动显示动画 composable
 * 利用 IntersectionObserver 检测元素进入视口，触发入场动画
 *
 * @param {Object} [options] - 配置项
 * @param {number} [options.threshold=0.1] - 可见比例阈值
 * @param {string} [options.rootMargin='0px 0px -40px 0px'] - 视口扩展边距
 * @returns {{ target: Ref, isVisible: Ref<boolean> }}
 *
 * @example
 * const { target, isVisible } = useScrollReveal()
 * // template: <div ref="target" :class="{ visible: isVisible }">
 */
export function useScrollReveal(options = {}) {
  const target = ref(null)
  const isVisible = ref(false)

  useIntersectionObserver(target, ([{ isIntersecting }]) => {
    if (isIntersecting) {
      isVisible.value = true
    }
  }, { threshold: options.threshold ?? 0.1, rootMargin: options.rootMargin ?? '0px 0px -40px 0px' })

  return { target, isVisible }
}
