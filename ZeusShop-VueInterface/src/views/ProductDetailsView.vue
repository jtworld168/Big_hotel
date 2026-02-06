<template>
  <div class="product-details-view">
    <header class="details-header">
      <button class="back-button" @click="goBack">
        <span>←</span>
        <span>{{ t('common.back') }}</span>
      </button>
      <LanguageSwitcher />
    </header>

    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>

    <div v-else-if="product" class="details-content">
      <div class="product-image-section">
        <img 
          :src="product.mainImageUrl || '/placeholder.jpg'" 
          :alt="product.itemDisplayName" 
          class="main-image"
        />
        <div v-if="product.employeeEligible" class="employee-badge">
          {{ t('product.employeePrice') }}
        </div>
        <div v-if="product.stockQuantity <= 10 && product.stockQuantity > 0" class="stock-badge warning">
          {{ t('product.lowStock') }}
        </div>
        <div v-else-if="product.stockQuantity === 0" class="stock-badge danger">
          {{ t('product.outOfStock') }}
        </div>
      </div>

      <div class="product-info-section">
        <h1 class="product-title">{{ product.itemDisplayName }}</h1>
        
        <div class="product-meta">
          <span class="meta-item">
            <strong>SKU:</strong> {{ product.itemSku }}
          </span>
          <span class="meta-item">
            <strong>{{ t('product.category') }}:</strong> {{ product.categoryLabel }}
          </span>
          <span class="meta-item">
            <strong>{{ t('product.stock') }}:</strong> {{ product.stockQuantity }} {{ t('common.units') }}
          </span>
        </div>

        <div class="product-description">
          <h3>{{ t('product.description') }}</h3>
          <p>{{ product.itemDescription || t('product.noDescription') }}</p>
        </div>

        <div class="price-section">
          <div class="price-info">
            <div class="current-price">
              <span class="price-label">{{ t('product.price') }}:</span>
              <span class="price-value">¥{{ product.finalPriceYuan.toFixed(2) }}</span>
            </div>
            <div v-if="showDiscountInfo" class="discount-info">
              <span class="original-price">¥{{ product.standardPriceYuan.toFixed(2) }}</span>
              <span class="discount-badge">{{ t('user.role.employee') }}</span>
            </div>
          </div>
        </div>

        <div class="action-section">
          <div class="quantity-selector">
            <button 
              class="qty-btn" 
              @click="decreaseQuantity" 
              :disabled="quantity <= 1"
            >
              -
            </button>
            <input 
              type="number" 
              v-model.number="quantity" 
              class="qty-input"
              min="1"
              :max="product.stockQuantity"
            />
            <button 
              class="qty-btn" 
              @click="increaseQuantity"
              :disabled="quantity >= product.stockQuantity"
            >
              +
            </button>
          </div>

          <button 
            class="add-to-cart-btn" 
            @click="addToCart"
            :disabled="product.stockQuantity === 0 || isAddingToCart"
          >
            <span v-if="!isAddingToCart">🛒 {{ t('product.addToCart') }}</span>
            <span v-else>{{ t('common.loading') }}</span>
          </button>
        </div>
      </div>
    </div>

    <div v-else class="error-message">
      {{ t('product.notFound') }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { merchandiseCatalogApi, MerchandiseDisplay } from '@/api/merchandiseApi'
import { useBasketStore } from '@/stores/basketStore'
import { useShopperStore } from '@/stores/shopperStore'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const basketStore = useBasketStore()
const shopperStore = useShopperStore()

const product = ref<MerchandiseDisplay | null>(null)
const isLoading = ref(false)
const isAddingToCart = ref(false)
const quantity = ref(1)

const showDiscountInfo = computed(() => {
  return product.value?.employeeEligible && 
         product.value?.finalPriceYuan < product.value?.standardPriceYuan
})

async function loadProductDetails() {
  const itemId = Number(route.params.id)
  if (!itemId) {
    return
  }

  isLoading.value = true
  try {
    product.value = await merchandiseCatalogApi.viewItemDetails(itemId)
  } catch (error) {
    console.error('Failed to load product details:', error)
  } finally {
    isLoading.value = false
  }
}

function increaseQuantity() {
  if (product.value && quantity.value < product.value.stockQuantity) {
    quantity.value++
  }
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

async function addToCart() {
  if (!product.value || product.value.stockQuantity === 0) {
    return
  }

  isAddingToCart.value = true
  try {
    const success = await basketStore.addMerchandiseToBasket(
      product.value.itemIdentifier, 
      quantity.value
    )
    
    if (success) {
      alert(t('cart.itemAdded'))
      quantity.value = 1 // Reset quantity
    }
  } catch (error) {
    console.error('Failed to add to cart:', error)
    alert(t('common.failed'))
  } finally {
    isAddingToCart.value = false
  }
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadProductDetails()
})
</script>

<style scoped>
.product-details-view {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 40px;
}

.details-header {
  background: white;
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.details-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
}

.product-image-section {
  position: relative;
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.main-image {
  width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: contain;
  border-radius: 12px;
}

.employee-badge, .stock-badge {
  position: absolute;
  top: 20px;
  padding: 8px 16px;
  color: white;
  font-size: 13px;
  font-weight: 600;
  border-radius: 20px;
}

.employee-badge {
  left: 20px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stock-badge {
  right: 20px;
}

.stock-badge.warning {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stock-badge.danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.product-info-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0;
}

.product-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  font-size: 14px;
  color: #666;
}

.meta-item strong {
  color: #333;
}

.product-description h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-description p {
  font-size: 15px;
  line-height: 1.6;
  color: #666;
}

.price-section {
  padding: 20px 0;
  border-top: 2px solid #f0f0f0;
  border-bottom: 2px solid #f0f0f0;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price-label {
  font-size: 16px;
  color: #666;
}

.price-value {
  font-size: 32px;
  font-weight: 700;
  color: #ef4444;
}

.discount-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.original-price {
  font-size: 18px;
  color: #999;
  text-decoration: line-through;
}

.discount-badge {
  padding: 4px 12px;
  background: #f59e0b;
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 12px;
}

.action-section {
  display: flex;
  gap: 16px;
  align-items: center;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 12px;
}

.qty-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.qty-btn:hover:not(:disabled) {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.qty-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-input {
  width: 60px;
  height: 36px;
  text-align: center;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
}

.qty-input:focus {
  outline: none;
  border-color: #667eea;
}

.add-to-cart-btn {
  flex: 1;
  padding: 16px 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-to-cart-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.add-to-cart-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error-message {
  text-align: center;
  padding: 60px 20px;
  color: white;
  font-size: 18px;
}

@media (max-width: 768px) {
  .details-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .product-title {
    font-size: 24px;
  }

  .price-value {
    font-size: 28px;
  }

  .action-section {
    flex-direction: column;
  }

  .quantity-selector {
    width: 100%;
    justify-content: center;
  }
}
</style>
