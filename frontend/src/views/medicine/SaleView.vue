// 药品销售管理页面

<template>
  <div class="sale-container">
    <el-card class="sale-card">
      <template #header>
        <div class="card-header">
          <h3>药品销售管理</h3>
          <el-button type="primary" @click="handleAdd">新增销售</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="销售单号">
          <el-input v-model="queryParams.saleNumber" placeholder="请输入销售单号" clearable />
        </el-form-item>
        <el-form-item label="药品名称">
          <el-select v-model="queryParams.medicineId" placeholder="请选择药品" clearable filterable>
            <el-option v-for="item in medicineOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="queryParams.patientName" placeholder="请输入患者姓名" clearable />
        </el-form-item>
        <el-form-item label="销售日期">
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
      
      <el-table v-loading="loading" :data="saleList" style="width: 100%">
        <el-table-column prop="id" label="销售ID" width="80" />
        <el-table-column prop="saleNumber" label="销售单号" width="120" />
        <el-table-column label="药品名称" width="150">
          <template #default="scope">
            {{ getMedicineName(scope.row.medicineId) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="salePrice" label="售价" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="100" />
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="saleTime" label="销售时间" width="180" />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '已退货' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === 1" type="danger" link @click="handleRefund(scope.row)">退货</el-button>
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
    
    <!-- 销售表单对话框 -->
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
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :min="0" :precision="2" :step="0.01" @change="calculateTotal" />
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
            <el-form-item label="患者姓名" prop="patientName">
              <el-input v-model="form.patientName" placeholder="请输入患者姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售时间" prop="saleTime">
              <el-date-picker v-model="form.saleTime" type="datetime" placeholder="选择日期时间" />
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
    <el-dialog v-model="detailDialogVisible" title="销售详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="销售ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="销售单号">{{ detail.saleNumber }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ getMedicineName(detail.medicineId || 0) }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ detail.batchNumber }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }} {{ detail.unit }}</el-descriptions-item>
        <el-descriptions-item label="售价">{{ detail.salePrice }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detail.patientName }}</el-descriptions-item>
        <el-descriptions-item label="销售时间">{{ detail.saleTime }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'danger'">
            {{ detail.status === 1 ? '正常' : '已退货' }}
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
  getMedicineSaleList, 
  getMedicineSaleById, 
  addMedicineSale, 
  refundMedicineSale 
} from '@/api/medicine'
import { getMedicineList } from '@/api/medicine'
import type { MedicineSale, Medicine } from '@/types'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  saleNumber: '',
  medicineId: undefined as number | undefined,
  patientName: '',
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
const saleList = ref<MedicineSale[]>([])
const total = ref(0)

// 药品选项
const medicineOptions = ref<Medicine[]>([])

// 表单数据
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Partial<MedicineSale>>({
  saleNumber: '',
  medicineId: undefined,
  batchNumber: '',
  quantity: 1,
  unit: '',
  salePrice: 0,
  totalAmount: 0,
  patientName: '',
  saleTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
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
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
  saleTime: [{ required: true, message: '请选择销售时间', trigger: 'change' }]
}

// 详情数据
const detailDialogVisible = ref(false)
const detail = reactive<Partial<MedicineSale>>({})

// 初始化
onMounted(() => {
  fetchMedicineOptions()
  fetchSaleList()
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

// 获取销售记录列表
const fetchSaleList = async () => {
  loading.value = true
  try {
    const res = await getMedicineSaleList({
      ...queryParams,
      startDate: queryParams.startDate,
      endDate: queryParams.endDate
    })
    // 确保返回的数据有正确的结构
    if (res.data && Array.isArray(res.data.records)) {
      saleList.value = res.data.records
      total.value = res.data.total || 0
    } else {
      saleList.value = []
      total.value = 0
      console.error('获取销售记录返回的数据格式不正确', res.data)
    }
  } catch (error) {
    console.error('获取销售记录失败', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.current = 1
  fetchSaleList()
}

// 重置查询
const resetQuery = () => {
  queryParams.current = 1
  queryParams.medicineId = undefined
  queryParams.saleNumber = ''
  queryParams.patientName = ''
  dateRange.value = null
  queryParams.startDate = undefined
  queryParams.endDate = undefined
  fetchSaleList()
}

// 处理页码变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  fetchSaleList()
}

const handleCurrentChange = (current: number) => {
  queryParams.current = current
  fetchSaleList()
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
    form.salePrice = medicine.salePrice
    calculateTotal()
  }
}

// 计算总金额
const calculateTotal = () => {
  if (form.quantity && form.salePrice) {
    form.totalAmount = form.quantity * form.salePrice
  }
}

// 新增销售
const handleAdd = () => {
  dialogTitle.value = '新增销售'
  dialogVisible.value = true
  resetForm()
  form.saleTime = new Date().toISOString().slice(0, 19).replace('T', ' ')
  form.operatorId = userStore.user?.id
  form.operatorName = userStore.user?.realName
}

// 查看详情
const handleDetail = (row: MedicineSale) => {
  getMedicineSaleById(row.id).then(res => {
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
    saleNumber: '',
    medicineId: undefined,
    batchNumber: '',
    quantity: 1,
    unit: '',
    salePrice: 0,
    totalAmount: 0,
    patientName: '',
    saleTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
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
        await addMedicineSale(form as MedicineSale)
        ElMessage.success('销售成功')
        dialogVisible.value = false
        fetchSaleList()
      } catch (error) {
        console.error('销售失败', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 退货
const handleRefund = (row: MedicineSale) => {
  ElMessageBox.confirm('确定要退货吗？退货后将增加对应药品的库存', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await refundMedicineSale(row.id)
      ElMessage.success('退货成功')
      fetchSaleList()
    } catch (error) {
      console.error('退货失败', error)
    }
  }).catch(() => {})
}
</script>

<style scoped>
.sale-container {
  padding: 20px;
}

.sale-card {
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
