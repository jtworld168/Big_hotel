<template>
  <div class="order-history-view">
    <header class="history-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">订单历史</h1>
      <div style="width: 60px;"></div>
    </header>
    
    <div class="history-content">
      <div v-if="orders.length > 0" class="orders-list">
        <div v-for="order in orders" :key="order.orderIdentifier" class="order-card">
          <div class="order-header-row">
            <span class="order-number">订单号: {{ order.orderNumber }}</span>
            <span class="order-status" :class="getStatusClass(order.orderStatus)">
              {{ getStatusText(order.orderStatus) }}
            </span>
          </div>
          
          <div class="order-time">{{ formatDateTime(order.recordCreatedTime) }}</div>
          
          <div class="order-details">
            <div class="detail-row">
              <span class="detail-label">商品总额</span>
              <span class="detail-value">¥{{ order.totalAmountYuan.toFixed(2) }}</span>
            </div>
            <div class="detail-row" v-if="order.discountAmountYuan > 0">
              <span class="detail-label">优惠金额</span>
              <span class="detail-value discount">-¥{{ order.discountAmountYuan.toFixed(2) }}</span>
            </div>
            <div class="detail-row total">
              <span class="detail-label">实付金额</span>
              <span class="detail-value amount">¥{{ order.finalPaymentYuan.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <span class="payment-method">{{ getPaymentMethod(order.paymentMethod) }}</span>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-orders">
        <div class="empty-icon">📦</div>
        <p class="empty-text">暂无订单记录</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { purchaseOrderApi, PurchaseOrder } from '@/api/orderApi'

const router = useRouter()
const orders = ref<PurchaseOrder[]>([])

async function loadOrders() {
  try {
    orders.value = await purchaseOrderApi.fetchMyOrders()
  } catch (error) {
    console.error('Failed to load orders:', error)
  }
}

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已完成'
  }
  return statusMap[status] || '未知'
}

function getStatusClass(status: number): string {
  const classMap: Record<number, string> = {
    0: 'pending',
    1: 'paid',
    2: 'completed'
  }
  return classMap[status] || ''
}

function getPaymentMethod(method: number): string {
  return method === 1 ? '微信支付' : '支付宝'
}

function formatDateTime(dateString: string): string {
  return dateString.replace('T', ' ').substring(0, 19)
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-history-view {
  min-height: 100vh;
  background: var(--background-light);
}

.history-header {
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

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.history-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.order-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-number {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.order-status.pending {
  background: #fef3c7;
  color: #92400e;
}

.order-status.paid {
  background: #d1fae5;
  color: #065f46;
}

.order-status.completed {
  background: #dbeafe;
  color: #1e40af;
}

.order-time {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.order-details {
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
  padding: 16px 0;
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-row.total {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed var(--border-color);
  font-size: 16px;
  font-weight: 600;
}

.detail-label {
  color: var(--text-secondary);
}

.detail-value {
  font-weight: 500;
}

.detail-value.discount {
  color: var(--success-color);
}

.detail-value.amount {
  color: var(--danger-color);
  font-size: 18px;
  font-weight: 700;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
}

.payment-method {
  font-size: 13px;
  color: var(--text-secondary);
}

.empty-orders {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
  color: var(--text-secondary);
}
</style>
