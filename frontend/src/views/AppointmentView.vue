<template>
  <div class="appointment-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-form :inline="true" :model="searchForm">
            <el-form-item>
              <el-select v-model="searchForm.status" placeholder="状态" clearable>
                <el-option
                  v-for="(label, value) in statusMap"
                  :key="value"
                  :label="label"
                  :value="value"
                />
              </el-select>
            </el-form-item>
            <template v-if="userStore.user?.role === 'ADMIN'">
              <el-form-item>
                <el-input v-model="searchForm.patientName" placeholder="患者姓名" clearable />
              </el-form-item>
              <el-form-item>
                <el-input v-model="searchForm.doctorName" placeholder="医生姓名" clearable />
              </el-form-item>
            </template>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button
                v-if="userStore.user?.role === 'USER'"
                type="success"
                @click="handleAdd"
              >
                预约挂号
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="appointmentTime" label="预约时间" />
        <el-table-column label="医生信息">
          <template #default="{ row }">
            <div>{{ row.doctorName }}</div>
            <div>{{ row.department }} - {{ row.title }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="就诊描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ statusMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <template v-if="userStore.user?.role === 'DOCTOR' || userStore.user?.role === 'ADMIN'">
                <el-button type="success" link @click="handleApprove(row)">
                  通过
                </el-button>
                <el-button type="danger" link @click="handleReject(row)">
                  拒绝
                </el-button>
              </template>
              <template v-if="userStore.user?.role === 'USER'">
                <el-button type="danger" link @click="handleCancel(row)">
                  取消
                </el-button>
              </template>
            </template>
            <template v-if="row.status === 'APPROVED' && userStore.user?.role === 'ADMIN'">
              <el-button type="primary" link @click="handleComplete(row)">
                完成
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="searchForm.current"
          v-model:page-size="searchForm.size"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="预约挂号" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="科室" prop="department">
          <el-select v-model="form.department" placeholder="请选择科室" @change="handleDepartmentChange">
            <el-option
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="医生" prop="doctorId">
          <el-select v-model="form.doctorId" placeholder="请选择医生">
            <el-option
              v-for="doctor in doctors"
              :key="doctor.id"
              :label="doctor.realName"
              :value="doctor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" prop="appointmentTime">
          <el-date-picker
            v-model="form.appointmentTime"
            type="datetime"
            placeholder="请选择预约时间"
            :disabled-date="disabledDate"
            :disabled-hours="disabledHours"
          />
        </el-form-item>
        <el-form-item label="就诊描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="remarkDialogVisible" :title="remarkTitle" width="400px">
      <el-form>
        <el-form-item label="备注" label-width="100px">
          <el-input v-model="remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="remarkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRemarkSubmit" :loading="loading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import {
  getAppointmentPage,
  createAppointment,
  updateAppointmentStatus,
  cancelAppointment
} from '@/api/appointment'
import { getDoctors } from '@/api/doctor'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { Appointment, Doctor } from '@/types'

const userStore = useUserStore()
const loading = ref(false)
const total = ref(0)
const tableData = ref<Appointment[]>([])
const dialogVisible = ref(false)
const remarkDialogVisible = ref(false)
const formRef = ref<FormInstance>()
const doctors = ref<Doctor[]>([])
const remark = ref('')
const currentAppointment = ref<Appointment>()
const remarkTitle = ref('')
const remarkAction = ref<'approve' | 'reject' | 'cancel'>('approve')

const departments = [
  '内科',
  '外科',
  '妇产科',
  '儿科',
  '眼科',
  '耳鼻喉科',
  '口腔科',
  '皮肤科',
  '精神科',
  '中医科'
]

const statusMap: Record<string, string> = {
  'PENDING': '待审核',
  'APPROVED': '已通过',
  'REJECTED': '已拒绝',
  'CANCELLED': '已取消',
  'COMPLETED': '已完成'
}

const searchForm = reactive({
  current: 1,
  size: 10,
  status: '',
  patientId: userStore.user?.role === 'USER' ? userStore.user?.id : undefined,
  doctorId: userStore.user?.role === 'DOCTOR' ? userStore.user?.id : undefined,
  patientName: '',
  doctorName: ''
})

const form = reactive({
  patientId: userStore.user?.id,
  doctorId: undefined as number | undefined,
  appointmentTime: '',
  description: '',
  department: ''
})

const rules = {
  department: [{ required: true, message: '请选择科室', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  appointmentTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入就诊描述', trigger: 'blur' }]
}

const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info',
    'COMPLETED': ''
  }
  return typeMap[status]
}

const loadData = async () => {
  try {
    loading.value = true
    const res = await getAppointmentPage(searchForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.current = 1
  loadData()
}

const handleSizeChange = (val: number) => {
  searchForm.size = val
  loadData()
}

const handleCurrentChange = (val: number) => {
  searchForm.current = val
  loadData()
}

const handleDepartmentChange = async () => {
  if (!form.department) return
  const res = await getDoctors({
    current: 1,
    size: 100,
    department: form.department
  })
  doctors.value = res.data.records
}

const handleAdd = () => {
  Object.assign(form, {
    patientId: userStore.user?.id,
    doctorId: undefined,
    appointmentTime: '',
    description: '',
    department: ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  
  try {
    loading.value = true
    const appointmentData: Partial<Appointment> = {
      patientId: form.patientId,
      doctorId: form.doctorId,
      appointmentTime: form.appointmentTime,
      description: form.description
    }
    await createAppointment(appointmentData as Appointment)
    ElMessage.success('预约成功')
    dialogVisible.value = false
    loadData()
  } finally {
    loading.value = false
  }
}

const handleApprove = (row: Appointment) => {
  currentAppointment.value = row
  remarkTitle.value = '通过预约'
  remarkAction.value = 'approve'
  remark.value = ''
  remarkDialogVisible.value = true
}

const handleReject = (row: Appointment) => {
  currentAppointment.value = row
  remarkTitle.value = '拒绝预约'
  remarkAction.value = 'reject'
  remark.value = ''
  remarkDialogVisible.value = true
}

const handleCancel = (row: Appointment) => {
  currentAppointment.value = row
  remarkTitle.value = '取消预约'
  remarkAction.value = 'cancel'
  remark.value = ''
  remarkDialogVisible.value = true
}

const handleComplete = (row: Appointment) => {
  currentAppointment.value = row
  remarkTitle.value = '完成预约'
  remarkAction.value = 'approve'
  remark.value = ''
  remarkDialogVisible.value = true
}

const handleRemarkSubmit = async () => {
  if (!currentAppointment.value) return
  
  try {
    loading.value = true
    if (remarkAction.value === 'cancel') {
      await cancelAppointment(currentAppointment.value.id, remark.value)
    } else if (remarkTitle.value === '完成预约') {
      await updateAppointmentStatus(
        currentAppointment.value.id,
        'COMPLETED',
        remark.value
      )
    } else {
      await updateAppointmentStatus(
        currentAppointment.value.id,
        remarkAction.value === 'approve' ? 'APPROVED' : 'REJECTED',
        remark.value
      )
    }
    ElMessage.success('操作成功')
    remarkDialogVisible.value = false
    loadData()
  } finally {
    loading.value = false
  }
}

const disabledDate = (date: Date) => {
  return date.getTime() < Date.now() - 8.64e7
}

const disabledHours = () => {
  return Array.from({ length: 24 }, (_, i) => i).filter(h => h < 8 || h > 17)
}

loadData()
</script>

<style scoped>
.appointment-container {
  min-height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 