<template>
  <div>
    <div class="page-title">
      <div>
        <h2>{{ editingId ? '编辑评估' : '新建能力评估' }}</h2>
        <p>{{ form.no }} · {{ form.date }}</p>
      </div>
      <div>
      <button class="ghost" @click="router.push('/assessments')">取消</button>
        <button class="primary" @click="saveAssessment">保存评估</button>
      </div>
    </div>
    <div class="steps">
      <span :class="{ on: step >= 1 }">1 基本信息</span><i></i
      ><span :class="{ on: step >= 2 }">2 能力评估</span><i></i
      ><span :class="{ on: step >= 3 }">3 结果报告</span>
    </div>

    <div v-if="step === 1" class="panel form-panel">
      <h3>A.1 评估信息</h3>
      <div class="form-grid">
        <label>评估编号<input v-model="form.no" /></label
        ><label>评估基准日期<input type="date" v-model="form.date" /></label>
        <label>
          评估原因
          <select v-model="form.reason">
            <option
              v-for="item in assessmentReasonOptions"
              :key="item.dictVal"
              :value="item.dictVal"
            >
              {{ item.dictName }}
            </option>
          </select>
        </label>
      </div>
      <h3>A.2 评估对象基本信息</h3>
      <div class="form-grid">
        <label>姓名 *<input v-model="form.basic.name" placeholder="请输入姓名" /></label>
        <label>
          性别
          <select v-model="form.basic.gender">
            <option v-for="item in genderOptions" :key="item.dictVal" :value="item.dictVal">
              {{ item.dictName }}
            </option>
          </select>
        </label>
        <label>出生日期<input type="date" v-model="form.basic.birthDate" /></label>
        <label>身高（cm）<input type="number" v-model.number="form.basic.height" /></label
        ><label>体重（kg）<input type="number" v-model.number="form.basic.weight" /></label>
        <label>民族<input v-model="form.basic.ethnicity" /></label>
        <label>
          宗教信仰
          <select v-model="form.basic.religion">
            <option v-for="item in religionOptions" :key="item.dictVal" :value="item.dictVal">
              {{ item.dictName }}
            </option>
          </select>
        </label>
        <label>公民身份号码<input v-model="form.basic.idNo" /></label>

        <label>
          文化程度
          <select v-model="form.basic.education">
            <option v-for="item in educationOptions" :key="item.dictVal" :value="item.dictVal">
              {{ item.dictName }}
            </option>
          </select>
        </label>
        <label class="wide"
          >居住情况（多选）
          <div class="checks">
            <label v-for="x in lives">
              <input type="checkbox" :value="x.dictVal" v-model="form.basic.living" />
              {{ x.dictName }}
            </label>
          </div>
        </label>
        <label>
          婚姻状况
          <select v-model="form.basic.marriage">
            <option v-for="item in marriageOptions" :key="item.dictVal" :value="item.dictVal">
              {{ item.dictName }}
            </option>
          </select>
        </label>
      </div>
      <h3>A.2.14 近30天照护风险事件</h3>
      <div class="risk-grid">
        <label v-for="(k,i) in riskOptions"
          >{{ k.dictName
          }}<select v-model.number="form.risks[k.dictVal]">
            <option :value="0">无</option>
            <option :value="1">发生过1次</option>
            <option :value="2">发生过2次</option>
            <option :value="3">发生过3次及以上</option>
          </select></label
        >
      </div>
      <h3>A.3 信息提供者及联系人</h3>
      <div class="form-grid">
        <label>信息提供者姓名<input v-model="form.provider.name" /></label>

        <label>
          与老年人关系
          <select v-model="form.provider.relation">
            <option
              v-for="item in providerRelationOptions"
              :key="item.dictVal"
              :value="item.dictVal"
            >
              {{ item.dictName }}
            </option>
          </select>
        </label>

        <label>联系人姓名<input v-model="form.provider.contact" /></label
        ><label>联系人电话<input v-model="form.provider.phone" /></label>
      </div>
      <h3>A.4 疾病诊断与用药</h3>
      <div class="checks disease">
        <label v-for="x in diseases">
          <input type="checkbox" :value="x.dictVal" v-model="form.disease" /> {{ x.dictName }}
        </label>
      </div>
      <input class="full" v-model="form.diseaseOther" placeholder="其他疾病（如有）" />
      <div class="meds">
        <div v-for="(m, i) in form.medications" class="med-row">
          <input v-model="m.name" placeholder="药物名称" /><input
            v-model="m.method"
            placeholder="服药方法"
          /><input v-model="m.dose" placeholder="用药剂量" /><input
            v-model="m.frequency"
            placeholder="用药频率"
          /><button @click="form.medications.splice(i, 1)">×</button>
        </div>
        <button
          class="ghost"
          @click="form.medications.push({ name: '', method: '', dose: '', frequency: '' })"
        >
          ＋ 添加药物
        </button>
      </div>
      <h3>A.5 健康相关问题</h3>
      <div class="health-grid">
        <label>
          压力性损伤
          <select v-model="form.health.pressure">
            <option v-for="item in pressureInjuryOptions" :key="item.dictVal" :value="item.dictVal">
              {{ item.dictName }}
            </option>
          </select>
        </label>
      </div>
      <div class="footer-actions">
        <button class="primary big" @click="step = 2">下一步：开始能力评估 →</button>
      </div>
    </div>

    <div v-else-if="step === 2">
      <div v-for="cat in categories" :key="cat.code" class="panel score-panel">
        <div class="score-head">
          <div>
            <h3>{{ cat.name }}</h3>
            <p>
              {{
                cat.name === '自理能力'
                  ? 'B.1'
                  : cat.name === '基础运动能力'
                    ? 'B.2'
                    : cat.name === '精神状态'
                      ? 'B.3'
                      : 'B.4'
              }}
              · 逐项选择最符合实际情况的描述
            </p>
          </div>
          <strong>{{ categoryScore(cat.name) }} / {{ cat.maxScore }}</strong>
        </div>
        <div v-for="it in groupedItems[cat.name]" :key="it.code" class="question">
          <div class="q-title">
            <b>{{ it.code }} {{ it.name }}</b
            ><span v-if="answers[it.code] !== undefined">{{ answers[it.code] }} 分</span>
          </div>
          <p>{{ it.description }}</p>
          <div class="options">
            <label v-for="o in it.options" :class="{ selected: answers[it.code] === o.score }"
              ><input
                type="radio"
                :name="it.code"
                :value="o.score"
                v-model.number="answers[it.code]"
              /><b>{{ o.score }}分</b><span>{{ o.label }}</span></label
            >
          </div>
        </div>
      </div>
      <div class="sticky-result">
        <div>
          当前总分 <b>{{ totalScore }}</b> / 90
        </div>
        <button class="ghost" @click="step = 1">← 上一步</button
        ><button class="primary big" @click="step = 3">查看评估结果 →</button>
      </div>
    </div>

    <div v-else class="report">
      <div class="report-card hero">
        <div>
          <span>老年人能力最终等级</span>
          <h2>{{ finalLevel.name }}</h2>
          <p>{{ finalLevel.level }} 级 · 总分 {{ totalScore }} / 100</p>
        </div>
        <div class="score-circle">{{ totalScore }}</div>
      </div>
      <div class="panel">
        <h3>能力评估报告</h3>
        <div class="report-grid">
          <div v-for="cat in categories" class="report-stat">
            <span>{{ cat.name }}</span
            ><b>{{ categoryScore(cat.name) }} / {{ cat.maxScore }}</b>
            <div class="bar">
              <i :style="{ width: (categoryScore(cat.name) / cat.maxScore) * 100 + '%' }"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="panel warning" v-if="changeReasons.length">
        <h3>能力等级变更依据</h3>
        <ul>
          <li v-for="x in changeReasons">{{ x }}</li>
        </ul>
        <p>根据标准，符合条件时在原有能力级别上提高一个等级；昏迷直接评定为能力完全丧失。</p>
      </div>
      <div class="panel">
        <h3>评估结论</h3>
        <p>
          评估对象：<b>{{ form.basic.name || '未填写' }}</b
          >。本次初步得分为 <b>{{ totalScore }}</b> 分，初步等级为 <b>{{ initialLevel.name }}</b
          >。{{ changeReasons.length ? '根据等级变更规则，最终等级为' : '最终等级为' }}
          <b>{{ finalLevel.name }}</b
          >。
        </p>
      </div>
      <div class="footer-actions">
        <button class="ghost" @click="step = 2">← 返回修改</button
        ><button class="primary big" @click="saveAssessment">保存并完成评估</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { createAssessment,updateAssessment,getAssessment } from '@/api/assess'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const page = ref('dashboard'),
  step = ref(1)


