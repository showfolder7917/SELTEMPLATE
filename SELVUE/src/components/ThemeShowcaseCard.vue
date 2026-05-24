<script setup>
// 主题样例卡负责展示 SELTHEME 的可复用组件方向。
defineProps({
  items: { type: Array, required: true }
})

// 卡片需要把“进入主题页”的动作显式抛给页面层处理。
const emit = defineEmits(['open-theme'])
</script>

<template>
  <section class="theme-card glass-panel">
    <div class="brand" style="justify-content: space-between;">
      <div>
        <div class="eyebrow">SELTHEME</div>
        <h3>液态玻璃组件样例</h3>
      </div>
      <div class="fine-print">主题源已单独沉淀到 `SELTHEME` 目录。</div>
    </div>

    <p class="theme-card-copy">
      当前这里只保留主题入口摘要。点击下方按钮进入正式样例页，分别查看按钮、表单和浮层组件。
    </p>

    <div class="theme-preview-grid">
      <div
        v-for="item in items"
        :key="item.key"
        class="theme-preview-chip glass-panel"
        :class="item.tone"
      >
        {{ item.label }}
      </div>
    </div>

    <button class="glass-button primary theme-card-action" type="button" @click="emit('open-theme')">
      打开主题样例页
    </button>
  </section>
</template>
