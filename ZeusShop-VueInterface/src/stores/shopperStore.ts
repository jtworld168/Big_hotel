import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { shopperAccountApi, ShopperProfile } from '@/api/shopperAccountApi'

export const useShopperStore = defineStore('shopper-session', () => {
  const currentProfile = ref<ShopperProfile | null>(null)
  const authenticationToken = ref<string>('')
  const sessionActive = ref(false)
  
  const isEmployeeAccount = computed(() => {
    return currentProfile.value?.employeeStatusFlag === 1
  })
  
  const displayName = computed(() => {
    return currentProfile.value?.displayNickname || '游客'
  })
  
  const avatarUrl = computed(() => {
    return currentProfile.value?.portraitImageLink || '/default-avatar.png'
  })
  
  async function performLoginAction(username: string, password: string) {
    try {
      const token = await shopperAccountApi.performLogin({
        loginUsername: username,
        password
      })
      
      authenticationToken.value = token
      localStorage.setItem('zeus_auth_token', token)
      
      await loadProfileData()
      sessionActive.value = true
      
      return true
    } catch (error) {
      console.error('Login failed:', error)
      return false
    }
  }
  
  async function performRegistrationAction(data: any) {
    try {
      await shopperAccountApi.registerNewAccount(data)
      return await performLoginAction(data.loginUsername, data.password)
    } catch (error) {
      console.error('Registration failed:', error)
      return false
    }
  }
  
  async function loadProfileData() {
    try {
      const profile = await shopperAccountApi.fetchMyProfile()
      currentProfile.value = profile
    } catch (error) {
      console.error('Failed to load profile:', error)
    }
  }
  
  async function updateProfileData(data: Partial<ShopperProfile>) {
    try {
      await shopperAccountApi.updateMyProfile(data)
      await loadProfileData()
      return true
    } catch (error) {
      console.error('Failed to update profile:', error)
      return false
    }
  }
  
  async function performLogoutAction() {
    try {
      await shopperAccountApi.performSignOut()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      currentProfile.value = null
      authenticationToken.value = ''
      sessionActive.value = false
      localStorage.removeItem('zeus_auth_token')
    }
  }
  
  function autoRestoreSession() {
    const savedToken = localStorage.getItem('zeus_auth_token')
    if (savedToken) {
      authenticationToken.value = savedToken
      sessionActive.value = true
      loadProfileData()
    }
  }
  
  return {
    currentProfile,
    sessionActive,
    isEmployeeAccount,
    displayName,
    avatarUrl,
    performLoginAction,
    performRegistrationAction,
    updateProfileData,
    performLogoutAction,
    autoRestoreSession,
    loadProfileData
  }
})
