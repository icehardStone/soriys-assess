import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/main',
    },
    {
      path: '/login',
      name: 'login',
      meta: {
        // public: true,
      },
      component: () => import('@/views/Login/index.vue'),
    },
     {
      path: '/profile',
      name: 'profile',
      meta: {
        // public: true,
      },
      component: () => import('@/views/Profile/index.vue'),
    },
    {
      path: '/register',
      name: 'register',
      meta: {
        public: true,
      },
      component: () => import('@/views/Register/index.vue'),
    },
    {
      path: '/main',
      component: () => import('@/layout/Main/index.vue'),
      redirect: '/main/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard/index.vue'),
        },
        {
          path: 'assessments',
          name: 'Assessments',
          component: () => import('@/views/Assessments/index.vue'),
        },
        {
          path: 'assessments/new',
          name: 'AssessmentNew',
          component: () => import('@/views/AssessmentForm/index.vue'),
        },
        {
          path: 'assessments/:id/edit',
          name: 'AssessmentEdit',
          component: () => import('@/views/AssessmentForm/index.vue'),
        },
        {
          path: 'assessments/:id',
          name: 'AssessmentDetail',
          component: () => import('@/views/AssessmentDetail/index.vue'),
        },
        {
          path: 'standards',
          name: 'Standards',
          component: () => import('@/views/Standards/index.vue'),
        },
      ],
    },
  ],
})

const WHITE_LIST = ['/login', '/register']

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  debugger
  // 1. 未登录
  if (!token) {
    // 目标在白名单 → 放行
    if (WHITE_LIST.includes(to.path)) {
      return next()
    }
    // 避免自己跳自己
    if (to.path === '/login') {
      return next()
    }
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // 2. 已登录，访问 login/register → 跳首页
  if (to.path === '/login' || to.path === '/register') {
    // 避免跳转到 '/' 后 '/' 又重定向回 login
    return next('/')
  }

  // 3. 其他
  next()
})

// router.beforeEach((to, from, next) => {
//   const token = localStorage.getItem('token')
//   const isPublic = to.meta?.public === true

//   debugger

  

//   if (!token && !isPublic) {
//     next({ path: '/login', query: { redirect: to.fullPath } })
//   } else if (token && (to.path === '/login' || to.path === '/register')) {
//     next('/')
//   } else {
//     next()
//   }
// })

export default router
