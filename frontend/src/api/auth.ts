import request from '@/utils/request'
import type { Result, User } from '@/types'

interface LoginRequest {
  username: string
  password: string
}

interface LoginResponse {
  token: string
  user: User
}

export function login(data: LoginRequest) {
  console.log('调用登录API，参数:', data)
  return request.post<any, Result<LoginResponse>>('/api/auth/login', data)
    .then(res => {
      console.log('登录API返回结果:', res)
      return res
    })
    .catch(err => {
      console.error('登录API错误:', err)
      throw err
    })
}

export function register(data: Partial<User>) {
  // 确保密码字段被正确传递
  const registerData = {
    ...data,
    password: data.password // 显式包含密码字段
  };
  console.log('调用注册API，参数:', JSON.stringify(registerData, null, 2))
  return request.post<any, Result<void>>('/api/auth/register', registerData)
    .then(res => {
      console.log('注册API返回结果:', res)
      return res
    })
    .catch(err => {
      console.error('注册API错误:', err)
      throw err
    })
} 