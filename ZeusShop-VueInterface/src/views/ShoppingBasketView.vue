<template>
  <div class="shopping-basket-view">
    <header class="basket-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">购物篮</h1>
      <button class="clear-button" @click="clearAll" v-if="basketStore.hasItems">清空</button>
    </header>
    
    <div class="basket-content">
      <div v-if="basketStore.hasItems" class="basket-items">
        <div v-for="item in basketStore.basketContents" :key="item.basketItemId" class="basket-item-card">
          <img :src="item.mainImageUrl || '/placeholder.jpg'" class="item-image" />
          
          <div class="item-details">
            <h3 class="item-name">{{ item.itemDisplayName }}</h3>
            <div class="item-price">
              ¥{{ displayPrice(item).toFixed(2) }}
            </div>
          </div>
          
          <div class="quantity-controls">
            <button class="qty-button" @click="decreaseQty(item)">-</button>
            <span class="qty-display">{{ item.selectedQuantity }}</span>
            <button class="qty-button" @click="increaseQty(item)">+</button>
          </div>
          
          <button class="remove-button" @click="removeItem(item.basketItemId)">🗑️</button>
        </div>
      </div>
      
      <div v-else class="empty-basket">
        <div class="empty-icon">🛒</div>
        <p class="empty-text">购物篮空空如也</p>
        <ZeusButton @click="goToShop">去逛逛</ZeusButton>
      </div>
    </div>
    
    <div v-if="basketStore.hasItems" class="basket-footer">
      <div class="total-section">
        <span class="total-label">总计：</span>
        <span class="total-amount">¥{{ basketStore.totalPriceAmount.toFixed(2) }}</span>
      </div>
      <ZeusButton size="large" @click="proceedToCheckout">去结算</ZeusButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBasketStore } from '@/stores/basketStore'
import { useShopperStore } from '@/stores/shopperStore'
import { BasketItem } from '@/api/basketApi'
import ZeusButton from '@/components/ZeusButton.vue'

const router = useRouter()
const basketStore = useBasketStore()
const shopperStore = useShopperStore()

function displayPrice(item: BasketItem): number {
  return shopperStore.isEmployeeAccount ? item.staffDiscountPriceYuan : item.standardPriceYuan
}

async function increaseQty(item: BasketItem) {
  if (item.selectedQuantity < item.stockQuantity) {
    await basketStore.modifyItemQuantity(item.basketItemId, item.selectedQuantity + 1)
  }
}

async function decreaseQty(item: BasketItem) {
  if (item.selectedQuantity > 1) {
    await basketStore.modifyItemQuantity(item.basketItemId, item.selectedQuantity - 1)
  } else {
    await removeItem(item.basketItemId)
  }
}

async function removeItem(basketItemId: number) {
  await basketStore.removeItemFromBasket(basketItemId)
}

async function clearAll() {
  if (confirm('确定要清空购物篮吗？')) {
    await basketStore.clearAllItems()
  }
}

function proceedToCheckout() {
  router.push('/payment-checkout')
}

function goBack() {
  router.back()
}

function goToShop() {
  router.push('/merchandise-hall')
}

onMounted(() => {
  basketStore.refreshBasketData()
})
</script>

<style scoped>
.shopping-basket-view {
  min-height: 100vh;
  background: var(--background-light);
  display: flex;
  flex-direction: column;
}

.basket-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
}

.back-button, .clear-button {
  padding: 8px 16px;
  border: none;
  background: var(--background-light);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.back-button:hover, .clear-button:hover {
  background: var(--border-color);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.basket-content {
  flex: 1;
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.basket-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.basket-item-card {
  background: white;
  border-radius: var(--radius-md);
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-sm);
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.item-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--danger-color);
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.qty-button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.qty-button:hover {
  transform: scale(1.1);
}

.qty-display {
  min-width: 40px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
}

.remove-button {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  font-size: 20px;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.remove-button:hover {
  opacity: 1;
}

.empty-basket {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 18px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

.basket-footer {
  background: white;
  padding: 20px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 800px;
  width: 100%;
  margin: 0 auto;
}

.total-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 16px;
  color: var(--text-secondary);
}

.total-amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--danger-color);
}
</style>
