// 电影海报服务
// 优先使用后端上传的海报，如果没有则使用兜底图

const BASE_URL = 'https://picsum.photos'

export const moviePosterMap = {
  '流浪地球2': { id: 1025, seed: 'wandering-earth-2' },
  '满江红': { id: 1040, seed: 'full-river-red' },
  '长津湖': { id: 1071, seed: 'battle-lake-changjin' },
  '你好，李焕英': { id: 1074, seed: 'hi-mom' },
  '唐人街探案3': { id: 1084, seed: 'detective-chinatown-3' },
  '我不是药神': { id: 1036, seed: 'dying-to-survive' },
  '哪吒之魔童降世': { id: 1015, seed: 'ne-zha' },
  '疯狂的外星人': { id: 1006, seed: 'crazy-alien' },
  '飞驰人生': { id: 1031, seed: 'pegasus' },
  '红海行动': { id: 1048, seed: 'operation-red-sea' },
  '战狼2': { id: 1060, seed: 'wolf-warrior-2' },
  '万里归途': { id: 1069, seed: 'home-coming' },
  '消失的她': { id: 1082, seed: 'lost-in-stars' },
  '孤注一掷': { id: 1083, seed: 'no-more-bets' },
  '封神第一部': { id: 1080, seed: 'creation-of-gods' },
  '流浪地球': { id: 1019, seed: 'wandering-earth' },
  '长津湖之水门桥': { id: 1075, seed: 'water-gate-bridge' },
  '独行月球': { id: 1035, seed: 'moon-man' },
  '这个杀手不太冷静': { id: 1081, seed: 'too-cool-to-kill' },
  '流浪地球3': { id: 1020, seed: 'wandering-earth-3' },
  '飞驰人生3': { id: 1032, seed: 'pegasus-3' },
}

const FALLBACK_IDS = [1006, 1015, 1019, 1025, 1031, 1036, 1040, 1048, 1060, 1071, 1074, 1080, 1082, 1083, 1084]

function buildUrl(id, w, h) {
  return `${BASE_URL}/id/${id}/${w}/${h}`
}

/**
 * 获取电影海报
 * @param {string} title - 电影标题
 * @param {string|null} posterPath - 后端上传的海报路径
 * @param {number} width - 图片宽度（默认600，轮播图用1200）
 * @returns {string} 海报URL
 */
export function getMoviePoster(title, posterPath = null, width = 600) {
  if (posterPath) {
    const base = import.meta.env.VITE_API_URL ? import.meta.env.VITE_API_URL.replace('/api', '') : ''
    return `${base}/uploads/${posterPath}`
  }

  const height = Math.round(width * 1.43)
  const entry = moviePosterMap[title]

  if (entry) {
    return buildUrl(entry.id, width, height)
  }

  const hash = title.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0)
  const id = FALLBACK_IDS[hash % FALLBACK_IDS.length]
  return buildUrl(id, width, height)
}

/**
 * 获取轮播图专用大图（高清晰度）
 */
export function getHeroPoster(title, posterPath = null) {
  return getMoviePoster(title, posterPath, 1200)
}

/**
 * 获取海报信息
 */
export function getPosterInfo(title, posterPath) {
  const url = getMoviePoster(title, posterPath)
  return {
    url,
    source: posterPath ? '上传海报' : 'Picsum Photos',
    title,
  }
}
