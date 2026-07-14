<template>
  <div class="user-page">
    <div class="page-header">
      <h2 class="page-title">用户主页</h2>
      <el-button type="danger" @click="logout" class="logout-btn">
        <el-icon><SwitchButton /></el-icon> 退出登录
      </el-button>
    </div>

    <el-card v-if="member" class="profile-card">
      <div class="profile-header">
        <div class="profile-avatar">🏋️</div>
        <div class="profile-info">
          <h3 class="profile-name">{{ member.memberName }}</h3>
          <div class="profile-meta">
            <el-tag :type="member.memberType === 'member' ? 'success' : 'info'" size="small">
              {{ member.memberType === 'member' ? '会员' : '非会员' }}
            </el-tag>
            <span class="profile-id">账号：{{ member.memberAccount }}</span>
          </div>
        </div>
      </div>
      <el-divider />
      <el-row :gutter="24">
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">性别</div>
            <div class="info-value">{{ member.memberGender }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">年龄</div>
            <div class="info-value">{{ member.memberAge }} 岁</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">身高</div>
            <div class="info-value">{{ member.memberHeight }} cm</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <div class="info-label">体重</div>
            <div class="info-value">{{ member.memberWeight }} kg</div>
          </div>
        </el-col>
      </el-row>
      <el-divider v-if="member.memberType === 'member'" />
      <el-row v-if="member.memberType === 'member'" :gutter="24">
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">办卡时间</div>
            <div class="info-value">{{ member.cardTime || '-' }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">到期时间</div>
            <div class="info-value">{{ member.cardExpireTime || '-' }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <div class="info-label">剩余课时</div>
            <div class="info-value" :class="{ 'text-danger': member.cardNextClass <= 5 }">
              {{ member.cardNextClass }} 节
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    <el-card v-else class="profile-card">未登录或会话失效。</el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import api, { postForm } from '../api/client'

const router = useRouter()
const member = ref(null)

async function load() {
  const resp = await api.get('/api/toUserMain')
  const data = resp.data || {}
  member.value = data.member || null
}

async function logout() {
  sessionStorage.removeItem('gym_chat_messages')
  await postForm('/api/logout', {})
  router.push('/toUserLogin')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.user-page {
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
.profile-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}
.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
}
.profile-avatar {
  font-size: 48px;
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
}
.profile-info {
  flex: 1;
}
.profile-name {
  margin: 0 0 8px 0;
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
}
.profile-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}
.profile-id {
  color: #666;
  font-size: 14px;
}
.info-item {
  text-align: center;
  padding: 12px;
  background: #f8f9fc;
  border-radius: 12px;
}
.info-label {
  font-size: 13px;
  color: #888;
  margin-bottom: 6px;
}
.info-value {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
}
.text-danger {
  color: #f5576c;
}
</style>

