<template>
  <div class="avatar-upload">
    <label class="avatar-label">{{ label }}</label>

    <div class="avatar-wrapper">
      <!-- 预览区 -->
      <div class="avatar-preview" @click="triggerSelect">
        <img v-if="modelValue" :src="modelValue" alt="头像预览" />
        <div v-else class="avatar-placeholder">
          <span>点击上传</span>
        </div>

        <!-- 移除按钮 -->
        <button
          v-if="modelValue"
          type="button"
          class="btn-remove"
          @click.stop="removeAvatar"
        >
          ×
        </button>
      </div>

      <!-- 隐藏的文件选择框 -->
      <input
        ref="inputRef"
        type="file"
        accept="image/*"
        class="hidden-input"
        @change="handleChange"
      />

      <p class="tip">支持 jpg / png / gif，大小不超过 {{ maxSizeMB }}MB</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  label: {
    type: String,
    default: '头像',
  },
  maxSizeMB: {
    type: Number,
    default: 2,
  },
})

const emit = defineEmits(['update:modelValue', 'error'])

const inputRef = ref(null)

function triggerSelect() {
  inputRef.value?.click()
}

function handleChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  // 校验类型
  if (!file.type.startsWith('image/')) {
    emit('error', '请选择图片文件')
    event.target.value = ''
    return
  }

  // 校验大小
  const maxSize = props.maxSizeMB * 1024 * 1024
  if (file.size > maxSize) {
    emit('error', `图片不能超过 ${props.maxSizeMB}MB`)
    event.target.value = ''
    return
  }

  // 转 Base64
  const reader = new FileReader()
  reader.onload = () => {
    emit('update:modelValue', reader.result)
    emit('error', '')
  }
  reader.onerror = () => {
    emit('error', '图片读取失败')
  }
  reader.readAsDataURL(file)
}

function removeAvatar() {
  emit('update:modelValue', '')
  if (inputRef.value) {
    inputRef.value.value = ''
  }
}
</script>

<style scoped>
.avatar-upload {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.avatar-label {
  font-size: 14px;
  color: #555;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  position: relative;
  width: 96px;
  height: 96px;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: border-color 0.2s;
}

.avatar-preview:hover {
  border-color: #667eea;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  font-size: 13px;
  color: #999;
  text-align: center;
  padding: 0 8px;
}

.hidden-input {
  display: none;
}

.btn-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-remove:hover {
  background: rgba(0, 0, 0, 0.8);
}

.tip {
  font-size: 12px;
  color: #999;
  margin: 0;
}
</style>