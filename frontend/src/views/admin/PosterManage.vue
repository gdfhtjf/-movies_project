<template>
  <div class="poster-manage">
    <h3 class="page-title" style="border-bottom:none; margin-bottom:24px">🖼️ 海报管理</h3>

    <n-card class="upload-card" title="📤 上传新海报">
      <n-form inline>
        <n-form-item label="选择电影">
          <n-select
            v-model:value="upload.movieId"
            :options="movieSelectOptions"
            placeholder="请选择电影"
            filterable
            style="width:260px"
          />
        </n-form-item>
        <n-form-item>
          <n-upload :max="1" accept="image/*" @change="handleUploadChange">
            <n-button type="info">选择文件</n-button>
          </n-upload>
        </n-form-item>
        <n-form-item>
          <n-button type="error" :loading="uploading" :disabled="!uploadFile" @click="doUpload">
            上传海报
          </n-button>
        </n-form-item>
      </n-form>
    </n-card>

    <div class="poster-grid">
      <div v-for="m in movies" :key="m.id" class="poster-card">
        <div class="poster-img-wrap">
          <img :src="m.posterPath ? `/uploads/${m.posterPath}` : getMoviePoster(m.title)" :alt="m.title" />
          <div class="poster-overlay">
            <n-button size="small" type="warning" @click="openReplace(m)">替换</n-button>
            <n-button size="small" type="error" @click="delPoster(m)">删除</n-button>
          </div>
        </div>
        <div class="card-footer">
          <h5>{{ m.title }}</h5>
        </div>
      </div>
    </div>

    <div v-if="!movies.length" class="empty-state">
      <div class="icon">🎬</div>
      <h3>暂无电影记录</h3>
      <p>请先添加电影后再上传海报</p>
    </div>

    <n-modal v-model:show="replaceVisible" preset="card" title="替换海报" style="width:450px">
      <p>当前电影：<strong>{{ replaceMovie?.title }}</strong></p>
      <n-upload :max="1" accept="image/*" @change="handleReplaceFile">
        <n-button type="info">选择新海报</n-button>
      </n-upload>
      <template #footer>
        <n-space justify="end">
          <n-button @click="replaceVisible = false">取消</n-button>
          <n-button type="warning" :loading="replacing" :disabled="!replaceFile" @click="doReplace">
            确认替换
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'
import { getMoviePoster } from '@/utils/poster'

const { message, dialog } = createDiscreteApi(['message', 'dialog'])

const movies = ref([])
const movieSelectOptions = ref([])
const upload = reactive({ movieId: null })
const uploadFile = ref(null)
const uploading = ref(false)

const replaceVisible = ref(false)
const replaceMovie = ref(null)
const replaceFile = ref(null)
const replacing = ref(false)

onMounted(loadData)

async function loadData() {
  const res = await api.get('/movies', { params: { page: 1, size: 999 } })
  const list = res.data?.records || []
  movies.value = list
  movieSelectOptions.value = list.map(m => ({
    label: `${m.title} ${m.posterPath ? '(已有海报)' : '(无海报)'}`,
    value: m.id
  }))
}

function handleUploadChange({ file }) { uploadFile.value = file.file }

async function doUpload() {
  if (!upload.movieId) { message.warning('请选择电影'); return }
  uploading.value = true
  const fd = new FormData()
  fd.append('movieId', upload.movieId)
  fd.append('poster', uploadFile.value)
  try {
    await api.post('/posters/upload', fd)
    message.success('上传成功')
    upload.movieId = null; uploadFile.value = null
    loadData()
  } catch { /* handled */ }
  uploading.value = false
}

function openReplace(movie) {
  replaceMovie.value = movie
  replaceFile.value = null
  replaceVisible.value = true
}

function handleReplaceFile({ file }) { replaceFile.value = file.file }

async function doReplace() {
  if (!replaceFile.value) return
  replacing.value = true
  const fd = new FormData()
  fd.append('poster', replaceFile.value)
  try {
    await api.put(`/posters/${replaceMovie.value.id}`, fd)
    message.success('替换成功')
    replaceVisible.value = false
    loadData()
  } catch { /* handled */ }
  replacing.value = false
}

async function delPoster(movie) {
  dialog.warning({
    title: '删除海报',
    content: `确定要删除《${movie.title}》的海报吗？`,
    positiveText: '删除', negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await api.delete(`/posters/${movie.id}`)
        message.success('已删除')
        loadData()
      } catch { /* handled */ }
    },
  })
}
</script>

<style scoped>
.poster-manage { max-width: 1280px; }

.upload-card {
  margin-bottom: 24px;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
}

.poster-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.poster-card {
  background: #1a1a1a;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
}

.poster-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(229, 9, 20, 0.12);
}

.poster-img-wrap {
  position: relative;
  width: 100%;
  aspect-ratio: 2/3;
  overflow: hidden;
}

.poster-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-poster {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2d2d2d;
  color: #e0e0e0;
  font-size: 0.9rem;
}

.poster-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
  display: flex;
  gap: 8px;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.25s;
}

.poster-card:hover .poster-overlay {
  opacity: 1;
}

.card-footer {
  padding: 12px 14px;
}

.card-footer h5 {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-state .icon { font-size: 4rem; margin-bottom: 16px; }
.empty-state h3 { font-size: 1.2rem; color: #e0e0e0; margin-bottom: 8px; }
.empty-state p { color: #e0e0e0; font-size: 0.9rem; }

@media (max-width: 992px) {
  .poster-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 576px) {
  .poster-grid { grid-template-columns: 1fr; }
}
</style>
