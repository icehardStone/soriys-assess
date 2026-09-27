<template>
  <div class="assessment-detail">
    <div class="page-title no-print">
      <div>
        <h2>评估记录详情</h2>
        <p>{{ record?.no || '评估记录' }}</p>
      </div>
      <div class="actions">
        <button class="ghost" @click="router.push('/main/assessments')">返回列表</button>
        <button class="primary" :disabled="loading || !record" @click="printRecord">打印记录</button>
      </div>
    </div>

    <p v-if="loading" class="state-message">正在加载评估记录…</p>
    <p v-else-if="errorMessage" class="state-message error">{{ errorMessage }}</p>

    <article v-else-if="record" id="printable-record" class="printable-record">
      <header class="record-header">
        <p class="standard">GB/T 42195-2022</p>
        <h1>老年人能力评估记录</h1>
        <p>评估编号：{{ display(record.no) }}</p>
      </header>

      <section class="record-section summary">
        <h2>评估结论</h2>
        <div class="result-grid">
          <div><span>评估日期</span><strong>{{ display(record.assessmentDate) }}</strong></div>
          <div><span>评估原因</span><strong>{{ reasonName(record.reason) }}</strong></div>
          <div><span>总分</span><strong>{{ record.totalScore ?? 0 }} / 90</strong></div>
          <div><span>最终等级</span><strong>{{ levelName(record.finalLevel) }}</strong></div>
        </div>
      </section>

      <section class="record-section">
        <h2>A. 基本信息</h2>
        <dl class="detail-grid">
          <div><dt>姓名</dt><dd>{{ display(record.basic?.name) }}</dd></div>
          <div><dt>性别</dt><dd>{{ genderName(record.basic?.gender) }}</dd></div>
          <div><dt>出生日期</dt><dd>{{ display(record.basic?.birthDate) }}</dd></div>
          <div><dt>公民身份号码</dt><dd>{{ display(record.basic?.idNo) }}</dd></div>
          <div><dt>身高</dt><dd>{{ unit(record.basic?.height, 'cm') }}</dd></div>
          <div><dt>体重</dt><dd>{{ unit(record.basic?.weight, 'kg') }}</dd></div>
          <div><dt>民族</dt><dd>{{ display(record.basic?.ethnicity) }}</dd></div>
          <div><dt>宗教信仰</dt><dd>{{ religionName(record.basic?.religion) }}</dd></div>
          <div><dt>文化程度</dt><dd>{{ educationName(record.basic?.education) }}</dd></div>
          <div><dt>居住情况</dt><dd>{{ listDisplay(record.basic?.living, livingNames) }}</dd></div>
          <div><dt>婚姻状况</dt><dd>{{ marriageName(record.basic?.marriage) }}</dd></div>
        </dl>
      </section>

      <section class="record-section">
        <h2>A. 相关信息</h2>
        <dl class="detail-grid">
          <div><dt>信息提供者</dt><dd>{{ display(record.provider?.name) }}</dd></div>
          <div><dt>与老人关系</dt><dd>{{ providerRelationName(record.provider?.relation) }}</dd></div>
          <div><dt>联系人</dt><dd>{{ display(record.provider?.contact) }}</dd></div>
          <div><dt>联系电话</dt><dd>{{ display(record.provider?.phone) }}</dd></div>
          <div class="wide"><dt>疾病诊断</dt><dd>{{ diseases }}</dd></div>
          <div class="wide"><dt>用药情况</dt><dd>{{ medications }}</dd></div>
          <div class="wide"><dt>近 30 天照护风险事件</dt><dd>{{ riskSummary }}</dd></div>
        </dl>
      </section>

      <section class="record-section">
        <h2>B. 能力评估明细</h2>
        <div v-for="category in categories" :key="category.name" class="category">
          <h3>{{ category.name }} <span>{{ categoryScore(category) }} / {{ category.maxScore }}</span></h3>
          <table>
            <thead><tr><th>项目</th><th>评估结果</th><th>得分</th></tr></thead>
            <tbody>
              <tr v-for="item in category.items" :key="item.code">
                <td>{{ item.code }} {{ item.name }}</td>
                <td>{{ answerLabel(item) }}</td>
                <td>{{ score(item) }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <footer class="record-footer">
        <span>初步等级：{{ levelName(record.initialLevel) }}</span>
        <span>打印日期：{{ printedDate }}</span>
      </footer>
    </article>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAssessment } from '@/api/assess'

