<template>
  <div class="zeus-input-wrapper" :class="{ 'has-error': errorMessage, 'is-focused': isFocused }">
    <label v-if="label" class="input-label">{{ label }}</label>
    <div class="input-container">
      <span v-if="prefixIcon" class="prefix-icon">{{ prefixIcon }}</span>
      <input
        :type="inputType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        class="zeus-input"
        @input="handleInput"
        @focus="isFocused = true"
        @blur="isFocused = false"
      />
      <span v-if="suffixIcon" class="suffix-icon" @click="handleSuffixClick">{{ suffixIcon }}</span>
    </div>
    <span v-if="errorMessage" class="error-text">{{ errorMessage }}</span>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  modelValue: string
  label?: string
  placeholder?: string
  type?: string
  prefixIcon?: string
  suffixIcon?: string
  errorMessage?: string
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text'
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'suffix-click': []
}>()

const isFocused = ref(false)

const inputType = computed(() => props.type)

function handleInput(event: Event) {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}

function handleSuffixClick() {
  emit('suffix-click')
}
</script>

<style scoped>
.zeus-input-wrapper {
  margin-bottom: 16px;
}

.input-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.zeus-input {
  width: 100%;
  padding: 12px 16px;
  font-size: 15px;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  outline: none;
  transition: all 0.3s ease;
  background: white;
}

.zeus-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.zeus-input:disabled {
  background: var(--background-light);
  cursor: not-allowed;
}

.prefix-icon,
.suffix-icon {
  position: absolute;
  font-size: 18px;
  color: var(--text-secondary);
}

.prefix-icon {
  left: 12px;
}

.suffix-icon {
  right: 12px;
  cursor: pointer;
}

.zeus-input:has(~ .prefix-icon) {
  padding-left: 40px;
}

.zeus-input:has(~ .suffix-icon) {
  padding-right: 40px;
}

.has-error .zeus-input {
  border-color: var(--danger-color);
}

.error-text {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: var(--danger-color);
}

.is-focused .input-label {
  color: var(--primary-color);
}
</style>
