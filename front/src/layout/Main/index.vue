
<template>
  <div class="app">
    <header class="topbar">
      <div class="brand">
        <div class="logo">评</div>
        <div>
          <h1>老年人能力评估管理系统</h1><span>GB/T 42195-2022 · 2022版</span>
        </div>
      </div>
      <div class="top-actions"><button class="ghost" @click="exportJson">导出数据</button>
 <!-- 个人信息 + 悬停下拉 -->
        <div class="user-info" @mouseenter="showMenu = true" @mouseleave="showMenu = false">
        <!-- 头像：有图片用 img，否则显示首字母 -->
          <div class="avatar">
            <img v-if="auth.photo" :src="auth.photo" alt="头像" />
            <span v-else>{{ avatarText }}</span>
          </div>
 <div class="user-meta">
            <div class="user-name">{{ auth.realName || auth.username }}</div>
            <div class="user-role">{{ auth.email }}</div>
          </div>
          <span class="caret" :class="{ open: showMenu }">▾</span>

          <transition name="fade">
            <div v-show="showMenu" class="dropdown">
              <div class="dropdown-item" @click="goProfile">
                <span class="icon">👤</span> 个人信息
              </div>
              <div class="dropdown-item" @click="goSettings">
                <span class="icon">⚙️</span> 账号设置
              </div>
              <div class="dropdown-divider"></div>
              <div class="dropdown-item danger" @click="handleLogout">
                <span class="icon">⎋</span> 退出登录
              </div>
            </div>
          </transition>
        </div>
        
        </div>
    </header>
    <div class="layout">
<aside class="sidebar">
  <RouterLink
    v-for="m in menus"
    :key="m.key"
    :to="m.path"
    class="menu"
    active-class="active"
  >
    <span>{{ m.icon }}</span>
    {{ m.label }}
  </RouterLink>

  <div class="side-note">
    <b>评估标准</b><br>
    GB/T 42195-2022<br>
    <small>老年人能力评估管理系统</small>
  </div>
</aside>
      <main class="content">
  <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
// 头像文字：取真实姓名或用户名的第一个字
const avatarText = computed(() => {
  const name = auth.realName || auth.username || '用'
  return name.charAt(0).toUpperCase()
})

const menus = [
  {
    key: 'dashboard',
    label: '评估工作台',
    icon: '⌂',
    path: '/main/dashboard'
  },
  {
    key: 'records',
    label: '评估记录',
    icon: '▤',
    path: '/main/assessments'
  },
  {
    key: 'assessment',
    label: '新建评估',
    icon: '＋',
    path: '/main/assessments/new'
  },
  {
    key: 'standard',
    label: '标准与规则',
    icon: '◎',
    path: '/main/standards'
  }
]

const records = ref([])
const showMenu = ref(false)

function exportJson() { 
  const blob = new Blob([JSON.stringify(records.value, null, 2)], { type: 'application/json' }); 
  const a = document.createElement('a'); 
  a.href = URL.createObjectURL(blob); 
  a.download = 'elderly-assessment-records.json'; 
  a.click(); 
  URL.revokeObjectURL(a.href) 
}

onMounted(async () => { 
      records.value = JSON.parse(localStorage.getItem('elderlyAssessmentRecords') || '[]') 
})


function goProfile() {
  showMenu.value = false
  const { href } = router.resolve('/profile')
  window.open(href, '_blank')
}

function goSettings() {
  showMenu.value = false
  const { href } = router.resolve('/settings')
  window.open(href, '_blank')
}

// function goProfile() {
//   showMenu.value = false
//   router.push('/profile')

// }

// function goSettings() {
//   showMenu.value = false
//   router.push('/settings')
// }

function handleLogout() {
  showMenu.value = false
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    router.push('/login')
  }
}

</script>

<style scoped>

/* 用户信息区 */
.user-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px 6px 8px;
  background: #f4f6fa;
  border-radius: 999px;
  cursor: pointer;
  transition: background 0.2s;
  user-select: none;
}

.user-info:hover {
  background: #e9eef7;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4a7dff, #2b56d6);
  color: #fff;
  font-weight: 600;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.avatar img {
   width: 36px;
  height: 36px;
   border-radius: 50%;
}
.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
  
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2a44;
}

.user-role {
  font-size: 12px;
  color: #7a869a;
}

.caret {
  font-size: 10px;
  color: #7a869a;
  transition: transform 0.2s;
}

.caret.open {
  transform: rotate(180deg);
}

/* 下拉菜单 */
.dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 160px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(20, 35, 70, 0.14);
  padding: 6px;
  z-index: 999;
}

/* 用一个透明区域连接 user-info 和 dropdown，避免鼠标经过间隙时消失 */
.dropdown::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 0;
  right: 0;
  height: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border-radius: 6px;
  font-size: 14px;
  color: #1f2a44;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
}

.dropdown-item:hover {
  background: #f1f4fb;
  color: #2b56d6;
}

.dropdown-item .icon {
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.dropdown-item.danger:hover {
  background: #fff1f1;
  color: #d9363e;
}

.dropdown-divider {
  height: 1px;
  background: #eef1f6;
  margin: 4px 6px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
