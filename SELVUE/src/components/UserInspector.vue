<script setup>
// 右侧详情卡负责查看当前用户并触发编辑或删除确认。
defineProps({
  user: { type: Object, default: null },
  pendingDeleteId: { type: Number, default: null }
})

// 所有高风险操作都回交给页面状态层统一执行。
const emit = defineEmits(['edit', 'delete-request', 'delete-cancel', 'delete-confirm'])

/**
 * 生成头像缩写。
 *
 * @param {string} name 用户名
 * @returns {string} 缩写
 */
function initials(name) {
  // 通过姓名首字母生成轻量头像文案，避免依赖外部头像图片。
  return String(name || 'U')
    .split(' ')
    .map((part) => part[0] || '')
    .join('')
    .slice(0, 2)
    .toUpperCase()
}
</script>

<template>
  <section class="inspector-card glass-panel">
    <div v-if="!user" class="empty-state">
      <div>
        <div class="eyebrow">DETAIL</div>
        <h3>请选择一个用户</h3>
      </div>
    </div>

    <template v-else>
      <div class="brand">
        <div class="user-avatar">{{ initials(user.name) }}</div>
        <div class="inspector-meta">
          <div class="eyebrow">USER PROFILE</div>
          <h3>{{ user.name }}</h3>
          <p class="fine-print">{{ user.email }}</p>
        </div>
      </div>

      <div class="detail-list">
        <div class="detail-item">
          <span>状态</span>
          <strong>{{ user.statusLabel }}</strong>
        </div>
        <div class="detail-item">
          <span>创建时间</span>
          <strong>{{ user.createdAt || '--' }}</strong>
        </div>
        <div class="detail-item">
          <span>更新时间</span>
          <strong>{{ user.updatedAt || '--' }}</strong>
        </div>
      </div>

      <div class="inspector-actions">
        <button class="glass-button primary" @click="emit('edit', user)">编辑用户</button>
        <button class="glass-button danger" @click="emit('delete-request', user.id)">删除用户</button>
      </div>

      <div v-if="Number(pendingDeleteId) === Number(user.id)" class="confirmation-card glass-panel">
        <div class="eyebrow">CONFIRM DELETE</div>
        <h4>确认删除 {{ user.name }} ?</h4>
        <p class="fine-print">若后端在线，将同步删除 SELSP 正式数据。</p>
        <div class="confirmation-actions">
          <button class="glass-button" @click="emit('delete-cancel')">取消</button>
          <button class="glass-button danger" @click="emit('delete-confirm')">确认删除</button>
        </div>
      </div>
    </template>
  </section>
</template>
