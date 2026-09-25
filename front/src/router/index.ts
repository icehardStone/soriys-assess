import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/Dashboard/index.vue')
    },
    {
      path: '/assessments',
      name: 'Assessments',
      component: () => import('@/views/Assessments/index.vue')
    },
    {
      path: '/assessments/new',
      name: 'AssessmentNew',
      component: () => import('@/views/AssessmentForm/index.vue')
    },
    {
      path: '/assessments/:id/edit',
      name: 'AssessmentEdit',
      component: () => import('@/views/AssessmentForm/index.vue')
    },
    {
      path: '/assessments/:id',
      name: 'AssessmentDetail',
      component: () => import('@/views/AssessmentDetail/index.vue')
    },
    {
      path: '/standards',
      name: 'Standards',
      component: () => import('@/views/Standards/index.vue')
    }
  ],
})

export default router
