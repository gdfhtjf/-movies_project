<template>
  <div class="movie-form-page">
    <h3 class="page-title" style="border-bottom:none; margin-bottom:24px">
      {{ isEdit ? '✏️ 编辑电影' : '➕ 电影录入' }}
    </h3>

    <n-card class="form-section-card" title="📋 基本信息">
      <n-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-placement="left"
        label-width="100px"
        require-mark-placement="left"
      >
        <n-grid :cols="2" :x-gap="24">
          <n-gi>
            <n-form-item label="电影名称" path="title">
              <n-input v-model:value="form.title" placeholder="输入电影名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="导演" path="director">
              <n-input v-model:value="form.director" placeholder="输入导演姓名" />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="3" :x-gap="24">
          <n-gi>
            <n-form-item label="票价" path="price">
              <n-input-number
                v-model:value="form.price"
                :min="0.01"
                :precision="2"
                placeholder="0.00"
                style="width:100%"
              >
                <template #prefix>¥</template>
              </n-input-number>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="库存" path="stock">
              <n-input-number
                v-model:value="form.stock"
                :min="0"
                placeholder="0"
                style="width:100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="影片时长(分)" path="duration">
              <n-input-number
                v-model:value="form.duration"
                :min="1"
                placeholder="120"
                style="width:100%"
              />
            </n-form-item>
          </n-gi>
        </n-grid>

        <n-grid :cols="3" :x-gap="24">
          <n-gi>
            <n-form-item label="影片类型" path="genre">
              <n-input v-model:value="form.genre" placeholder="动作、科幻" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="上映日期" path="releaseDate">
              <n-date-picker
                v-model:value="form.releaseDate"
                type="date"
                style="width:100%"
              />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="预告片地址">
              <n-input v-model:value="form.trailerPath" placeholder="download/xxx.mp4" />
            </n-form-item>
          </n-gi>
        </n-grid>
      </n-form>
    </n-card>

    <n-card class="form-section-card" title="📝 详细信息">
      <n-form
        label-placement="left"
        label-width="100px"
        require-mark-placement="left"
      >
        <n-form-item label="主演">
          <n-input
            v-model:value="form.cast"
            placeholder="多个主演用逗号分隔，如：吴京、刘德华"
          />
        </n-form-item>

        <n-form-item label="剧情简介">
          <n-input
            v-model:value="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入电影的剧情简介..."
          />
        </n-form-item>

        <n-form-item label="海报" :required="!isEdit">
          <n-upload
            :max="1"
            list-type="image-card"
            accept="image/*"
            :default-file-list="uploadFileList"
            @change="handleUploadChange"
          />
          <div
            v-if="isEdit && form.posterPath && !uploadFileList.length"
            class="current-poster"
          >
            <img
              :src="`/uploads/${form.posterPath}`"
              alt="当前海报"
            />
            <p class="muted">当前海报，上传新文件将替换</p>
          </div>
        </n-form-item>
      </n-form>
    </n-card>

    <div class="form-actions">
      <n-button type="error" size="large" :loading="submitting" @click="submit">
        {{ isEdit ? '更新电影' : '发布电影' }}
      </n-button>
      <n-button size="large" @click="$router.back()">返回</n-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/api'
import { createDiscreteApi } from 'naive-ui'

const { message } = createDiscreteApi(['message'])

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const uploadFileList = ref([])

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '', director: '', price: null, stock: 100, duration: null,
  genre: '', releaseDate: null, trailerPath: '', cast: '', description: '',
  posterPath: '',
})

const rules = {
  title: [{ required: true, message: '请输入电影名称', trigger: 'blur' }],
  director: [{ required: true, message: '请输入导演', trigger: 'blur' }],
  price: [{ required: true, message: '请输入票价', trigger: 'blur' }],
}

let rawFile = null

function handleUploadChange({ file, fileList }) {
  rawFile = file.file
  uploadFileList.value = fileList.map(f => ({
    id: f.id, name: f.name, status: 'finished',
    url: f.url || (f.file ? URL.createObjectURL(f.file) : '')
  }))
}

function formatDateOnly(d) {
  if (!d) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

onMounted(async () => {
  if (isEdit.value) {
    const res = await api.get(`/movies/${route.params.id}`)
    const d = res.data
    Object.keys(form).forEach(k => {
      if (k === 'releaseDate' && d[k]) {
        form[k] = new Date(d[k])
      } else if (d[k] !== undefined) {
        form[k] = d[k]
      }
    })
  }
})

async function submit() {
  if (!isEdit.value && !rawFile) { message.warning('请选择海报文件'); return }

  submitting.value = true
  try {
    const fd = new FormData()
    Object.keys(form).forEach(k => {
      let v = form[k]
      if (k === 'releaseDate') v = formatDateOnly(v)
      if (v !== null && v !== undefined && v !== '' && k !== 'posterPath') {
        fd.append(k, v)
      }
    })
    if (rawFile) fd.append('poster', rawFile)

    if (isEdit.value) {
      await api.put(`/movies/${route.params.id}`, fd)
      message.success('更新成功')
    } else {
      await api.post('/movies', fd)
      message.success('添加成功')
    }
    router.push('/admin/movies/list')
  } catch { /* handled */ }
  submitting.value = false
}
</script>

<style scoped>
.movie-form-page {
  max-width: 900px;
}

.form-section-card {
  margin-bottom: 24px;
  border: 1px solid #2d2d2d;
  border-radius: 8px;
}

.current-poster {
  margin-top: 12px;
}

.current-poster img {
  width: 120px;
  border-radius: 8px;
  border: 1px solid #2d2d2d;
}

.muted {
  color: #e0e0e0;
  font-size: 12px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
