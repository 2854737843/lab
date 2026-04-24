<template>
  <div style="max-width: 420px; margin: 60px auto;">
    <h2>登录</h2>
    <form @submit.prevent="submit">
      <div>
        <label>用户名</label>
        <input v-model="username" />
      </div>
      <div style="margin-top: 10px;">
        <label>密码</label>
        <input v-model="password" type="password" />
      </div>
      <button style="margin-top: 16px;" type="submit">登录</button>
    </form>
    <p v-if="error" style="color: red;">{{ error }}</p>
    <p style="margin-top: 18px; color: #666;">
      测试账号：admin/admin123, mentor/mentor123, student/student123
    </p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../lib/api'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')

async function submit() {
  error.value = ''
  try {
    const { data } = await api.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })
    localStorage.setItem('token', data.token)
    localStorage.setItem('username', data.username)
    localStorage.setItem('role', data.role)
    router.push('/')
  } catch (e) {
    error.value = '登录失败，请检查账号密码。'
  }
}
</script>
