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
  background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
}

.voucher-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.back-button {
  padding: 8px 16px;
  border: none;
  background: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(253, 203, 110, 0.4);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
  color: #2d3436;
  text-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.vouchers-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.voucher-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.voucher-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.voucher-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: linear-gradient(135deg, #fdcb6e 0%, #e17055 100%);
}

.voucher-card.available::before {
  background: linear-gradient(135deg, #55efc4 0%, #00b894 100%);
}

.voucher-card.owned::before {
  background: linear-gradient(135deg, #a29bfe 0%, #6c5ce7 100%);
}

.voucher-card.owned {
  opacity: 0.8;
  background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
}

.used-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #b2bec3 0%, #636e72 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.voucher-amount {
  display: flex;
  align-items: baseline;
  gap: 4px;
  min-width: 100px;
}

.amount-symbol {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.amount-value {
  font-size: 36px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.voucher-info {
  flex: 1;
}

.voucher-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2d3436;
}

.voucher-condition {
  font-size: 14px;
  color: #636e72;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.voucher-condition::before {
  content: '📍';
  font-size: 12px;
}

.voucher-validity,
.voucher-status,
.voucher-claim-time {
  font-size: 13px;
  color: #95a5a6;
  display: flex;
  align-items: center;
  gap: 6px;
}

.voucher-validity::before {
  content: '⏰';
  font-size: 12px;
}

.voucher-status::before {
  content: '💳';
  font-size: 12px;
}

.voucher-claim-time::before {
  content: '📅';
  font-size: 12px;
}

.claim-button {
  padding: 12px 28px;
  border: none;
  border-radius: 24px;
  background: linear-gradient(135deg, #55efc4 0%, #00b894 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 184, 148, 0.3);
}

.claim-button:hover {
  transform: scale(1.05) translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 184, 148, 0.4);
}

.claim-button:active {
  transform: scale(0.98);
}

@media (max-width: 768px) {
  .voucher-content {
    padding: 12px;
  }
  
  .voucher-card {
    padding: 16px;
    gap: 12px;
  }
  
  .amount-value {
    font-size: 28px;
  }
  
  .voucher-amount {
    min-width: 80px;
  }
  
  .claim-button {
    padding: 10px 20px;
    font-size: 13px;
  }
}
</style>
