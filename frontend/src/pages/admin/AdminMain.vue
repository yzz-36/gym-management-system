<template>
  <div class="admin-page">
    <div class="page-header">
      <h2 class="page-title">管理员主页</h2>
      <el-button type="danger" @click="logout" class="logout-btn">
        <el-icon><SwitchButton /></el-icon> 退出登录
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card stat-blue">
          <div class="stat-icon">👥</div>
          <div class="stat-info">
            <div class="stat-label">总人数</div>
            <div class="stat-value">{{ totals.humanTotal }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-green">
          <div class="stat-icon">🧑‍💼</div>
          <div class="stat-info">
            <div class="stat-label">员工人数</div>
            <div class="stat-value">{{ totals.employeeTotal }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-purple">
          <div class="stat-icon">🏅</div>
          <div class="stat-info">
            <div class="stat-label">会员人数</div>
            <div class="stat-value">{{ totals.memberTotal }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-orange">
          <div class="stat-icon">🏋️</div>
          <div class="stat-info">
            <div class="stat-label">器材数量</div>
            <div class="stat-value">{{ totals.equipmentTotal }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <div class="stat-card stat-cyan">
          <div class="stat-icon">📅</div>
          <div class="stat-info">
            <div class="stat-label">课程预约数</div>
            <div class="stat-value">{{ totals.classOrderTotal }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-success">
          <div class="stat-icon">✅</div>
          <div class="stat-info">
            <div class="stat-label">器材正常</div>
            <div class="stat-value">{{ totals.equipmentNormalCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-warning">
          <div class="stat-icon">🔧</div>
          <div class="stat-info">
            <div class="stat-label">维护中</div>
            <div class="stat-value">{{ totals.equipmentMaintenanceCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-danger">
          <div class="stat-icon">⚠️</div>
          <div class="stat-info">
            <div class="stat-label">已损坏</div>
            <div class="stat-value">{{ totals.equipmentDamagedCount }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 24px;">
      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3 class="panel-title">👤 最近会员</h3>
          </div>
          <div class="panel-body">
            <el-table :data="recentMembers" border stripe style="width: 100%" v-loading="loading">
              <el-table-column prop="memberName" label="姓名" width="80" />
              <el-table-column prop="memberAccount" label="账号" width="120" />
              <el-table-column prop="memberGender" label="性别" width="60" />
              <el-table-column prop="memberPhone" label="电话" width="130" />
              <el-table-column prop="memberRole" label="身份" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.memberRole === 'member' ? 'success' : 'info'" size="small">
                    {{ row.memberRole === 'member' ? '会员' : '访客' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
            <div v-if="recentMembers.length === 0" class="empty-tip">
              <el-empty description="暂无会员信息" />
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="12">
        <div class="panel">
          <div class="panel-header">
            <h3 class="panel-title">📚 最近课程</h3>
          </div>
          <div class="panel-body">
            <el-table :data="recentClasses" border stripe style="width: 100%" v-loading="loading">
              <el-table-column prop="className" label="课程名称" width="120" />
              <el-table-column prop="classBegin" label="开课日期" width="140" />
              <el-table-column prop="coach" label="教练" width="100" />
              <el-table-column prop="memberName" label="选课人" width="100" />
            </el-table>
            <div v-if="recentClasses.length === 0" class="empty-tip">
              <el-empty description="暂无课程信息" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'

import api, { postForm } from '../../api/client'

const router = useRouter()
const loading = ref(false)
const totals = reactive({
  memberTotal: 0,
  employeeTotal: 0,
  humanTotal: 0,
  equipmentTotal: 0,
  classOrderTotal: 0,
  equipmentNormalCount: 0,
  equipmentMaintenanceCount: 0,
  equipmentDamagedCount: 0
})
const recentMembers = ref([])
const recentClasses = ref([])
let refreshTimer = null

async function load() {
  loading.value = true
  try {
    const resp = await api.get('/api/toAdminMain')
    const data = resp.data || {}
    totals.memberTotal = data.memberTotal || 0
    totals.employeeTotal = data.employeeTotal || 0
    totals.humanTotal = data.humanTotal || 0
    totals.equipmentTotal = data.equipmentTotal || 0
    totals.classOrderTotal = data.classOrderTotal || 0
    totals.equipmentNormalCount = data.equipmentNormalCount || 0
    totals.equipmentMaintenanceCount = data.equipmentMaintenanceCount || 0
    totals.equipmentDamagedCount = data.equipmentDamagedCount || 0
    recentMembers.value = data.recentMembers || []
    recentClasses.value = data.recentClasses || []
  } finally {
    loading.value = false
  }
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/')
}

function startAutoRefresh() {
  refreshTimer = setInterval(() => {
    load().catch(() => {})
  }, 10000)
}

function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

onMounted(() => {
  load().catch(() => {})
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.admin-page {
  padding: 28px 32px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
}
.logout-btn {
  border-radius: 10px;
}
.stat-card {
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}
.stat-blue,
.stat-green,
.stat-purple,
.stat-orange,
.stat-cyan,
.stat-success,
.stat-warning,
.stat-danger {
  border-top: 4px solid #e4e7ed;
}
.stat-icon {
  font-size: 36px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 14px;
}
.stat-info {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
}
.panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  overflow: hidden;
}
.panel-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.panel-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}
.panel-body {
  padding: 20px 24px;
}
.empty-tip {
  padding: 40px 0;
}
</style>