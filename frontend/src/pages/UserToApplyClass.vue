<template>
  <div style="padding: 24px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2 style="margin: 0">报名选课</h2>
      <el-button @click="router.push('/user/toUserClass')">返回我的课程</el-button>
    </div>

    <el-alert
      v-if="member && member.memberType !== 'member'"
      title="非会员无法报名课程，请联系管理员办理会员卡"
      type="warning"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-alert
      v-if="member && member.memberType === 'member' && isExpired"
      title="会员已到期，请联系管理员续期"
      type="warning"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-card>
      <el-table :data="classList" style="width: 100%">
        <el-table-column prop="classId" label="编号" width="120" />
        <el-table-column prop="className" label="名称" />
        <el-table-column prop="classBegin" label="时间" width="180" />
        <el-table-column prop="classTime" label="时长" width="120" />
        <el-table-column prop="coach" label="教练" width="160" />
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              :disabled="!canApply"
              @click="apply(scope.row.classId)"
            >报名</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!classList.length" style="color: #666; margin-top: 16px">暂无课程</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const classList = ref([])
const member = ref(null)

const isExpired = computed(() => {
  if (!member.value || !member.value.cardExpireTime) return false
  const expireDate = new Date(member.value.cardExpireTime)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  expireDate.setHours(0, 0, 0, 0)
  return expireDate < today
})

const canApply = computed(() => {
  if (!member.value) return false
  if (member.value.memberType !== 'member') return false
  return !isExpired.value
})

async function load() {
  const resp = await api.get('/api/user/toApplyClass')
  classList.value = resp.data?.classList || []
  member.value = resp.data?.member || null
}

async function apply(classId) {
  await postForm('/api/user/applyClass', { classId })
  await load()
  router.push('/user/toUserClass')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

