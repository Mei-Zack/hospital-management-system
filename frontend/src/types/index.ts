export interface User {
  id: number
  username: string
  password?: string
  realName: string
  phone: string
  email: string
  role: string
  status: number
}

export interface Doctor {
  id: number
  userId: number
  realName: string
  department: string
  title: string
  specialty: string
  introduction: string
  schedule: string
  status: number
}

export interface Appointment {
  id: number
  patientId: number
  doctorId: number
  appointmentTime: string
  status: string
  description: string
  remark: string
}

export interface Result<T> {
  code: number
  message: string
  data: T
} 