<script setup>
// 工具条只承接筛选输入和主操作按钮。
defineProps({
  filters: { type: Object, required: true },
  statusOptions: { type: Array, required: true }
})

// 所有输入和按钮动作都回交给上层状态层处理。
const emit = defineEmits(['keyword-change', 'status-change', 'reset', 'create'])
</script>

<template>
  <section class="toolbar-card glass-panel">
    <div class="glass-input">
      <input
        :value="filters.keyword"
        type="text"
        placeholder="搜索姓名或邮箱..."
        @input="emit('keyword-change', $event.target.value)"
      >
    </div>

    <div class="glass-input">
      <select :value="filters.status" @change="emit('status-change', $event.target.value)">
        <option v-for="item in statusOptions" :key="item.value" :value="item.value">
          {{ item.label }}
        </option>
      </select>
    </div>

    <button class="glass-button" @click="emit('reset')">重置筛选</button>
    <button class="glass-button primary" @click="emit('create')">新增用户</button>
  </section>
</template>
