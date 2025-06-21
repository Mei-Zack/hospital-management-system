import request from '@/utils/request'
import type { Result, User } from '@/types'

interface PageParams {
  current: number
  size: number
  username?: string
  role?: string
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function getUserPage(params: PageParams) {
  return request.get<any, Result<PageResult<User>>>('/api/users/page', params)
}

export function updatePassword(data: {
  userId: number
  oldPassword: string
  newPassword: string
}) {
  return request.put<any, Result<void>>('/api/users/password', data)
}

export function updateUserStatus(id: number, status: number) {
  return request.put<any, Result<void>>(`/api/users/${id}/status`, null, {
    params: { status }
  })
} 