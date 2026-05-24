<script setup>
// props 承接当前表单模式、草稿和保存状态。
const props = defineProps({
  open: { type: Boolean, required: true },
  mode: { type: String, required: true },
  draft: { type: Object, required: true },
  saving: { type: Boolean, required: true }
})

// 所有字段更新和提交动作都通过事件交回页面状态层。
const emit = defineEmits(['close', 'submit', 'update:draft'])

/**
 * 更新草稿字段。
 *
 * @param {string} field 字段名
 * @param {string} value 字段值
 */
function updateField(field, value) {
  // 每次更新都返回完整草稿副本，避免子组件直接修改 prop。
  emit('update:draft', {
    id: props.draft.id,
    name: props.draft.name,
    email: props.draft.email,
    status: props.draft.status,
    [field]: value
  })
}
</script>

<template>
  <div v-if="open" class="modal-backdrop">
    <section class="form-shell glass-panel">
      <div class="brand" style="justify-content: space-between;">
        <div>
          <div class="eyebrow">{{ mode === 'create' ? 'CREATE USER' : 'EDIT USER' }}</div>
          <h3>{{ mode === 'create' ? '新增用户' : '编辑用户' }}</h3>
        </div>
        <button class="glass-button" @click="emit('close')">关闭</button>
      </div>

      <div class="form-grid">
        <div class="field-group">
          <label>姓名</label>
          <div class="glass-input">
            <input :value="draft.name" type="text" placeholder="请输入姓名" @input="updateField('name', $event.target.value)">
          </div>
        </div>

        <div class="field-group">
          <label>邮箱</label>
          <div class="glass-input">
            <input :value="draft.email" type="email" placeholder="请输入邮箱" @input="updateField('email', $event.target.value)">
          </div>
        </div>

        <div class="field-group full">
          <label>状态</label>
          <div class="glass-input">
            <select :value="draft.status" @change="updateField('status', $event.target.value)">
              <option value="ACTIVE">启用中</option>
              <option value="DISABLED">已停用</option>
            </select>
          </div>
        </div>
      </div>

      <div class="modal-actions" style="margin-top: 18px;">
        <button class="glass-button" @click="emit('close')">取消</button>
        <button class="glass-button primary" :disabled="saving" @click="emit('submit')">
          {{ saving ? '保存中...' : '提交保存' }}
        </button>
      </div>
    </section>
  </div>
</template>
