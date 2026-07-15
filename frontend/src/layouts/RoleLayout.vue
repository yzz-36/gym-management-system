<template>
  <el-container class="role-layout">
    <el-aside width="240px" class="role-aside">
      <div class="role-brand">
        <div class="role-brand-icon">💪</div>
        <div class="role-brand-text">健身房管理系统</div>
      </div>

      <el-menu
        :router="true"
        :default-active="activePath"
        class="role-menu"
        background-color="transparent"
        text-color="rgba(255,255,255,0.7)"
        active-text-color="#ffffff"
      >
        <template v-if="role === 'admin'">
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>管理员主页</span>
          </el-menu-item>
          <el-menu-item index="/member/list">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/member/card-applications">
            <el-icon><CreditCard /></el-icon>
            <span>办卡申请</span>
          </el-menu-item>
          <el-menu-item index="/employee/list">
            <el-icon><Avatar /></el-icon>
            <span>员工管理</span>
          </el-menu-item>
          <el-menu-item index="/equipment/list">
            <el-icon><Basketball /></el-icon>
            <span>器材管理</span>
          </el-menu-item>
          <el-menu-item index="/class/list">
            <el-icon><Calendar /></el-icon>
            <span>课程管理</span>
          </el-menu-item>
        </template>

        <template v-else>
          <el-menu-item index="/user/home">
            <el-icon><HomeFilled /></el-icon>
            <span>用户主页</span>
          </el-menu-item>
          <el-menu-item index="/user/info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="/user/chat">
            <el-icon><ChatDotRound /></el-icon>
            <span>聊天</span>
          </el-menu-item>
          <el-menu-item index="/user/apply-card">
            <el-icon><CreditCard /></el-icon>
            <span>申请办卡</span>
          </el-menu-item>
          <el-sub-menu index="user-course">
            <template #title>
              <el-icon><Calendar /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/user/apply-class">报名选课</el-menu-item>
            <el-menu-item index="/user/classes">我的课程</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>

      <div class="role-footer">
        <div class="role-footer-avatar">{{ role === 'admin' ? '👤' : '🏋️' }}</div>
        <div class="role-footer-info">
          <div class="role-footer-name">{{ role === 'admin' ? '管理员' : '用户' }}</div>
          <div class="role-footer-role">{{ role === 'admin' ? 'Admin' : 'Member' }}</div>
        </div>
      </div>
    </el-aside>

    <el-main class="role-main">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled, UserFilled, CreditCard, Avatar, Basketball, Calendar, User, ChatDotRound } from '@element-plus/icons-vue'

type Role = 'admin' | 'user'

const props = defineProps<{
  role: Role
}>()

const route = useRoute()
const activePath = computed(() => route.path)
</script>

<style scoped>
.role-layout {
  min-height: 100svh;
}

.role-aside {
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #bfc5d1;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 20px rgba(0,0,0,0.15);
}

.role-brand {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.role-brand-icon {
  font-size: 28px;
}

.role-brand-text {
  font-size: 18px;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 1px;
}

.role-menu {
  border-right: none;
  flex: 1;
  padding: 12px 8px;
}

.role-menu :deep(.el-menu-item),
.role-menu :deep(.el-sub-menu__title) {
  border-radius: 10px;
  margin-bottom: 4px;
  height: 48px;
  line-height: 48px;
  transition: all 0.25s ease;
}

.role-menu :deep(.el-menu-item:hover) {
  background: rgba(255,255,255,0.08) !important;
}

.role-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.role-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255,255,255,0.08);
  display: flex;
  align-items: center;
  gap: 12px;
}

.role-footer-avatar {
  font-size: 28px;
}

.role-footer-info {
  flex: 1;
}

.role-footer-name {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.role-footer-role {
  font-size: 12px;
  color: rgba(255,255,255,0.5);
  margin-top: 2px;
}

.role-main {
  padding: 0;
  background: #f5f7fa;
}
</style>

