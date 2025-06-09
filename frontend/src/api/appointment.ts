import request from '@/utils/request'
import type { Result, Appointment } from '@/types'

interface PageParams {
  current: number
  size: number
  patientId?: number
  doctorId?: number
  status?: string
}

interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
}

export function getAppointmentPage(params: PageParams) {
  return request.get<any, Result<PageResult<Appointment>>>('/api/appointments/page', { params })
}

export function createAppointment(data: Appointment) {
  return request.post<any, Result<void>>('/api/appointments', data)
}

export function updateAppointmentStatus(id: number, status: string, remark?: string) {
  return request.put<any, Result<void>>(`/api/appointments/${id}/status`, null, {
    params: { status, remark }
  })
}

export function cancelAppointment(id: number, remark: string) {
  return request.put<any, Result<void>>(`/api/appointments/${id}/cancel`, null, {
    params: { remark }
  })
} 