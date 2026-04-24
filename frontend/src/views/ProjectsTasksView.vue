<template>
  <div style="max-width: 1100px; margin: 40px auto;">
    <h2>项目与任务协作（看板 TODO/DOING/DONE）</h2>
    <div style="display:flex; gap:8px; flex-wrap:wrap;">
      <button @click="router.push('/')">返回主页</button>
      <button @click="loadMyGroups">刷新我的课题组</button>
    </div>

    <div style="margin-top: 14px;">
      <label>选择课题组：</label>
      <select v-model.number="groupId" @change="onGroupChange">
        <option :value="0">请选择</option>
        <option v-for="g in groups" :key="g.id" :value="g.id">{{ g.name }} (#{{ g.id }})</option>
      </select>
    </div>

    <div v-if="groupId" style="margin-top: 14px; padding: 12px; border:1px solid #ddd;">
      <h3>项目</h3>
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <button @click="loadProjects">刷新项目</button>
      </div>

      <div v-if="role !== 'STUDENT'" style="margin-top: 10px;">
        <h4>创建项目</h4>
        <div style="display:flex; gap:8px; flex-wrap:wrap;">
          <input v-model="projName" placeholder="项目名称" />
          <input v-model="projDesc" placeholder="描述" style="min-width:320px;" />
          <button @click="createProject">创建</button>
        </div>
        <p v-if="projErr" style="color:red;">{{ projErr }}</p>
      </div>

      <table border="1" cellpadding="6" cellspacing="0" style="width:100%; border-collapse: collapse; margin-top: 10px;">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in projects" :key="p.id">
            <td>{{ p.id }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.description }}</td>
            <td><button @click="selectProject(p)">进入看板</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="project" style="margin-top: 16px;">
      <h3>任务看板：{{ project.name }} (#{{ project.id }})</h3>
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <button @click="loadTasks">刷新任务</button>
      </div>

      <div style="margin-top: 10px; padding: 12px; border:1px solid #ddd;">
        <h4>创建任务</h4>
        <div style="display:flex; gap:8px; flex-wrap:wrap;">
          <input v-model="taskTitle" placeholder="标题" />
          <input v-model="taskDesc" placeholder="描述" style="min-width:360px;" />
          <button @click="createTask">创建</button>
        </div>
        <p v-if="taskErr" style="color:red;">{{ taskErr }}</p>
      </div>

      <div style="display:grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; margin-top: 10px;">
        <div style="border:1px solid #ddd; padding:10px;">
          <h4>TODO</h4>
          <div v-for="t in tasksTodo" :key="t.id" style="border:1px solid #eee; padding:8px; margin-bottom: 8px;">
            <b>{{ t.title }}</b>
            <div style="color:#666;">{{ t.description }}</div>
            <div style="margin-top:6px; display:flex; gap:6px; flex-wrap:wrap;">
              <button @click="move(t,'DOING')">→ DOING</button>
              <button @click="move(t,'DONE')">→ DONE</button>
            </div>
          </div>
        </div>

        <div style="border:1px solid #ddd; padding:10px;">
          <h4>DOING</h4>
          <div v-for="t in tasksDoing" :key="t.id" style="border:1px solid #eee; padding:8px; margin-bottom: 8px;">
            <b>{{ t.title }}</b>
            <div style="color:#666;">{{ t.description }}</div>
            <div style="margin-top:6px; display:flex; gap:6px; flex-wrap:wrap;">
              <button @click="move(t,'TODO')">← TODO</button>
              <button @click="move(t,'DONE')">→ DONE</button>
            </div>
          </div>
        </div>

        <div style="border:1px solid #ddd; padding:10px;">
          <h4>DONE</h4>
          <div v-for="t in tasksDone" :key="t.id" style="border:1px solid #eee; padding:8px; margin-bottom: 8px;">
            <b>{{ t.title }}</b>
            <div style="color:#666;">{{ t.description }}</div>
            <div style="margin-top:6px; display:flex; gap:6px; flex-wrap:wrap;">
              <button @click="move(t,'DOING')">← DOING</button>
              <button @click="move(t,'TODO')">← TODO</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <p v-if="error" style="color:red;">{{ error }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../lib/api'

const router = useRouter()
const role = computed(() => localStorage.getItem('role') || '')

const groups = ref([])
const groupId = ref(0)
const projects = ref([])
const project = ref(null)

const projName = ref('')
const projDesc = ref('')
const projErr = ref('')

const tasks = ref([])
const taskTitle = ref('')
const taskDesc = ref('')
const taskErr = ref('')

const error = ref('')

const tasksTodo = computed(() => tasks.value.filter(t => t.status === 'TODO'))
const tasksDoing = computed(() => tasks.value.filter(t => t.status === 'DOING'))
const tasksDone = computed(() => tasks.value.filter(t => t.status === 'DONE'))

async function loadMyGroups() {
  const { data } = await api.get('/api/groups/my')
  groups.value = data
}

async function onGroupChange() {
  projects.value = []
  project.value = null
  tasks.value = []
  if (groupId.value) {
    await loadProjects()
  }
}

async function loadProjects() {
  if (!groupId.value) return
  const { data } = await api.get(`/api/projects/by-group/${groupId.value}`)
  projects.value = data
}

async function createProject() {
  projErr.value = ''
  try {
    await api.post('/api/projects', { groupId: groupId.value, name: projName.value, description: projDesc.value })
    projName.value = ''
    projDesc.value = ''
    await loadProjects()
  } catch (e) {
    projErr.value = '创建项目失败（需要导师/管理员，且必须是组成员）。'
  }
}

function selectProject(p) {
  project.value = p
  loadTasks()
}

async function loadTasks() {
  if (!project.value) return
  const { data } = await api.get(`/api/tasks/by-project/${project.value.id}`)
  tasks.value = data
}

async function createTask() {
  taskErr.value = ''
  if (!project.value) return
  try {
    await api.post('/api/tasks', {
      projectId: project.value.id,
      title: taskTitle.value,
      description: taskDesc.value
    })
    taskTitle.value = ''
    taskDesc.value = ''
    await loadTasks()
  } catch (e) {
    taskErr.value = '创建任务失败（必须是课题组成员）。'
  }
}

async function move(t, status) {
  try {
    await api.put(`/api/tasks/${t.id}`, {
      title: t.title,
      description: t.description,
      status,
      assigneeUserId: t.assigneeUserId
    })
    await loadTasks()
  } catch (e) {
    error.value = '更新任务状态失败。'
  }
}

onMounted(async () => {
  await loadMyGroups()
})
</script>
