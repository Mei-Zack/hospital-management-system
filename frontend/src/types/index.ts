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

export interface Medicine {
  id: number
  name: string
  code: string
  category: string
  dosageForm: string
  specification: string
  usage: string
  stock: number
  unit: string
  manufacturer: string
  purchasePrice: number
  salePrice: number
  batchNumber: string
  productionDate: string
  expirationDate: string
  contraindication: string
  status: number
  remark: string
  createdTime: string
  updatedTime: string
}

export interface MedicineInbound {
  id: number
  medicineId: number
  batchNumber: string
  quantity: number
  unit: string
  purchasePrice: number
  totalAmount: number
  supplier: string
  productionDate: string
  expirationDate: string
  inboundDate: string
  operatorId: number
  operatorName: string
  status: number
  remark: string
  createdTime: string
  updatedTime: string
}

export interface MedicineSale {
  id: number
  saleNumber: string
  medicineId: number
  batchNumber: string
  quantity: number
  unit: string
  salePrice: number
  totalAmount: number
  patientId: number
  patientName: string
  saleTime: string
  operatorId: number
  operatorName: string
  prescriptionId: number
  status: number
  remark: string
  createdTime: string
  updatedTime: string
}

export interface Result<T> {
  code: number
  message: string
  data: T
} 