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
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import api, { postForm } from '../../api/client'

const router = useRouter()
const totals = reactive({
  memberTotal: 0,
  employeeTotal: 0,
  humanTotal: 0,
  equipmentTotal: 0
})

async function load() {
  const resp = await api.get('/api/toAdminMain')
  const data = resp.data || {}
  totals.memberTotal = data.memberTotal || 0
  totals.employeeTotal = data.employeeTotal || 0
  totals.humanTotal = data.humanTotal || 0
  totals.equipmentTotal = data.equipmentTotal || 0
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/')
}

onMounted(() => {
  load().catch(() => {})
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
  color: #fff;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  transition: transform 0.2s;
}
.stat-card:hover {
  transform: translateY(-4px);
}
.stat-blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.stat-green {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}
.stat-purple {
  background: linear-gradient(135deg, #8e2de2 0%, #4a00e0 100%);
}
.stat-orange {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}
.stat-icon {
  font-size: 36px;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.2);
  border-radius: 14px;
}
.stat-info {
  flex: 1;
}
.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 32px;
  font-weight: 700;
}
</style>

