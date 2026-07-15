<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">🏋️</div>
        <h2 class="login-title">用户登录</h2>
        <div class="login-subtitle">欢迎使用健身房管理系统</div>
      </div>

      <div class="login-form">
        <div class="input-group">
          <label>账号</label>
          <el-input v-model="form.memberAccount" placeholder="请输入账号" size="large" />
        </div>
        <div class="input-group">
          <label>密码</label>
          <el-input
            v-model="form.memberPassword"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
            @keyup.enter="submit"
          />
        </div>

        <el-alert v-if="msg" :title="msg" type="error" :closable="false" show-icon style="margin-top: 12px" />

        <el-button type="primary" class="login-btn" size="large" @click="submit">
          登 录
        </el-button>

        <div class="login-links">
          <el-link type="primary" @click="router.push('/toUserRegister')">注册账号</el-link>
          <el-link type="primary" @click="router.push('/admin')">管理员登录</el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { postForm } from '../../api/client'

const router = useRouter()
const form = reactive({
  memberAccount: '',
  memberPassword: ''
})
const msg = ref('')

async function submit() {
  msg.value = ''
  try {
    const resp = await postForm('/api/userLogin', {
      memberAccount: form.memberAccount,
      memberPassword: form.memberPassword
    })
    if (resp.data && resp.data.success) {
      sessionStorage.setItem('gym_user_account', form.memberAccount)
      router.push('/toUserMain')
    } else {
      msg.value = resp.data?.message || '登录失败'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '登录失败'
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100svh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 20px;
  padding: 40px 36px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.login-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.login-title {
  margin: 0 0 6px 0;
  font-size: 26px;
  font-weight: 700;
  color: #1a1a2e;
}
.login-subtitle {
  font-size: 14px;
  color: #888;
}
.input-group {
  margin-bottom: 16px;
}
.input-group label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 6px;
  font-weight: 500;
}
.login-btn {
  width: 100%;
  margin-top: 8px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
}
.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
}
</style>