// 从路由参数获取 editingId（字符串或 undefined）
const editingId = ref(route.params.id || null)

const categories = ref([]),
  items = ref([]),
  levels = ref([]),
  records = ref([])


// 风险
const riskOptions = [
  { dictVal: 'Falls', dictName: '跌倒' },
  { dictVal: 'Wandering', dictName: '走失' }, // 或 Getting lost
  { dictVal: 'Choking', dictName: '噎食' },
  { dictVal: 'Suicide/self-harm', dictName: '自杀、自伤' },
  { dictVal: 'Other', dictName: '其他' },
]

// 文化程度
const educationOptions = [
  { dictVal: 'Illiterate', dictName: '文盲' },
  { dictVal: 'Primary school', dictName: '小学' },
  { dictVal: 'Junior high school', dictName: '初中' },
  {
    dictVal: 'High school / technical school / secondary specialized school',
    dictName: '高中/技校/中专',
  },
  { dictVal: 'College diploma or above', dictName: '大学专科及以上' },
  { dictVal: 'Unknown', dictName: '不详' },
]

// 宗教信仰
const religionOptions = [
  { dictVal: 'NoReligion', dictName: '无' },
  { dictVal: 'HasReligion', dictName: '有' },
]

// 性别
const genderOptions = [
  { dictVal: 'Male', dictName: '男' },
  { dictVal: 'Female', dictName: '女' },
]
// 评估原因
const assessmentReasonOptions = [
  { dictVal: 'Initial assessment', dictName: '首次评估' },
  { dictVal: 'Routine assessment', dictName: '常规评估' },
  { dictVal: 'Immediate assessment', dictName: '即时评估' },
  {
    dictVal: 'Reassessment due to doubts about the assessment result',
    dictName: '因对评估结果有疑问进行的复评',
  },
  { dictVal: 'Other', dictName: '其他' },
]
// 与老年人关系
const providerRelationOptions = [
  { dictVal: 'Self', dictName: '本人' },
  { dictVal: 'Spouse', dictName: '配偶' },
  { dictVal: 'Children', dictName: '子女' },
  { dictVal: 'OtherRelatives', dictName: '其他亲属' },
  { dictVal: 'HiredCaregiver', dictName: '雇佣照顾者' },
  { dictVal: 'VillageOrResidentsCommitteeStaff', dictName: '村（居）民委员会工作人员' },
  { dictVal: 'Other', dictName: '其他' },
]

