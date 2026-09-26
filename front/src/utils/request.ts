import axios, { type AxiosInstance, type AxiosResponse } from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// ===== 请求拦截器 =====
request.interceptors.request.use(
  (config) => {
    const auth = useAuthStore()
    // debugger
    if (auth.token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${auth.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ===== 响应拦截器 =====
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data

    // 统一包装 { code, message, data }
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code !== 200 && res.code !== 0) {
        return Promise.reject(new Error(res.message || '请求失败'))
      }
      return res.data ?? res
    }

    return res
  },
  (error) => {
    const { response } = error

    if (response) {
      if (response.status === 401) {
        const auth = useAuthStore()
        auth.clearAuth()

        if (router.currentRoute.value.path !== '/login') {
          router.push({
            path: '/login',
            query: { redirect: router.currentRoute.value.fullPath },
          })
        }
        return Promise.reject(new Error('登录已过期，请重新登录'))
      }

      const msg = response.data?.message || `请求失败（${response.status}）`
      return Promise.reject(new Error(msg))
    }

    return Promise.reject(new Error('网络错误，请稍后重试'))
  }
)

export default request