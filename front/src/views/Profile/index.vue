<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人信息</h2>
      <button class="btn-close" @click="closeWindow">关闭</button>
    </div>

    <div class="profile-body">
      <!-- 左侧：头像 + 基本信息 -->
      <div class="profile-left">
        <div class="avatar-block">
          <div class="avatar-large" @click="triggerUpload">
            <img v-if="form.photo" :src="form.photo" alt="头像" />
            <span v-else>{{ avatarText }}</span>
            <div class="avatar-mask">
              <span>更换头像</span>
            </div>
          </div>
          <input
            ref="photoInputRef"
            type="file"
            accept="image/*"
            class="hidden-input"
            @change="handlePhotoChange"
          />
          <p class="username">{{ auth.username }}</p>
          <p class="role-tag">{{ auth.realName || '评估员' }}</p>
        </div>
      </div>

      <!-- 右侧：详细表单 -->
      <div class="profile-right">
        <div class="section-title">基本资料</div>

        <div class="form-grid">
          <div class="form-item">
            <label>用户名</label>
            <input :value="auth.username" type="text" disabled />
          </div>

          <div class="form-item">
            <label>邮箱</label>
            <input v-model.trim="form.email" type="email" placeholder="请输入邮箱" />
          </div>

          <div class="form-item">
            <label>真实姓名</label>
            <input v-model.trim="form.realName" type="text" placeholder="请输入真实姓名" />
          </div>

          <div class="form-item">
            <label>手机号</label>
            <input v-model.trim="form.phone" type="tel" placeholder="请输入手机号" />
          </div>

          <div class="form-item">
            <label>应用名称</label>
            <input v-model.trim="form.appName" type="text" placeholder="请输入应用名称" />
          </div>
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
        <div v-if="successMsg" class="success-msg">{{ successMsg }}</div>

        <div class="btn-group">
          <button class="btn btn-primary" :disabled="saving" @click="handleSave">
            {{ saving ? '保存中...' : '保存修改' }}
          </button>
          <button class="btn btn-secondary" @click="resetForm">重置</button>
        </div>
      </div>
    </div>

    <!-- 应用信息 -->
    <div class="app-section">
      <div class="section-header">
        <span class="section-title">应用信息</span>
        <button
          v-if="appData?.isCreator"
          class="btn btn-primary btn-sm"
          @click="openInviteModal"
        >
          + 邀请用户
        </button>
      </div>

      <div v-if="appLoading" class="app-status">加载中...</div>
      <div v-else-if="!appData" class="app-status">暂无应用信息</div>
      <template v-else>
        <div class="app-info-grid">
          <div class="info-item">
            <label>应用名称</label>
            <span>{{ appData.app.appName || '-' }}</span>
          </div>
          <div class="info-item">
            <label>状态</label>
            <span :class="appData.app.enabled ? 'tag-ok' : 'tag-off'">
              {{ appData.app.enabled ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="info-item">
            <label>创建者</label>
            <span>{{ creatorName }}</span>
          </div>
          <div class="info-item">
            <label>创建时间</label>
            <span>{{ formatDate(appData.app.createdAt) }}</span>
          </div>
          <div class="info-item info-item-full">
            <label>AppKey</label>
            <span class="mono">{{ appData.app.appKey || '-' }}</span>
          </div>
          <div class="info-item info-item-full">
            <label>AppSecret</label>
            <span class="mono">{{ appData.app.appSecret || '-' }}</span>
          </div>
          <div v-if="appData.app.description" class="info-item info-item-full">
            <label>应用描述</label>
            <span>{{ appData.app.description }}</span>
          </div>
        </div>

        <div class="users-block">
          <div class="users-title">应用用户（{{ appData.users.length }}）</div>
          <table class="users-table">
            <thead>
              <tr>
                <th>用户名</th>
                <th>真实姓名</th>
                <th>手机号</th>
                <th>邮箱</th>
                <th>角色</th>
                <th>状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="u in appData.users" :key="u.id">
                <td>{{ u.username }}</td>
                <td>{{ u.realName || '-' }}</td>
                <td>{{ u.phone || '-' }}</td>
                <td>{{ u.email || '-' }}</td>
                <td>
                  <span
                    v-if="appData.app.creatorId === u.id"
                    class="role-creator"
                  >创建者</span>
                  <span v-else class="role-member">成员</span>
                </td>
                <td>
                  <span :class="u.enabled ? 'tag-ok' : 'tag-off'">
                    {{ u.enabled ? '启用' : '禁用' }}
                  </span>
                </td>
              </tr>
              <tr v-if="!appData.users.length">
                <td colspan="6" class="empty-row">暂无用户</td>
              </tr>
            </tbody>
          </table>
        </div>
      </template>
    </div>

    <!-- 邀请用户弹窗 -->
    <div v-if="showInviteModal" class="modal-mask" @click.self="closeInviteModal">
      <div class="modal">
        <div class="modal-header">
          <h3>邀请用户</h3>
          <button class="modal-close" @click="closeInviteModal">×</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>用户名 <span class="req">*</span></label>
            <input
              v-model.trim="inviteForm.username"
              type="text"
              placeholder="请输入用户名"
            />
          </div>
          <div class="form-item">
            <label>密码 <span class="req">*</span></label>
            <input
              v-model.trim="inviteForm.password"
              type="password"
              placeholder="请输入密码"
            />
          </div>
          <div class="form-item">
            <label>真实姓名</label>
            <input
              v-model.trim="inviteForm.realName"
              type="text"
              placeholder="请输入真实姓名"
            />
          </div>
          <div class="form-item">
            <label>手机号</label>
            <input
              v-model.trim="inviteForm.phone"
              type="tel"
              placeholder="请输入手机号"
            />
          </div>
          <div class="form-item">
            <label>邮箱</label>
            <input
              v-model.trim="inviteForm.email"
              type="email"
              placeholder="请输入邮箱"
            />
          </div>
          <div v-if="inviteError" class="error-msg">{{ inviteError }}</div>
          <div v-if="inviteSuccess" class="success-msg">
            {{ inviteSuccess }}
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeInviteModal">
            取消
          </button>
          <button
            class="btn btn-primary"
            :disabled="inviting"
            @click="handleInvite"
          >
            {{ inviting ? '邀请中...' : '确认邀请' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { profile, getCurrentApp, inviteUser } from '@/api/auth'
const auth = useAuthStore()

const photoInputRef = ref(null)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = reactive({
  email: '',
  realName: '',
  phone: '',
  appName: '',
  photo: '',
  id: null
})

// ===== 应用信息 =====
const appData = ref(null)
const appLoading = ref(false)
const showInviteModal = ref(false)
const inviting = ref(false)
const inviteError = ref('')
const inviteSuccess = ref('')

const inviteForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: ''
})

const creatorName = computed(() => {
  if (!appData.value) return '-'
  const creatorId = appData.value.app.creatorId
  const creator = appData.value.users.find((u) => u.id === creatorId)
  return creator ? creator.realName || creator.username : '-'
})

function formatDate(d) {
  if (!d) return '-'
  return String(d).replace('T', ' ').substring(0, 19)
}

async function loadAppInfo() {
  appLoading.value = true
  try {
    appData.value = await getCurrentApp()
  } catch (err) {
    appData.value = null
  } finally {
    appLoading.value = false
  }
}

function openInviteModal() {
  inviteError.value = ''
  inviteSuccess.value = ''
  showInviteModal.value = true
}

function closeInviteModal() {
  showInviteModal.value = false
  inviteError.value = ''
  inviteSuccess.value = ''
}

async function handleInvite() {
  inviteError.value = ''
  inviteSuccess.value = ''

  if (!inviteForm.username || !inviteForm.password) {
    inviteError.value = '用户名和密码不能为空'
    return
  }
  if (
    inviteForm.email &&
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(inviteForm.email)
  ) {
    inviteError.value = '邮箱格式不正确'
    return
  }

  inviting.value = true
  try {
    await inviteUser({ ...inviteForm })
    inviteSuccess.value = '邀请成功'
    await loadAppInfo()
    setTimeout(() => {
      showInviteModal.value = false
      Object.assign(inviteForm, {
        username: '',
        password: '',
        realName: '',
        phone: '',
        email: ''
      })
      inviteSuccess.value = ''
    }, 1200)
  } catch (err) {
    inviteError.value = err.message || '邀请失败'
  } finally {
    inviting.value = false
  }
}


const avatarText = computed(() => {
  const name = form.realName || auth.username || '用'
  return name.charAt(0).toUpperCase()
})

function loadForm() {
  form.email = auth.user?.email || auth.user?.emal || ''
  form.realName = auth.user?.realName || ''
  form.phone = auth.user?.phone || ''
  form.appName = auth.user?.appName || ''
  form.photo = auth.user?.photo || ''
  form.id = auth.user?.id || null
}

onMounted(() => {
  loadForm()
  loadAppInfo()
})

function resetForm() {
  loadForm()
  errorMsg.value = ''
  successMsg.value = ''
}

function triggerUpload() {
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
    successMsg.value = ''
  }
  reader.readAsDataURL(file)
}

async function handleSave() {
  errorMsg.value = ''
  successMsg.value = ''

  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errorMsg.value = '邮箱格式不正确'
    return
  }

  saving.value = true
  try {
    const data = await profile(form)

    // 更新 store
    auth.setUser(data.user ?? { ...auth.user, ...form })

    successMsg.value = data.message
    setTimeout(() => (successMsg.value = ''), 2000)
  } catch (err) {
    errorMsg.value = err.message || '保存失败'
  } finally {
    saving.value = false
  }
}

