import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // ===== state =====
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  // ===== getters =====
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => user.value?.username || '')
  const realName = computed(() => user.value?.realName || '')
  const email = computed(() => user.value?.email || user.value?.emal || '')
  const photo = computed(() => user.value?.photo || '')

  // ===== actions =====
  function setAuth(newToken, newUser) {
    token.value = newToken
    user.value = newUser
    localStorage.setItem('token', newToken)
    localStorage.setItem('user', JSON.stringify(newUser || {}))
  }

  function setUser(newUser) {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser || {}))
  }

  function clearAuth() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return {
    token,
    user,
    isLoggedIn,
    username,
    realName,
    email,
    photo,
    setAuth,
    setUser,
    clearAuth,
  }
})