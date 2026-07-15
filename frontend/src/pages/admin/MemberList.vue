<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
    </div>

    <el-card class="table-card">
      <el-table :data="memberList" style="width: 100%" stripe>
        <el-table-column prop="memberAccount" label="用户账号" width="130" />
        <el-table-column prop="memberName" label="姓名" width="100" />
        <el-table-column prop="memberType" label="身份" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.memberType === 'member' ? 'success' : 'info'" effect="dark" size="small">
              {{ scope.row.memberType === 'member' ? '会员' : '非会员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cardTime" label="办卡时间" width="120" />
        <el-table-column prop="cardExpireTime" label="到期时间" width="120" />
        <el-table-column prop="memberGender" label="性别" width="70" align="center" />
        <el-table-column prop="memberAge" label="年龄" width="70" align="center" />
        <el-table-column prop="memberPhone" label="联系方式" width="130" />
        <el-table-column prop="memberHeight" label="身高" width="80" align="center" />
        <el-table-column prop="memberWeight" label="体重" width="80" align="center" />
        <el-table-column prop="cardClass" label="总课时" width="90" align="center" />
        <el-table-column prop="cardNextClass" label="剩余" width="80" align="center">
          <template #default="scope">
            <span :class="{ 'text-danger': scope.row.cardNextClass <= 5 }">{{ scope.row.cardNextClass }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="edit(scope.row.memberAccount)">编辑</el-button>
            <el-button v-if="scope.row.memberType === 'member'" size="small" type="warning" plain @click="cancelMember(scope.row)">
              取消会员
            </el-button>
            <el-button size="small" type="danger" plain @click="del(scope.row.memberAccount)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!memberList.length" description="暂无数据" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api, { postForm } from '../../api/client'

const router = useRouter()
const memberList = ref([])

async function load() {
  const resp = await api.get('/api/member/selMember')
  memberList.value = resp.data?.memberList || []
}

function edit(memberAccount) {
  router.push({ path: '/member/edit', query: { memberAccount } })
}

async function cancelMember(member) {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入退卡原因（可选）', `取消会员 - ${member.memberName}`, {
      confirmButtonText: '确认取消',
      cancelButtonText: '取消操作',
      inputPlaceholder: '请输入退卡原因',
      inputValidator: () => true
    })
    const resp = await postForm('/api/member/cancelMember', {
      memberAccount: member.memberAccount,
      remark: remark || ''
    })
    if (resp.data?.success) {
      ElMessage.success('取消会员成功，已记录退卡信息')
    } else {
      ElMessage.error(resp.data?.message || '取消会员失败')
    }
    await load()
  } catch (e) {
    // 用户取消了操作
  }
}

async function del(memberAccount) {
  if (!confirm('确定要删除吗？删除后数据无法恢复！')) return
  const resp = await postForm('/api/member/delMember', { memberAccount })
  if (resp.data?.success) {
    ElMessage.success('删除成功')
  } else {
    ElMessage.error(resp.data?.message || '删除失败')
  }
  await load()
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.page-container {
  padding: 28px 32px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
}
.table-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}
.text-danger {
  color: #f5576c;
  font-weight: 600;
}
</style>

