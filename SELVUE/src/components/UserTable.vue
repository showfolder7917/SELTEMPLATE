<script setup>
// 用户表格负责渲染列表和点击选中。
defineProps({
  users: { type: Array, required: true },
  selectedUserId: { type: Number, default: null },
  loading: { type: Boolean, required: true }
})

// 表格只上抛选中动作，不直接改业务状态。
const emit = defineEmits(['select'])
</script>

<template>
  <section class="table-card glass-panel">
    <div>
      <div class="eyebrow">USER DIRECTORY</div>
      <h3>用户列表</h3>
    </div>

    <div v-if="loading" class="empty-state">
      <div>
        <div class="eyebrow">LOADING</div>
        <h3>正在同步用户列表</h3>
      </div>
    </div>

    <div v-else-if="!users.length" class="empty-state">
      <div>
        <div class="eyebrow">EMPTY</div>
        <h3>当前筛选条件下没有用户</h3>
      </div>
    </div>

    <div v-else class="table-shell">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>邮箱</th>
            <th>状态</th>
            <th>创建日</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in users"
            :key="item.id"
            class="table-row"
            :class="{ active: Number(item.id) === Number(selectedUserId) }"
            @click="emit('select', item.id)"
          >
            <td>#{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.email }}</td>
            <td><span class="status-pill" :class="item.statusTone">{{ item.statusLabel }}</span></td>
            <td>{{ item.joinedLabel }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
