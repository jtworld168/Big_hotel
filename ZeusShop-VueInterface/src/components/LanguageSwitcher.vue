<template>
  <div class="language-switcher">
    <button 
      @click="toggleLanguage" 
      class="lang-toggle-btn"
      :title="currentLocale === 'zh-CN' ? 'Switch to English' : '切换到中文'"
    >
      <span class="lang-icon">🌐</span>
      <span class="lang-text">{{ currentLocale === 'zh-CN' ? '中文' : 'EN' }}</span>
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

const currentLocale = computed(() => locale.value)

const toggleLanguage = () => {
  const newLocale = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = newLocale
  localStorage.setItem('locale', newLocale)
}
</script>

<style scoped>
.language-switcher {
  display: inline-flex;
  align-items: center;
}

.lang-toggle-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.lang-toggle-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.lang-toggle-btn:active {
  transform: translateY(0);
}

.lang-icon {
  font-size: 16px;
}

.lang-text {
  font-weight: 600;
  letter-spacing: 0.5px;
}
</style>
