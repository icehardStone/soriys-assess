<template>
  <div>
    <div class="page-title">
      <div>
        <h2>评估工作台</h2>
        <p>管理老年人能力评估、查看结果与历史记录</p>
      </div>
      <button class="primary" @click="startNew">开始一次评估 →</button>
    </div>

    <div class="stats" aria-label="评估统计">
      <div class="stat"><span>评估总数</span><b>{{ statistics.total }}</b><em>份记录</em></div>
      <div class="stat"><span>能力完好</span><b>{{ statistics.level0 }}</b><em>等级 0</em></div>
      <div class="stat"><span>轻度/中度受损</span><b>{{ mildOrModerate }}</b><em>等级 1-2</em></div>
      <div class="stat"><span>重度/完全丧失</span><b>{{ severeOrLost }}</b><em>等级 3-4</em></div>
    </div>

    <div class="panel">
      <div class="panel-head">
        <h3>最近评估</h3>
        <RouterLink class="text-btn" to="/main/assessments">查看全部 →</RouterLink>
      </div>
      <p v-if="errorMessage" class="load-error">{{ errorMessage }}</p>
      <table v-else>
        <thead>
          <tr><th>评估编号</th><th>老人姓名</th><th>评估日期</th><th>总分</th><th>最终等级</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="record in records" :key="record.id">
            <td>{{ record.no }}</td>
            <td><b>{{ record.basic?.name || '未填写' }}</b></td>
            <td>{{ record.assessmentDate }}</td>
            <td><strong>{{ record.totalScore ?? 0 }}</strong>/90</td>
            <td><span :class="`tag l${record.finalLevel}`">{{ levelName(record.finalLevel) }}</span></td>
            <td><button class="link" @click="viewRecord(record.id)">查看</button><button class="link" @click="editRecord(record.id)">编辑</button></td>
          </tr>
          <tr v-if="!loading && !records.length"><td colspan="6" class="empty">暂无评估记录，点击“开始一次评估”开始。</td></tr>
          <tr v-if="loading"><td colspan="6" class="empty">正在加载评估数据…</td></tr>
        </tbody>
      </table>
    </div>

    <div class="info-grid">
      <div class="info-card"><b>标准结构</b><p>4 个一级指标 · 26 个二级指标 · 满分 90 分</p></div>
      <div class="info-card"><b>等级规则</b><p>90 / 66–89 / 46–65 / 30–45 / 0–29</p></div>
      <div class="info-card"><b>等级变更</b><p>昏迷直接完全丧失；特定疾病或风险事件可上调一级</p></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import {
  getAssessmentStatistics,
  pageAssessments,
  type AssessmentRecord,
  type AssessmentStatistics,
} from '@/api/assess'

const router = useRouter()
const records = ref<AssessmentRecord[]>([])
const loading = ref(true)
const errorMessage = ref('')
const statistics = ref<AssessmentStatistics>({ total: 0, level0: 0, level1: 0, level2: 0, level3: 0, level4: 0 })
const mildOrModerate = computed(() => statistics.value.level1 + statistics.value.level2)
const severeOrLost = computed(() => statistics.value.level3 + statistics.value.level4)

const levelNames = ['能力完好', '轻度受损', '中度受损', '重度受损', '能力完全丧失']
function levelName(level?: number) { return level === undefined || level === null ? '未评估' : (levelNames[level] || '未评估') }
function startNew() { router.push({ name: 'AssessmentNew' }) }
function viewRecord(id: number) { router.push({ name: 'AssessmentDetail', params: { id } }) }
function editRecord(id: number) { router.push({ name: 'AssessmentEdit', params: { id } }) }

onMounted(async () => {
  try {
    const [stats, page] = await Promise.all([getAssessmentStatistics(), pageAssessments({ current: 1, size: 6 })])
    statistics.value = stats
    records.value = page.records
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '首页数据加载失败，请稍后重试。'
  } finally {
    loading.value = false
  }
})
</script>
