<template>
  <div style="display: flex; justify-content: center; padding: 40px">
    <el-card style="width: 420px">
      <h2 style="margin: 0 0 16px 0; text-align: center">注册账号</h2>

      <el-input v-model="form.memberAccount" placeholder="账号（数字）" />
      <el-input
        v-model="form.memberPassword"
        placeholder="密码"
        type="password"
        show-password
        style="margin-top: 12px"
      />
      <el-input v-model="form.memberName" placeholder="姓名" style="margin-top: 12px" />
      <el-select v-model="form.memberGender" placeholder="性别" style="margin-top: 12px; width: 100%">
        <el-option label="男" value="男" />
        <el-option label="女" value="女" />
      </el-select>
      <el-input v-model="form.memberAge" placeholder="年龄" type="number" style="margin-top: 12px" />

      <el-button type="primary" style="width: 100%; margin-top: 16px" @click="submit">
        注册
      </el-button>

      <div style="color: #d00; margin-top: 10px" v-if="msg">{{ msg }}</div>

      <div style="text-align: center; margin-top: 14px">
        <el-link type="primary" @click="router.push('/toUserLogin')">已有账号？去登录</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { postForm } from '../api/client'

const router = useRouter()
const form = reactive({
  memberAccount: '',
  memberPassword: '',
  memberName: '',
  memberGender: '',
  memberAge: ''
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
      memberAge: form.memberAge ? Number(form.memberAge) : null
    })
    if (resp.data && resp.data.success) {
      alert('注册成功，请登录')
      router.push('/toUserLogin')
    } else {
      msg.value = resp.data?.message || '注册失败'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '注册失败'
  }
}
</script>
