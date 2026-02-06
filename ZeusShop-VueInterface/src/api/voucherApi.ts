import { httpManager } from '@/utils/httpManager'

export interface DiscountVoucher {
  voucherIdentifier: number
  voucherCode: string
  voucherTitle: string
  voucherType: number
  discountAmount: number
  minimumPurchase: number
  validFromTime: string
  validUntilTime: string
}

export interface VoucherClaim {
  claimIdentifier: number
  voucherId: number
  usageStatus: number
  claimedAtTime: string
}

class VoucherDistributionApi {
  async listAvailableVouchers(): Promise<DiscountVoucher[]> {
    return httpManager.performGetRequest('/gateway/vouchers/available-list')
  }
  
  async claimVoucher(voucherId: number): Promise<string> {
    return httpManager.performPostRequest(`/gateway/vouchers/claim/${voucherId}`)
  }
  
  async fetchMyVouchers(): Promise<VoucherClaim[]> {
    return httpManager.performGetRequest('/gateway/vouchers/my-collection')
  }
}

export const voucherDistributionApi = new VoucherDistributionApi()
