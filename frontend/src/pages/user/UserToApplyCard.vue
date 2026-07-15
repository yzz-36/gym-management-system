<template>
  <div style="padding: 24px">
    <h2 style="margin: 0 0 16px 0">申请办卡</h2>

    <el-alert
      v-if="member && member.memberType === 'member'"
      title="您已经是会员，无需申请办卡"
      type="info"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-card v-else style="max-width: 500px">
      <el-form label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="member.memberName" disabled />
        </el-form-item>
        <el-form-item label="账号">
          <el-input v-model="member.memberAccount" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交申请</el-button>
        </el-form-item>
      </el-form>

      <div style="color: #d00; margin-top: 10px" v-if="msg">{{ msg }}</div>
    </el-card>

    <h3 style="margin-top: 24px">我的申请记录</h3>
    <el-card>
      <el-table :data="applicationList" style="width: 100%">
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column prop="memberPhone" label="联系电话" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else-if="scope.row.status === 'processed'" type="primary">已办卡</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <div v-if="!applicationList.length" style="color: #666; margin-top: 16px">暂无申请记录</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { postForm } from '../../api/client'
import api from '../../api/client'

const member = ref({})
const phone = ref('')
const msg = ref('')
const applicationList = ref([])

async function loadMember() {
  const resp = await api.get('/api/user/info')
  member.value = resp.data?.member || {}
}

async function loadApplications() {
  const resp = await api.get('/api/user/myCardApplications')
  applicationList.value = resp.data?.list || []
}

async function submit() {
  msg.value = ''
  if (!phone.value || phone.value.trim().length < 5) {
    msg.value = '请输入有效的联系电话'
    return
  }
  try {
    const resp = await postForm('/api/user/applyCard', { memberPhone: phone.value.trim() })
    if (resp.data && resp.data.success) {
      alert(resp.data.message || '申请提交成功')
      phone.value = ''
      loadApplications()
    } else {
      msg.value = resp.data?.message || '申请失败'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '申请失败'
  }
}

onMounted(() => {
  loadMember().catch(() => {})
  loadApplications().catch(() => {})
})
</script>
