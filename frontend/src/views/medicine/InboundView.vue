// 药品入库管理页面

<template>
  <div class="inbound-container">
    <el-card class="inbound-card">
      <template #header>
        <div class="card-header">
          <h3>药品入库管理</h3>
          <el-button type="primary" @click="handleAdd">新增入库</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="药品名称">
          <el-select v-model="queryParams.medicineId" placeholder="请选择药品" clearable filterable>
            <el-option v-for="item in medicineOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="queryParams.batchNumber" placeholder="请输入批次号" clearable />
        </el-form-item>
        <el-form-item label="入库日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table v-loading="loading" :data="inboundList" style="width: 100%">
        <el-table-column prop="id" label="入库ID" width="80" />
        <el-table-column label="药品名称" width="150">
          <template #default="scope">
            {{ getMedicineName(scope.row.medicineId) }}
          </template>
        </el-table-column>
        <el-table-column prop="batchNumber" label="批次号" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="purchasePrice" label="进价" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <el-table-column prop="supplier" label="供应商" width="150" />
        <el-table-column prop="productionDate" label="生产日期" width="120" />
        <el-table-column prop="expirationDate" label="有效期至" width="120" />
        <el-table-column prop="inboundDate" label="入库日期" width="120" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '已撤销' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === 1" type="danger" link @click="handleCancel(scope.row)">撤销</el-button>
            <el-button type="primary" link @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <!-- 入库表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品" prop="medicineId">
              <el-select v-model="form.medicineId" placeholder="请选择药品" filterable @change="handleMedicineChange">
                <el-option v-for="item in medicineOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNumber">
              <el-input v-model="form.batchNumber" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" :precision="0" @change="calculateTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入单位" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="进价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" :step="0.01" @change="calculateTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额">
              <el-input v-model="form.totalAmount" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input v-model="form.supplier" placeholder="请输入供应商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库日期" prop="inboundDate">
              <el-date-picker v-model="form.inboundDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker v-model="form.productionDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期至" prop="expirationDate">
              <el-date-picker v-model="form.expirationDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="入库详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入库ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ getMedicineName(detail.medicineId || 0) }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ detail.batchNumber }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="进价">{{ detail.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detail.supplier }}</el-descriptions-item>
        <el-descriptions-item label="入库日期">{{ detail.inboundDate }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{ detail.productionDate }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">{{ detail.expirationDate }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
            {{ detail.status === 1 ? '正常' : '已撤销' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { 
  getMedicineInboundList, 
  getMedicineInboundById, 
  addMedicineInbound, 
  cancelMedicineInbound 
} from '@/api/medicine'
import { getMedicineList } from '@/api/medicine'
import type { MedicineInbound, Medicine } from '@/types'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  medicineId: undefined as number | undefined,
  batchNumber: '',
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})

// 日期范围
const dateRange = ref<[string, string] | null>(null)

// 监听日期范围变化
const handleDateRangeChange = computed(() => {
  if (dateRange.value) {
    queryParams.startDate = dateRange.value[0]
    queryParams.endDate = dateRange.value[1]
  } else {
    queryParams.startDate = undefined
    queryParams.endDate = undefined
  }
  return dateRange.value
})

// 表格数据
const loading = ref(false)
const inboundList = ref<MedicineInbound[]>([])
const total = ref(0)

// 药品选项
const medicineOptions = ref<Medicine[]>([])

// 表单数据
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Partial<MedicineInbound>>({
  medicineId: undefined,
  batchNumber: '',
  quantity: 1,
  unit: '',
  purchasePrice: 0,
  totalAmount: 0,
  supplier: '',
  productionDate: '',
  expirationDate: '',
  inboundDate: new Date().toISOString().split('T')[0],
  operatorId: userStore.user?.id,
  operatorName: userStore.user?.realName,
  status: 1,
  remark: ''
})

// 表单验证规则
const rules = {
  medicineId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入进价', trigger: 'blur' }],
  supplier: [{ required: true, message: '请输入供应商', trigger: 'blur' }],
  inboundDate: [{ required: true, message: '请选择入库日期', trigger: 'change' }]
}

// 详情数据
const detailDialogVisible = ref(false)
const detail = reactive<Partial<MedicineInbound>>({})

// 初始化
onMounted(() => {
  fetchMedicineOptions()
  fetchInboundList()
})

  // 获取药品选项
const fetchMedicineOptions = async () => {
  try {
    const res = await getMedicineList({ current: 1, size: 1000 })
    // 确保返回的数据有正确的结构
    if (res.data && Array.isArray(res.data.records)) {
      medicineOptions.value = res.data.records
    } else {
      medicineOptions.value = []
      console.error('获取药品列表返回的数据格式不正确', res.data)
    }
  } catch (error) {
    console.error('获取药品列表失败', error)
  }
}

// 获取入库记录列表
const fetchInboundList = async () => {
  loading.value = true
  try {
    const res = await getMedicineInboundList({
      ...queryParams,
      startDate: queryParams.startDate,
      endDate: queryParams.endDate
    })
    // 确保返回的数据有正确的结构
    if (res.data && Array.isArray(res.data.records)) {
      inboundList.value = res.data.records
      total.value = res.data.total || 0
    } else {
      inboundList.value = []
      total.value = 0
      console.error('获取入库记录返回的数据格式不正确', res.data)
    }
  } catch (error) {
    console.error('获取入库记录失败', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.current = 1
  fetchInboundList()
}

// 重置查询
const resetQuery = () => {
  queryParams.current = 1
  queryParams.medicineId = undefined
  queryParams.batchNumber = ''
  dateRange.value = null
  queryParams.startDate = undefined
  queryParams.endDate = undefined
  fetchInboundList()
}

// 处理页码变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  fetchInboundList()
}

const handleCurrentChange = (current: number) => {
  queryParams.current = current
  fetchInboundList()
}

// 获取药品名称
const getMedicineName = (medicineId: number) => {
  const medicine = medicineOptions.value.find(item => item.id === medicineId)
  return medicine ? medicine.name : ''
}

// 处理药品选择变化
const handleMedicineChange = (medicineId: number) => {
  const medicine = medicineOptions.value.find(item => item.id === medicineId)
  if (medicine) {
    form.unit = medicine.unit || ''
    form.purchasePrice = medicine.purchasePrice
    calculateTotal()
  }
}

// 计算总金额
const calculateTotal = () => {
  if (form.quantity && form.purchasePrice) {
    form.totalAmount = form.quantity * form.purchasePrice
  }
}

// 新增入库
const handleAdd = () => {
  dialogTitle.value = '新增入库'
  dialogVisible.value = true
  resetForm()
  form.inboundDate = new Date().toISOString().split('T')[0]
  form.operatorId = userStore.user?.id
  form.operatorName = userStore.user?.realName
}

// 查看详情
const handleDetail = (row: MedicineInbound) => {
  getMedicineInboundById(row.id).then(res => {
    Object.assign(detail, res.data)
    detailDialogVisible.value = true
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    medicineId: undefined,
    batchNumber: '',
    quantity: 1,
    unit: '',
    purchasePrice: 0,
    totalAmount: 0,
    supplier: '',
    productionDate: '',
    expirationDate: '',
    inboundDate: new Date().toISOString().split('T')[0],
    operatorId: userStore.user?.id,
    operatorName: userStore.user?.realName,
    status: 1,
    remark: ''
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await addMedicineInbound(form as MedicineInbound)
        ElMessage.success('入库成功')
        dialogVisible.value = false
        fetchInboundList()
      } catch (error) {
        console.error('入库失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 撤销入库
const handleCancel = (row: MedicineInbound) => {
  ElMessageBox.confirm('确定要撤销该入库记录吗？撤销后将减少对应药品的库存', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelMedicineInbound(row.id)
      ElMessage.success('撤销成功')
      fetchInboundList()
    } catch (error) {
      console.error('撤销失败', error)
    }
  }).catch(() => {})
}
</script>

<style scoped>
.inbound-container {
  padding: 20px;
}

.inbound-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
