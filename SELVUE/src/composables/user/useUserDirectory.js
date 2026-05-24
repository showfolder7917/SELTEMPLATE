// 组合式状态入口统一承接用户列表、筛选、表单和危险动作确认。
import { computed, onMounted, proxyRefs, ref, watch } from 'vue'
// 常量层提供导航、状态筛选和主题样例数据。
import { NAV_ITEMS, THEME_PREVIEW_ITEMS, THEME_SHOWCASE_SECTIONS, USER_STATUS_OPTIONS } from '../../constants/userConsole'
// 服务层统一负责联调和本地回退逻辑。
import { createUser, deleteUser, listUsers, updateUser } from '../../services/userApi'

/**
 * 创建用户管理台共享上下文。
 *
 * @returns {object} 页面和组件共享状态
 */
export function useUserDirectory() {
  // 当前导航页用于控制侧边栏高亮和右侧主题样例显示。
  const currentPage = ref('users')
  // 用户列表承接后端或本地模式返回的记录。
  const users = ref([])
  // 当前选中用户主键驱动右侧详情卡。
  const selectedUserId = ref(null)
  // 筛选条件统一收敛，便于 watch 自动刷新。
  const filters = ref({ keyword: '', status: 'all' })
  // loading 用于首屏和筛选变更后的刷新反馈。
  const loading = ref(false)
  // saving 覆盖新增、编辑和删除动作，防止重复提交。
  const saving = ref(false)
  // 数据模式区分当前是正式后端联调还是本地回退模式。
  const dataMode = ref('remote')
  // 表单开关控制新增和编辑弹层。
  const formOpen = ref(false)
  // 表单模式用于区分新增与编辑提交逻辑。
  const formMode = ref('create')
  // 表单草稿用于双向绑定当前编辑内容。
  const formDraft = ref(defaultDraft())
  // 待删除用户主键用于二次确认危险操作。
  const pendingDeleteId = ref(null)
  // 提示消息用于给出保存结果和模式回退反馈。
  const flash = ref(null)

  // 当前选中用户负责驱动详情卡和编辑入口。
  const selectedUser = computed(() =>
    users.value.find((item) => Number(item.id) === Number(selectedUserId.value)) || users.value[0] || null
  )
  // 顶部概览指标用于展示总量、启用和停用统计。
  const stats = computed(() => ({
    total: users.value.length,
    active: users.value.filter((item) => item.status === 'ACTIVE').length,
    disabled: users.value.filter((item) => item.status === 'DISABLED').length
  }))
  // 同步标签让用户明确当前页面是否已经连到 SELSP 后端。
  const syncLabel = computed(() => dataMode.value === 'remote' ? '已连接 SELSP 正式接口' : '后端离线，当前使用本地回退数据')
  // 同步色调用于区分正式联调与本地演示状态。
  const syncTone = computed(() => dataMode.value === 'remote' ? 'success' : 'fallback')

  // 页面挂载时先拉取用户列表，保证首屏即有可见内容。
  onMounted(() => {
    loadUsers()
  })

  // 筛选条件变化后自动刷新列表，减少额外交互步骤。
  watch(filters, () => {
    loadUsers()
  }, { deep: true })

  // 列表变化后自动修正当前选中项，避免右侧详情卡落到空对象。
  watch(users, (records) => {
    if (!records.length) {
      selectedUserId.value = null
      return
    }
    if (!records.some((item) => Number(item.id) === Number(selectedUserId.value))) {
      selectedUserId.value = records[0].id
    }
  }, { immediate: true })

  /**
   * 拉取用户列表。
   */
  async function loadUsers() {
    // 刷新前打开 loading，保证页面交互有明确反馈。
    loading.value = true
    try {
      // 服务层统一返回模式和记录列表，页面只消费结构化数据。
      const result = await listUsers(filters.value)
      users.value = result.records
      dataMode.value = result.mode
    } catch (error) {
      // 理论上服务层已回退；若仍失败，则提示用户当前加载异常。
      openFlash('error', '加载失败', error.message || '用户列表暂时无法加载')
    } finally {
      // 无论成功失败都关闭 loading，恢复页面可操作状态。
      loading.value = false
    }
  }

  /**
   * 打开新增表单。
   */
  function openCreateForm() {
    // 新增模式使用空草稿，避免残留历史编辑内容。
    formMode.value = 'create'
    formDraft.value = defaultDraft()
    formOpen.value = true
  }

  /**
   * 打开编辑表单。
   *
   * @param {object|null} user 目标用户
   */
  function openEditForm(user = selectedUser.value) {
    // 编辑模式先把当前选中用户写入草稿，保持详情卡与弹层一致。
    if (!user) {
      return
    }
    formMode.value = 'edit'
    formDraft.value = {
      id: user.id,
      name: user.name,
      email: user.email,
      status: user.status
    }
    formOpen.value = true
  }

  /**
   * 关闭表单。
   */
  function closeForm() {
    // 关闭时只收口弹层，不影响列表和提示消息。
    formOpen.value = false
  }

  /**
   * 提交当前表单。
   */
  async function submitForm() {
    // 表单提交统一进入 saving，避免重复点击。
    saving.value = true
    try {
      // 根据模式分流到新增或编辑接口。
      const action = formMode.value === 'create'
        ? createUser(formDraft.value)
        : updateUser(formDraft.value.id, formDraft.value)
      const result = await action
      dataMode.value = result.mode
      await loadUsers()
      selectedUserId.value = result.record.id
      formOpen.value = false
      openFlash('success', formMode.value === 'create' ? '创建成功' : '更新成功', dataMode.value === 'remote' ? '正式接口已完成写入。' : '已写入本地回退数据。')
    } catch (error) {
      // 保存失败时给出明确错误，避免用户误判为提交成功。
      openFlash('error', '保存失败', error.message || '当前提交未能完成')
    } finally {
      // 提交结束后恢复按钮可点击状态。
      saving.value = false
    }
  }

  /**
   * 进入删除确认状态。
   *
   * @param {number|null} userId 目标主键
   */
  function askDelete(userId = selectedUser.value?.id) {
    // 删除前仅记录目标用户，不立即执行危险动作。
    pendingDeleteId.value = userId || null
  }

  /**
   * 取消删除确认。
   */
  function cancelDelete() {
    // 取消时清理待删除主键，右侧确认卡自动隐藏。
    pendingDeleteId.value = null
  }

  /**
   * 确认删除当前用户。
   */
  async function confirmDelete() {
    // 没有待删除项时直接退出，防止误删。
    if (!pendingDeleteId.value) {
      return
    }
    saving.value = true
    try {
      // 删除动作统一走服务层，保持联调和本地模式一致。
      const result = await deleteUser(pendingDeleteId.value)
      dataMode.value = result.mode
      pendingDeleteId.value = null
      await loadUsers()
      openFlash('info', '删除完成', dataMode.value === 'remote' ? '正式接口已完成删除。' : '本地回退数据已完成删除。')
    } catch (error) {
      // 删除失败时明确提示原因，并保留当前确认状态。
      openFlash('error', '删除失败', error.message || '当前用户未能删除')
    } finally {
      // 删除收尾后恢复页面其他操作能力。
      saving.value = false
    }
  }

  /**
   * 修改关键字。
   *
   * @param {string} value 输入值
   */
  function setKeyword(value) {
    // 统一通过函数改筛选对象，避免子组件直接改复杂引用。
    filters.value = {
      ...filters.value,
      keyword: value
    }
  }

  /**
   * 修改状态筛选。
   *
   * @param {string} value 状态值
   */
  function setStatus(value) {
    // 状态筛选变化后会自动触发列表刷新。
    filters.value = {
      ...filters.value,
      status: value
    }
  }

  /**
   * 重置筛选条件。
   */
  function resetFilters() {
    // 重置回“全部 + 空关键字”的默认浏览状态。
    filters.value = {
      keyword: '',
      status: 'all'
    }
  }

  /**
   * 选中指定用户。
   *
   * @param {number} userId 用户主键
   */
  function selectUser(userId) {
    // 表格点击只更新选中主键，详情卡自动联动。
    selectedUserId.value = userId
  }

  /**
   * 切换到主题样例页。
   */
  function openThemePage() {
    // 主题入口统一通过导航状态切页，避免点击卡片后页面没有变化。
    currentPage.value = 'theme'
  }

  /**
   * 切换回用户管理页。
   */
  function openUsersPage() {
    // 返回主业务页时只恢复页面标识，不重置当前用户和筛选条件。
    currentPage.value = 'users'
  }

  /**
   * 打开提示消息。
   *
   * @param {'info'|'success'|'error'} tone 提示色调
   * @param {string} title 标题
   * @param {string} description 描述
   */
  function openFlash(tone, title, description) {
    // 每次提示都重新构建对象，确保页面能正确感知状态变化。
    flash.value = {
      tone,
      title,
      description
    }
    // 提示自动消失，避免页面长期堆积旧消息。
    window.setTimeout(() => {
      flash.value = null
    }, 2800)
  }

  // 对外暴露给页面和组件的状态与动作。
  return proxyRefs({
    currentPage,
    navItems: NAV_ITEMS,
    users,
    selectedUser,
    selectedUserId,
    stats,
    syncLabel,
    syncTone,
    filters,
    loading,
    saving,
    formOpen,
    formMode,
    formDraft,
    pendingDeleteId,
    flash,
    statusOptions: USER_STATUS_OPTIONS,
    themePreviewItems: THEME_PREVIEW_ITEMS,
    themeShowcaseSections: THEME_SHOWCASE_SECTIONS,
    loadUsers,
    openCreateForm,
    openEditForm,
    closeForm,
    submitForm,
    askDelete,
    cancelDelete,
    confirmDelete,
    setKeyword,
    setStatus,
    resetFilters,
    selectUser,
    openThemePage,
    openUsersPage
  })
}

/**
 * 生成默认表单草稿。
 *
 * @returns {{ id: null, name: string, email: string, status: string }} 默认草稿
 */
function defaultDraft() {
  // 默认草稿仅保留用户表单最小字段集合。
  return {
    id: null,
    name: '',
    email: '',
    status: 'ACTIVE'
  }
}
