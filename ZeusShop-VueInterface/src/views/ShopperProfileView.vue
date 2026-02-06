<template>
  <div class="shopper-profile-view">
    <header class="profile-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">个人中心</h1>
      <button class="logout-button" @click="handleLogout">退出</button>
    </header>
    
    <div class="profile-content">
      <div class="avatar-section">
        <div class="avatar-wrapper">
          <img :src="shopperStore.avatarUrl" class="avatar-image" />
          <label class="avatar-edit-button">
            📷
            <input type="file" accept="image/*" @change="handleAvatarChange" style="display: none;" />
          </label>
        </div>
        <h2 class="user-name">{{ shopperStore.displayName }}</h2>
        <span v-if="shopperStore.isEmployeeAccount" class="employee-tag">✨ 员工账号</span>
      </div>
      
      <div class="info-cards">
        <div class="info-card">
          <div class="card-label">用户名</div>
          <div class="card-value">{{ shopperStore.currentProfile?.loginUsername }}</div>
        </div>
        
        <div class="info-card">
          <div class="card-label">手机号</div>
          <div class="card-value">{{ shopperStore.currentProfile?.contactPhoneNumber || '未设置' }}</div>
        </div>
      </div>
      
      <div class="action-list">
        <button class="action-item" @click="goToOrders">
          <span class="action-icon">📦</span>
          <span class="action-text">我的订单</span>
          <span class="action-arrow">→</span>
        </button>
        
        <button class="action-item" @click="goToVouchers">
          <span class="action-icon">🎫</span>
          <span class="action-text">我的优惠券</span>
          <span class="action-arrow">→</span>
        </button>
        
        <button class="action-item" @click="goToScanner">
          <span class="action-icon">📱</span>
          <span class="action-text">扫码购物</span>
          <span class="action-arrow">→</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useShopperStore } from '@/stores/shopperStore'

const router = useRouter()
const shopperStore = useShopperStore()

function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (file) {
    const reader = new FileReader()
    reader.onload = async (e) => {
      const imageData = e.target?.result as string
      await shopperStore.updateProfileData({ portraitImageLink: imageData })
    }
    reader.readAsDataURL(file)
  }
}

async function handleLogout() {
  if (confirm('确定要退出登录吗？')) {
    await shopperStore.performLogoutAction()
    router.push('/login-portal')
  }
}

function goBack() {
  router.back()
}

function goToOrders() {
  router.push('/order-history')
}

function goToVouchers() {
  router.push('/voucher-center')
}

function goToScanner() {
  router.push('/qr-scanner')
}
</script>

<style scoped>
.shopper-profile-view {
  min-height: 100vh;
  background: var(--background-light);
}

.profile-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
}

.back-button, .logout-button {
  padding: 8px 16px;
  border: none;
  background: var(--background-light);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-button:hover, .logout-button:hover {
  background: var(--border-color);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.profile-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.avatar-section {
  background: white;
  border-radius: var(--radius-lg);
  padding: 40px 20px;
  text-align: center;
  box-shadow: var(--shadow-sm);
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.avatar-image {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--primary-color);
}

.avatar-edit-button {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: var(--primary-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 16px;
  box-shadow: var(--shadow-md);
}

.user-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.employee-tag {
  display: inline-block;
  padding: 4px 12px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.info-card {
  background: white;
  border-radius: var(--radius-md);
  padding: 16px;
  box-shadow: var(--shadow-sm);
}

.card-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.card-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.action-list {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.action-item {
  width: 100%;
  padding: 20px;
  border: none;
  background: white;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
  border-bottom: 1px solid var(--border-color);
}

.action-item:last-child {
  border-bottom: none;
}

.action-item:hover {
  background: var(--background-light);
}

.action-icon {
  font-size: 24px;
}

.action-text {
  flex: 1;
  text-align: left;
  font-size: 16px;
  font-weight: 500;
}

.action-arrow {
  font-size: 18px;
  color: var(--text-secondary);
}
</style>
