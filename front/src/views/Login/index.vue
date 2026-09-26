<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2 class="title">用户登录</h2>

      <form @submit.prevent="handleLogin" class="form">
        <div class="form-item">
          <label>用户名</label>
          <input
            v-model.trim="form.username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
            required
          />
        </div>

        <div class="form-item">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
            required
          />
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

        <div class="btn-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
          <button type="button" class="btn btn-secondary" @click="goRegister">
            注册
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<!-- 
function goRegister() {
  router.push('/register')
} -->
<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { login } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
})

function goRegister() {
  router.push('/register')
}

function validate() {
  if (!form.username) return '请输入用户名'
  if (!form.password) return '请输入密码'
  return ''
}

async function handleLogin() {
  errorMsg.value = ''
  const msg = validate()
  if (msg) {
    errorMsg.value = msg
    return
  }

  loading.value = true
  try {
    const data = await login(form)

    // 存入 Pinia（同时写 localStorage）
    auth.setAuth(data.token, data.user)

    // 跳回来源页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (err) {
    errorMsg.value = err.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  padding: 40px 32px;
}

.title {
  text-align: center;
  margin-bottom: 28px;
  font-size: 22px;
  color: #333;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 14px;
  color: #555;
}

.form-item input {
  height: 40px;
  padding: 0 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-item input:focus {
  border-color: #667eea;
}

.error-msg {
  color: #e53935;
  font-size: 13px;
  text-align: center;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  flex: 1;
  height: 42px;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #667eea;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #5568d3;
}

.btn-secondary {
  background: #f0f0f0;
  color: #333;
}

.btn-secondary:hover:not(:disabled) {
  background: #e0e0e0;
}
</style>