<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>医院管理系统注册</h2>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import type { User } from '@/types'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'USER',
  status: 1
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 确保表单数据完整
    if (!form.username || !form.password || !form.realName || !form.phone || !form.email) {
      ElMessage.error('请填写所有必填字段')
      return
    }
    
    // 处理表单数据，去除空格
    form.username = form.username.trim()
    form.password = form.password.trim()
    form.realName = form.realName.trim()
    form.phone = form.phone.trim()
    form.email = form.email.trim()
    
    // 在提交前先进行一次检查
    console.log('注册前检查密码字段:', form.password)
    if (!form.password) {
      ElMessage.error('密码不能为空')
      return
    }
    
    loading.value = true
    // 创建一个新对象来确保所有字段被正确传递
    const userData = {
      username: form.username,
      password: form.password,
      realName: form.realName,
      phone: form.phone,
      email: form.email,
      role: form.role,
      status: form.status
    }
    await register(userData)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error: any) {
    console.error('注册失败:', error)
    // 错误信息已由请求拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.register-card {
  width: 400px;
}

:deep(.el-card__header) {
  text-align: center;
}
</style> 