import request from '@/utils/request'
import type { Result, Doctor } from '@/types'

interface PageParams {
  current: number
  size: number
  department?: string
  title?: string
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function getDoctors(params: PageParams) {
  return request.get<any, Result<PageResult<Doctor>>>('/api/doctors/page', params)
}

export function getDoctorById(id: number) {
  return request.get<any, Result<Doctor>>(`/api/doctors/${id}`)
}

export function createDoctor(data: Doctor) {
  return request.post<any, Result<void>>('/api/doctors', data)
}

export function updateDoctor(data: Doctor) {
  return request.put<any, Result<void>>(`/api/doctors/${data.id}`, data)
}

export function updateDoctorStatus(id: number, status: number) {
  return request.put<any, Result<void>>(`/api/doctors/${id}/status`, null, {
    params: { status }
  })
} 