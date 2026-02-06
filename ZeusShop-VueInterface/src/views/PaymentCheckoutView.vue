<template>
  <div class="payment-checkout-view">
    <header class="checkout-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">确认订单</h1>
      <div style="width: 60px;"></div>
    </header>
    
    <div class="checkout-content">
      <div class="order-summary">
        <h2 class="section-title">📦 订单明细</h2>
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
          <div v-if="selectedVoucher" class="breakdown-row discount">
            <span>🎫 优惠券抵扣</span>
            <span class="discount-amount">-¥{{ selectedVoucher.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="breakdown-row total">
            <span>应付金额</span>
            <span class="total-price">¥{{ finalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </div>
      
      <!-- Voucher Selection Section -->
      <div class="voucher-section">
        <h2 class="section-title">🎁 选择优惠券</h2>
        <div v-if="availableVouchers.length === 0" class="no-vouchers">
          <p>暂无可用优惠券</p>
        </div>
        <div v-else class="voucher-list">
          <div 
            v-for="voucher in availableVouchers" 
            :key="voucher.voucherIdentifier"
            class="voucher-item"
            :class="{ selected: selectedVoucher?.voucherIdentifier === voucher.voucherIdentifier }"
            @click="selectVoucher(voucher)"
          >
            <div class="voucher-amount">¥{{ voucher.discountAmount }}</div>
            <div class="voucher-info">
              <div class="voucher-title">{{ voucher.voucherTitle }}</div>
              <div class="voucher-condition">满{{ voucher.minimumPurchase }}元可用</div>
            </div>
            <div class="voucher-check">
              <span v-if="selectedVoucher?.voucherIdentifier === voucher.voucherIdentifier">✓</span>
            </div>
          </div>
        </div>
        <button v-if="selectedVoucher" class="clear-voucher" @click="clearVoucher">
          不使用优惠券
        </button>
      </div>
      
      <!-- Payment methods commented out as per requirements -->
      <!-- WeChat and Alipay payment integration disabled -->
      <!--
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
      -->
      
      <ZeusButton 
        size="large" 
        style="width: 100%; margin-top: 24px;"
        @click="submitOrder"
        :loading="isSubmitting"
      >
        提交订单
      </ZeusButton>
    </div>
    
    <!-- Success Modal -->
    <div v-if="showSuccessModal" class="payment-modal" @click="closeModal">
      <div class="modal-content success-modal" @click.stop>
        <div class="success-icon">✓</div>
        <h2 class="modal-title">订单创建成功！</h2>
        <p class="order-number">订单编号: {{ createdOrderNumber }}</p>
        <p class="success-message">您的订单已成功生成</p>
        <ZeusButton @click="goToOrders" style="margin-top: 20px;">查看我的订单</ZeusButton>
      </div>
    </div>
    
    <!-- Payment QR Code Modal - Commented out as per requirements -->
    <!--
    <div v-if="showPaymentQr" class="payment-modal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h2 class="modal-title">扫码支付</h2>
        <img v-if="qrCodeImage" :src="qrCodeImage" class="qr-code-image" />
        <p class="payment-tip">请使用{{ selectedMethod === 1 ? '微信' : '支付宝' }}扫码支付</p>
        <p class="payment-amount">支付金额: ¥{{ finalAmount.toFixed(2) }}</p>
        <ZeusButton variant="secondary" @click="closeModal">关闭</ZeusButton>
      </div>
    </div>
    -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBasketStore } from '@/stores/basketStore'
import { useShopperStore } from '@/stores/shopperStore'
import { purchaseOrderApi } from '@/api/orderApi'
// Payment API commented out - no longer needed for direct order creation
// import { paymentChannelApi } from '@/api/paymentApi'
import { voucherDistributionApi, type VoucherClaim, type DiscountVoucher } from '@/api/voucherApi'
import { BasketItem } from '@/api/basketApi'
import ZeusButton from '@/components/ZeusButton.vue'

const router = useRouter()
const basketStore = useBasketStore()
const shopperStore = useShopperStore()

const selectedMethod = ref(1) // Keep for backend compatibility
const isSubmitting = ref(false)
const showSuccessModal = ref(false)
const createdOrderNumber = ref('')

// Payment QR code functionality commented out
// const showPaymentQr = ref(false)
// const qrCodeImage = ref('')

// Voucher selection state
const availableVouchers = ref<Array<DiscountVoucher & { voucherIdentifier: number }>>([])
const myVoucherClaims = ref<VoucherClaim[]>([])
const selectedVoucher = ref<(DiscountVoucher & { voucherIdentifier: number }) | null>(null)

const finalAmount = computed(() => {
  let amount = basketStore.totalPriceAmount
  if (selectedVoucher.value) {
    amount -= selectedVoucher.value.discountAmount
  }
  return amount > 0 ? amount : 0
})

function calculateItemTotal(item: BasketItem): number {
  const price = shopperStore.isEmployeeAccount ? item.staffDiscountPriceYuan : item.standardPriceYuan
  return price * item.selectedQuantity
}

async function loadAvailableVouchers() {
  try {
    const allVouchers = await voucherDistributionApi.listAvailableVouchers()
    myVoucherClaims.value = await voucherDistributionApi.fetchMyVouchers()
    
    // Filter vouchers that user owns and hasn't used
    const myVoucherIds = myVoucherClaims.value
      .filter(claim => claim.usageStatus === 0)
      .map(claim => claim.voucherId)
    
    // Only show vouchers that meet minimum purchase requirement
    availableVouchers.value = allVouchers.filter(v => 
      myVoucherIds.includes(v.voucherIdentifier) && 
      basketStore.totalPriceAmount >= v.minimumPurchase
    )
  } catch (error) {
    console.error('Failed to load vouchers:', error)
  }
}

function selectVoucher(voucher: DiscountVoucher & { voucherIdentifier: number }) {
  if (selectedVoucher.value?.voucherIdentifier === voucher.voucherIdentifier) {
    selectedVoucher.value = null
  } else {
    selectedVoucher.value = voucher
  }
}

function clearVoucher() {
  selectedVoucher.value = null
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
    
    // Create order with optional voucher
    const order = await purchaseOrderApi.createOrder({
      orderItems,
      voucherId: selectedVoucher.value?.voucherIdentifier,
      paymentMethod: selectedMethod.value
    })
    
    // Payment integration commented out - direct order creation without payment
    // const paymentData = selectedMethod.value === 1
    //   ? await paymentChannelApi.initiateWeChatPayment(order.orderNumber, order.finalPaymentYuan, '商品购买')
    //   : await paymentChannelApi.initiateAlipayPayment(order.orderNumber, order.finalPaymentYuan, '商品购买')
    // 
    // qrCodeImage.value = paymentData.qrCodeImage
    // showPaymentQr.value = true
    
    // Instead, show success modal and navigate to orders
    createdOrderNumber.value = order.orderNumber
    showSuccessModal.value = true
    
    // Clear basket after successful order
    await basketStore.refreshBasket()
    
    // Auto-redirect after 3 seconds
    setTimeout(() => {
      closeModal()
      router.push('/order-history')
    }, 3000)
    
  } catch (error) {
    console.error('Order submission failed:', error)
    alert('订单创建失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

function closeModal() {
  showSuccessModal.value = false
  // showPaymentQr.value = false
}

function goBack() {
  router.back()
}

function goToOrders() {
  closeModal()
  router.push('/order-history')
}

onMounted(() => {
  loadAvailableVouchers()
})
</script>

<style scoped>
.payment-checkout-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.checkout-header {
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.checkout-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.order-summary,
.payment-methods,
.voucher-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.order-summary:hover,
.voucher-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.summary-items {
  border-bottom: 1px solid #f0f0f0;
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
  color: #666;
}

.item-price {
  font-weight: 600;
  color: #ff6b6b;
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

.breakdown-row.discount {
  color: #51cf66;
  font-weight: 600;
}

.discount-amount {
  color: #51cf66;
}

.breakdown-row.total {
  padding-top: 12px;
  border-top: 2px solid #f0f0f0;
  font-size: 18px;
  font-weight: 700;
}

.total-price {
  color: #ff6b6b;
  font-size: 24px;
}

/* Voucher Section Styles */
.voucher-section {
  background: linear-gradient(135deg, #fff9e6 0%, #ffffff 100%);
}

.no-vouchers {
  text-align: center;
  padding: 20px;
  color: #999;
}

.voucher-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.voucher-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: white;
  border: 2px solid #ffe066;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.voucher-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
}

.voucher-item.selected {
  border-color: #ffc107;
  background: linear-gradient(135deg, #fff9e6 0%, #ffffff 100%);
  box-shadow: 0 4px 16px rgba(255, 193, 7, 0.4);
}

.voucher-amount {
  min-width: 60px;
  font-size: 20px;
  font-weight: 700;
  color: #ff6b6b;
  margin-right: 16px;
}

.voucher-info {
  flex: 1;
}

.voucher-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.voucher-condition {
  font-size: 13px;
  color: #999;
}

.voucher-check {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ffc107;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.clear-voucher {
  margin-top: 12px;
  padding: 8px 16px;
  background: #f1f3f5;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s ease;
}

.clear-voucher:hover {
  background: #e9ecef;
  color: #333;
}

.method-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.method-option {
  padding: 20px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-option:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.method-option.selected {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
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

/* Success Modal */
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
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 400px;
  text-align: center;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.success-modal {
  background: linear-gradient(135deg, #f0fff4 0%, #ffffff 100%);
}

.success-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #51cf66 0%, #37b24d 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  margin: 0 auto 20px;
  animation: scaleIn 0.5s ease;
}

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

.modal-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.order-number {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  font-family: monospace;
}

.success-message {
  font-size: 15px;
  color: #51cf66;
  font-weight: 500;
}

.qr-code-image {
  width: 250px;
  height: 250px;
  margin: 0 auto 20px;
  border: 2px solid #e9ecef;
  border-radius: 12px;
}

.payment-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.payment-amount {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b6b;
  margin-bottom: 24px;
}

@media (max-width: 768px) {
  .checkout-content {
    padding: 12px;
  }
  
  .order-summary,
  .voucher-section,
  .payment-methods {
    padding: 16px;
  }
  
  .voucher-item {
    padding: 12px;
  }
  
  .voucher-amount {
    font-size: 18px;
    min-width: 50px;
  }
}
</style>
