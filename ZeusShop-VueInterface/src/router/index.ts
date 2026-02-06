import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useShopperStore } from '@/stores/shopperStore'

const routeDefinitions: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/merchandise-hall'
  },
  {
    path: '/login-portal',
    name: 'LoginPortal',
    component: () => import('@/views/LoginPortalView.vue')
  },
  {
    path: '/merchandise-hall',
    name: 'MerchandiseHall',
    component: () => import('@/views/MerchandiseHallView.vue')
  },
  {
    path: '/shopping-basket',
    name: 'ShoppingBasket',
    component: () => import('@/views/ShoppingBasketView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/voucher-center',
    name: 'VoucherCenter',
    component: () => import('@/views/VoucherCenterView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/shopper-profile',
    name: 'ShopperProfile',
    component: () => import('@/views/ShopperProfileView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/payment-checkout',
    name: 'PaymentCheckout',
    component: () => import('@/views/PaymentCheckoutView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order-history',
    name: 'OrderHistory',
    component: () => import('@/views/OrderHistoryView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/qr-scanner',
    name: 'QrScanner',
    component: () => import('@/views/QrScannerView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-panel',
    name: 'AdminPanel',
    component: () => import('@/views/AdminPanelView.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: routeDefinitions
})

router.beforeEach((to, from, next) => {
  const shopperStore = useShopperStore()
  
  if (to.meta.requiresAuth && !shopperStore.sessionActive) {
    next({ name: 'LoginPortal', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
