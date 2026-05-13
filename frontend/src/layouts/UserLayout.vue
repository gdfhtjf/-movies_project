<template>
  <div class="user-shell">
    <header class="navbar" :class="{ scrolled: isScrolled }">
      <div class="navbar-inner">
        <router-link to="/" class="brand">
          <span class="brand-icon">🎬</span>
          <span class="brand-text">Moviego</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/" class="nav-item" exact-active-class="active">首页</router-link>
          <router-link to="/movies" class="nav-item" active-class="active">电影列表</router-link>
          <template v-if="auth.isLoggedIn">
            <router-link to="/orders" class="nav-item" active-class="active">
              📋 我的订单
            </router-link>
            <router-link to="/favorites" class="nav-item" active-class="active">
              ❤️ 我的收藏
            </router-link>
          </template>
        </nav>

        <div class="user-area">
          <n-dropdown v-if="auth.isLoggedIn" trigger="hover" :options="userMenuOptions" @select="onUserMenuSelect">
            <n-button text class="user-btn">
              <span class="user-avatar">👤</span>
              <span class="user-name">欢迎，{{ auth.user?.name || '用户' }}</span>
            </n-button>
          </n-dropdown>
          <template v-else>
            <n-button text @click="$router.push('/login')">登录</n-button>
            <n-button type="error" size="small" @click="$router.push('/register')">注册</n-button>
          </template>
        </div>
      </div>
    </header>

    <main class="page-main">
      <router-view />
    </main>

    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="brand-icon">🎬</span>
          <span>Moviego - 在线电影票务平台</span>
        </div>
        <p class="footer-copy">&copy; 2026 Moviego. Java web课程设计</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { h } from 'vue'
import { PersonCircleOutline, LogOutOutline } from '@vicons/ionicons5'

const auth = useAuthStore()
const router = useRouter()
const isScrolled = ref(false)

const navItems = [
  { label: '首页', path: '/' },
  { label: '电影列表', path: '/movies' },
]

const userMenuOptions = computed(() => {
  const items = []
  if (auth.isAdmin) {
    items.push({ key: 'admin', label: '后台管理' })
    items.push({ type: 'divider' })
  }
  items.push({ key: 'logout', label: '退出登录' })
  return items
})

function onUserMenuSelect(key) {
  if (key === 'admin') router.push('/admin')
  else if (key === 'logout') {
    auth.logout()
    router.push('/')
  }
}

function onScroll() {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.user-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0f0f0f;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 64px;
  transition: background 0.3s, backdrop-filter 0.3s, box-shadow 0.3s;
  background: transparent;
}

.navbar.scrolled {
  background: rgba(15, 15, 15, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.3);
}

.navbar-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #fff;
}

.brand-icon { font-size: 1.5rem; }

.brand-text {
  font-family: var(--font-heading);
  font-size: 1.3rem;
  font-weight: 700;
  background: linear-gradient(135deg, #e50914, #ff9f1c);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-links { display: flex; gap: 6px; }

.nav-item {
  color: #e0e0e0;
  padding: 10px 18px;
  border-radius: 24px;
  text-decoration: none;
  font-size: 0.95rem;
  font-weight: 500;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
}

.nav-item:hover {
  color: #fff;
  background: linear-gradient(135deg, rgba(229, 9, 20, 0.15), rgba(255, 159, 28, 0.1));
  transform: translateY(-2px);
}

.nav-item.active {
  color: #fff;
  background: linear-gradient(135deg, #e50914, #c40812);
  box-shadow: 0 4px 15px rgba(229, 9, 20, 0.3);
}

.nav-item.active:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(229, 9, 20, 0.4);
}

.user-area { display: flex; align-items: center; gap: 8px; }

.user-btn { display: flex; align-items: center; gap: 6px; color: #e0e0e0; }
.user-btn:hover { color: #e50914; }
.user-avatar { font-size: 1.1rem; }
.user-name { font-size: 0.9rem; }

.page-main {
  flex: 1;
  margin-top: 64px;
  background: #0f0f0f;
  position: relative;
  z-index: 2;
}

.site-footer {
  background: #1a1a1a;
  border-top: 1px solid #2d2d2d;
  padding: 32px 24px;
  text-align: center;
  position: relative;
  z-index: 2;
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #e0e0e0;
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 8px;
}

.footer-copy { color: #c0c0c0; font-size: 0.8rem; }

@media (max-width: 992px) {
  .nav-links { gap: 4px; }
  .nav-item { padding: 8px 14px; font-size: 0.9rem; }
}

@media (max-width: 768px) {
  .nav-links { gap: 2px; }
  .nav-item { padding: 6px 10px; font-size: 0.8rem; }
  .user-name { display: none; }
}
</style>
