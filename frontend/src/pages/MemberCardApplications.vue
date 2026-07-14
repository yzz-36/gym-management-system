<template>
  <div style="padding: 24px">
    <h2 style="margin: 0 0 16px 0">办卡申请管理</h2>

    <el-card>
      <el-table :data="applicationList" style="width: 100%">
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="memberAccount" label="申请人账号" width="120" />
        <el-table-column prop="memberName" label="申请人姓名" />
        <el-table-column prop="memberPhone" label="联系电话" />
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="scope.row.status === 'approved'" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <template v-if="scope.row.status === 'pending'">
              <el-input-number v-model="scope.row.cardClass" :min="1" :max="100" size="small" style="width: 100px" placeholder="课时" />
              <el-button size="small" type="success" @click="approve(scope.row)" style="margin-left: 8px">通过</el-button>
              <el-button size="small" type="danger" @click="reject(scope.row)">拒绝</el-button>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!applicationList.length" style="color: #666; margin-top: 16px">暂无办卡申请</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { postForm } from '../api/client'
import api from '../api/client'

const applicationList = ref([])

async function load() {
  const resp = await api.get('/api/member/cardApplications')
  applicationList.value = (resp.data?.list || []).map(item => ({ ...item, cardClass: 30 }))
}

async function approve(row) {
  if (!row.cardClass || row.cardClass <= 0) {
    alert('请输入有效的课时数量')
    return
  }
  try {
    const resp = await postForm('/api/member/handleCardApplication', {
      id: row.id,
      status: 'approved',
      remark: '审核通过，分配课时：' + row.cardClass,
      cardClass: row.cardClass
    })
    if (resp.data && resp.data.success) {
      alert('已通过')
      load()
    } else {
      alert(resp.data?.message || '处理失败')
    }
  } catch (e) {
    alert(e?.response?.data?.message || '处理失败')
  }
}

async function reject(row) {
  const remark = prompt('请输入拒绝原因（可选）：') || '不符合办卡条件'
  try {
    const resp = await postForm('/api/member/handleCardApplication', {
      id: row.id,
      status: 'rejected',
      remark: remark
    })
    if (resp.data && resp.data.success) {
      alert('已拒绝')
      load()
    } else {
      alert(resp.data?.message || '处理失败')
    }
  } catch (e) {
    alert(e?.response?.data?.message || '处理失败')
  }
}

onMounted(() => {
  load().catch(() => {})
})
</script>