const route = useRoute()
const router = useRouter()
const record = ref(null)
const items = ref([])
const loading = ref(true)
const errorMessage = ref('')

const labels = {
  Male: '男', Female: '女', NoReligion: '无', HasReligion: '有',
  Illiterate: '文盲', 'Primary school': '小学', 'Junior high school': '初中',
  'High school / technical school / secondary specialized school': '高中/技校/中专',
  'College diploma or above': '大学专科及以上', Unknown: '不详',
  Unmarried: '未婚', Married: '已婚', Widowed: '丧偶', Divorced: '离婚', 'Not specified': '未说明',
  'Initial assessment': '首次评估', 'Routine assessment': '常规评估', 'Immediate assessment': '即时评估',
  'Reassessment due to doubts about the assessment result': '因对评估结果有疑问进行的复评', Other: '其他',
  Self: '本人', Spouse: '配偶', Children: '子女', OtherRelatives: '其他亲属',
  HiredCaregiver: '雇佣照顾者', VillageOrResidentsCommitteeStaff: '村（居）民委员会工作人员',
}
const livingNames = {
  'Living alone': '独居', 'Living with spouse': '与配偶居住', 'Living with children': '与子女居住',
  'Living with parents': '与父母居住', 'Living with siblings': '与兄弟姐妹居住',
  'Living with other relatives': '与其他亲属居住', 'Living with non-relatives': '与非亲属关系的人居住', 'Nursing home': '养老机构',
}
const riskNames = { Falls: '跌倒', Wandering: '走失', Choking: '噎食', 'Suicide/self-harm': '自杀、自伤', Other: '其他' }
const riskTimes = ['无', '发生过 1 次', '发生过 2 次', '发生过 3 次及以上']
const levelNames = ['能力完好', '轻度失能', '中度失能', '重度失能', '能力完全丧失（完全失能）']

const categories = computed(() => {
  const definitions = [
    { name: '自理能力', maxScore: 32 }, { name: '基础运动能力', maxScore: 15 },
    { name: '精神状态', maxScore: 29 }, { name: '感知觉与社会参与', maxScore: 15 },
  ]
  return definitions.map((category) => ({ ...category, items: items.value.filter((item) => item.category === category.name) }))
})
const diseases = computed(() => {
  const selected = Array.isArray(record.value?.disease) ? record.value.disease : []
  return [...selected, record.value?.diseaseOther].filter(Boolean).join('；') || '—'
})
const medications = computed(() => {
  const values = Array.isArray(record.value?.medications) ? record.value.medications : []
  return values.filter((medicine) => medicine?.name)
    .map((medicine) => [medicine.name, medicine.method, medicine.dose, medicine.frequency].filter(Boolean).join('，'))
    .join('；') || '—'
})
const riskSummary = computed(() => Object.entries(record.value?.risks || {})
  .filter(([, count]) => Number(count) > 0)
  .map(([name, count]) => `${riskNames[name] || name}：${riskTimes[Number(count)] || count}`)
  .join('；') || '无')
const printedDate = new Intl.DateTimeFormat('zh-CN').format(new Date())

