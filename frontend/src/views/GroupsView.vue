<template>
  <div style="max-width: 1000px; margin: 40px auto;">
    <h2>课题组管理</h2>
    <div style="display:flex; gap:8px;">
      <button @click="router.push('/')">返回主页</button>
      <button v-if="role !== 'STUDENT'" @click="loadAllUsers">刷新用户列表</button>
      <button @click="loadMyGroups">刷新我的课题组</button>
    </div>

    <div v-if="role !== 'STUDENT'" style="margin-top: 16px; padding: 12px; border: 1px solid #ddd;">
      <h3>创建课题组（导师/管理员）</h3>
      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <input v-model="createName" placeholder="课题组名称" />
        <input v-model="createDesc" placeholder="描述" style="min-width: 320px;" />
        <button @click="createGroup">创建</button>
      </div>
      <p v-if="createError" style="color:red;">{{ createError }}</p>
    </div>

    <h3 style="margin-top: 18px;">我的课题组</h3>
    <table border="1" cellpadding="6" cellspacing="0" style="width:100%; border-collapse: collapse;">
      <thead>
        <tr>
          <th>ID</th>
          <th>名称</th>
          <th>描述</th>
          <th>归档</th>
          <th>成员</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="g in groups" :key="g.id">
          <td>{{ g.id }}</td>
          <td>{{ g.name }}</td>
          <td>{{ g.description }}</td>
          <td>{{ g.archived }}</td>
          <td><button @click="selectGroup(g)">查看</button></td>
        </tr>
      </tbody>
    </table>

    <div v-if="selected" style="margin-top: 18px; padding: 12px; border: 1px solid #ddd;">
      <h3>课题组：{{ selected.name }}（ID={{ selected.id }}）</h3>

      <div style="display:flex; gap:8px; flex-wrap:wrap;">
        <button @click="loadMembers">刷新成员</button>
      </div>

      <h4 style="margin-top: 10px;">成员列表</h4>
      <table border="1" cellpadding="6" cellspacing="0" style="width:100%; border-collapse: collapse;">
        <thead>
          <tr>
            <th>成员ID</th>
            <th>用户</th>
            <th>组内角色</th>
            <th>active</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in members" :key="m.id">
            <td>{{ m.id }}</td>
            <td>{{ m.username }}(#{{ m.userId }})</td>
            <td>{{ m.role }}</td>
            <td>{{ m.active }}</td>
            <td>
              <button v-if="role !== 'STUDENT'" @click="removeMember(m)">移除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="role !== 'STUDENT'" style="margin-top: 12px;">
        <h4>添加成员</h4>
        <div style="display:flex; gap:8px; flex-wrap:wrap;">
          <select v-model.number="addUserId">
            <option :value="0">选择用户</option>
            <option v-for="u in allUsers" :key="u.id" :value="u.id">{{ u.username }} ({{ u.role }})</option>
          </select>
          <select v-model="addRole">
            <option value="MENTOR">MENTOR</option>
            <option value="STUDENT">STUDENT</option>
          </select>
          <button @click="addMember">添加</button>
        </div>
        <p v-if="memberError" style="color:red;">{{ memberError }}</p>
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
const selected = ref(null)
const members = ref([])

const createName = ref('')
const createDesc = ref('')
const createError = ref('')

const allUsers = ref([])
const addUserId = ref(0)
const addRole = ref('STUDENT')
const memberError = ref('')

const error = ref('')

async function loadMyGroups() {
  error.value = ''
  const { data } = await api.get('/api/groups/my')
  groups.value = data
}

async function createGroup() {
  createError.value = ''
  try {
    await api.post('/api/groups', {
      name: createName.value,
      description: createDesc.value
    })
    createName.value = ''
    createDesc.value = ''
    await loadMyGroups()
  } catch (e) {
    createError.value = '创建失败（需要导师/管理员权限）。'
  }
}

function selectGroup(g) {
  selected.value = g
  loadMembers()
}

async function loadMembers() {
  memberError.value = ''
  if (!selected.value) return
  try {
    const { data } = await api.get(`/api/groups/${selected.value.id}/members`)
    members.value = data
  } catch (e) {
    memberError.value = '加载成员失败（需要导师/管理员权限）。'
  }
}

async function loadAllUsers() {
  try {
    const { data } = await api.get('/api/users')
    allUsers.value = data
  } catch (e) {
    // ignore
  }
}

async function addMember() {
  memberError.value = ''
  if (!selected.value) return
  if (!addUserId.value) {
    memberError.value = '请选择用户'
    return
  }
  try {
    await api.post(`/api/groups/${selected.value.id}/members`, {
      userId: addUserId.value,
      role: addRole.value
    })
    await loadMembers()
  } catch (e) {
    memberError.value = '添加失败（需要导师/管理员权限）。'
  }
}

async function removeMember(m) {
  memberError.value = ''
  if (!selected.value) return
  try {
    await api.delete(`/api/groups/${selected.value.id}/members/${m.id}`)
    await loadMembers()
  } catch (e) {
    memberError.value = '移除失败（需要导师/管理员权限）。'
  }
}

onMounted(async () => {
  await loadMyGroups()
  if (role.value !== 'STUDENT') {
    await loadAllUsers()
  }
})
</script>
