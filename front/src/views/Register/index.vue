<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2 class="title">注册账号</h2>

      <form @submit.prevent="handleRegister" class="form">
        <!-- 头像区 -->
        <div class="avatar-section">
          <div class="avatar-preview" @click="triggerSelect">
            <img v-if="form.photo" :src="form.photo" alt="头像预览" />
            <div v-else class="avatar-placeholder">
              <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="1.8">
                <circle cx="12" cy="8" r="4" />
                <path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8" />
              </svg>
              <span>上传头像</span>
            </div>

            <button
              v-if="form.photo"
              type="button"
              class="btn-remove"
              @click.stop="removePhoto"
            >×</button>
          </div>
          <input
            ref="photoInputRef"
            type="file"
            accept="image/*"
            class="hidden-input"
            @change="handlePhotoChange"
          />
          <p class="tip">点击上传，支持 jpg / png，不超过 2MB</p>
        </div>

        <!-- 表单字段 -->
        <div class="grid">
          <div class="form-item">
            <label>应用名称 <span class="required">*</span></label>
            <input
              v-model.trim="form.appName"
              type="text"
              placeholder="请输入应用名称"
              required
            />
          </div>

          <div class="form-item">
            <label>邮箱 <span class="required">*</span></label>
            <input
              v-model.trim="form.email"
              type="email"
              placeholder="请输入邮箱"
              autocomplete="email"
              required
            />
          </div>

          <div class="form-item">
            <label>用户名 <span class="required">*</span></label>
            <input
              v-model.trim="form.username"
              type="text"
              placeholder="请输入用户名"
              autocomplete="username"
              required
            />
          </div>

          <div class="form-item">
            <label>手机号</label>
            <input
              v-model.trim="form.phone"
              type="tel"
              placeholder="请输入手机号"
              autocomplete="tel"
            />
          </div>

          <div class="form-item">
            <label>密码 <span class="required">*</span></label>
            <input
              v-model="form.password"
              type="password"
              placeholder="至少 6 位"
              autocomplete="new-password"
              required
              minlength="6"
            />
          </div>

          <div class="form-item">
            <label>确认密码 <span class="required">*</span></label>
            <input
              v-model="form.confirmPassword"
              type="password"
              placeholder="再次输入密码"
              autocomplete="new-password"
              required
            />
          </div>

          <div class="form-item full">
            <label>真实姓名</label>
            <input
              v-model.trim="form.realName"
              type="text"
              placeholder="请输入真实姓名"
            />
          </div>
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>

        <div class="btn-group">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '注册中...' : '注册' }}
          </button>
          <button type="button" class="btn btn-secondary" @click="goLogin">
            返回登录
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')
const photoInputRef = ref(null)

const form = reactive({
  appName: '',
  email: '',
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  photo: '',
})

function goLogin() {
  router.push('/login')
}

function triggerSelect() {
  photoInputRef.value?.click()
}

function handlePhotoChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    errorMsg.value = '请选择图片文件'
    event.target.value = ''
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    errorMsg.value = '图片不能超过 2MB'
    event.target.value = ''
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    form.photo = reader.result
    errorMsg.value = ''
  }
  reader.readAsDataURL(file)
}

function removePhoto() {
  form.photo = ''
  if (photoInputRef.value) photoInputRef.value.value = ''
}

function validate() {
  if (!form.appName) return '请输入应用名称'
  if (!form.email) return '请输入邮箱'
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return '邮箱格式不正确'
  if (!form.username) return '请输入用户名'
  if (!form.password) return '请输入密码'
  if (form.password.length < 6) return '密码长度至少 6 位'
  if (form.password !== form.confirmPassword) return '两次输入的密码不一致'
  return ''
}

async function handleRegister() {
  errorMsg.value = ''

  const msg = validate()
  if (msg) {
    errorMsg.value = msg
    return
  }

  loading.value = true
  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        appName: form.appName,
        email: form.email,
        username: form.username,
        password: form.password,
        realName: form.realName,
        phone: form.phone,
        photo: form.photo,
      }),
    })

    const data = await res.json()
    if (!res.ok) {
      errorMsg.value = data.message || '注册失败'
      return
    }

    alert('注册成功，请登录')
    router.push('/login')
  } catch (err) {
    errorMsg.value = '网络错误，请稍后重试'
    console.error(err)
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
  padding: 24px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.auth-card {
  width: 100%;
  max-width: 560px;
  max-height: calc(100vh - 48px);
  overflow-y: auto;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.18);
  padding: 32px 36px 28px;
}

.title {
  text-align: center;
  margin: 0 0 24px;
  font-size: 22px;
  font-weight: 600;
  color: #222;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.avatar-preview {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: 2px dashed #d0d5dd;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s, background 0.2s;
}

.avatar-preview:hover {
  border-color: #667eea;
  background: #f0f2ff;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  color: #98a2b3;
  font-size: 12px;
}

.btn-remove {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.55);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: rgba(0, 0, 0, 0.8);
}

.hidden-input {
  display: none;
}

.tip {
  font-size: 12px;
  color: #98a2b3;
  margin: 0;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item.full {
  grid-column: 1 / -1;
}

.form-item label {
  font-size: 13px;
  color: #475467;
  font-weight: 500;
}

.required {
  color: #e53935;
}

.form-item input {
  height: 40px;
  padding: 0 12px;
  border: 1px solid #d0d5dd;
  border-radius: 8px;
  font-size: 14px;
  color: #222;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: #fff;
}

.form-item input::placeholder {
  color: #b0b7c3;
}

.form-item input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.error-msg {
  color: #e53935;
  font-size: 13px;
  text-align: center;
  background: #fdecec;
  border-radius: 6px;
  padding: 8px 12px;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 4px;
}

.btn {
  flex: 1;
  height: 42px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, opacity 0.2s;
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
  background: #f2f4f7;
  color: #344054;
}

.btn-secondary:hover:not(:disabled) {
  background: #e4e7ec;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 24px 20px;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .form-item.full {
    grid-column: auto;
  }
}
</style>