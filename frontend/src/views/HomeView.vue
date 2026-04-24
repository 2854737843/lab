<template>
  <div style="max-width: 900px; margin: 40px auto;">
    <h2>主页</h2>
    <p>已登录用户：{{ username }}（{{ role }}）</p>
    <div style="display:flex; gap:8px; flex-wrap: wrap;">
      <button @click="go('/groups')">课题组管理</button>
      <button v-if="role !== 'STUDENT'" @click="go('/audit')">审计查询</button>
      <button @click="logout">退出登录</button>
    </div>

    <hr style="margin: 20px 0;" />
    <p>
      Swagger API 文档：<a :href="swaggerUrl" target="_blank">{{ swaggerUrl }}</a>
    </p>

    <p style="color:#666;">
      任务协作 / 周报提交与审核将在下一步补齐。
    </p>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = computed(() => localStorage.getItem('username') || '')
const role = computed(() => localStorage.getItem('role') || '')
const swaggerUrl = computed(() => 'http://localhost:8080/swagger-ui/index.html')

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('role')
  router.push('/login')
}

function go(path) {
  router.push(path)
}
</script>
