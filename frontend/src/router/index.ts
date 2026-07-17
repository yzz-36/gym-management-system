import { createRouter, createWebHistory } from 'vue-router'
import NotImplemented from '../pages/common/NotImplemented.vue'
import AdminLogin from '../pages/admin/AdminLogin.vue'
import UserLogin from '../pages/user/UserLogin.vue'
import AdminMain from '../pages/admin/AdminMain.vue'
import UserMain from '../pages/user/UserMain.vue'
import ClassList from '../pages/admin/ClassList.vue'
import ClassAdd from '../pages/admin/ClassAdd.vue'
import ClassOrderList from '../pages/admin/ClassOrderList.vue'
import MemberList from '../pages/admin/MemberList.vue'
import MemberAdd from '../pages/admin/MemberAdd.vue'
import MemberEdit from '../pages/admin/MemberEdit.vue'
import MemberSearch from '../pages/admin/MemberSearch.vue'
import EmployeeList from '../pages/admin/EmployeeList.vue'
import EmployeeAdd from '../pages/admin/EmployeeAdd.vue'
import EmployeeEdit from '../pages/admin/EmployeeEdit.vue'
import EquipmentList from '../pages/admin/EquipmentList.vue'
import EquipmentAdd from '../pages/admin/EquipmentAdd.vue'
import EquipmentEdit from '../pages/admin/EquipmentEdit.vue'
import UserInfo from '../pages/user/UserInfo.vue'
import UserEditInfo from '../pages/user/UserEditInfo.vue'
import UserClassList from '../pages/user/UserClassList.vue'
import UserApplyClass from '../pages/user/UserApplyClass.vue'
import UserChat from '../pages/user/UserChat.vue'
import UserRegister from '../pages/user/UserRegister.vue'
import ForgotPassword from '../pages/user/ForgotPassword.vue'
import UserApplyCard from '../pages/user/UserApplyCard.vue'
import CardApplicationList from '../pages/admin/CardApplicationList.vue'
import api from '../api/client'

const routes = [
  { path: '/', component: UserLogin },
  { path: '/admin', component: AdminLogin },
  { path: '/register', component: UserRegister },
  { path: '/forgot-password', component: ForgotPassword },

  { path: '/admin/dashboard', component: AdminMain, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/user/home', component: UserMain, meta: { requiresAuth: true, role: 'user' } },

  // 管理端
  { path: '/member/list', component: MemberList, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/add', component: MemberAdd, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/edit', component: MemberEdit, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/search', component: MemberSearch, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/member/card-applications', component: CardApplicationList, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/employee/list', component: EmployeeList, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/add', component: EmployeeAdd, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/employee/edit', component: EmployeeEdit, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/equipment/list', component: EquipmentList, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/add', component: EquipmentAdd, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/equipment/edit', component: EquipmentEdit, meta: { requiresAuth: true, role: 'admin' } },

  { path: '/class/list', component: ClassList, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/add', component: ClassAdd, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/class/orders', component: ClassOrderList, meta: { requiresAuth: true, role: 'admin' } },

  // 用户端
  { path: '/user/info', component: UserInfo, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/edit-info', component: UserEditInfo, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/classes', component: UserClassList, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/apply-class', component: UserApplyClass, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/chat', component: UserChat, meta: { requiresAuth: true, role: 'user' } },
  { path: '/user/apply-card', component: UserApplyCard, meta: { requiresAuth: true, role: 'user' } },

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
