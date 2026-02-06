import { httpManager } from '@/utils/httpManager'

export interface OrderItemDto {
  itemId: number
  itemName: string
  quantity: number
  unitPrice: number
  subtotal: number
}

export interface CreateOrderRequest {
  orderItems: OrderItemDto[]
  voucherId?: number
  paymentMethod: number
}

export interface PurchaseOrder {
  orderIdentifier: number
  orderNumber: string
  totalAmountYuan: number
  discountAmountYuan: number
  finalPaymentYuan: number
  paymentMethod: number
  orderStatus: number
  orderItemsJson: string
  recordCreatedTime: string
}

class PurchaseOrderApi {
  async createOrder(request: CreateOrderRequest): Promise<PurchaseOrder> {
    return httpManager.performPostRequest('/gateway/purchase-orders/create-new', request)
  }
  
  async fetchMyOrders(): Promise<PurchaseOrder[]> {
    return httpManager.performGetRequest('/gateway/purchase-orders/my-orders')
  }
  
  async viewOrderDetails(orderId: number): Promise<PurchaseOrder> {
    return httpManager.performGetRequest(`/gateway/purchase-orders/details/${orderId}`)
  }
  
  async updatePaymentStatus(orderId: number, transactionId: string): Promise<string> {
    return httpManager.performPostRequest(`/gateway/purchase-orders/update-payment/${orderId}`, null, {
      params: { transactionId }
    })
  }
}

export const purchaseOrderApi = new PurchaseOrderApi()
