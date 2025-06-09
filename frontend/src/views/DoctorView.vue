<template>
  <div class="doctor-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-form :inline="true" :model="searchForm">
            <el-form-item>
              <el-select v-model="searchForm.department" placeholder="科室" clearable>
                <el-option
                  v-for="dept in departments"
                  :key="dept"
                  :label="dept"
                  :value="dept"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="searchForm.title" placeholder="职称" clearable>
                <el-option
                  v-for="title in titles"
                  :key="title"
                  :label="title"
                  :value="title"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button
                v-if="userStore.user?.role === 'ADMIN'"
                type="success"
                @click="handleAdd"
              >
                添加医生
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="department" label="科室" />
        <el-table-column prop="title" label="职称" />
        <el-table-column prop="specialty" label="专长" />
        <el-table-column prop="schedule" label="出诊时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">
              查看
            </el-button>
            <template v-if="userStore.user?.role === 'ADMIN'">
              <el-button type="success" link @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button
                v-if="row.status === 1"
                type="danger"
                link
                @click="handleUpdateStatus(row.id, 0)"
              >
                离职
              </el-button>
              <el-button
                v-else
                type="success"
                link
                @click="handleUpdateStatus(row.id, 1)"
              >
                复职
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加医生' : dialogType === 'edit' ? '编辑医生' : '医生详情'"
      width="500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        :disabled="dialogType === 'view'"
      >
        <el-form-item label="科室" prop="department">
          <el-select v-model="form.department" placeholder="请选择科室">
            <el-option
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称">
            <el-option
              v-for="title in titles"
              :key="title"
              :label="title"
              :value="title"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="form.specialty" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="出诊时间" prop="schedule">
          <el-input v-model="form.schedule" type="textarea" :rows="2" placeholder='请输入JSON格式，例如：{"周一": "上午9:00-11:30", "周三": "下午2:00-5:00"}' />
          <div class="form-tip">请使用JSON格式输入出诊时间，例如：{"周一": "上午9:00-11:30", "周三": "下午2:00-5:00"}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button
            v-if="dialogType !== 'view'"
            type="primary"
            @click="handleSubmit"
          >
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getDoctors,
  createDoctor,
  updateDoctor,
  updateDoctorStatus
} from '@/api/doctor'

const userStore = useUserStore()

// 数据列表
const tableData = ref([])
// 加载状态
const loading = ref(false)
// 总数
const total = ref(0)
// 对话框可见性
const dialogVisible = ref(false)
// 对话框类型: add, edit, view
const dialogType = ref('')
// 表单引用
const formRef = ref(null)

// 科室列表
const departments = [
  '内科',
  '外科',
  '妇产科',
  '儿科',
  '眼科',
  '耳鼻喉科',
  '口腔科',
  '皮肤科',
  '神经科',
  '精神科',
  '中医科',
  '麻醉科',
  '影像科',
  '检验科',
  '病理科',
  '营养科',
  '康复科'
]

// 职称列表
const titles = [
  '主任医师',
  '副主任医师',
  '主治医师',
  '住院医师',
  '医士'
]

// 搜索表单
const searchForm = reactive({
  department: '',
  title: '',
  current: 1,
  size: 10
})

// 表单数据
const form = reactive({
  id: null,
  department: '',
  title: '',
  specialty: '',
  schedule: ''
})

// 表单校验规则
const rules = {
  department: [{ required: true, message: '请选择科室', trigger: 'change' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  specialty: [{ required: true, message: '请填写专长', trigger: 'blur' }],
  schedule: [{ required: true, message: '请填写出诊时间', trigger: 'blur' }]
}

// 初始化
onMounted(() => {
  fetchDoctors()
})

// 获取医生列表
const fetchDoctors = async () => {
  loading.value = true
  try {
    const res = await getDoctors({
      ...searchForm
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取医生列表失败', error)
    ElMessage.error('获取医生列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  searchForm.current = 1
  fetchDoctors()
}

// 处理页码变化
const handleCurrentChange = (val) => {
  searchForm.current = val
  fetchDoctors()
}

// 处理每页数量变化
const handleSizeChange = (val) => {
  searchForm.size = val
  searchForm.current = 1
  fetchDoctors()
}

// 处理添加按钮点击
const handleAdd = () => {
  resetForm()
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 处理编辑按钮点击
const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogType.value = 'edit'
  dialogVisible.value = true
}

// 处理查看按钮点击
const handleView = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogType.value = 'view'
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.department = ''
  form.title = ''
  form.specialty = ''
  form.schedule = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 处理表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 处理schedule字段，确保它是有效的JSON格式
      try {
        // 如果用户输入的不是JSON格式，尝试将其转换为JSON对象
        if (form.schedule && typeof form.schedule === 'string') {
          // 尝试解析用户输入的JSON
          JSON.parse(form.schedule);
        }
      } catch (jsonError) {
        // 如果解析失败，说明用户输入的不是有效的JSON
        ElMessage.error('出诊时间必须是有效的JSON格式，例如：{"周一": "上午9:00-11:30", "周三": "下午2:00-5:00"}');
        return;
      }
      
      if (dialogType.value === 'add') {
        await createDoctor(form)
        ElMessage.success('添加成功')
      } else if (dialogType.value === 'edit') {
        await updateDoctor(form)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      fetchDoctors()
    } catch (error) {
      console.error('操作失败', error)
      ElMessage.error('操作失败')
    }
  })
}

// 处理状态更新
const handleUpdateStatus = (id, status) => {
  const statusText = status === 1 ? '复职' : '离职'
  ElMessageBox.confirm(
    `确定要将该医生设为${statusText}状态吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await updateDoctorStatus(id, { status })
      ElMessage.success(`${statusText}操作成功`)
      fetchDoctors()
    } catch (error) {
      console.error('操作失败', error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {
    // 取消操作
  })
}
</script>

<style scoped>
.doctor-container {
  padding: 20px;
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

.form-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
  margin-top: 5px;
}
</style> 