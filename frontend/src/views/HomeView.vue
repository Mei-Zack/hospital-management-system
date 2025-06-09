<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用</span>
            </div>
          </template>
          <div class="welcome-content">
            <h2>医院管理系统</h2>
            <p>欢迎您，{{ userStore.user?.realName }}</p>
            <p>角色：{{ roleMap[userStore.user?.role || ''] }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>医生统计</span>
            </div>
          </template>
          <div class="stat-content">
            <el-statistic title="在职医生" :value="statistics.doctorCount">
              <template #suffix>
                <el-icon><User /></el-icon>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>预约统计</span>
            </div>
          </template>
          <div class="stat-content">
            <el-statistic title="今日预约" :value="statistics.appointmentCount">
              <template #suffix>
                <el-icon><Calendar /></el-icon>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>预约状态分布</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>各科室医生数量</span>
            </div>
          </template>
          <div ref="barChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in activities"
              :key="index"
              :timestamp="activity.timestamp"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/appointments')">
              预约挂号
            </el-button>
            <el-button type="success" @click="$router.push('/doctors')">
              查看医生
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/stores/user'
import { User, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const userStore = useUserStore()
const pieChartRef = ref()
const barChartRef = ref()
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

const roleMap: Record<string, string> = {
  'ADMIN': '系统管理员',
  'USER': '普通用户',
  'DOCTOR': '医生'
}

const statistics = reactive({
  doctorCount: 0,
  appointmentCount: 0,
  userCount: 0,
  departmentCount: 0
})

const activities = [
  {
    content: '系统上线公告：医院管理系统正式上线运行',
    timestamp: '2025-06-01'
  },
  {
    content: '系统维护通知：系统将于本周日凌晨2点进行例行维护',
    timestamp: '2025-06-18'
  },
  {
    content: '新功能上线：在线预约挂号功能正式开放',
    timestamp: '2025-06-09'
  }
]

// 模拟获取统计数据
const fetchStatistics = () => {
  // 实际项目中应该从后端获取这些数据
  statistics.doctorCount = 25
  statistics.appointmentCount = 18
  statistics.userCount = 150
  statistics.departmentCount = 10
}

// 初始化饼图
const initPieChart = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['待审核', '已通过', '已拒绝', '已取消', '已完成']
      },
      series: [
        {
          name: '预约状态',
          type: 'pie',
          radius: '70%',
          center: ['50%', '50%'],
          data: [
            { value: 10, name: '待审核' },
            { value: 25, name: '已通过' },
            { value: 5, name: '已拒绝' },
            { value: 8, name: '已取消' },
            { value: 30, name: '已完成' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    pieChart.setOption(option)
  }
}

// 初始化柱状图
const initBarChart = () => {
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'category',
        data: ['内科', '外科', '妇产科', '儿科', '眼科', '耳鼻喉科', '口腔科', '皮肤科', '精神科', '中医科']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '医生数量',
          type: 'bar',
          data: [5, 4, 3, 2, 2, 2, 3, 1, 1, 2]
        }
      ]
    }
    barChart.setOption(option)
  }
}

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  initPieChart()
  initBarChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.home-container {
  min-height: 100%;
}

.mt-20 {
  margin-top: 20px;
}

.welcome-card {
  height: 200px;
}

.stat-card {
  height: 200px;
}

.welcome-content {
  text-align: center;
  padding: 20px 0;
  position: relative;
  top: 20%;
  transform: translateY(-20%);
}

.stat-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
}

.quick-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  padding: 20px 0;
}

.chart-container {
  height: 300px;
  width: 100%;
}

:deep(.el-card__header) {
  padding: 10px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
 