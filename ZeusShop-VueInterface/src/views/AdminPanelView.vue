<template>
  <div class="admin-panel-view">
    <header class="panel-header">
      <div class="header-content">
        <h1 class="panel-title">⚙️ {{ t('admin.panel') }}</h1>
        <div class="header-actions">
          <LanguageSwitcher />
          <button class="icon-button" @click="navigateBack">🏠</button>
        </div>
      </div>
    </header>

    <div class="panel-content">
      <div class="admin-sections">
        <!-- User Management Section -->
        <section class="admin-section">
          <h2 class="section-title">👥 {{ t('admin.userManagement') }}</h2>
          <div class="users-list">
            <div v-if="isLoadingUsers" class="loading-spinner"></div>
            <div v-else-if="users.length === 0" class="empty-state">
              {{ t('common.noData') }}
            </div>
            <div v-else class="user-card" v-for="user in users" :key="user.profileIdentifier">
              <div class="user-info">
                <img 
                  :src="user.portraitImageLink || '/default-avatar.png'" 
                  :alt="user.displayNickname"
                  class="user-avatar"
                />
                <div class="user-details">
                  <div class="user-name">{{ user.displayNickname }}</div>
                  <div class="user-meta">
                    <span class="user-username">@{{ user.loginUsername }}</span>
                    <span class="user-role" :class="`role-${user.userRole || 0}`">
                      {{ getRoleLabel(user.userRole || 0) }}
                    </span>
                  </div>
                  <div class="user-contact">📱 {{ user.contactPhoneNumber }}</div>
                </div>
              </div>
              <button 
                class="distribute-btn" 
                @click="openDistributeDialog(user)"
                :disabled="selectedVoucher === null"
              >
                {{ t('voucher.distributeVoucher') }}
              </button>
            </div>
          </div>
        </section>

        <!-- Voucher Selection Section -->
        <section class="admin-section">
          <h2 class="section-title">🎫 {{ t('admin.voucherManagement') }}</h2>
          <div class="voucher-select-area">
            <label class="select-label">{{ t('admin.selectUser') }}:</label>
            <select 
              v-model="selectedVoucher" 
              class="voucher-select"
            >
              <option :value="null">-- {{ t('voucher.availableVouchers') }} --</option>
              <option 
                v-for="voucher in availableVouchers" 
                :key="voucher.voucherIdentifier"
                :value="voucher.voucherIdentifier"
              >
                {{ voucher.voucherTitle }} - ¥{{ voucher.discountAmount }}
              </option>
            </select>
          </div>
          
          <div class="vouchers-list">
            <div v-if="isLoadingVouchers" class="loading-spinner"></div>
            <div v-else-if="availableVouchers.length === 0" class="empty-state">
              {{ t('common.noData') }}
            </div>
            <div v-else class="voucher-card" v-for="voucher in availableVouchers" :key="voucher.voucherIdentifier">
              <div class="voucher-header">
                <h3 class="voucher-title">{{ voucher.voucherTitle }}</h3>
                <span class="voucher-code">{{ voucher.voucherCode }}</span>
              </div>
              <div class="voucher-details">
                <div class="voucher-amount">¥{{ voucher.discountAmount }}</div>
                <div class="voucher-info">
                  <div>{{ t('voucher.minPurchase') }}: ¥{{ voucher.minimumPurchase }}</div>
                  <div>{{ t('voucher.claimed') }}: {{ voucher.claimedQuantity }}/{{ voucher.totalIssueQuantity }}</div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>

    <!-- Distribution Confirmation Dialog -->
    <div v-if="showDistributeDialog" class="modal-overlay" @click="closeDistributeDialog">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">{{ t('voucher.distributeVoucher') }}</h3>
        <p class="modal-message">
          {{ t('admin.distributeToUser') }}: <strong>{{ selectedUser?.displayNickname }}</strong>?
        </p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeDistributeDialog">{{ t('common.cancel') }}</button>
          <button class="btn-confirm" @click="confirmDistribute">{{ t('admin.distribute') }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'
import { shopperAccountApi } from '@/api/shopperAccountApi'
import { voucherApi } from '@/api/voucherApi'

const router = useRouter()
const { t, locale } = useI18n()

interface User {
  profileIdentifier: number
  loginUsername: string
  displayNickname: string
  portraitImageLink: string | null
  contactPhoneNumber: string
  userRole: number
}

interface Voucher {
  voucherIdentifier: number
  voucherCode: string
  voucherTitle: string
  discountAmount: number
  minimumPurchase: number
  totalIssueQuantity: number
  claimedQuantity: number
}

const users = ref<User[]>([])
const availableVouchers = ref<Voucher[]>([])
const selectedVoucher = ref<number | null>(null)
const selectedUser = ref<User | null>(null)
const showDistributeDialog = ref(false)
const isLoadingUsers = ref(false)
const isLoadingVouchers = ref(false)

const getRoleLabel = (roleCode: number) => {
  switch(roleCode) {
    case 2: return t('user.role.admin')
    case 1: return t('user.role.employee')
    default: return t('user.role.ordinary')
  }
}

const loadUsers = async () => {
  isLoadingUsers.value = true
  try {
    const response = await shopperAccountApi.listAllShoppers()
    if (response.success) {
      users.value = response.data
    }
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    isLoadingUsers.value = false
  }
}

const loadVouchers = async () => {
  isLoadingVouchers.value = true
  try {
    const response = await voucherApi.listAvailable()
    if (response.success) {
      availableVouchers.value = response.data
    }
  } catch (error) {
    console.error('Failed to load vouchers:', error)
  } finally {
    isLoadingVouchers.value = false
  }
}

const openDistributeDialog = (user: User) => {
  if (!selectedVoucher.value) {
    alert('请先选择要分发的优惠券')
    return
  }
  selectedUser.value = user
  showDistributeDialog.value = true
}

const closeDistributeDialog = () => {
  showDistributeDialog.value = false
  selectedUser.value = null
}

const confirmDistribute = async () => {
  if (!selectedVoucher.value || !selectedUser.value) return
  
  try {
    const response = await voucherApi.distributeToUser(selectedVoucher.value, selectedUser.value.profileIdentifier)
    if (response.success) {
      alert(t('admin.distributeSuccess'))
      closeDistributeDialog()
    } else {
      alert(response.message || t('common.failed'))
    }
  } catch (error) {
    console.error('Failed to distribute voucher:', error)
    alert(t('common.failed'))
  }
}

const navigateBack = () => {
  router.push('/merchandise-hall')
}

onMounted(() => {
  loadUsers()
  loadVouchers()
})
</script>

<style scoped>
.admin-panel-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 40px;
}

