<template>
  <div class="user-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-form :inline="true" :model="searchForm">
            <el-form-item>
              <el-input
                v-model="searchForm.username"
                placeholder="用户名"
                clearable
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item>
              <el-select v-model="searchForm.role" placeholder="角色" clearable>
                <el-option label="管理员" value="ADMIN" />
                <el-option label="用户" value="USER" />
                <el-option label="医生" value="DOCTOR" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>

      <el-table v-loading="loading" :data="tableData" border style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'DOCTOR' ? 'success' : ''">
              {{ roleMap[row.role] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="danger"
              link
              @click="handleUpdateStatus(row.id, 0)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleUpdateStatus(row.id, 1)"
            >
              启用
            </el-button>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getUserPage, updateUserStatus } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { User } from '@/types'

const loading = ref(false)
const total = ref(0)
const tableData = ref<User[]>([])

const searchForm = reactive({
  current: 1,
  size: 10,
  username: '',
  role: ''
})

const roleMap: Record<string, string> = {
  'ADMIN': '管理员',
  'USER': '用户',
  'DOCTOR': '医生'
}

const loadData = async () => {
  try {
    loading.value = true
    const res = await getUserPage(searchForm)
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

const handleUpdateStatus = async (id: number, status: number) => {
  try {
    await ElMessageBox.confirm(
      `确认要${status === 1 ? '启用' : '禁用'}该用户吗？`,
      '提示',
      {
        type: 'warning'
      }
    )
    await updateUserStatus(id, status)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-container {
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