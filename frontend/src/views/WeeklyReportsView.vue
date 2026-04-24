<template>
  <div style="max-width: 1100px; margin: 40px auto;">
    <h2>周报（自然周）</h2>
    <div style="display:flex; gap:8px; flex-wrap:wrap;">
      <button @click="router.push('/')">返回主页</button>
      <button @click="loadMyGroups">刷新我的课题组</button>
      <button @click="loadMyReports">刷新我的周报</button>
    </div>

    <div style="margin-top: 14px;">
      <label>选择课题组：</label>
      <select v-model.number="groupId">
        <option :value="0">请选择</option>
        <option v-for="g in groups" :key="g.id" :value="g.id">{{ g.name }} (#{{ g.id }})</option>
      </select>
    </div>

    <div style="margin-top: 14px; padding: 12px; border: 1px solid #ddd;">
      <h3>提交周报（学生也可）</h3>
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <input v-model.number="weekYear" type="number" placeholder="年(weekYear)" style="width:140px;" />
        <input v-model.number="weekNumber" type="number" placeholder="周(1-53)" style="width:120px;" />
        <input v-model="title" placeholder="标题" style="min-width:240px;" />
      </div>
      <div style="margin-top: 8px;">
        <textarea v-model="content" placeholder="本周工作内容/进展/问题/计划" rows="6" style="width:100%;"></textarea>
      </div>
      <button style="margin-top: 8px;" @click="submit">提交</button>
      <p v-if="submitErr" style="color:red;">{{ submitErr }}</p>
    </div>

    <h3 style="margin-top: 16px;">我的周报</h3>
    <table border="1" cellpadding="6" cellspacing="0" style="width:100%; border-collapse: collapse;">
      <thead>
        <tr>
          <th>ID</th>
          <th>课题组</th>
          <th>周</th>
          <th>标题</th>
          <th>状态</th>
          <th>导师意见</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in myReports" :key="r.id">
          <td>{{ r.id }}</td>
          <td>{{ r.groupId }}</td>
          <td>{{ r.weekYear }}-W{{ r.weekNumber }}</td>
          <td>{{ r.title }}</td>
          <td>{{ r.status }}</td>
          <td style="max-width: 320px; word-break: break-word;">{{ r.mentorComment }}</td>
        </tr>
      </tbody>
    </table>

    <div v-if="role !== 'STUDENT'" style="margin-top: 16px; padding: 12px; border: 1px solid #ddd;">
      <h3>导师审核（按课题组查看）</h3>
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <button @click="loadGroupReports">加载该组周报</button>
      </div>

      <table v-if="groupReports.length" border="1" cellpadding="6" cellspacing="0" style="width:100%; border-collapse: collapse; margin-top: 10px;">
        <thead>
          <tr>
            <th>ID</th>
            <th>作者</th>
            <th>周</th>
            <th>标题</th>
            <th>状态</th>
            <th>审核</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="r in groupReports" :key="r.id">
            <td>{{ r.id }}</td>
            <td>{{ r.authorUsername }}</td>
            <td>{{ r.weekYear }}-W{{ r.weekNumber }}</td>
            <td>{{ r.title }}</td>
            <td>{{ r.status }}</td>
            <td>
              <div style="display:flex; gap:6px; flex-wrap:wrap;">
                <input v-model="reviewComment[r.id]" placeholder="审核意见" style="min-width:220px;" />
                <button @click="review(r.id, true)">通过</button>
                <button @click="review(r.id, false)">驳回</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else style="margin-top: 10px; color:#666;">未加载或暂无周报。</p>

      <p v-if="reviewErr" style="color:red;">{{ reviewErr }}</p>
    </div>

    <p v-if="error" style="color:red;">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../lib/api'

const router = useRouter()
const role = computed(() => localStorage.getItem('role') || '')

const groups = ref([])
const groupId = ref(0)

const weekYear = ref(new Date().getFullYear())
const weekNumber = ref(1)
const title = ref('')
const content = ref('')
const submitErr = ref('')

const myReports = ref([])
const groupReports = ref([])

const reviewComment = reactive({})
const reviewErr = ref('')
const error = ref('')

async function loadMyGroups() {
  const { data } = await api.get('/api/groups/my')
  groups.value = data
}

async function loadMyReports() {
  const { data } = await api.get('/api/weekly-reports/my')
  myReports.value = data
}

async function submit() {
  submitErr.value = ''
  if (!groupId.value) {
    submitErr.value = '请选择课题组'
    return
  }
  try {
    await api.post('/api/weekly-reports/submit', {
      groupId: groupId.value,
      weekYear: weekYear.value,
      weekNumber: weekNumber.value,
      title: title.value,
      content: content.value
    })
    title.value = ''
    content.value = ''
    await loadMyReports()
  } catch (e) {
    submitErr.value = '提交失败（必须是该组成员；同一自然周可能已提交）。'
  }
}

async function loadGroupReports() {
  reviewErr.value = ''
  if (!groupId.value) {
    reviewErr.value = '请选择课题组'
    return
  }
  try {
    const { data } = await api.get(`/api/weekly-reports/by-group/${groupId.value}`)
    groupReports.value = data
  } catch (e) {
    reviewErr.value = '加载失败（需要导师/管理员，且必须是组成员）。'
  }
}

async function review(reportId, approve) {
  reviewErr.value = ''
  try {
    await api.post(`/api/weekly-reports/${reportId}/review`, {
      approve,
      mentorComment: reviewComment[reportId] || 'OK'
    })
    await loadGroupReports()
    await loadMyReports()
  } catch (e) {
    reviewErr.value = '审核失败（需要导师/管理员权限）。'
  }
}

onMounted(async () => {
  await loadMyGroups()
  await loadMyReports()
})
</script>