// 压力性损伤
const pressureInjuryOptions = [
  { dictVal: 'None', dictName: '无' },
  { dictVal: 'Stage I', dictName: 'Ⅰ期' },
  { dictVal: 'Stage II', dictName: 'Ⅱ期' },
  { dictVal: 'Stage III', dictName: 'Ⅲ期' },
  { dictVal: 'Stage IV', dictName: 'Ⅳ期' },
  { dictVal: 'Unstageable', dictName: '不可分期' },
]

const marriageOptions = [
  { dictVal: 'Unmarried', dictName: '未婚' },
  { dictVal: 'Married', dictName: '已婚' },
  { dictVal: 'Widowed', dictName: '丧偶' },
  { dictVal: 'Divorced', dictName: '离婚' },
  { dictVal: 'Not specified', dictName: '未说明' },
]

const diseases = [
  { dictVal: 'Hypertension I10-I15', dictName: '高血压病 I10-I15' },
  { dictVal: 'Coronary heart disease I25', dictName: '冠心病 I25' },
  { dictVal: 'Diabetes mellitus E10-E14', dictName: '糖尿病 E10-E14' },
  { dictVal: 'Pneumonia J12-J18', dictName: '肺炎 J12-J18' },
  { dictVal: 'Chronic obstructive pulmonary disease J44', dictName: '慢性阻塞性肺疾病 J44' },
  { dictVal: 'Cerebral hemorrhage I60-I62', dictName: '脑出血 I60-I62' },
  { dictVal: 'Cerebral infarction I63', dictName: '脑梗塞 I63' },
  { dictVal: 'Urinary tract infection (within 30 days)', dictName: '尿路感染（30天内）' },
  { dictVal: 'Parkinsonism G20-G22', dictName: '帕金森综合征 G20-G22' },
  { dictVal: 'Chronic renal failure N18-N19', dictName: '慢性肾衰竭 N18-N19' },
  { dictVal: 'Liver cirrhosis K74', dictName: '肝硬化 K74' },
  { dictVal: 'Peptic ulcer K20-K31', dictName: '消化性溃疡 K20-K31' },
  { dictVal: 'Neoplasms C00-C48', dictName: '肿瘤 C00-C48' },
  { dictVal: 'Amputation (within 6 months)', dictName: '截肢（6个月内）' },
  { dictVal: 'Fracture (within 3 months) M84', dictName: '骨折（3个月内）M84' },
  { dictVal: 'Epilepsy G40', dictName: '癫痫 G40' },
  { dictVal: 'Hypothyroidism E01-E03', dictName: '甲状腺功能减退症 E01-E03' },
  { dictVal: 'Cataract H25-H26', dictName: '白内障 H25-H26' },
  { dictVal: 'Glaucoma H40-H42', dictName: '青光眼 H40-H42' },
  { dictVal: 'Osteoporosis M80-82', dictName: '骨质疏松症 M80-82' },
  { dictVal: 'Dementia F00-F03', dictName: '痴呆 F00-F03' },
  {
    dictVal: 'Other mental and behavioral disorders F04-F99',
    dictName: '其他精神和行为障碍 F04-F99',
  },
]