.panel-header {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.panel-title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.icon-button {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.icon-button:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.panel-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.admin-sections {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.admin-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 22px;
  font-weight: bold;
  margin: 0 0 20px 0;
  color: #333;
}

.users-list,
.vouchers-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 600px;
  overflow-y: auto;
}

.user-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.user-card:hover {
  border-color: #667eea;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.2);
}

.user-info {
  display: flex;
  gap: 15px;
  align-items: center;
  flex: 1;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.user-meta {
  display: flex;
  gap: 10px;
  align-items: center;
}

.user-username {
  font-size: 13px;
  color: #666;
}

.user-role {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.role-0 {
  background: #e3f2fd;
  color: #1976d2;
}

.role-1 {
  background: #fff3e0;
  color: #f57c00;
}

.role-2 {
  background: #fce4ec;
  color: #c2185b;
}

.user-contact {
  font-size: 13px;
  color: #888;
}

.distribute-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.distribute-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.distribute-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.voucher-select-area {
  margin-bottom: 20px;
}

.select-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.voucher-select {
  width: 100%;
  padding: 10px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s ease;
}

.voucher-select:focus {
  border-color: #667eea;
}

.voucher-card {
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.voucher-card:hover {
  border-color: #667eea;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.2);
}

.voucher-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.voucher-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.voucher-code {
  font-size: 12px;
  color: #666;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.voucher-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.voucher-amount {
  font-size: 24px;
  font-weight: bold;
  color: #667eea;
}

.voucher-info {
  text-align: right;
  font-size: 13px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

.loading-spinner {
  text-align: center;
  padding: 40px;
}

.loading-spinner::after {
  content: '';
  display: inline-block;
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 15px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.modal-title {
  margin: 0 0 15px 0;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.modal-message {
  margin: 0 0 25px 0;
  color: #666;
  line-height: 1.6;
}

.modal-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.btn-cancel,
.btn-confirm {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #f5f5f5;
  color: #666;
}

.btn-cancel:hover {
  background: #e0e0e0;
}

.btn-confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .admin-sections {
    grid-template-columns: 1fr;
  }
}
</style>
