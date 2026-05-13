import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import UserLayout from '@/layouts/UserLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    component: UserLayout,
    children: [
      { path: '', name: 'Home', component: () => import('@/views/user/Home.vue') },
      { path: 'movies', name: 'Movies', component: () => import('@/views/user/MovieList.vue') },
      { path: 'detail/:id', name: 'MovieDetail', component: () => import('@/views/user/MovieDetail.vue') },
      { path: 'seat/:screeningId', name: 'SeatSelection', component: () => import('@/views/user/SeatSelection.vue'), meta: { auth: true } },
      { path: 'login', name: 'Login', component: () => import('@/views/user/Login.vue') },
      { path: 'register', name: 'Register', component: () => import('@/views/user/Register.vue') },
      { path: 'orders', name: 'Orders', component: () => import('@/views/user/MyOrders.vue'), meta: { auth: true } },
      { path: 'favorites', name: 'Favorites', component: () => import('@/views/user/MyFavorites.vue'), meta: { auth: true } },
    ],
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { auth: true, admin: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'movies/add', name: 'MovieAdd', component: () => import('@/views/admin/MovieAdd.vue') },
      { path: 'movies/edit/:id', name: 'MovieEdit', component: () => import('@/views/admin/MovieAdd.vue') },
      { path: 'movies/list', name: 'MovieManage', component: () => import('@/views/admin/MovieManage.vue') },
      { path: 'screenings', name: 'ScreeningManage', component: () => import('@/views/admin/ScreeningManage.vue') },
      { path: 'posters', name: 'PosterManage', component: () => import('@/views/admin/PosterManage.vue') },
      { path: 'orders', name: 'OrderManage', component: () => import('@/views/admin/OrderManage.vue') },
        { path: 'users', name: 'UserManage', component: () => import('@/views/admin/UserManage.vue') },
      ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 始终滚动到顶部
    return { top: 0, left: 0 }
  },
})

router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()
  const needsAuth = to.meta.auth || to.meta.admin
  if (needsAuth && !auth.user) {
    await auth.fetchMe()
  }
  if (to.meta.admin && auth.user) {
    await auth.fetchMe()
  }
  if (to.meta.auth && !auth.user) {
    return next('/login')
  }
  if (to.meta.admin && !auth.isAdmin) {
    return next('/')
  }
  next()
})

export default router
