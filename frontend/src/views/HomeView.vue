<template>
  <div style="max-width: 760px; margin: 40px auto;">
    <h2>主页</h2>
    <p>已登录用户：{{ username }}（{{ role }}）</p>
    <button @click="logout">退出登录</button>

    <hr style="margin: 20px 0;" />
    <p>后续页面将逐步加入：课题组管理 / 任务协作 / 周报提交与审核 / 审计追溯。</p>
    <p>
      Swagger API 文档：<a :href="swaggerUrl" target="_blank">{{ swaggerUrl }}</a>
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
</script>
