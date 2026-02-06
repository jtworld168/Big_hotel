<template>
  <div class="product-card" @click="handleCardClick">
    <div class="card-image-wrapper">
      <img :src="product.mainImageUrl || '/placeholder.jpg'" :alt="product.itemDisplayName" class="product-image" />
      <div v-if="product.employeeEligible" class="employee-badge">员工特惠</div>
      <div v-if="product.stockQuantity <= 10" class="low-stock-badge">仅剩{{ product.stockQuantity }}件</div>
    </div>
    
    <div class="card-content">
      <h3 class="product-name">{{ product.itemDisplayName }}</h3>
      <p class="product-description">{{ truncatedDescription }}</p>
      
      <div class="price-section">
        <div class="price-container">
          <span class="final-price">¥{{ product.finalPriceYuan.toFixed(2) }}</span>
          <span v-if="showOriginalPrice" class="original-price">¥{{ product.standardPriceYuan.toFixed(2) }}</span>
        </div>
        
        <button class="add-button" @click.stop="handleAddToCart">
          <span class="button-icon">+</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { MerchandiseDisplay } from '@/api/merchandiseApi'

interface Props {
  product: MerchandiseDisplay
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'add-to-cart': [product: MerchandiseDisplay]
  'view-details': [product: MerchandiseDisplay]
}>()

const showOriginalPrice = computed(() => {
  return props.product.employeeEligible && 
         props.product.finalPriceYuan < props.product.standardPriceYuan
})

const truncatedDescription = computed(() => {
  const desc = props.product.itemDescription || ''
  return desc.length > 50 ? desc.substring(0, 50) + '...' : desc
})

function handleCardClick() {
  emit('view-details', props.product)
}

function handleAddToCart() {
  emit('add-to-cart', props.product)
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  background: var(--background-light);
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.employee-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
}

.low-stock-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 12px;
  background: var(--danger-color);
  color: white;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
}

.card-content {
  padding: 16px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-description {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
  min-height: 38px;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.final-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--danger-color);
}

.original-price {
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: line-through;
}

.add-button {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-button:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.add-button:active {
  transform: scale(0.95);
}

.button-icon {
  font-size: 20px;
  font-weight: 600;
}
</style>
