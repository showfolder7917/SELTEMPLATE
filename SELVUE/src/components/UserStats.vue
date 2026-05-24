<script setup>
// 顶部状态卡仅展示概览指标和当前同步模式。
defineProps({
  stats: { type: Object, required: true },
  syncLabel: { type: String, required: true },
  syncTone: { type: String, required: true }
})
</script>

<template>
  <section class="status-card glass-panel">
    <div class="eyebrow">SYNC STATUS</div>
    <h3>{{ syncLabel }}</h3>
    <div class="status-grid">
      <div class="status-item">
        <div class="eyebrow">TOTAL</div>
        <strong>{{ stats.total }}</strong>
      </div>
      <div class="status-item">
        <div class="eyebrow">ACTIVE</div>
        <strong>{{ stats.active }}</strong>
      </div>
      <div class="status-item">
        <div class="eyebrow">DISABLED</div>
        <strong>{{ stats.disabled }}</strong>
      </div>
      <div class="status-item">
        <div class="eyebrow">MODE</div>
        <strong>{{ syncTone === 'success' ? 'REMOTE' : 'LOCAL' }}</strong>
      </div>
    </div>
  </section>
</template>