const lives = [
  { dictVal: 'Living alone', dictName: '独居' },
  { dictVal: 'Living with spouse', dictName: '与配偶居住' },
  { dictVal: 'Living with children', dictName: '与子女居住' },
  { dictVal: 'Living with parents', dictName: '与父母居住' },
  { dictVal: 'Living with siblings', dictName: '与兄弟姐妹居住' },
  { dictVal: 'Living with other relatives', dictName: '与其他亲属居住' },
  { dictVal: 'Living with non-relatives', dictName: '与非亲属关系的人居住' },
  { dictVal: 'Nursing home', dictName: '养老机构' },
]
const form = reactive({
  no: '',
  date: '',
  reason: '首次评估',
  basic: {
    name: '',
    gender: '男',
    birthDate: '',
    height: '',
    weight: '',
    ethnicity: '汉族',
    religion: '无',
    idNo: '',
    education: '不详',
    living: [],
    marriage: '未说明',
  },
  risks: { 

   },
  provider: { name: '', relation: '本人', contact: '', phone: '' },
  disease: [],
  diseaseOther: '',
  medications: [{ name: '', method: '', dose: '', frequency: '' }],
  health: {
    pressure: '无',
    joint: '没有影响日常生活功能',
    pain: '无疼痛',
    malnutrition: '无',
    airway: '无',
    coma: '无',
  },
})
const answers = reactive({})
const groupedItems = computed(() => {
  const g = {}
  categories.value.forEach((c) => (g[c.name] = items.value.filter((i) => i.category === c.name)))
  return g
})
const totalScore = computed(() =>
  Object.values(answers).reduce((a, b) => a + (Number.isFinite(Number(b)) ? Number(b) : 0), 0),
)
const initialLevel = computed(
  () =>
    levels.value.find((l) => totalScore.value >= l.min && totalScore.value <= l.max) ||
    levels.value[4] || { level: 4, name: '能力完全丧失（完全失能）' },
)
const changeReasons = computed(() => {
  const a = []
  if (form.health.coma === '有') a.push('存在昏迷状态：直接评定为能力完全丧失。')
  const selectedDiseases = []
  if (form.diseaseOther) selectedDiseases.push(form.diseaseOther)
  const riskCount = Object.values(form.risks).filter((v) => Number(v) >= 2).length
  if (riskCount >= 2) a.push('近30天内存在2次及以上照护风险事件。')
  return a
})
const finalLevel = computed(() => {
  if (form.health.coma === '有') return levels.value[4] || initialLevel.value
  let n = initialLevel.value.level
  const hasMentalDisease =
    (form.diseaseOther || '').includes('痴呆') || (form.diseaseOther || '').includes('精神')
  const riskCount = Object.values(form.risks).filter((v) => Number(v) >= 2).length
  if (hasMentalDisease || riskCount >= 2) n = Math.min(4, n + 1)
  return levels.value.find((x) => x.level === n) || initialLevel.value
})

