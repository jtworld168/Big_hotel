import { httpManager } from '@/utils/httpManager'

export interface MerchandiseDisplay {
  itemIdentifier: number
  itemSku: string
  itemDisplayName: string
  itemDescription: string
  categoryLabel: string
  standardPriceYuan: number
  staffDiscountPriceYuan: number
  finalPriceYuan: number
  mainImageUrl: string
  stockQuantity: number
  employeeEligible: boolean
}

export interface PaginatedMerchandise {
  records: MerchandiseDisplay[]
  total: number
  current: number
  size: number
}

class MerchandiseCatalogApi {
  async browseCatalog(pageNum: number, pageSize: number, category?: string): Promise<PaginatedMerchandise> {
    return httpManager.performGetRequest('/gateway/merchandise/browse-catalog', {
      pageNum,
      pageSize,
      categoryLabel: category
    })
  }
  
  async viewItemDetails(itemId: number): Promise<MerchandiseDisplay> {
    return httpManager.performGetRequest(`/gateway/merchandise/details/${itemId}`)
  }
}

export const merchandiseCatalogApi = new MerchandiseCatalogApi()
