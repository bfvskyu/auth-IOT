import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue' // 我们即将创建的首页
import { useUserStore } from '../store/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: Login },
    { path: '/home', name: 'Home', component: Home }
  ]
})

// 【核心新增：前端路由守卫】—— 每次跳转页面前都会触发
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 如果用户要去的是登录页，直接放行
  if (to.path === '/login') {
    return next()
  }
  
  // 如果去的不是登录页，检查口袋里有没有 Token
  if (!userStore.token) {
    // 没 Token？打回登录页！
    return next('/login')
  }
  
  // 有 Token，放行！
  next()
})

export default router