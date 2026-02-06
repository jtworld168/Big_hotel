<template>
  <div class="voucher-center-view">
    <header class="voucher-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">优惠券中心</h1>
      <div style="width: 60px;"></div>
    </header>
    
    <div class="voucher-content">
      <div class="section-title">可领取优惠券</div>
      <div class="vouchers-list">
        <div v-for="voucher in availableVouchers" :key="voucher.voucherIdentifier" class="voucher-card available">
          <div class="voucher-amount">
            <span class="amount-symbol">¥</span>
            <span class="amount-value">{{ voucher.discountAmount }}</span>
          </div>
          <div class="voucher-info">
            <div class="voucher-title">{{ voucher.voucherTitle }}</div>
            <div class="voucher-condition">满{{ voucher.minimumPurchase }}元可用</div>
            <div class="voucher-validity">有效期至: {{ formatDate(voucher.validUntilTime) }}</div>
          </div>
          <button class="claim-button" @click="claimVoucher(voucher.voucherIdentifier)">
            领取
          </button>
        </div>
      </div>
      
      <div class="section-title" style="margin-top: 32px;">我的优惠券</div>
      <div class="vouchers-list">
        <div v-for="claim in myVouchers" :key="claim.claimIdentifier" class="voucher-card owned">
          <div class="used-badge" v-if="claim.usageStatus === 1">已使用</div>
          <div class="voucher-amount">
            <span class="amount-symbol">¥</span>
            <span class="amount-value">--</span>
          </div>
          <div class="voucher-info">
            <div class="voucher-status">{{ claim.usageStatus === 0 ? '未使用' : '已使用' }}</div>
            <div class="voucher-claim-time">领取时间: {{ formatDate(claim.claimedAtTime) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { voucherDistributionApi, DiscountVoucher, VoucherClaim } from '@/api/voucherApi'

const router = useRouter()

const availableVouchers = ref<DiscountVoucher[]>([])
const myVouchers = ref<VoucherClaim[]>([])

async function loadVouchers() {
  try {
    availableVouchers.value = await voucherDistributionApi.listAvailableVouchers()
    myVouchers.value = await voucherDistributionApi.fetchMyVouchers()
  } catch (error) {
    console.error('Failed to load vouchers:', error)
  }
}

async function claimVoucher(voucherId: number) {
  try {
    await voucherDistributionApi.claimVoucher(voucherId)
    await loadVouchers()
  } catch (error) {
    console.error('Failed to claim voucher:', error)
  }
}

function formatDate(dateString: string): string {
  return dateString.substring(0, 10)
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadVouchers()
})
</script>

<style scoped>
.voucher-center-view {
  min-height: 100vh;
  background: var(--background-light);
}

.voucher-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
}

.back-button {
  padding: 8px 16px;
  border: none;
  background: var(--background-light);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-button:hover {
  background: var(--border-color);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.voucher-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: var(--text-primary);
}

.vouchers-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.voucher-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  position: relative;
  overflow: hidden;
}

.voucher-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
}

.voucher-card.owned::before {
  background: var(--text-secondary);
}

.voucher-card.owned {
  opacity: 0.7;
}

.used-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  background: var(--text-secondary);
  color: white;
  font-size: 12px;
  border-radius: 12px;
}

.voucher-amount {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.amount-symbol {
  font-size: 20px;
  font-weight: 600;
  color: var(--danger-color);
}

.amount-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--danger-color);
}

.voucher-info {
  flex: 1;
}

.voucher-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.voucher-condition {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.voucher-validity,
.voucher-status,
.voucher-claim-time {
  font-size: 13px;
  color: var(--text-secondary);
}

.claim-button {
  padding: 10px 24px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.claim-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
