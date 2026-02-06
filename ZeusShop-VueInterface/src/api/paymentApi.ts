import { httpManager } from '@/utils/httpManager'

export interface PaymentFlowData {
  channelType: string
  qrCodeImage: string
  orderReference: string
  expirySeconds?: number
}

class PaymentChannelApi {
  async initiateWeChatPayment(orderNumber: string, amount: number, description: string): Promise<PaymentFlowData> {
    return httpManager.performPostRequest('/gateway/payment-channels/wechat/initiate', null, {
      params: { orderNumber, amount, description }
    })
  }
  
  async initiateAlipayPayment(orderNumber: string, amount: number, description: string): Promise<PaymentFlowData> {
    return httpManager.performPostRequest('/gateway/payment-channels/alipay/initiate', null, {
      params: { orderNumber, amount, description }
    })
  }
  
  async generateQrCode(content: string, size?: number): Promise<string> {
    return httpManager.performGetRequest('/gateway/payment-channels/generate-qr', {
      content,
      size
    })
  }
}

export const paymentChannelApi = new PaymentChannelApi()
