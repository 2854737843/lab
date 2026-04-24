<template>
  <div style="max-width: 1000px; margin: 40px auto;">
    <h2>审计查询（导师/管理员）</h2>
    <div style="display:flex; gap:8px; flex-wrap:wrap;">
      <button @click="router.push('/')">返回主页</button>
      <input v-model="actor" placeholder="actor (like)" />
      <input v-model="entityType" placeholder="entityType (e.g. GROUP)" />
      <input v-model="action" placeholder="action (e.g. CREATE)" />
      <button @click="load">查询</button>
    </div>

    <p v-if="error" style="color:red;">{{ error }}</p>

    <table v-if="rows.length" border="1" cellpadding="6" cellspacing="0" style="width:100%; margin-top: 14px; border-collapse: collapse;">
      <thead>
        <tr>
          <th>ID</th>
          <th>时间</th>
          <th>actor</th>
          <th>entityType</th>
          <th>entityId</th>
          <th>action</th>
          <th>detail</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in rows" :key="r.id">
          <td>{{ r.id }}</td>
          <td>{{ r.at }}</td>
          <td>{{ r.actor }}</td>
          <td>{{ r.entityType }}</td>
          <td>{{ r.entityId }}</td>
          <td>{{ r.action }}</td>
          <td style="max-width: 380px; word-break: break-word;">{{ r.detail }}</td>
        </tr>
      </tbody>
    </table>

    <p v-else style="margin-top: 14px; color:#666;">暂无数据或未查询。</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../lib/api'

const router = useRouter()
const actor = ref('')
const entityType = ref('')
const action = ref('')
const rows = ref([])
const error = ref('')

async function load() {
  error.value = ''
  try {
    const { data } = await api.get('/api/audit', {
      params: {
        actor: actor.value || undefined,
        entityType: entityType.value || undefined,
        action: action.value || undefined
      }
    })
    rows.value = data
  } catch (e) {
    error.value = '查询失败（需要导师/管理员权限）。'
  }
}
</script>
