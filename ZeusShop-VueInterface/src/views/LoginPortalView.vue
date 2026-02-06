<template>
  <div class="login-portal-view">
    <div class="login-container">
      <div class="logo-section">
        <h1 class="app-title">宙斯大酒店</h1>
        <p class="app-subtitle">智能便利店系统</p>
      </div>
      
      <div class="form-section">
        <div class="tab-switcher">
          <div 
            class="tab-item" 
            :class="{ active: currentMode === 'login' }"
            @click="currentMode = 'login'"
          >
            登录
          </div>
          <div 
            class="tab-item" 
            :class="{ active: currentMode === 'register' }"
            @click="currentMode = 'register'"
          >
            注册
          </div>
        </div>
        
        <form @submit.prevent="handleSubmit" class="auth-form">
          <ZeusInput
            v-model="formData.username"
            label="用户名"
            placeholder="请输入用户名"
            prefix-icon="👤"
          />
          
          <ZeusInput
            v-model="formData.password"
            label="密码"
            type="password"
            placeholder="请输入密码"
            prefix-icon="🔒"
          />
          
          <template v-if="currentMode === 'register'">
            <ZeusInput
              v-model="formData.nickname"
              label="昵称"
              placeholder="请输入昵称"
              prefix-icon="✨"
            />
            
            <ZeusInput
              v-model="formData.phoneNumber"
              label="手机号"
              placeholder="请输入手机号"
              prefix-icon="📱"
            />
          </template>
          
          <ZeusButton
            type="submit"
            variant="primary"
            size="large"
            :loading="isSubmitting"
            style="width: 100%; margin-top: 24px;"
          >
            {{ currentMode === 'login' ? '登录' : '注册' }}
          </ZeusButton>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useShopperStore } from '@/stores/shopperStore'
import ZeusInput from '@/components/ZeusInput.vue'
import ZeusButton from '@/components/ZeusButton.vue'

const router = useRouter()
const shopperStore = useShopperStore()

const currentMode = ref<'login' | 'register'>('login')
const isSubmitting = ref(false)

const formData = reactive({
  username: '',
  password: '',
  nickname: '',
  phoneNumber: ''
})

async function handleSubmit() {
  isSubmitting.value = true
  
  try {
    let success = false
    
    if (currentMode.value === 'login') {
      success = await shopperStore.performLoginAction(formData.username, formData.password)
    } else {
      success = await shopperStore.performRegistrationAction({
        loginUsername: formData.username,
        password: formData.password,
        nickname: formData.nickname,
        phoneNumber: formData.phoneNumber
      })
    }
    
    if (success) {
      router.push('/merchandise-hall')
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.login-portal-view {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 100%;
  max-width: 440px;
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.logo-section {
  padding: 48px 32px 32px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.app-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.app-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

.form-section {
  padding: 32px;
}

.tab-switcher {
  display: flex;
  background: var(--background-light);
  border-radius: var(--radius-md);
  padding: 4px;
  margin-bottom: 32px;
}

.tab-item {
  flex: 1;
  padding: 12px;
  text-align: center;
  font-weight: 500;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: all 0.3s ease;
  color: var(--text-secondary);
}

.tab-item.active {
  background: white;
  color: var(--primary-color);
  box-shadow: var(--shadow-sm);
}

.auth-form {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
