import http from '@/utils/request'
import type { Medicine, MedicineInbound, MedicineSale, Result } from '@/types'

// 定义分页结果接口
export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 药品管理API
export function getMedicineList(params: any) {
  return http.get<Result<PageResult<Medicine>>>('/api/medicines/page', params)
}

export function getMedicineById(id: number) {
  return http.get<Result<Medicine>>(`/api/medicines/${id}`)
}

export function addMedicine(data: Medicine) {
  return http.post<Result<boolean>>('/api/medicines', data)
}

export function updateMedicine(data: Medicine) {
  return http.put<Result<boolean>>('/api/medicines', data)
}

export function deleteMedicine(id: number) {
  return http.delete<Result<boolean>>(`/api/medicines/${id}`)
}

export function updateMedicineStock(id: number, quantity: number) {
  return http.put<Result<boolean>>(`/api/medicines/${id}/stock`, null, {
    params: { quantity }
  })
}

export function getNearExpirationMedicines() {
  return http.get<Result<Medicine[]>>('/api/medicines/near-expiration')
}

export function getExpiredMedicines() {
  return http.get<Result<Medicine[]>>('/api/medicines/expired')
}

export function getLowStockMedicines() {
  return http.get<Result<Medicine[]>>('/api/medicines/low-stock')
}

export function getMedicineCategoryStatistics() {
  return http.get<Result<any[]>>('/api/medicines/statistics/category')
}

export function getMedicineStockValueStatistics() {
  return http.get<Result<any[]>>('/api/medicines/statistics/stock-value')
}

// 药品入库API
export function getMedicineInboundList(params: any) {
  return http.get<Result<PageResult<MedicineInbound>>>('/api/medicine-inbounds/page', params)
}

export function getMedicineInboundById(id: number) {
  return http.get<Result<MedicineInbound>>(`/api/medicine-inbounds/${id}`)
}

export function addMedicineInbound(data: MedicineInbound) {
  return http.post<Result<boolean>>('/api/medicine-inbounds', data)
}

export function cancelMedicineInbound(id: number) {
  return http.put<Result<boolean>>(`/api/medicine-inbounds/${id}/cancel`)
}

export function getInboundTotalAmount(startDate: string, endDate: string) {
  return http.get<Result<number>>('/api/medicine-inbounds/statistics/total-amount', { 
    startDate, endDate 
  })
}

export function getInboundCount(startDate: string, endDate: string) {
  return http.get<Result<number>>('/api/medicine-inbounds/statistics/count', { 
    startDate, endDate 
  })
}

export function getInboundStatisticsByDate(startDate: string, endDate: string) {
  return http.get<Result<any[]>>('/api/medicine-inbounds/statistics/by-date', { 
    startDate, endDate 
  })
}

export function getInboundStatisticsByCategory(startDate: string, endDate: string) {
  return http.get<Result<any[]>>('/api/medicine-inbounds/statistics/by-category', { 
    startDate, endDate 
  })
}

// 药品销售API
export function getMedicineSaleList(params: any) {
  return http.get<Result<PageResult<MedicineSale>>>('/api/medicine-sales/page', params)
}

export function getMedicineSaleById(id: number) {
  return http.get<Result<MedicineSale>>(`/api/medicine-sales/${id}`)
}

export function addMedicineSale(data: MedicineSale) {
  return http.post<Result<boolean>>('/api/medicine-sales', data)
}

export function refundMedicineSale(id: number) {
  return http.put<Result<boolean>>(`/api/medicine-sales/${id}/refund`)
}

export function getSaleTotalAmount(startDate: string, endDate: string) {
  return http.get<Result<number>>('/api/medicine-sales/statistics/total-amount', { 
    startDate, endDate 
  })
}

export function getSaleCount(startDate: string, endDate: string) {
  return http.get<Result<number>>('/api/medicine-sales/statistics/count', { 
    startDate, endDate 
  })
}

export function getSaleStatisticsByDate(startDate: string, endDate: string) {
  return http.get<Result<any[]>>('/api/medicine-sales/statistics/by-date', { 
    startDate, endDate 
  })
}

export function getSaleStatisticsByCategory(startDate: string, endDate: string) {
  return http.get<Result<any[]>>('/api/medicine-sales/statistics/by-category', { 
    startDate, endDate 
  })
}

export function getTopSellingMedicines(startDate: string, endDate: string) {
  return http.get<Result<any[]>>('/api/medicine-sales/statistics/top-selling', { 
    startDate, endDate 
  })
} 