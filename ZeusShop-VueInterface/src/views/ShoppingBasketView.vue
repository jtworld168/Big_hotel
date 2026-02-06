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
  background: linear-gradient(135deg, #e0f2f7 0%, #b2ebf2 100%);
  display: flex;
  flex-direction: column;
}

.basket-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.back-button, .clear-button {
  padding: 8px 16px;
  border: none;
  background: linear-gradient(135deg, #00acc1 0%, #0097a7 100%);
  color: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-button:hover, .clear-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 172, 193, 0.4);
}

.clear-button {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
}

.clear-button:hover {
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #00acc1 0%, #0097a7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
  border-radius: 16px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.basket-item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 12px;
  border: 2px solid #e0f2f7;
}

.item-details {
  flex: 1;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #2d3436;
}

.item-price {
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 24px;
}

.qty-button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #00acc1 0%, #0097a7 100%);
  color: white;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.qty-button:hover {
  transform: scale(1.15);
  box-shadow: 0 4px 12px rgba(0, 172, 193, 0.4);
}

.qty-button:active {
  transform: scale(0.95);
}

.qty-display {
  min-width: 40px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #2d3436;
}

.remove-button {
  width: 36px;
  height: 36px;
  border: none;
  background: #ffe0e0;
  border-radius: 50%;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-button:hover {
  background: #ffcccc;
  transform: rotate(15deg) scale(1.1);
}

.empty-basket {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-text {
  font-size: 18px;
  color: #636e72;
  margin-bottom: 24px;
}

.basket-footer {
  background: white;
  padding: 20px;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.total-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.total-label {
  font-size: 16px;
  font-weight: 500;
  color: #636e72;
}

.total-amount {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@media (max-width: 768px) {
  .basket-content {
    padding: 12px;
  }
  
  .basket-item-card {
    padding: 12px;
    gap: 12px;
  }
  
  .item-image {
    width: 60px;
    height: 60px;
  }
  
  .item-name {
    font-size: 14px;
  }
  
  .item-price {
    font-size: 16px;
  }
  
  .basket-footer {
    flex-direction: column;
    gap: 12px;
  }
  
  .total-amount {
    font-size: 24px;
  }
}
</style>
