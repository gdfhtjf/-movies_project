<template>
  <div class="admin-shell">
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <router-link to="/admin" class="sidebar-logo">
          <span class="logo-icon">🎬</span>
          <Transition name="fade">
            <span v-show="!sidebarCollapsed" class="logo-text">管理后台</span>
          </Transition>
        </router-link>
      </div>

      <n-menu
        v-model:value="activeKey"
        v-model:expanded-keys="expandedKeys"
        :options="menuOptions"
        :collapsed="sidebarCollapsed"
        :collapsed-width="64"
        :collapsed-icon-size="20"
        :root-indent="18"
        :indent="24"
        class="sidebar-menu"
      />

      <div class="sidebar-footer">
        <n-button text class="collapse-btn" @click="sidebarCollapsed = !sidebarCollapsed">
          <template #icon>
            <ChevronBackOutline v-if="!sidebarCollapsed" />
            <ChevronForwardOutline v-else />
          </template>
        </n-button>
      </div>
    </aside>

    <div class="main-area">
      <header class="admin-header">
        <div class="breadcrumb">
          <slot name="breadcrumb">
            <span>{{ pageTitle }}</span>
          </slot>
        </div>
        <div class="header-actions">
          <span class="welcome">管理员：{{ auth.user?.name }}</span>
          <n-button text @click="goToUser">
            <template #icon><HomeOutline /></template>
            用户端
          </n-button>
          <n-button text type="error" @click="doLogout">
            <template #icon><LogOutOutline /></template>
            退出
          </n-button>
        </div>
      </header>
      <div class="content-area">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { h, ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  AnalyticsOutline, FilmOutline, AddOutline, ListOutline,
  CalendarOutline, ImageOutline, TicketOutline, PersonOutline,
  ChevronBackOutline, ChevronForwardOutline,
  HomeOutline, LogOutOutline
} from '@vicons/ionicons5'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const sidebarCollapsed = ref(false)
const activeKey = ref(route.path || '/admin')
const expandedKeys = ref(route.path?.startsWith('/admin/movies') ? ['movie-group'] : [])

watch(activeKey, (key) => {
  if (key && key.startsWith('/') && key !== route.path) {
    router.push(key).catch(() => {})
  }
})

watch(() => route.path, (path) => {
  const p = path || '/admin'
  if (activeKey.value !== p) {
    activeKey.value = p
  }
  const needExpand = p.startsWith('/admin/movies')
  const hasGroup = expandedKeys.value.includes('movie-group')
  if (needExpand && !hasGroup) {
    expandedKeys.value = [...expandedKeys.value, 'movie-group']
  } else if (!needExpand && hasGroup) {
    expandedKeys.value = expandedKeys.value.filter(k => k !== 'movie-group')
  }
})

const pageTitle = computed(() => route.meta?.title || '')

function renderIcon(icon) { return () => h(icon) }

const menuOptions = [
  { label: '仪表盘', key: '/admin', icon: renderIcon(AnalyticsOutline) },
  {
    label: '电影管理', key: 'movie-group', icon: renderIcon(FilmOutline),
    children: [
      { label: '电影录入', key: '/admin/movies/add', icon: renderIcon(AddOutline) },
      { label: '电影列表', key: '/admin/movies/list', icon: renderIcon(ListOutline) },
    ]
  },
  { label: '场次管理', key: '/admin/screenings', icon: renderIcon(CalendarOutline) },
  { label: '海报管理', key: '/admin/posters', icon: renderIcon(ImageOutline) },
  { label: '订单管理', key: '/admin/orders', icon: renderIcon(TicketOutline) },
  { label: '用户管理', key: '/admin/users', icon: renderIcon(PersonOutline) },
]

async function doLogout() {
  await auth.logout()
  router.push('/')
}

function goToUser() {
  router.push('/')
}
</script>

<style scoped>
.admin-shell {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  min-width: 240px;
  background: #0f0f0f;
  border-right: 1px solid #2d2d2d;
  display: flex;
  flex-direction: column;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1),
              min-width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 30;
}

.sidebar.collapsed {
  width: 64px;
  min-width: 64px;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #2d2d2d;
  overflow: hidden;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ff9f1c;
  font-family: var(--font-heading);
  font-size: 1.1rem;
  font-weight: 600;
  white-space: nowrap;
  text-decoration: none;
}

.sidebar-logo:hover {
  color: #ff9f1c;
}

.logo-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.logo-text {
  flex-shrink: 0;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-top: 8px;
}

.sidebar-footer {
  padding: 12px;
  border-top: 1px solid #2d2d2d;
  text-align: center;
  margin-top: auto;
}

.collapse-btn {
  width: 100%;
  color: #e0e0e0;
}

.collapse-btn:hover {
  color: #e50914;
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-width: 0;
}

.admin-header {
  height: 60px;
  background: #1a1a1a;
  border-bottom: 1px solid #2d2d2d;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
}

.breadcrumb {
  color: #e0e0e0;
  font-size: 0.9rem;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.welcome {
  color: #e0e0e0;
  font-size: 0.85rem;
  margin-right: 8px;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #0f0f0f;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
