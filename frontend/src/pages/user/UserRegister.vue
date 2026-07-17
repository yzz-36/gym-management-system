<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="login-icon">🏋️</div>
        <h2 class="login-title">注册账号</h2>
        <div class="login-subtitle">欢迎加入健身房管理系统</div>
      </div>

      <div class="login-form">
        <div class="input-group">
          <label>账号</label>
          <el-input v-model="form.memberAccount" placeholder="请输入数字账号" size="large" />
        </div>
        <div class="input-group">
          <label>密码</label>
          <el-input
            v-model="form.memberPassword"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
          />
        </div>
        <div class="input-group">
          <label>姓名</label>
          <el-input v-model="form.memberName" placeholder="请输入姓名" size="large" />
        </div>
        <div class="input-group">
          <label>性别</label>
          <el-select v-model="form.memberGender" placeholder="请选择性别" size="large" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </div>
        <div class="input-group">
          <label>年龄</label>
          <el-input v-model="form.memberAge" placeholder="请输入年龄" type="number" size="large" />
        </div>
        <div class="input-group">
          <label>邮箱</label>
          <el-input v-model="form.memberEmail" placeholder="请输入邮箱（用于找回密码）" size="large" />
        </div>

        <el-alert v-if="msg" :title="msg" type="error" :closable="false" show-icon style="margin-top: 12px" />

        <el-button type="primary" class="login-btn" size="large" @click="submit">
          注 册
        </el-button>

        <div class="login-links">
          <el-link type="primary" @click="router.push('/')">已有账号？去登录</el-link>
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
  memberPassword: '',
  memberName: '',
  memberGender: '',
  memberAge: '',
  memberEmail: ''
})
const msg = ref('')

async function submit() {
  msg.value = ''
  const account = Number(form.memberAccount)
  if (!account || isNaN(account)) {
    msg.value = '账号必须为数字'
    return
  }
  try {
    const resp = await postForm('/api/user/register', {
      memberAccount: account,
      memberPassword: form.memberPassword,
      memberName: form.memberName,
      memberGender: form.memberGender,
      memberAge: form.memberAge ? Number(form.memberAge) : null,
      memberEmail: form.memberEmail || null
    })
    if (resp.data && resp.data.success) {
      alert('注册成功，请登录')
      router.push('/')
    } else {
      msg.value = resp.data?.message || '注册失败'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '注册失败'
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
