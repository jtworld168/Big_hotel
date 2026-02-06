<template>
  <div class="qr-scanner-view">
    <header class="scanner-header">
      <button class="back-button" @click="goBack">← 返回</button>
      <h1 class="page-title">扫码购物</h1>
      <div style="width: 60px;"></div>
    </header>
    
    <div class="scanner-content">
      <div class="camera-container">
        <video ref="videoElement" class="camera-feed" autoplay playsinline></video>
        <canvas ref="canvasElement" style="display: none;"></canvas>
        <div class="scan-overlay">
          <div class="scan-box"></div>
          <p class="scan-tip">将二维码对准扫描框</p>
        </div>
      </div>
      
      <div class="scanner-controls">
        <ZeusButton 
          v-if="!isScanning" 
          @click="startCamera"
          size="large"
          style="width: 100%;"
        >
          启动摄像头
        </ZeusButton>
        <ZeusButton 
          v-else 
          @click="stopCamera"
          variant="danger"
          size="large"
          style="width: 100%;"
        >
          停止扫描
        </ZeusButton>
      </div>
      
      <div v-if="scannedResult" class="scan-result">
        <h3 class="result-title">扫描结果</h3>
        <p class="result-text">{{ scannedResult }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import ZeusButton from '@/components/ZeusButton.vue'

const router = useRouter()

const videoElement = ref<HTMLVideoElement | null>(null)
const canvasElement = ref<HTMLCanvasElement | null>(null)
const isScanning = ref(false)
const scannedResult = ref('')
const mediaStream = ref<MediaStream | null>(null)

async function startCamera() {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ 
      video: { facingMode: 'environment' } 
    })
    
    if (videoElement.value) {
      videoElement.value.srcObject = stream
      mediaStream.value = stream
      isScanning.value = true
      scanQrCode()
    }
  } catch (error) {
    console.error('Camera access failed:', error)
    alert('无法访问摄像头，请检查权限设置')
  }
}

function scanQrCode() {
  if (!isScanning.value) return
  
  if (videoElement.value && canvasElement.value) {
    const canvas = canvasElement.value
    const video = videoElement.value
    const context = canvas.getContext('2d')
    
    if (context && video.readyState === video.HAVE_ENOUGH_DATA) {
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      context.drawImage(video, 0, 0, canvas.width, canvas.height)
      
      const imageData = context.getImageData(0, 0, canvas.width, canvas.height)
      
      scannedResult.value = '模拟扫描结果: 商品编码12345'
    }
  }
  
  if (isScanning.value) {
    requestAnimationFrame(scanQrCode)
  }
}

function stopCamera() {
  isScanning.value = false
  
  if (mediaStream.value) {
    mediaStream.value.getTracks().forEach(track => track.stop())
    mediaStream.value = null
  }
  
  if (videoElement.value) {
    videoElement.value.srcObject = null
  }
}

function goBack() {
  stopCamera()
  router.back()
}

onBeforeUnmount(() => {
  stopCamera()
})
</script>

<style scoped>
.qr-scanner-view {
  min-height: 100vh;
  background: var(--background-light);
}

.scanner-header {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: var(--shadow-sm);
}

.back-button {
  padding: 8px 16px;
  border: none;
  background: var(--background-light);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
}

.scanner-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.camera-container {
  position: relative;
  width: 100%;
  aspect-ratio: 4 / 3;
  background: #000;
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 20px;
}

.camera-feed {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.scan-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.scan-box {
  width: 250px;
  height: 250px;
  border: 3px solid rgba(102, 126, 234, 0.8);
  border-radius: var(--radius-md);
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.8;
  }
}

.scan-tip {
  margin-top: 20px;
  color: white;
  font-size: 14px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.scanner-controls {
  margin-bottom: 20px;
}

.scan-result {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-sm);
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.result-text {
  font-size: 14px;
  color: var(--text-secondary);
  word-break: break-all;
}
</style>
