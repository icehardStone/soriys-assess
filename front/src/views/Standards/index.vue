<template>
    <div>
         <div class="page-title">
            <div>
              <h2>标准与规则</h2>
              <p>GB/T 42195-2022 评估框架</p>
            </div>
          </div>
          <div class="panel">
            <h3>标准信息</h3>
            <p>GB/T 42195-2022《老年人能力评估规范》为现行推荐性国家标准，2022-12-30 发布并实施。标准包含评估指标与评分、组织实施、评估结果及附录 A/B/C。citeturn0search0
            </p>
          </div>
          <div class="info-grid">
            <div class="info-card" v-for="c in categories"><b>{{ c.name }}</b>
              <p>{{ groupedItems[c.name].length }} 个二级指标 · 满分 {{ c.maxScore }} 分</p>
            </div>
          </div>
          <div class="panel">
            <h3>等级划分</h3>
            <table>
              <thead>
                <tr>
                  <th>等级</th>
                  <th>名称</th>
                  <th>分数</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="l in levels">
                  <td>{{ l.level }}</td>
                  <td>{{ l.name }}</td>
                  <td>{{ l.range }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="panel">
            <h3>等级变更规则</h3>
            <ul>
              <li>处于昏迷状态者，直接评定为能力完全丧失。</li>
              <li>确诊痴呆（F00-F03）或精神科专科医生诊断的其他精神和行为障碍（F04-F99），在原有能力级别上提高一个等级。</li>
              <li>近30天内发生过2次及以上照护风险事件（如跌倒、噎食、自杀、自伤、走失等），在原有能力级别上提高一个等级。</li>
            </ul>
            <p class="muted">以上规则依据标准附录 C 的能力等级变更依据整理。citeturn0search16</p>
          </div>
    </div>
</template> 

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
const categories = ref([]), items = ref([]), levels = ref([]), records = ref([])
const groupedItems = computed(() => { const g = {}; categories.value.forEach(c => g[c.name] = items.value.filter(i => i.category === c.name)); return g })

onMounted(async () => { 
    const res = await fetch('/data/assessment-data.json'); 
    const d = await res.json(); 
    categories.value = d.categories; 
    items.value = d.items; 
    levels.value = d.levels; 
    records.value = JSON.parse(localStorage.getItem('elderlyAssessmentRecords') || '[]')
})
</script>
