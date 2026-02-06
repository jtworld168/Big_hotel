<template>
  <div class="merchandise-hall-view">
    <header class="hall-header">
      <div class="header-content">
        <h1 class="hall-title">🏪 {{ t('product.catalog') }}</h1>
        <div class="header-actions">
          <LanguageSwitcher />
          <button class="icon-button" @click="navigateToAdmin" v-if="isAdmin" title="Admin Panel">
            ⚙️
          </button>
          <button class="icon-button" @click="navigateToBasket">
            🛒
            <span v-if="basketStore.totalItemCount > 0" class="badge">{{ basketStore.totalItemCount }}</span>
          </button>
          <button class="icon-button" @click="navigateToProfile">👤</button>
        </div>
      </div>
      
      <div class="category-filter">
        <button 
          v-for="cat in categories" 
          :key="cat.value"
          class="category-button"
          :class="{ active: selectedCategory === cat.value }"
          @click="selectCategory(cat.value)"
        >
          {{ cat.label }}
        </button>
      </div>
    </header>
    
    <div class="products-grid">
      <ProductCard
        v-for="product in productList"
        :key="product.itemIdentifier"
        :product="product"
        @add-to-cart="handleAddToCart"
      />
    </div>
    
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner-large"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { merchandiseCatalogApi, MerchandiseDisplay } from '@/api/merchandiseApi'
import { useBasketStore } from '@/stores/basketStore'
import { useShopperStore } from '@/stores/shopperStore'
import ProductCard from '@/components/ProductCard.vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const router = useRouter()
const { t } = useI18n()
const basketStore = useBasketStore()
const shopperStore = useShopperStore()

const productList = ref<MerchandiseDisplay[]>([])
const selectedCategory = ref<string | null>(null)
const isLoading = ref(false)

const isAdmin = computed(() => shopperStore.currentUser?.userRole === 2)

const categories = computed(() => [
  { label: t('product.all'), value: null },
  { label: t('product.beverage'), value: '饮料' },
  { label: t('product.snack'), value: '零食' },
  { label: t('product.instant'), value: '方便食品' }
])

async function loadProducts() {
  isLoading.value = true
  try {
    const result = await merchandiseCatalogApi.browseCatalog(1, 100, selectedCategory.value || undefined)
    productList.value = result.records
  } finally {
    isLoading.value = false
  }
}

function selectCategory(category: string | null) {
  selectedCategory.value = category
  loadProducts()
}

async function handleAddToCart(product: MerchandiseDisplay) {
  await basketStore.addMerchandiseToBasket(product.itemIdentifier, 1)
}

function navigateToBasket() {
  router.push('/shopping-basket')
}

function navigateToProfile() {
  router.push('/shopper-profile')
}

function navigateToAdmin() {
  router.push('/admin-panel')
}

onMounted(() => {
  loadProducts()
  basketStore.refreshBasketData()
})
</script>

<style scoped>
.merchandise-hall-view {
  min-height: 100vh;
  background: var(--background-light);
}

.hall-header {
  background: white;
  box-shadow: var(--shadow-sm);
  padding: 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.hall-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  gap: 12px;
}

.icon-button {
  position: relative;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: var(--background-light);
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.icon-button:hover {
  background: var(--border-color);
  transform: scale(1.1);
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 20px;
  height: 20px;
  background: var(--danger-color);
  color: white;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}

.category-filter {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
  overflow-x: auto;
}

.category-button {
  padding: 8px 20px;
  border: 2px solid var(--border-color);
  border-radius: 20px;
  background: white;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.category-button.active {
  border-color: var(--primary-color);
  background: var(--primary-color);
  color: white;
}

.category-button:hover:not(.active) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.products-grid {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.loading-spinner-large {
  width: 60px;
  height: 60px;
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
