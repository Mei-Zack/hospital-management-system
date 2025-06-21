// 药品统计分析页面

<template>
  <div class="statistics-container">
    <el-card class="statistics-card">
      <template #header>
        <div class="card-header">
          <h3>药品统计分析</h3>
          <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
            <el-radio-button label="year">本年</el-radio-button>
            <el-radio-button label="custom">自定义</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <div v-if="timeRange === 'custom'" class="custom-date-range">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
        />
      </div>
      
      <!-- 概览数据 -->
      <el-row :gutter="20" class="overview-row">
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-title">销售总额</div>
              <div class="overview-value">¥{{ formatNumber(overviewData.saleAmount) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-title">销售笔数</div>
              <div class="overview-value">{{ overviewData.saleCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-title">入库总额</div>
              <div class="overview-value">¥{{ formatNumber(overviewData.inboundAmount) }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card">
            <div class="overview-item">
              <div class="overview-title">入库笔数</div>
              <div class="overview-value">{{ overviewData.inboundCount }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 图表区域 -->
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-title">销售趋势</div>
            <div ref="saleTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-title">入库趋势</div>
            <div ref="inboundTrendChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="chart-row">
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-title">销售分类占比</div>
            <div ref="saleCategoryChart" class="chart-container"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="chart-card">
            <div class="chart-title">库存分类占比</div>
            <div ref="stockCategoryChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-row :gutter="20" class="chart-row">
        <el-col :span="24">
          <el-card class="chart-card">
            <div class="chart-title">热销药品排行</div>
            <div ref="topSellingChart" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import {
  getSaleTotalAmount,
  getSaleCount,
  getInboundTotalAmount,
  getInboundCount,
  getSaleStatisticsByDate,
  getInboundStatisticsByDate,
  getSaleStatisticsByCategory,
  getMedicineCategoryStatistics,
  getTopSellingMedicines
} from '@/api/medicine'

// 时间范围
const timeRange = ref('month')
const dateRange = ref<[string, string] | null>(null)

// 概览数据
const overviewData = reactive({
  saleAmount: 0,
  saleCount: 0,
  inboundAmount: 0,
  inboundCount: 0
})

// 图表引用
const saleTrendChart = ref<HTMLElement | null>(null)
const inboundTrendChart = ref<HTMLElement | null>(null)
const saleCategoryChart = ref<HTMLElement | null>(null)
const stockCategoryChart = ref<HTMLElement | null>(null)
const topSellingChart = ref<HTMLElement | null>(null)

// 图表实例
let saleTrendChartInstance: echarts.ECharts | null = null
let inboundTrendChartInstance: echarts.ECharts | null = null
let saleCategoryChartInstance: echarts.ECharts | null = null
let stockCategoryChartInstance: echarts.ECharts | null = null
let topSellingChartInstance: echarts.ECharts | null = null

// 初始化
onMounted(() => {
  updateDateRange()
  initCharts()
  loadData()
  
  window.addEventListener('resize', handleResize)
})

// 销毁
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})

// 监听窗口大小变化
const handleResize = () => {
  saleTrendChartInstance?.resize()
  inboundTrendChartInstance?.resize()
  saleCategoryChartInstance?.resize()
  stockCategoryChartInstance?.resize()
  topSellingChartInstance?.resize()
}

// 格式化日期
const formatDate = (date: Date): string => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化数字
const formatNumber = (num: number): string => {
  return num.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,')
}

// 初始化图表
const initCharts = () => {
  // 销售趋势图表
  if (saleTrendChart.value) {
    saleTrendChartInstance = echarts.init(saleTrendChart.value)
  }
  
  // 入库趋势图表
  if (inboundTrendChart.value) {
    inboundTrendChartInstance = echarts.init(inboundTrendChart.value)
  }
  
  // 销售分类占比图表
  if (saleCategoryChart.value) {
    saleCategoryChartInstance = echarts.init(saleCategoryChart.value)
  }
  
  // 库存分类占比图表
  if (stockCategoryChart.value) {
    stockCategoryChartInstance = echarts.init(stockCategoryChart.value)
  }
  
  // 热销药品排行图表
  if (topSellingChart.value) {
    topSellingChartInstance = echarts.init(topSellingChart.value)
  }
}

// 销毁图表
const disposeCharts = () => {
  saleTrendChartInstance?.dispose()
  inboundTrendChartInstance?.dispose()
  saleCategoryChartInstance?.dispose()
  stockCategoryChartInstance?.dispose()
  topSellingChartInstance?.dispose()
  
  saleTrendChartInstance = null
  inboundTrendChartInstance = null
  saleCategoryChartInstance = null
  stockCategoryChartInstance = null
  topSellingChartInstance = null
}

// 处理时间范围变化
const handleTimeRangeChange = () => {
  if (timeRange.value !== 'custom') {
    updateDateRange()
    loadData()
  }
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  if (dateRange.value) {
    loadData()
  }
}

// 更新日期范围
const updateDateRange = () => {
  const now = new Date()
  let startDate: Date
  let endDate = now
  
  switch (timeRange.value) {
    case 'today':
      startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      break
    case 'week':
      const day = now.getDay() || 7
      startDate = new Date(now.getFullYear(), now.getMonth(), now.getDate() - day + 1)
      break
    case 'month':
      startDate = new Date(now.getFullYear(), now.getMonth(), 1)
      break
    case 'year':
      startDate = new Date(now.getFullYear(), 0, 1)
      break
    default:
      startDate = new Date(now.getFullYear(), now.getMonth(), 1)
  }
  
  dateRange.value = [
    formatDate(startDate),
    formatDate(endDate)
  ]
}

// 加载数据
const loadData = async () => {
  if (!dateRange.value) return
  
  const [startDate, endDate] = dateRange.value
  
  try {
    // 加载概览数据
    await loadOverviewData(startDate, endDate)
    
    // 加载销售趋势数据
    await loadSaleTrendData(startDate, endDate)
    
    // 加载入库趋势数据
    await loadInboundTrendData(startDate, endDate)
    
    // 加载销售分类占比数据
    await loadSaleCategoryData(startDate, endDate)
    
    // 加载库存分类占比数据
    await loadStockCategoryData()
    
    // 加载热销药品排行数据
    await loadTopSellingData(startDate, endDate)
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载概览数据
const loadOverviewData = async (startDate: string, endDate: string) => {
  try {
    const [saleAmountRes, saleCountRes, inboundAmountRes, inboundCountRes] = await Promise.all([
      getSaleTotalAmount(startDate, endDate),
      getSaleCount(startDate, endDate),
      getInboundTotalAmount(startDate, endDate),
      getInboundCount(startDate, endDate)
    ])
    
    overviewData.saleAmount = saleAmountRes.data
    overviewData.saleCount = saleCountRes.data
    overviewData.inboundAmount = inboundAmountRes.data
    overviewData.inboundCount = inboundCountRes.data
  } catch (error) {
    console.error('加载概览数据失败', error)
  }
}

// 加载销售趋势数据
const loadSaleTrendData = async (startDate: string, endDate: string) => {
  try {
    const res = await getSaleStatisticsByDate(startDate, endDate)
    const data = res.data
    
    const dates: string[] = []
    const amounts: number[] = []
    
    data.forEach((item: any) => {
      dates.push(item.sale_date)
      amounts.push(item.total_amount)
    })
    
    // 更新销售趋势图表
    if (saleTrendChartInstance) {
      saleTrendChartInstance.setOption({
        title: {
          text: '销售趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br />销售金额: ¥{c}'
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '销售金额（元）'
        },
        series: [
          {
            name: '销售金额',
            type: 'line',
            data: amounts,
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载销售趋势数据失败', error)
  }
}

// 加载入库趋势数据
const loadInboundTrendData = async (startDate: string, endDate: string) => {
  try {
    const res = await getInboundStatisticsByDate(startDate, endDate)
    const data = res.data
    
    const dates: string[] = []
    const amounts: number[] = []
    
    data.forEach((item: any) => {
      dates.push(item.inbound_date)
      amounts.push(item.total_amount)
    })
    
    // 更新入库趋势图表
    if (inboundTrendChartInstance) {
      inboundTrendChartInstance.setOption({
        title: {
          text: '入库趋势',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}<br />入库金额: ¥{c}'
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '入库金额（元）'
        },
        series: [
          {
            name: '入库金额',
            type: 'line',
            data: amounts,
            smooth: true,
            lineStyle: {
              width: 3,
              color: '#67C23A'
            },
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载入库趋势数据失败', error)
  }
}

// 加载销售分类占比数据
const loadSaleCategoryData = async (startDate: string, endDate: string) => {
  try {
    const res = await getSaleStatisticsByCategory(startDate, endDate)
    const data = res.data
    
    const categories: string[] = []
    const amounts: number[] = []
    
    data.forEach((item: any) => {
      categories.push(getCategoryLabel(item.category))
      amounts.push(item.total_amount)
    })
    
    // 更新销售分类占比图表
    if (saleCategoryChartInstance) {
      saleCategoryChartInstance.setOption({
        title: {
          text: '销售分类占比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: categories
        },
        series: [
          {
            name: '销售金额',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: categories.map((category, index) => ({
              value: amounts[index],
              name: category
            }))
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载销售分类占比数据失败', error)
  }
}

// 加载库存分类占比数据
const loadStockCategoryData = async () => {
  try {
    const res = await getMedicineCategoryStatistics()
    const data = res.data
    
    const categories: string[] = []
    const counts: number[] = []
    
    data.forEach((item: any) => {
      categories.push(getCategoryLabel(item.category))
      counts.push(item.count)
    })
    
    // 更新库存分类占比图表
    if (stockCategoryChartInstance) {
      stockCategoryChartInstance.setOption({
        title: {
          text: '库存分类占比',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: categories
        },
        series: [
          {
            name: '库存数量',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: categories.map((category, index) => ({
              value: counts[index],
              name: category
            }))
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载库存分类占比数据失败', error)
  }
}

// 加载热销药品排行数据
const loadTopSellingData = async (startDate: string, endDate: string) => {
  try {
    const res = await getTopSellingMedicines(startDate, endDate)
    const data = res.data
    
    const names: string[] = []
    const sales: number[] = []
    
    data.forEach((item: any) => {
      names.push(item.medicine_name)
      sales.push(item.total_quantity)
    })
    
    // 更新热销药品排行图表
    if (topSellingChartInstance) {
      topSellingChartInstance.setOption({
        title: {
          text: '热销药品排行',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: '{b}: {c}件'
        },
        xAxis: {
          type: 'category',
          data: names,
          axisLabel: {
            interval: 0,
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          name: '销售数量（件）'
        },
        series: [
          {
            name: '销售数量',
            type: 'bar',
            data: sales,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error('加载热销药品排行数据失败', error)
  }
}

// 获取分类标签
const getCategoryLabel = (value: string): string => {
  const categoryMap: Record<string, string> = {
    'WESTERN_EXTERNAL': '西药外服',
    'WESTERN_INTERNAL': '西药内用',
    'WESTERN_INJECTION': '西药注射',
    'CHINESE_PATENT': '中成药',
    'CHINESE_HERBAL': '中药饮片',
    'PILL_POWDER': '丸散膏等'
  }
  return categoryMap[value] || value
}
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.statistics-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-date-range {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.overview-row {
  margin-bottom: 20px;
}

.overview-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overview-item {
  text-align: center;
}

.overview-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.overview-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  padding: 10px;
}

.chart-title {
  font-size: 16px;
  text-align: center;
  margin-bottom: 10px;
}

.chart-container {
  height: 300px;
}
</style>
