<template>
  <button 
    class="zeus-button" 
    :class="[variantClass, sizeClass, { 'is-loading': loading, 'is-disabled': disabled }]"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner"></span>
    <slot v-else></slot>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'ghost'
  size?: 'small' | 'medium' | 'large'
  loading?: boolean
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'medium',
  loading: false,
  disabled: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const variantClass = computed(() => `variant-${props.variant}`)
const sizeClass = computed(() => `size-${props.size}`)

function handleClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.zeus-button {
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.zeus-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.zeus-button:active::before {
  width: 300px;
  height: 300px;
}

.size-small {
  padding: 8px 16px;
  font-size: 13px;
}

.size-medium {
  padding: 12px 24px;
  font-size: 15px;
}

.size-large {
  padding: 16px 32px;
  font-size: 17px;
}

.variant-primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  color: white;
}

.variant-primary:hover:not(.is-disabled) {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.variant-secondary {
  background: var(--background-light);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.variant-secondary:hover:not(.is-disabled) {
  background: #e5e7eb;
}

.variant-success {
  background: var(--success-color);
  color: white;
}

.variant-danger {
  background: var(--danger-color);
  color: white;
}

.variant-ghost {
  background: transparent;
  color: var(--primary-color);
  border: 1px solid var(--primary-color);
}

.is-loading,
.is-disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
