import { createRouter, createWebHistory } from 'vue-router'
import NotImplemented from '../pages/common/NotImplemented.vue'
import AdminLogin from '../pages/admin/AdminLogin.vue'
import UserLogin from '../pages/user/UserLogin.vue'
import AdminMain from '../pages/admin/AdminMain.vue'
import UserMain from '../pages/user/UserMain.vue'
import ClassSelClass from '../pages/admin/ClassSelClass.vue'
import ClassToAddClass from '../pages/admin/ClassToAddClass.vue'
import ClassSelClassOrder from '../pages/admin/ClassSelClassOrder.vue'
import MemberSelMember from '../pages/admin/MemberSelMember.vue'
import MemberToAddMember from '../pages/admin/MemberToAddMember.vue'
import MemberToUpdateMember from '../pages/admin/MemberToUpdateMember.vue'
import MemberToSelByCard from '../pages/admin/MemberToSelByCard.vue'
import EmployeeSelEmployee from '../pages/admin/EmployeeSelEmployee.vue'
import EmployeeToAddEmployee from '../pages/admin/EmployeeToAddEmployee.vue'
import EmployeeToUpdateEmployee from '../pages/admin/EmployeeToUpdateEmployee.vue'
import EquipmentSelEquipment from '../pages/admin/EquipmentSelEquipment.vue'
import EquipmentToAddEquipment from '../pages/admin/EquipmentToAddEquipment.vue'
import EquipmentToUpdateEquipment from '../pages/admin/EquipmentToUpdateEquipment.vue'
import UserToUserInfo from '../pages/user/UserToUserInfo.vue'
import UserToUpdateInfo from '../pages/user/UserToUpdateInfo.vue'
import UserToUserClass from '../pages/user/UserToUserClass.vue'
import UserToApplyClass from '../pages/user/UserToApplyClass.vue'
import UserChat from '../pages/user/UserChat.vue'
import UserRegister from '../pages/user/UserRegister.vue'
import UserToApplyCard from '../pages/user/UserToApplyCard.vue'
import MemberCardApplications from '../pages/admin/MemberCardApplications.vue'
import api from '../api/client'

const routes = [
  { path: '/', component: UserLogin },
  { path: '/admin', component: AdminLogin },
  { path: '/toUserRegister', component: UserRegister },

  { path: '/toAdminMain', component: AdminMain, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/toUserMain', component: UserMain, meta: { requiresAuth: true, role: 'user' } },

  // 管理端（先占位，后续把 templates 全量迁移为 Vue 页面组件）
  { path: '/member/selMember', component: MemberSelMember, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toAddMember', component: MemberToAddMember, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toUpdateMember', component: MemberToUpdateMember, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/toSelByCard', component: MemberToSelByCard, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/cardApplications', component: MemberCardApplications, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/employee/selEmployee', component: EmployeeSelEmployee, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/toAddEmployee', component: EmployeeToAddEmployee, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/toUpdateEmployee', component: EmployeeToUpdateEmployee, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/equipment/selEquipment', component: EquipmentSelEquipment, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/toAddEquipment', component: EquipmentToAddEquipment, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/toUpdateEquipment', component: EquipmentToUpdateEquipment, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/class/selClass', component: ClassSelClass, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/toAddClass', component: ClassToAddClass, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/selClassOrder', component: ClassSelClassOrder, meta: { requiresAuth: true, role: 'admin' } },

  // 用户端
  { path: '/user/toUserInfo', component: UserToUserInfo, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toUpdateInfo', component: UserToUpdateInfo, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toUserClass', component: UserToUserClass, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toApplyClass', component: UserToApplyClass, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toChat', component: UserChat, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/toApplyCard', component: UserToApplyCard, meta: { requiresAuth: true, role: 'user' } },

  // fallback
  { path: '/:pathMatch(.*)*', component: NotImplemented }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to) => {
  const meta = to.meta as any
  if (!meta?.requiresAuth) return true

  const role = meta.role as string | undefined
  try {
    if (role === 'user') {
      await api.get('/api/toUserMain')
    } else {
      await api.get('/api/toAdminMain')
    }
    return true
  } catch (e) {
    // 未登录/会话失效：按角色回登录页
    return { path: role === 'user' ? '/' : '/admin' }
  }
})

export default router

