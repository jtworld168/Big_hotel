import { httpManager } from '@/utils/httpManager'

export interface BasketItem {
  basketItemId: number
  itemIdentifier: number
  itemDisplayName: string
  mainImageUrl: string
  standardPriceYuan: number
  staffDiscountPriceYuan: number
  selectedQuantity: number
  stockQuantity: number
}

class ShoppingBasketApi {
  async addItemToBasket(itemId: number, quantity: number): Promise<string> {
    return httpManager.performPostRequest('/gateway/shopping-basket/add-item', null, {
      params: { itemId, quantity }
    })
  }
  
  async fetchMyBasket(): Promise<BasketItem[]> {
    return httpManager.performGetRequest('/gateway/shopping-basket/my-basket')
  }
  
  async modifyQuantity(basketItemId: number, quantity: number): Promise<string> {
    return httpManager.performPutRequest(`/gateway/shopping-basket/modify-quantity/${basketItemId}`, null, {
      params: { quantity }
    })
  }
  
  async removeItem(basketItemId: number): Promise<string> {
    return httpManager.performDeleteRequest(`/gateway/shopping-basket/remove-item/${basketItemId}`)
  }
  
  async clearBasket(): Promise<string> {
    return httpManager.performDeleteRequest('/gateway/shopping-basket/clear-all')
  }
}

export const shoppingBasketApi = new ShoppingBasketApi()
