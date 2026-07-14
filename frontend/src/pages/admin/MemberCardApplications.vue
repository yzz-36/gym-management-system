<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">办卡申请管理</h2>
    </div>

    <el-tabs v-model="activeTab" class="app-tabs">
      <el-tab-pane label="办卡申请" name="apply">
        <el-card class="table-card">
          <el-table :data="applyList" style="width: 100%" stripe>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="memberAccount" label="申请人账号" width="120" />
            <el-table-column prop="memberName" label="申请人姓名" width="110" />
            <el-table-column prop="memberPhone" label="联系电话" width="140" />
            <el-table-column prop="applyTime" label="申请时间" width="180" />
            <el-table-column prop="status" label="状态" width="110" align="center">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 'pending'" type="warning" effect="dark" size="small">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 'approved'" type="success" effect="dark" size="small">已通过</el-tag>
                <el-tag v-else-if="scope.row.status === 'processed'" type="primary" effect="dark" size="small">已办卡</el-tag>
                <el-tag v-else type="danger" effect="dark" size="small">已拒绝</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="scope">
                <template v-if="scope.row.status === 'pending'">
                  <el-button size="small" type="success" plain @click="approve(scope.row)">通过</el-button>
                  <el-button size="small" type="danger" plain @click="reject(scope.row)">拒绝</el-button>
                </template>
                <template v-else-if="scope.row.status === 'approved'">
                  <el-button size="small" type="primary" @click="openCardDialog(scope.row)">办理会员卡</el-button>
                </template>
                <span v-else style="color: #999">-</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!applyList.length" description="暂无办卡申请" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="退卡记录" name="cancel">
        <el-card class="table-card">
          <el-table :data="cancelList" style="width: 100%" stripe>
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="memberAccount" label="用户账号" width="120" />
            <el-table-column prop="memberName" label="用户姓名" width="110" />
            <el-table-column prop="memberPhone" label="联系电话" width="140" />
            <el-table-column prop="applyTime" label="退卡时间" width="180" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag type="info" effect="dark" size="small">已退卡</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
          </el-table>
          <el-empty v-if="!cancelList.length" description="暂无退卡记录" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 办理会员卡弹窗 -->
    <el-dialog v-model="cardDialogVisible" title="办理会员卡" width="440px">
      <el-form label-width="100px">
        <el-form-item label="申请人">
          <span>{{ currentRow.memberName }}（{{ currentRow.memberAccount }}）</span>
        </el-form-item>
        <el-form-item label="课时数量">
          <el-input-number v-model="cardForm.cardClass" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="到期时间">
          <el-date-picker v-model="cardForm.cardExpireTime" type="date" placeholder="选择到期日期" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cardDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="processCard">确认办理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { postForm } from '../../api/client'
import api from '../../api/client'

const activeTab = ref('apply')
const allList = ref([])

const applyList = computed(() => allList.value.filter(item => item.type !== 'cancel'))
const cancelList = computed(() => allList.value.filter(item => item.type === 'cancel'))

const cardDialogVisible = ref(false)
const currentRow = ref({})
const cardForm = reactive({
  cardClass: 30,
  cardExpireTime: ''
})

async function load() {
  const resp = await api.get('/api/member/cardApplications')
  allList.value = resp.data?.list || []
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

async function approve(row) {
  try {
    const resp = await postForm('/api/member/handleCardApplication', {
      id: row.id,
      status: 'approved',
      remark: '审核通过，请前往办理会员卡'
    })
    if (resp.data && resp.data.success) {
      alert('已通过申请')
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

function openCardDialog(row) {
  currentRow.value = row
  cardForm.cardClass = 30
  cardForm.cardExpireTime = ''
  cardDialogVisible.value = true
}

async function processCard() {
  if (!cardForm.cardClass || cardForm.cardClass <= 0) {
    alert('请输入有效的课时数量')
    return
  }
  if (!cardForm.cardExpireTime) {
    alert('请选择会员到期时间')
    return
  }
  const expireTimeStr = formatDate(cardForm.cardExpireTime)
  try {
    const resp = await postForm('/api/member/processCard', {
      id: currentRow.value.id,
      cardClass: cardForm.cardClass,
      cardExpireTime: expireTimeStr
    })
    if (resp.data && resp.data.success) {
      alert('会员卡办理成功')
      cardDialogVisible.value = false
      load()
    } else {
      alert(resp.data?.message || '办理失败')
    }
  } catch (e) {
    alert(e?.response?.data?.message || '办理失败')
  }
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
  margin-bottom: 20px;
}
.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #1a1a2e;
}
.app-tabs :deep(.el-tabs__item) {
  font-size: 15px;
  height: 44px;
  line-height: 44px;
}
.app-tabs :deep(.el-tabs__active-bar) {
  background-color: #667eea;
}
.app-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
}
.table-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}
</style>
