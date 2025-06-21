<template>
  <div class="medicine-container">
    <el-card class="medicine-card">
      <template #header>
        <div class="card-header">
          <h3>药品管理</h3>
          <el-button type="primary" @click="handleAdd">新增药品</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="药品名称">
          <el-input v-model="queryParams.name" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品分类">
          <el-select v-model="queryParams.category" placeholder="请选择分类" clearable>
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table v-loading="loading" :data="medicineList" style="width: 100%">
        <el-table-column prop="code" label="药品编码" width="120" />
        <el-table-column prop="name" label="药品名称" width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="scope">
            {{ getCategoryLabel(scope.row.category) }}
          </template>
        </el-table-column>
        <el-table-column prop="dosageForm" label="剂型" width="100" />
        <el-table-column prop="specification" label="规格" width="120" />
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="manufacturer" label="厂家" width="150" />
        <el-table-column prop="purchasePrice" label="进价" width="100" />
        <el-table-column prop="salePrice" label="售价" width="100" />
        <el-table-column prop="expirationDate" label="有效期至" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="primary" link @click="handleUpdateStock(scope.row)">库存</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 药品表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入药品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药品编码" prop="code">
              <el-input v-model="form.code" placeholder="请输入药品编码" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药品分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="剂型" prop="dosageForm">
              <el-input v-model="form.dosageForm" placeholder="请输入剂型" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="规格" prop="specification">
              <el-input v-model="form.specification" placeholder="请输入规格" />
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
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" :precision="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="厂家" prop="manufacturer">
              <el-input v-model="form.manufacturer" placeholder="请输入厂家" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="进价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" :step="0.01" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :min="0" :precision="2" :step="0.01" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批号" prop="batchNumber">
              <el-input v-model="form.batchNumber" placeholder="请输入批号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产日期" prop="productionDate">
              <el-date-picker v-model="form.productionDate" type="date" placeholder="选择日期" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="有效期至" prop="expirationDate">
              <el-date-picker v-model="form.expirationDate" type="date" placeholder="选择日期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="用法用量" prop="usage">
          <el-input v-model="form.usage" type="textarea" :rows="2" placeholder="请输入用法用量" />
        </el-form-item>
        
        <el-form-item label="特殊禁忌" prop="contraindication">
          <el-input v-model="form.contraindication" type="textarea" :rows="2" placeholder="请输入特殊禁忌" />
        </el-form-item>
        
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 库存更新对话框 -->
    <el-dialog v-model="stockDialogVisible" title="更新库存" width="400px">
      <el-form ref="stockFormRef" :model="stockForm" label-width="100px">
        <el-form-item label="药品名称">
          <span>{{ stockForm.name }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="变更数量" prop="quantity">
          <el-input-number v-model="stockForm.quantity" :precision="0" />
          <div class="tip">正数为增加，负数为减少</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStockForm" :loading="stockSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { 
  getMedicineList, 
  getMedicineById, 
  addMedicine, 
  updateMedicine, 
  deleteMedicine,
  updateMedicineStock
} from '@/api/medicine'
import type { Medicine } from '@/types'

// 查询参数
const queryParams = reactive({
  current: 1,
  size: 10,
  name: '',
  category: '',
  status: undefined
})

// 药品分类选项
const categoryOptions = [
  { value: 'WESTERN_EXTERNAL', label: '西药外服' },
  { value: 'WESTERN_INTERNAL', label: '西药内用' },
  { value: 'WESTERN_INJECTION', label: '西药注射' },
  { value: 'CHINESE_PATENT', label: '中成药' },
  { value: 'CHINESE_HERBAL', label: '中药饮片' },
  { value: 'PILL_POWDER', label: '丸散膏等' }
]

// 状态选项
const statusOptions = [
  { value: 1, label: '正常' },
  { value: 0, label: '禁用' },
  { value: 2, label: '缺货' },
  { value: 3, label: '即将过期' },
  { value: 4, label: '已过期' }
]

// 列表数据
const loading = ref(false)
const medicineList = ref<Medicine[]>([])
const total = ref(0)

// 表单数据
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const form = reactive<Partial<Medicine>>({
  name: '',
  code: '',
  category: '',
  dosageForm: '',
  specification: '',
  usage: '',
  stock: 0,
  unit: '',
  manufacturer: '',
  purchasePrice: 0,
  salePrice: 0,
  batchNumber: '',
  productionDate: '',
  expirationDate: '',
  contraindication: '',
  status: 1,
  remark: ''
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择药品分类', trigger: 'change' }],
  dosageForm: [{ required: true, message: '请输入剂型', trigger: 'blur' }],
  specification: [{ required: true, message: '请输入规格', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入厂家', trigger: 'blur' }],
  purchasePrice: [{ required: true, message: '请输入进价', trigger: 'blur' }],
  salePrice: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

// 库存更新
const stockDialogVisible = ref(false)
const stockFormRef = ref<FormInstance>()
const stockSubmitLoading = ref(false)
const stockForm = reactive({
  id: 0,
  name: '',
  currentStock: 0,
  quantity: 0
})

// 初始化
onMounted(() => {
  getList()
})

// 获取列表数据
const getList = () => {
  loading.value = true
  getMedicineList(queryParams)
    .then(res => {
      medicineList.value = res.data.records
      total.value = res.data.total
    })
    .finally(() => {
      loading.value = false
    })
}

// 查询
const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.name = ''
  queryParams.category = ''
  queryParams.status = undefined
  handleQuery()
}

// 处理新增
const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增药品'
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: Medicine) => {
  resetForm()
  dialogTitle.value = '编辑药品'
  
  getMedicineById(row.id).then(res => {
    Object.assign(form, res.data)
    dialogVisible.value = true
  })
}

// 处理删除
const handleDelete = (row: Medicine) => {
  ElMessageBox.confirm(`确认删除药品 ${row.name} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteMedicine(row.id).then(() => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}

// 处理更新库存
const handleUpdateStock = (row: Medicine) => {
  stockForm.id = row.id
  stockForm.name = row.name
  stockForm.currentStock = row.stock
  stockForm.quantity = 0
  stockDialogVisible.value = true
}

// 提交库存更新
const submitStockForm = () => {
  if (stockForm.quantity === 0) {
    ElMessage.warning('请输入变更数量')
    return
  }
  
  stockSubmitLoading.value = true
  updateMedicineStock(stockForm.id, stockForm.quantity)
    .then(() => {
      ElMessage.success('库存更新成功')
      stockDialogVisible.value = false
      getList()
    })
    .finally(() => {
      stockSubmitLoading.value = false
    })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      
      const submitFunc = form.id ? updateMedicine : addMedicine
      
      submitFunc(form as Medicine)
        .then(() => {
          ElMessage.success(`${form.id ? '更新' : '新增'}成功`)
          dialogVisible.value = false
          getList()
        })
        .finally(() => {
          submitLoading.value = false
        })
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  
  Object.assign(form, {
    id: undefined,
    name: '',
    code: '',
    category: '',
    dosageForm: '',
    specification: '',
    usage: '',
    stock: 0,
    unit: '',
    manufacturer: '',
    purchasePrice: 0,
    salePrice: 0,
    batchNumber: '',
    productionDate: '',
    expirationDate: '',
    contraindication: '',
    status: 1,
    remark: ''
  })
}

// 分页大小变化
const handleSizeChange = (val: number) => {
  queryParams.size = val
  getList()
}

// 页码变化
const handleCurrentChange = (val: number) => {
  queryParams.current = val
  getList()
}

// 获取分类标签
const getCategoryLabel = (value: string) => {
  const option = categoryOptions.find(item => item.value === value)
  return option ? option.label : value
}

// 获取状态标签
const getStatusLabel = (value: number) => {
  const option = statusOptions.find(item => item.value === value)
  return option ? option.label : '未知'
}

// 获取状态类型
const getStatusType = (value: number) => {
  switch (value) {
    case 1: return 'success'
    case 0: return 'info'
    case 2: return 'warning'
    case 3: return 'warning'
    case 4: return 'danger'
    default: return 'info'
  }
}
</script>

<style scoped>
.medicine-container {
  padding: 20px;
}

.medicine-card {
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

.tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style> 