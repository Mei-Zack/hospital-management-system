import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('@/views/LayoutView.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue')
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/views/UserView.vue'),
          meta: { requiresAdmin: true }
        },
        {
          path: 'doctors',
          name: 'doctors',
          component: () => import('@/views/DoctorView.vue')
        },
        {
          path: 'appointments',
          name: 'appointments',
          component: () => import('@/views/AppointmentView.vue')
        },
        {
          path: 'medicines',
          name: 'medicines',
          component: () => import('@/views/medicine/MedicineView.vue')
        },
        {
          path: 'medicine-inbounds',
          name: 'medicine-inbounds',
          component: () => import('@/views/medicine/InboundView.vue')
        },
        {
          path: 'medicine-sales',
          name: 'medicine-sales',
          component: () => import('@/views/medicine/SaleView.vue')
        },
        {
          path: 'medicine-statistics',
          name: 'medicine-statistics',
          component: () => import('@/views/medicine/StatisticsView.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  if (!token) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin && userStore.user?.role !== 'ADMIN') {
    next('/')
    return
  }

  next()
})

export default router 