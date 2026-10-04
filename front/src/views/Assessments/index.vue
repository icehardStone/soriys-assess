<template>
  <div>
    <div class="page-title">
      <div>
        <h2>评估记录</h2>
        <p>共 {{ total }} 条记录</p>
      </div>
      <button class="primary" @click="startNew">＋ 新建评估</button>
    </div>
    <div class="panel">
      <div class="toolbar">
        <input
          v-model="keyword"
          placeholder="搜索姓名 / 评估编号 / 身份证号"
          @input="onKeywordChange"
        />
        <select v-model="levelFilter" @change="onFilterChange">
          <option value="">全部等级</option>
          <option v-for="l in levels" :value="l.level">{{ l.name }}</option>
        </select>
      </div>
      <table>
        <thead>
          <tr>
            <th>评估编号</th>
            <th>姓名</th>
            <th>评估日期</th>
            <th>评估原因</th>
            <th>总分</th>
            <th>最终等级</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in records" :key="r.id">
            <td>{{ r.no }}</td>
            <td>{{ r.basic?.name }}</td>
            <td>{{ r.assessmentDate }}</td>
            <td>{{ r.reason }}</td>
            <td>{{ r.totalScore }}/90</td>
            <td><span :class="'tag l' + r.finalLevel">{{ levelName(r.finalLevel) }}</span></td>
            <td>
              <button class="link" @click="viewRecord(r)">查看</button>
              <button class="link" @click="editRecord(r)">编辑</button>
              <button class="danger-link" @click="removeRecord(r.id)">删除</button>
            </td>
          </tr>
          <tr v-if="loading">
            <td colspan="7" class="empty">加载中...</td>
          </tr>
          <tr v-else-if="!records.length">
            <td colspan="7" class="empty">没有符合条件的记录</td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <span class="page-info">第 {{ current }} / {{ pages }} 页</span>
        <div class="page-actions">
          <button class="page-btn" :disabled="current <= 1" @click="goPage(current - 1)">上一页</button>
          <button
            v-for="p in pageNumbers"
            :key="p"
            class="page-btn"
            :class="{ active: p === current }"
            @click="goPage(p)"
          >{{ p }}</button>
          <button class="page-btn" :disabled="current >= pages" @click="goPage(current + 1)">下一页</button>
          <select v-model.number="size" class="page-size" @change="onSizeChange">
            <option :value="10">10 条/页</option>
            <option :value="20">20 条/页</option>
            <option :value="50">50 条/页</option>
          </select>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteAssessment, pageAssessments } from '@/api/assess'

const records = ref([])
const levels = ref([])
const router = useRouter()

// 筛选条件
const keyword = ref('')
const levelFilter = ref('')

// 分页状态
const current = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)

const pages = computed(() => Math.max(1, Math.ceil(total.value / size.value)))

// 生成分页页码（最多显示 7 个，含省略）
const pageNumbers = computed(() => {
  const totalPages = pages.value
  const cur = current.value
  if (totalPages <= 7) {
    return Array.from({ length: totalPages }, (_, i) => i + 1)
  }
  const nums = new Set([1, totalPages, cur, cur - 1, cur + 1])
  const sorted = [...nums].filter(n => n >= 1 && n <= totalPages).sort((a, b) => a - b)
  const result = []
  for (let i = 0; i < sorted.length; i++) {
    if (i > 0 && sorted[i] - sorted[i - 1] > 1) {
      result.push('...')
    }
    result.push(sorted[i])
  }
  return result
})

function levelName(n) {
  return levels.value.find(l => l.level === n)?.name || '未评估'
}

async function loadData() {
  loading.value = true
  try {
    const res = await pageAssessments({
      current: current.value,
      size: size.value,
      keyword: keyword.value.trim() || undefined,
      level: levelFilter.value === '' ? undefined : Number(levelFilter.value),
    })
    records.value = res.records || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function startNew() {
  router.push({ path: '/assessments/new' })
}

function viewRecord(r) {
  router.push({
    name: 'AssessmentDetail',
    params: { id: r.id },
  })
}

const editRecord = (r) => {
  router.push({
    name: 'AssessmentEdit',
    params: { id: r.id },
  })
}

async function removeRecord(id) {
  if (confirm('确定删除这条评估记录吗？')) {
    await deleteAssessment(id)
    // 删除后若当前页为空且不是第一页，回退一页
    if (records.value.length === 1 && current.value > 1) {
      current.value--
    }
    loadData()
  }
}

function goPage(p) {
  if (p < 1 || p > pages.value || p === current.value) return
  current.value = p
  loadData()
}

function onSizeChange() {
  current.value = 1
  loadData()
}

function onFilterChange() {
  current.value = 1
  loadData()
}

// 关键词输入防抖
let keywordTimer = null
function onKeywordChange() {
  if (keywordTimer) clearTimeout(keywordTimer)
  keywordTimer = setTimeout(() => {
    current.value = 1
    loadData()
  }, 300)
}

onMounted(async () => {
  const res = await fetch('/data/assessment-data.json')
  const d = await res.json()
  levels.value = d.levels
  loadData()
})
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18px;
  flex-wrap: wrap;
  gap: 10px;
}

.page-info {
  color: #667085;
  font-size: 13px;
}

.page-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.page-btn {
  border: 1px solid #d8dee9;
  background: #fff;
  color: #475467;
  border-radius: 7px;
  padding: 7px 12px;
  font-size: 13px;
  min-width: 34px;
  cursor: pointer;
  transition: all 0.15s;
}

.page-btn:hover:not(:disabled) {
  border-color: #7b9be8;
  color: #2457d6;
}

.page-btn.active {
  background: #2457d6;
  border-color: #2457d6;
  color: #fff;
}

.page-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.page-size {
  width: auto;
  padding: 7px 9px;
  font-size: 13px;
  margin-left: 6px;
}
</style>
