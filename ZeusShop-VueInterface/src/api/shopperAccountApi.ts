import { httpManager } from '@/utils/httpManager'

export interface ShopperProfile {
  profileIdentifier: number
  loginUsername: string
  displayNickname: string
  portraitImageLink: string
  contactPhoneNumber: string
  employeeStatusFlag: number
}

export interface LoginCredentials {
  loginUsername: string
  password: string
}

export interface RegistrationData {
  loginUsername: string
  password: string
  nickname: string
  phoneNumber: string
}

class ShopperAccountApi {
  async registerNewAccount(data: RegistrationData): Promise<string> {
    return httpManager.performPostRequest('/gateway/shopper-accounts/register-new', data)
  }
  
  async performLogin(credentials: LoginCredentials): Promise<string> {
    return httpManager.performPostRequest('/gateway/shopper-accounts/authenticate', credentials)
  }
  
  async fetchMyProfile(): Promise<ShopperProfile> {
    return httpManager.performGetRequest('/gateway/shopper-accounts/my-profile')
  }
  
  async updateMyProfile(data: Partial<ShopperProfile>): Promise<string> {
    return httpManager.performPutRequest('/gateway/shopper-accounts/update-profile', data)
  }
  
  async performSignOut(): Promise<string> {
    return httpManager.performPostRequest('/gateway/shopper-accounts/sign-out')
  }
}

export const shopperAccountApi = new ShopperAccountApi()
