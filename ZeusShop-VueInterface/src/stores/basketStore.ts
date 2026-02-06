import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { shoppingBasketApi, BasketItem } from '@/api/basketApi'
import { useShopperStore } from './shopperStore'

export const useBasketStore = defineStore('shopping-basket', () => {
  const basketContents = ref<BasketItem[]>([])
  const isLoading = ref(false)
  
  const totalItemCount = computed(() => {
    return basketContents.value.reduce((sum, item) => sum + item.selectedQuantity, 0)
  })
  
  const totalPriceAmount = computed(() => {
    const shopperStore = useShopperStore()
    const isEmployee = shopperStore.isEmployeeAccount
    
    return basketContents.value.reduce((sum, item) => {
      const price = isEmployee ? item.staffDiscountPriceYuan : item.standardPriceYuan
      return sum + (price * item.selectedQuantity)
    }, 0)
  })
  
  const hasItems = computed(() => basketContents.value.length > 0)
  
  async function refreshBasketData() {
    try {
      isLoading.value = true
      basketContents.value = await shoppingBasketApi.fetchMyBasket()
    } catch (error) {
      console.error('Failed to load basket:', error)
    } finally {
      isLoading.value = false
    }
  }
  
  async function addMerchandiseToBasket(itemId: number, quantity: number) {
    try {
      await shoppingBasketApi.addItemToBasket(itemId, quantity)
      await refreshBasketData()
      return true
    } catch (error) {
      console.error('Failed to add item:', error)
      return false
    }
  }
  
  async function modifyItemQuantity(basketItemId: number, newQuantity: number) {
    try {
      await shoppingBasketApi.modifyQuantity(basketItemId, newQuantity)
      await refreshBasketData()
      return true
    } catch (error) {
      console.error('Failed to modify quantity:', error)
      return false
    }
  }
  
  async function removeItemFromBasket(basketItemId: number) {
    try {
      await shoppingBasketApi.removeItem(basketItemId)
      await refreshBasketData()
      return true
    } catch (error) {
      console.error('Failed to remove item:', error)
      return false
    }
  }
  
  async function clearAllItems() {
    try {
      await shoppingBasketApi.clearBasket()
      basketContents.value = []
      return true
    } catch (error) {
      console.error('Failed to clear basket:', error)
      return false
    }
  }
  
  return {
    basketContents,
    isLoading,
    totalItemCount,
    totalPriceAmount,
    hasItems,
    refreshBasketData,
    addMerchandiseToBasket,
    modifyItemQuantity,
    removeItemFromBasket,
    clearAllItems
  }
})
