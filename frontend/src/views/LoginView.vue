<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>医院管理系统登录</h2>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    loading.value = true
    form.username = form.username.trim()
    form.password = form.password.trim()
    
    console.log('正在发送登录请求...')
    
    // 尝试使用原生fetch API进行请求
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: form.username,
          password: form.password
        })
      });
      
      const data = await response.json();
      console.log('原生fetch响应:', data);
      
      if (data.code === 200 && data.data) {
        userStore.setToken(data.data.token);
        userStore.setUser(data.data.user);
        router.push('/');
        return;
      }
    } catch (fetchError) {
      console.error('原生fetch请求失败:', fetchError);
    }
    
    // 如果原生fetch失败，回退到axios
    const res = await login(form)
    console.log('axios登录响应:', res)
    
    if (res.code === 200 && res.data) {
      // 确保token存在
      if (!res.data.token) {
        console.error('返回的响应中没有token')
        return
      }
      
      userStore.setToken(res.data.token)
      userStore.setUser(res.data.user)
      
      console.log('登录成功，正在跳转...')
      router.push('/')
    } else {
      console.error('登录响应格式不正确:', res)
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    // 这里不需要做任何处理，因为request.ts中的拦截器已经处理了错误提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
}

:deep(.el-card__header) {
  text-align: center;
}
</style> 