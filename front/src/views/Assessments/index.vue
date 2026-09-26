<template>
            <div>
          <div class="page-title">
            <div>
              <h2>评估记录</h2>
              <p>本地 JSON 数据与浏览器存储</p>
            </div><button class="primary" @click="startNew">＋ 新建评估</button>
          </div>
          <div class="panel">
            <div class="toolbar">
              <input v-model="keyword" placeholder="搜索姓名 / 评估编号" />
              <select v-model="levelFilter">
                <option value="">全部等级</option>
                <option v-for="l in levels" :value="l.level">{{ l.name }}</option>
              </select></div>
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
                <tr v-for="r in filteredRecords" :key="r.id">
                  <td>{{ r.no }}</td>
                  <td>{{ r.basic.name }}</td>
                  <td>{{ r.assessmentDate }}</td>
                  <td>{{ r.reason }}</td>
                  <td>{{ r.totalScore }}/90</td>
                  <td><span :class="'tag l' + r.level">{{ levelName(r.level) }}</span></td>
                  <td>
                    <button class="link" @click="viewRecord(r)">查看</button>
                    <button class="link" @click="editRecord(r)">编辑</button>
                    <button class="danger-link"
                      @click="removeRecord(r.id)">删除</button></td>
                </tr>
                <tr v-if="!filteredRecords.length">
                  <td colspan="7" class="empty">没有符合条件的记录</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
</template> 



<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { pageAssessments } from '@/api/assess'

const records = ref([]),levels = ref([])
const router = useRouter()
const  keyword = ref(''), levelFilter = ref('')

const filteredRecords = computed(() => 
    records.value.filter(r => (!keyword.value || r.basic.name.includes(keyword.value) || r.no.includes(keyword.value)) && (levelFilter.value === '' || String(r.level) === String(levelFilter.value)))
)
function levelName(n) { 
    return levels.value.find(l => l.level === n)?.name || '未评估' 
}

function startNew() {

  router.push({
    path:'/assessments/new'
  })
}

function viewRecord(r) { 
    router.push({
    name: 'AssessmentDetail',
    params: {
      id: r.id
    }
  })
}


const editRecord = (r) => {
  router.push({
    name: 'AssessmentEdit',
    params: {
      id: r.id
    }
  })
}


function removeRecord(id) { 
    if (confirm('确定删除这条评估记录吗？')) { 
        records.value = records.value.filter(x => x.id !== id);
         localStorage.setItem('elderlyAssessmentRecords', JSON.stringify(records.value)) 
} }

onMounted(async () => { 
    const res = await fetch('/data/assessment-data.json'); 
    const d = await res.json(); 
    // items.value = d.items; 
    levels.value = d.levels; 
    // records.value = JSON.parse(localStorage.getItem('elderlyAssessmentRecords') || '[]') 
      records.value = (await pageAssessments( {
    current: 1,
    size: 10,
    // keyword = 
  })).records

})
</script>