const display = (value) => value === undefined || value === null || value === '' ? '—' : value
const unit = (value, suffix) => value === undefined || value === null || value === '' ? '—' : `${value} ${suffix}`
const named = (value) => labels[value] || display(value)
const genderName = named
const religionName = named
const educationName = named
const marriageName = named
const reasonName = named
const providerRelationName = named
const listDisplay = (values, names) => Array.isArray(values) && values.length ? values.map((value) => names[value] || value).join('；') : '—'
const score = (item) => record.value?.answers?.[item.code] ?? '—'
const answerLabel = (item) => item.options?.find((option) => Number(option.score) === Number(record.value?.answers?.[item.code]))?.label || '—'
const categoryScore = (category) => category.items.reduce((total, item) => total + (Number(record.value?.answers?.[item.code]) || 0), 0)
const levelName = (level) => level === undefined || level === null ? '—' : levelNames[Number(level)] || `等级 ${level}`
function printRecord() { window.print() }

onMounted(async () => {
  try {
    const [assessment, definition] = await Promise.all([
      getAssessment(Number(route.params.id)), fetch('/data/assessment-data.json').then((response) => response.json()),
    ])
    record.value = assessment
    items.value = definition.items || []
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : '评估记录加载失败，请稍后重试。'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.actions { display: flex; gap: 10px; }
.actions button { margin-right: 0; }
.state-message { padding: 32px; text-align: center; color: #667085; }
.state-message.error { color: #b42318; }
.printable-record { max-width: 960px; margin: 0 auto; background: #fff; border: 1px solid #e7eaf0; border-radius: 12px; padding: 42px; }
.record-header { text-align: center; border-bottom: 2px solid #172033; padding-bottom: 18px; }
.record-header p { margin: 5px 0 0; color: #475467; font-size: 14px; }
.record-header .standard { color: #667085; }
h1 { margin: 10px 0; font-size: 28px; letter-spacing: 3px; }
.record-section { margin-top: 28px; }
.record-section h2 { font-size: 18px; margin: 0 0 14px; padding-bottom: 8px; border-bottom: 1px solid #dfe4ec; }
.result-grid, .detail-grid { display: grid; grid-template-columns: repeat(4, 1fr); border: 1px solid #dfe4ec; }
.result-grid div, .detail-grid div { min-height: 68px; padding: 12px 14px; border-right: 1px solid #dfe4ec; border-bottom: 1px solid #dfe4ec; }
.result-grid div:last-child { border-right: 0; }
.result-grid span, dt { display: block; margin-bottom: 7px; color: #667085; font-size: 12px; }
.result-grid strong, dd { margin: 0; font-size: 14px; font-weight: 600; word-break: break-word; }
.detail-grid { grid-template-columns: repeat(3, 1fr); }
.detail-grid div:nth-child(3n) { border-right: 0; }
.detail-grid .wide { grid-column: 1 / -1; border-right: 0; }
.category { margin-top: 20px; }
.category h3 { display: flex; justify-content: space-between; font-size: 15px; margin: 0; padding: 10px 12px; background: #f5f7fb; }
table { width: 100%; border-collapse: collapse; font-size: 13px; }
th, td { border: 1px solid #dfe4ec; padding: 9px 10px; text-align: left; vertical-align: top; }
th { background: #fafbfc; color: #475467; font-weight: 600; }
th:last-child, td:last-child { width: 70px; text-align: center; }
.record-footer { display: flex; justify-content: space-between; border-top: 1px solid #dfe4ec; margin-top: 28px; padding-top: 14px; color: #667085; font-size: 13px; }
@media (max-width: 800px) { .printable-record { padding: 22px; } .result-grid, .detail-grid { grid-template-columns: repeat(2, 1fr); } .detail-grid div:nth-child(3n) { border-right: 1px solid #dfe4ec; } .detail-grid div:nth-child(2n) { border-right: 0; } }
@media print { .no-print { display: none !important; } .assessment-detail { background: #fff; } .printable-record { max-width: none; border: 0; border-radius: 0; padding: 0; } .record-section { break-inside: avoid; } }
</style>
