<template>
  <div class="payment-checkout-view">
    <header class="checkout-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">确认支付</h1>
      <div style="width: 60px;"></div>
    </header>
    
    <div class="checkout-content">
      <div class="order-summary">
        <h2 class="section-title">订单明细</h2>
        <div class="summary-items">
          <div v-for="item in basketStore.basketContents" :key="item.basketItemId" class="summary-item">
            <span class="item-name">{{ item.itemDisplayName }} x{{ item.selectedQuantity }}</span>
            <span class="item-price">¥{{ calculateItemTotal(item).toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="price-breakdown">
          <div class="breakdown-row">
            <span>商品总价</span>
            <span>¥{{ basketStore.totalPriceAmount.toFixed(2) }}</span>
          </div>
          <div class="breakdown-row total">
            <span>应付金额</span>
            <span class="total-price">¥{{ finalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <div class="payment-methods">
        <h2 class="section-title">支付方式</h2>
        <div class="method-options">
          <div 
            class="method-option"
            :class="{ selected: selectedMethod === 1 }"
            @click="selectedMethod = 1"
          >
            <span class="method-icon">💚</span>
            <span class="method-name">微信支付</span>
          </div>
          <div 
            class="method-option"
            :class="{ selected: selectedMethod === 2 }"
            @click="selectedMethod = 2"
          >
            <span class="method-icon">💙</span>
            <span class="method-name">支付宝</span>
          </div>
        </div>
      </div>
      
      <ZeusButton 
        size="large" 
        style="width: 100%; margin-top: 24px;"
        @click="submitOrder"
        :loading="isSubmitting"
      >
        提交订单
      </ZeusButton>
    </div>
    
    <div v-if="showPaymentQr" class="payment-modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2 class="modal-title">扫码支付</h2>
        <img v-if="qrCodeImage" :src="qrCodeImage" class="qr-code-image" />
        <p class="payment-tip">请使用{{ selectedMethod === 1 ? '微信' : '支付宝' }}扫码支付</p>
        <p class="payment-amount">支付金额: ¥{{ finalAmount.toFixed(2) }}</p>
        <ZeusButton variant="secondary" @click="closeModal">关闭</ZeusButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useBasketStore } from '@/stores/basketStore'
import { useShopperStore } from '@/stores/shopperStore'
import { purchaseOrderApi } from '@/api/orderApi'
import { paymentChannelApi } from '@/api/paymentApi'
import { BasketItem } from '@/api/basketApi'
import ZeusButton from '@/components/ZeusButton.vue'

const router = useRouter()
const basketStore = useBasketStore()
const shopperStore = useShopperStore()

const selectedMethod = ref(1)
const isSubmitting = ref(false)
const showPaymentQr = ref(false)
const qrCodeImage = ref('')

const finalAmount = computed(() => basketStore.totalPriceAmount)

function calculateItemTotal(item: BasketItem): number {
  const price = shopperStore.isEmployeeAccount ? item.staffDiscountPriceYuan : item.standardPriceYuan
  return price * item.selectedQuantity
}

async function submitOrder() {
  isSubmitting.value = true
  
  try {
    const orderItems = basketStore.basketContents.map(item => ({
      itemId: item.itemIdentifier,
      itemName: item.itemDisplayName,
      quantity: item.selectedQuantity,
      unitPrice: shopperStore.isEmployeeAccount ? item.staffDiscountPriceYuan : item.standardPriceYuan,
      subtotal: calculateItemTotal(item)
    }))
    
    const order = await purchaseOrderApi.createOrder({
      orderItems,
      paymentMethod: selectedMethod.value
    })
    
    const paymentData = selectedMethod.value === 1
      ? await paymentChannelApi.initiateWeChatPayment(order.orderNumber, order.finalPaymentYuan, '商品购买')
      : await paymentChannelApi.initiateAlipayPayment(order.orderNumber, order.finalPaymentYuan, '商品购买')
    
    qrCodeImage.value = paymentData.qrCodeImage
    showPaymentQr.value = true
    
    setTimeout(() => {
      closeModal()
      router.push('/order-history')
    }, 5000)
    
  } catch (error) {
    console.error('Order submission failed:', error)
  } finally {
    isSubmitting.value = false
  }
}

function closeModal() {
  showPaymentQr.value = false
}

function goBack() {
  router.back()
}
</script>

<style scoped>
.payment-checkout-view {
  min-height: 100vh;
  background: var(--background-light);
}

.checkout-header {
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

.checkout-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.order-summary,
.payment-methods {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.summary-items {
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
  margin-bottom: 16px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.item-name {
  color: var(--text-secondary);
}

.item-price {
  font-weight: 600;
}

.price-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.breakdown-row {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
}

.breakdown-row.total {
  padding-top: 12px;
  border-top: 2px solid var(--border-color);
  font-size: 18px;
  font-weight: 700;
}

.total-price {
  color: var(--danger-color);
  font-size: 24px;
}

.method-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.method-option {
  padding: 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-option.selected {
  border-color: var(--primary-color);
  background: rgba(102, 126, 234, 0.05);
}

.method-icon {
  display: block;
  font-size: 32px;
  margin-bottom: 8px;
}

.method-name {
  font-size: 14px;
  font-weight: 500;
}

.payment-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: var(--radius-lg);
  padding: 32px;
  max-width: 400px;
  text-align: center;
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 24px;
}

.qr-code-image {
  width: 250px;
  height: 250px;
  margin: 0 auto 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
}

.payment-tip {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.payment-amount {
  font-size: 20px;
  font-weight: 700;
  color: var(--danger-color);
  margin-bottom: 24px;
}
</style>
