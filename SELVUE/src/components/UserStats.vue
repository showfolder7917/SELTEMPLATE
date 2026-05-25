<script setup>
// 顶部状态卡仅展示概览指标和当前同步模式。
defineProps({
  stats: { type: Object, required: true },
  syncLabel: { type: String, required: true },
  syncTone: { type: String, required: true },
  // 主题页顶部说明区收起时，状态卡同步切到紧凑模式，避免继续占满首屏高度。
  compact: { type: Boolean, default: false },
  // 收起态下让右侧状态卡直接跟左侧说明卡使用同一高度基准。
  matchCollapsedHeight: { type: Boolean, default: false }
})
</script>

<template>
  <section class="status-card glass-panel" :class="{ compact, 'match-collapsed-height': matchCollapsedHeight }">
    <div class="eyebrow">SYNC STATUS</div>
    <h3>{{ syncLabel }}</h3>
    <div class="status-grid" :class="{ compact }">
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
