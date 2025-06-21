import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'
import router from '@/router'

// 创建请求实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8',
  },
  withCredentials: false
})

// 请求计数器和加载实例
let requestCount = 0
let loadingInstance: ReturnType<typeof ElLoading.service> | null = null

// 显示全局loading
const showLoading = () => {
  if (requestCount === 0 && !loadingInstance) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.5)'
    })
  }
  requestCount++
}

// 隐藏全局loading
const hideLoading = () => {
  requestCount--
  if (requestCount === 0 && loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 是否需要显示loading
    const showGlobalLoading = config.headers?.showLoading !== false
    if (showGlobalLoading) {
      showLoading()
    }
    
    // 添加token
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    
    // 防止缓存
    if (config.method?.toUpperCase() === 'GET') {
      config.params = { ...config.params, _t: Date.now() }
    }
    
    return config
  },
  error => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data
    
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })
      
      // 特定错误码处理
      if (res.code === 401 || res.code === 403) {
        const userStore = useUserStore()
        userStore.clearUser()
        router.push('/login')
      }
      
      return Promise.reject(res)
    }
    return res
  },
  error => {
    hideLoading()
    console.error('请求错误:', error)
    
    // 处理网络错误
    if (!window.navigator.onLine) {
      ElMessage.error('网络连接已断开，请检查网络设置')
      return Promise.reject(error)
    }
    
    // 处理超时
    if (error.code === 'ECONNABORTED' && error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后重试')
      return Promise.reject(error)
    }
    
    // 处理HTTP错误
    const status = error.response?.status
    const errorMsg = error.response?.data?.message || error.message || '请求失败'
    
    switch (status) {
      case 400:
        ElMessage.error(`请求错误 (400): ${errorMsg}`)
        break
      case 401:
        ElMessage.error('登录已过期，请重新登录')
        const userStore = useUserStore()
        userStore.clearUser()
        router.push('/login')
        break
      case 403:
        ElMessage.error(`拒绝访问 (403): ${errorMsg}`)
        break
      case 404:
        ElMessage.error(`请求的资源不存在 (404): ${errorMsg}`)
        break
      case 500:
        ElMessage.error(`服务器错误 (500): ${errorMsg}`)
        break
      default:
        ElMessage.error(`连接错误 ${status || ''}: ${errorMsg}`)
    }
    
    return Promise.reject(error)
  }
)

// 请求方法封装
const http = {
  get<T = any>(url: string, params?: object, config?: object): Promise<T> {
    return request.get(url, { params, ...config })
  },
  post<T = any>(url: string, data?: object, config?: object): Promise<T> {
    return request.post(url, data, config)
  },
  put<T = any>(url: string, data?: object, config?: object): Promise<T> {
    return request.put(url, data, config)
  },
  delete<T = any>(url: string, params?: object, config?: object): Promise<T> {
    return request.delete(url, { params, ...config })
  }
}

export default http 