<template>
          <div>
          <div class="page-title">
            <div>
              <h2>评估工作台</h2>
              <p>管理老年人能力评估、查看结果与历史记录</p>
            </div><button class="primary" @click="startNew">开始一次评估 →</button>
          </div>
          <div class="stats">
            <div class="stat"><span>评估总数</span><b>{{ records.length }}</b><em>份记录</em></div>
            <div class="stat"><span>能力完好</span><b>{{ countLevel(0) }}</b><em>等级 0</em></div>
            <div class="stat"><span>轻度/中度受损</span><b>{{ countLevel(1) + countLevel(2) }}</b><em>等级 1-2</em></div>
            <div class="stat"><span>重度/完全丧失</span><b>{{ countLevel(3) + countLevel(4) }}</b><em>等级 3-4</em></div>
          </div>
          <div class="panel">
            <div class="panel-head">
              <h3>最近评估</h3><button class="text-btn"">
                  <RouterLink to="/assessments">查看全部 →</RouterLink>
              </button>
            </div>
            <table>
              <thead>
                <tr>
                  <th>评估编号</th>
                  <th>老人姓名</th>
                  <th>评估日期</th>
                  <th>总分</th>
                  <th>最终等级</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="r in records.slice(0, 6)" :key="r.id">
                  <td>{{ r.no }}</td>
                  <td><b>{{ r.basic.name || '未填写' }}</b></td>
                  <td>{{ r.date }}</td>
                  <td><strong>{{ r.total }}</strong>/90</td>
                  <td><span :class="'tag l' + r.level">{{ levelName(r.level) }}</span></td>
                  <td><button class="link" @click="viewRecord(r)">查看</button>
                  <button class="link" @click="editRecord(r)">编辑</button>
                </td>
                </tr>
                <tr v-if="!records.length">
                  <td colspan="6" class="empty">暂无评估记录，点击“新建评估”开始。</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="info-grid">
            <div class="info-card"><b>标准结构</b>
              <p>4 个一级指标 · 26 个二级指标 · 满分 90 分</p>
            </div>
            <div class="info-card"><b>等级规则</b>
              <p>90 / 66–89 / 46–65 / 30–45 / 0–29</p>
            </div>
            <div class="info-card"><b>等级变更</b>
              <p>昏迷直接完全丧失；特定疾病或风险事件可上调一级</p>
            </div>
          </div>  
        </div>

</template> 


<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
const categories = ref([]), items = ref([]), levels = ref([]), records = ref([])
const router = useRouter()
function startNew() { 
  router.push({
    path:'/assessments/new'
  })
}

function countLevel(n) { 
  return records.value.filter(r => r.level === n).length 
}
function levelName(n) { return levels.value.find(l => l.level === n)?.name || '未评估' }

onMounted(async () => { 
  const res = await fetch('/data/assessment-data.json'); 
  const d = await res.json(); 
  categories.value = d.categories; 
  items.value = d.items; 
  levels.value = d.levels; 
  records.value = JSON.parse(localStorage.getItem('elderlyAssessmentRecords') || '[]') 
})


const editRecord = (r) => {
  router.push({
    name: 'AssessmentEdit',
    params: {
      id: r.id
    }
  })
}

</script>