function categoryScore(name) {
  return (groupedItems.value[name] || []).reduce((s, i) => s + (answers[i.code] ?? 0), 0)
}

async function saveAssessment() {
  if (!form.basic.name) {
    alert('请先填写老人姓名')
    step.value = 1
    return
  }
  const obj = {
    id: editingId.value || null,
    no: form.no,
    assessmentDate: form.date,
    reason: form.reason,
    basic: JSON.parse(JSON.stringify(form.basic)),
    risks: JSON.parse(JSON.stringify(form.risks)),
    provider: JSON.parse(JSON.stringify(form.provider)),
    diseaseOther: form.diseaseOther,
    medications: JSON.parse(JSON.stringify(form.medications)),
    health: JSON.parse(JSON.stringify(form.health)),
    answers: { ...answers },
    total: totalScore.value,
    initialLevel: initialLevel.value.level,
    level: finalLevel.value.level,
  }
  console.log(obj)

  if(obj.id) {
    await updateAssessment(obj)
  } else {
    await createAssessment(obj)
  }
  // const idx = records.value.findIndex((x) => x.id === obj.id)
  // if (idx >= 0) records.value[idx] = obj
  // else records.value.unshift(obj)
  
  step.value = 1
}


async function loadAssessment(id) {
  const rec = await getAssessment(id)
  if (!rec) {
    alert('未找到对应的评估记录')
    router.replace('/assessments')
    return
  }

  editingId.value = rec.id
  form.no = rec.no || ''
  form.date = rec.date || ''
  form.reason = rec.reason || 'Initial assessment'

  // 合并子对象（避免直接替换成没有默认字段的对象）
  Object.assign(form.basic, rec.basic || {})
  Object.assign(form.risks, rec.risks || {})
  Object.assign(form.provider, rec.provider || {})
  Object.assign(form.health, rec.health || {})

  // 数组类字段直接替换
  form.disease = Array.isArray(rec.disease) ? [...rec.disease] : []
  form.diseaseOther = rec.diseaseOther || ''
  form.medications =
    rec.medications && rec.medications.length
      ? JSON.parse(JSON.stringify(rec.medications))
      : [{ name: '', method: '', dose: '', frequency: '' }]

  // 回填评分答案
  Object.keys(answers).forEach((k) => delete answers[k])
  if (rec.answers) Object.assign(answers, rec.answers)
}

onMounted(async () => {
  const res = await fetch('/data/assessment-data.json')
  const d = await res.json()
  categories.value = d.categories
  items.value = d.items
  levels.value = d.levels

  if (editingId.value) {
    await loadAssessment(editingId.value)   // ← 关键:回填
  } else {
    // 新建模式
    form.no = 'AS' + Date.now()
    form.date = new Date().toISOString().slice(0, 10)
  }
})

</script>
