<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">🏋️</div>
        <h2 class="login-title">忘记密码</h2>
        <div class="login-subtitle">通过邮箱验证重置密码</div>
      </div>

      <div class="login-form">
        <div class="input-group">
          <label>邮箱</label>
          <el-input v-model="form.email" placeholder="请输入注册时绑定的邮箱" size="large" />
        </div>

        <div class="input-group">
          <label>验证码</label>
          <div class="code-row">
            <el-input v-model="form.code" placeholder="请输入验证码" size="large" style="flex: 1" />
            <el-button
              type="primary"
              size="large"
              :disabled="countdown > 0"
              @click="sendCode"
              style="margin-left: 10px; min-width: 120px"
            >
              {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </div>

        <div class="input-group">
          <label>新密码</label>
          <el-input
            v-model="form.newPassword"
            placeholder="请输入新密码（不少于6位）"
            type="password"
            show-password
            size="large"
          />
        </div>

        <div class="input-group">
          <label>确认新密码</label>
          <el-input
            v-model="form.confirmPassword"
            placeholder="请再次输入新密码"
            type="password"
            show-password
            size="large"
          />
        </div>

        <el-alert v-if="msg" :title="msg" :type="alertType" :closable="false" show-icon style="margin-top: 12px" />

        <el-button type="primary" class="login-btn" size="large" @click="submit">
          重 置 密 码
        </el-button>

        <div class="login-links">
          <el-link type="primary" @click="router.push('/')">返回登录</el-link>
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
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})
const msg = ref('')
const alertType = ref('error')
const countdown = ref(0)
let timer = null

async function sendCode() {
  msg.value = ''
  if (!form.email || !form.email.trim()) {
    msg.value = '请输入邮箱'
    alertType.value = 'error'
    return
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.email.trim())) {
    msg.value = '邮箱格式不正确'
    alertType.value = 'error'
    return
  }

  try {
    const resp = await postForm('/api/forgot-password/send-code', {
      email: form.email.trim()
    })
    if (resp.data && resp.data.success) {
      msg.value = '验证码已发送，请查收邮件'
      alertType.value = 'success'
      startCountdown()
    } else {
      msg.value = resp.data?.message || '发送失败'
      alertType.value = 'error'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '发送失败'
    alertType.value = 'error'
  }
}

function startCountdown() {
  countdown.value = 60
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

async function submit() {
  msg.value = ''
  const email = form.email.trim()
  const code = form.code.trim()
  const newPassword = form.newPassword.trim()
  const confirmPassword = form.confirmPassword.trim()

  if (!email) {
    msg.value = '请输入邮箱'
    alertType.value = 'error'
    return
  }
  if (!code) {
    msg.value = '请输入验证码'
    alertType.value = 'error'
    return
  }
  if (!newPassword) {
    msg.value = '请输入新密码'
    alertType.value = 'error'
    return
  }
  if (newPassword.length < 6) {
    msg.value = '新密码长度不能少于6位'
    alertType.value = 'error'
    return
  }
  if (newPassword !== confirmPassword) {
    msg.value = '两次输入的密码不一致'
    alertType.value = 'error'
    return
  }

  try {
    const resp = await postForm('/api/forgot-password/reset', {
      email,
      code,
      newPassword
    })
    if (resp.data && resp.data.success) {
      msg.value = '密码重置成功，即将跳转登录页'
      alertType.value = 'success'
      setTimeout(() => {
        router.push('/')
      }, 1500)
    } else {
      msg.value = resp.data?.message || '重置失败'
      alertType.value = 'error'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '重置失败'
    alertType.value = 'error'
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
  margin-bottom: 14px;
}
.input-group label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 6px;
  font-weight: 500;
}
.code-row {
  display: flex;
  align-items: center;
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
  text-align: center;
  margin-top: 18px;
}
</style>