function closeWindow() {
  // 如果是通过 window.open 打开的，关闭当前窗口
  if (window.opener) {
    window.close()
  } else {
    // 否则返回上一页
    history.back()
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 24px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 0 4px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #1f2a44;
  font-weight: 600;
}

.btn-close {
  padding: 6px 14px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  background: #fff;
  color: #475467;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-close:hover {
  background: #f2f4f7;
}

.profile-body {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 24px;
  max-width: 900px;
  margin: 0 auto;
}

/* ===== 左侧 ===== */
.profile-left {
  background: #fff;
  border-radius: 12px;
  padding: 28px 20px;
  box-shadow: 0 2px 10px rgba(20, 35, 70, 0.06);
  text-align: center;
  height: fit-content;
}

.avatar-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.avatar-large {
  position: relative;
  width: 96px;
  height: 96px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4a7dff, #2b56d6);
  color: #fff;
  font-size: 36px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s;
}

.avatar-large:hover {
  transform: scale(1.03);
}

.avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-large:hover .avatar-mask {
  opacity: 1;
}

.hidden-input {
  display: none;
}

.username {
  margin: 8px 0 0;
  font-size: 15px;
  font-weight: 600;
  color: #1f2a44;
  word-break: break-all;
}

.role-tag {
  display: inline-block;
  margin: 0;
  padding: 2px 10px;
  font-size: 12px;
  color: #2b56d6;
  background: #eaf1ff;
  border-radius: 999px;
}

/* ===== 右侧 ===== */
.profile-right {
  background: #fff;
  border-radius: 12px;
  padding: 28px 28px 24px;
  box-shadow: 0 2px 10px rgba(20, 35, 70, 0.06);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2a44;
  padding-bottom: 12px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eef1f6;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-item label {
  font-size: 13px;
  color: #475467;
  font-weight: 500;
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
}

.form-item input:disabled {
  background: #f9fafb;
  color: #98a2b3;
  cursor: not-allowed;
}

.form-item input:focus:not(:disabled) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.error-msg {
  margin-top: 16px;
  padding: 8px 12px;
  color: #e53935;
  font-size: 13px;
  background: #fdecec;
  border-radius: 6px;
}

.success-msg {
  margin-top: 16px;
  padding: 8px 12px;
  color: #1b9e57;
  font-size: 13px;
  background: #eafaf1;
  border-radius: 6px;
}

.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eef1f6;
}

.btn {
  min-width: 110px;
  height: 40px;
  padding: 0 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s, opacity 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #4a7dff;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #2b56d6;
}

.btn-secondary {
  background: #f2f4f7;
  color: #344054;
}

.btn-secondary:hover:not(:disabled) {
  background: #e4e7ec;
}

/* ===== 应用信息 ===== */
.app-section {
  max-width: 900px;
  margin: 24px auto 0;
  background: #fff;
  border-radius: 12px;
  padding: 28px 28px 24px;
  box-shadow: 0 2px 10px rgba(20, 35, 70, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 12px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eef1f6;
}

.app-status {
  color: #98a2b3;
  font-size: 13px;
  padding: 8px 0;
}

.app-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-item-full {
  grid-column: 1 / -1;
}

.info-item label {
  font-size: 13px;
  color: #475467;
  font-weight: 500;
}

.info-item span {
  font-size: 14px;
  color: #1f2a44;
  word-break: break-all;
}

.info-item .mono {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 13px;
  color: #344054;
  background: #f4f6fa;
  padding: 6px 10px;
  border-radius: 6px;
}

.tag-ok {
  display: inline-block;
  width: fit-content;
  padding: 2px 10px;
  font-size: 12px;
  color: #1b9e57;
  background: #eafaf1;
  border-radius: 999px;
}

.tag-off {
  display: inline-block;
  width: fit-content;
  padding: 2px 10px;
  font-size: 12px;
  color: #e53935;
  background: #fdecec;
  border-radius: 999px;
}

/* 用户表格 */
.users-block {
  margin-top: 24px;
}

.users-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2a44;
  margin-bottom: 12px;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.users-table th,
.users-table td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #eef1f6;
  color: #344054;
}

.users-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #475467;
}

