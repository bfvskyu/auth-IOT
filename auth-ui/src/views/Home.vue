<template>
  <div class="home-container">
    <el-card class="box-card">
      <div class="header">
        <div class="search-area">
          <el-input v-model="queryParams.deviceName" placeholder="输入设备名称搜索" clearable style="width: 200px" />
          <el-button type="primary" @click="handleSearch" style="margin-left: 10px;">搜索</el-button>
          <el-button type="success" @click="openAddDialog">新增设备</el-button>
        </div>
        <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%; margin-top: 20px">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="deviceName" label="设备名称" min-width="150" />
        <el-table-column prop="deviceSn" label="SN序列号" width="150" />
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === '传感器' ? 'info' : 'warning'">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="部署位置" min-width="150" />
        <el-table-column prop="createTime" label="接入时间" width="180" />
        
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[3, 5, 10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" label-width="100px">
        <el-form-item label="设备名称" required>
          <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="SN序列号" required>
          <el-input v-model="form.deviceSn" placeholder="请输入SN码 (如 SN-001)" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="传感器" value="传感器" />
            <el-option label="控制器" value="控制器" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="在线" inactive-text="离线" />
        </el-form-item>
        <el-form-item label="部署位置">
          <el-input v-model="form.location" placeholder="请输入位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDevicePage, addDevice, updateDevice, deleteDevice } from '../api/device'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

// --- 表格与分页数据 ---
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 3, // 默认每页3条，方便你看到分页效果
  deviceName: ''
})

// --- 弹窗与表单数据 ---
const dialogVisible = ref(false)
const isEdit = ref(false) // 区分是新增还是编辑
const dialogTitle = ref('新增设备')
const form = reactive({
  id: null,
  deviceName: '',
  deviceSn: '',
  type: '传感器',
  status: 1,
  location: ''
})

// === 核心方法 ===

// 1. 获取表格数据 (一进页面或翻页时调用)
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getDevicePage(queryParams)
    // 根据 MyBatis-Plus 分页返回的数据结构进行赋值
    tableData.value = res.records 
    total.value = res.total
  } catch (error) {
    console.error("加载数据失败", error)
  } finally {
    loading.value = false
  }
}

// 2. 搜索
const handleSearch = () => {
  queryParams.pageNum = 1 // 搜索时重置回第一页
  fetchData()
}

// 3. 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
}

// 4. 打开编辑弹窗 (回显数据)
const openEditDialog = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  // 把当前行的数据浅拷贝给表单
  Object.assign(form, row)
  dialogVisible.value = true
}

// 5. 提交表单 (新增或修改)
const submitForm = async () => {
  try {
    if (isEdit.value) {
      await updateDevice(form)
      ElMessage.success('修改成功')
    } else {
      await addDevice(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData() // 刷新表格
  } catch (error) {
    console.error("提交失败", error)
  }
}

// 6. 删除设备
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除设备 [${row.deviceName}] 吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    await deleteDevice({ id: row.id })
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

// 7. 退出登录
const handleLogout = () => {
  userStore.clearToken() // 清空口袋里的 Token
  router.push('/login')  // 赶回登录页
  ElMessage.success('已安全退出')
}

// 8. 弹窗关闭时重置表单
const resetForm = () => {
  Object.assign(form, { id: null, deviceName: '', deviceSn: '', type: '传感器', status: 1, location: '' })
}

// 一进页面，立刻请求数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>