.users-table td.empty-row {
  text-align: center;
  color: #98a2b3;
}

.role-creator {
  display: inline-block;
  padding: 2px 10px;
  font-size: 12px;
  color: #2b56d6;
  background: #eaf1ff;
  border-radius: 999px;
}

.role-member {
  display: inline-block;
  padding: 2px 10px;
  font-size: 12px;
  color: #475467;
  background: #f2f4f7;
  border-radius: 999px;
}

.btn-sm {
  min-width: auto;
  height: 32px;
  padding: 0 14px;
  font-size: 13px;
}

/* ===== 弹窗 ===== */
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 420px;
  max-width: calc(100vw - 32px);
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 50px rgba(15, 23, 42, 0.25);
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #eef1f6;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2a44;
}

.modal-close {
  border: none;
  background: transparent;
  font-size: 22px;
  line-height: 1;
  color: #98a2b3;
  cursor: pointer;
}

.modal-close:hover {
  color: #1f2a44;
}

.modal-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.modal-body .form-item input {
  height: 38px;
  padding: 0 12px;
  border: 1px solid #d0d5dd;
  border-radius: 8px;
  font-size: 14px;
  color: #222;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.modal-body .form-item input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.req {
  color: #e53935;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #eef1f6;
  background: #fafbfc;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .profile-body {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .app-info-grid {
    grid-template-columns: 1fr;
  }

  .users-table {
    font-size: 12px;
  }

  .users-table th,
  .users-table td {
    padding: 8px 6px;
  }
}

</style>