<script setup>
// 主题工作区负责把按钮、表单和浮层按能力分层陈列，并提供最小可操作 demo。
import { computed, onBeforeUnmount, ref } from 'vue'

// 页面层把已经整理好的主题分组传进来，工作区只负责展示和交互。
const props = defineProps({
  sections: { type: Array, required: true }
})

// 当前选中分组默认落在第一组，保证主题页首屏就有完整内容。
const activeSectionKey = ref(props.sections[0]?.key || '')
// 最近一次互动名称用于在右上角反馈“刚刚点了什么”。
const lastActionLabel = ref('等待互动')
// 主按钮点击次数用于证明按钮样例不是静态摆件。
const primaryClicks = ref(0)
// 圆形按钮点击次数用于反馈快捷入口是否可操作。
const iconClicks = ref(0)
// 开关状态用于演示布尔型控件切换。
const switchEnabled = ref(true)
// Tabs 当前命中的页签索引用于演示分段导航切换。
const activeTabIndex = ref(0)
// 文本输入值默认留空，让示例词以 placeholder 形式展示。
const textFieldValue = ref('')
// 搜索输入值默认留空，让搜索示例词在聚焦后自然消失。
const searchFieldValue = ref('')
// 单选组当前值用于演示互斥选择场景。
const selectedRadio = ref('Workspace')
// 多选组当前值用于演示批量勾选类场景。
const selectedCheckboxes = ref(['Roles', 'Members'])
// 胶囊筛选器当前值用于演示状态轮转。
const filterIndex = ref(0)
// 建议面板当前高亮建议用于演示点击反馈。
const panelSuggestion = ref('Focus field suggestion')
// 表格选中行用于演示结构化数据浏览和行高亮。
const selectedTableRowId = ref('mem-102')
// 树结构当前展开分支用于演示层级折叠。
const expandedTreeKeys = ref(['workspace', 'workspace.members'])
// 树结构当前选中节点用于演示目录焦点切换。
const selectedTreeNode = ref('Workspace / Members / Product')
// 提示气泡是否展开用于演示弱提示开合。
const bubbleExpanded = ref(false)
// 指标卡数值用于演示概览类组件刷新。
const metricValue = ref(92)
// 资料卡的加入月份用于演示卡片状态变动。
const profileJoined = ref('March 2023')
// 模态框开关用于演示阻断式浮层。
const modalOpen = ref(false)
// Toast 开关用于演示轻提示。
const toastVisible = ref(false)
// Toast 文案用于反馈当前动作结果。
const toastMessage = ref('Theme interaction ready')
// 延时器句柄用于避免重复触发 toast 时出现残留计时器。
const toastTimer = ref(null)

// Tabs 的固定项模拟工作台里常见的二级内容切换。
const tabs = ['Overview', 'Members', 'Access']
// 单选组选项用于演示“只能选一个”的状态切换。
const radioOptions = ['Workspace', 'Department', 'Guest']
// 多选组选项用于演示权限或模块批量勾选。
const checkboxOptions = ['Roles', 'Members', 'Audit']
// 表格示例数据用于证明样例页已经能承接真正的数据浏览交互。
const tableRows = [
  { id: 'mem-101', name: 'Ava Chen', role: 'Admin', status: 'ACTIVE' },
  { id: 'mem-102', name: 'Noah Lin', role: 'Editor', status: 'ACTIVE' },
  { id: 'mem-103', name: 'Mia Zhou', role: 'Viewer', status: 'DISABLED' }
]
// 树结构示例数据用于模拟目录、权限树和资源树的层级操作。
const treeNodes = [
  {
    key: 'workspace',
    label: 'Workspace',
    children: [
      { key: 'workspace.overview', label: 'Overview' },
      {
        key: 'workspace.members',
        label: 'Members',
        children: [
          { key: 'workspace.members.product', label: 'Product' },
          { key: 'workspace.members.ops', label: 'Operations' }
        ]
      }
    ]
  },
  {
    key: 'resources',
    label: 'Resources',
    children: [
      { key: 'resources.brand', label: 'Brand Assets' },
      { key: 'resources.docs', label: 'Design Docs' }
    ]
  }
]

// 右侧展示区统一读取当前分组，左侧目录与内容共用同一状态。
const activeSection = computed(() =>
  props.sections.find((item) => item.key === activeSectionKey.value) || props.sections[0] || null
)

// 胶囊筛选器从固定选项里轮转，便于直接看见状态变化。
const filterLabel = computed(() => ['Select dropdown', 'Active only', 'Disabled only'][filterIndex.value])
// 当前 tab 标签用于在状态面板里说明当前查看的是哪一层内容。
const activeTabLabel = computed(() => tabs[activeTabIndex.value])
// 多选摘要用于在状态面板里直观看到勾选结果。
const checkboxSummary = computed(() => selectedCheckboxes.value.join(' / ') || '空')
// 当前选中行详情用于说明表格不只是静态边框。
const selectedTableRow = computed(() =>
  tableRows.find((row) => row.id === selectedTableRowId.value) || tableRows[0]
)
// 互动概览把页面上的关键状态压缩成一行摘要，便于确认样例真的动了。
const interactionSummary = computed(() =>
  `按钮 ${primaryClicks.value} 次 / 页签 ${activeTabLabel.value} / 单选 ${selectedRadio.value} / 多选 ${checkboxSummary.value} / 表格 ${selectedTableRow.value?.name || '空'} / 树 ${selectedTreeNode.value}`
)

// 点击目录时切换当前分组键，右侧展示区自动跟随刷新。
function selectSection(sectionKey) {
  activeSectionKey.value = sectionKey
  lastActionLabel.value = `切换到 ${sectionKey} 分组`
}

// 通用动作记录器负责同步顶部反馈和 toast 提示。
function recordAction(label, { withToast = false } = {}) {
  lastActionLabel.value = label
  if (withToast) {
    openToast(label)
  }
}

// 主按钮点击后累加次数，并回写到反馈区。
function handlePrimaryAction(sampleLabel) {
  primaryClicks.value += 1
  recordAction(`${sampleLabel} 已点击 ${primaryClicks.value} 次`, { withToast: true })
}

// 圆形快捷按钮点击后累加计数，模拟悬浮入口动作。
function handleIconAction() {
  iconClicks.value += 1
  recordAction(`快捷入口已触发 ${iconClicks.value} 次`, { withToast: true })
}

// 开关点击后翻转状态，验证开关控件确实可操作。
function toggleSwitch() {
  switchEnabled.value = !switchEnabled.value
  recordAction(`开关已${switchEnabled.value ? '开启' : '关闭'}`, { withToast: true })
}

// Tabs 点击后切换当前工作区页签，验证导航控件确实能切换。
function selectTab(index) {
  activeTabIndex.value = index
  recordAction(`已切换到 ${tabs[index]} 页签`, { withToast: true })
}

// 文本输入框输入时同步记录当前值，证明 placeholder 已让位给真实输入内容。
function handleTextInput(event) {
  textFieldValue.value = event.target.value
  recordAction(`输入框已更新为：${textFieldValue.value || '空值'}`)
}

// 搜索框输入时同步记录关键字，空值时回退为“空值”提示。
function handleSearchInput(event) {
  searchFieldValue.value = event.target.value
  recordAction(`搜索关键字：${searchFieldValue.value || '空值'}`)
}

// 搜索按钮显式提交当前值，给出一次动作反馈。
function submitSearch() {
  recordAction(`已提交搜索：${searchFieldValue.value || '空值'}`, { withToast: true })
}

// 单选点击后改写当前选项，模拟互斥型选择控件。
function selectRadio(option) {
  selectedRadio.value = option
  recordAction(`单选已切换为：${option}`, { withToast: true })
}

// 多选点击后在数组里增删项，模拟批量配置和权限勾选。
function toggleCheckboxOption(option) {
  if (selectedCheckboxes.value.includes(option)) {
    selectedCheckboxes.value = selectedCheckboxes.value.filter((item) => item !== option)
    recordAction(`多选已取消：${option}`)
    return
  }
  selectedCheckboxes.value = [...selectedCheckboxes.value, option]
  recordAction(`多选已加入：${option}`, { withToast: true })
}

// 判断某个多选项是否命中，用于驱动卡片上的勾选高亮。
function hasCheckboxOption(option) {
  return selectedCheckboxes.value.includes(option)
}

// 建议面板点击建议项后回写当前选中值，并把值填回文本输入框。
function chooseSuggestion(suggestion) {
  panelSuggestion.value = suggestion
  textFieldValue.value = suggestion
  recordAction(`已采用建议：${suggestion}`, { withToast: true })
}

// 胶囊筛选器每点一次轮转状态，模拟轻量筛选操作。
function cycleFilter() {
  filterIndex.value = (filterIndex.value + 1) % 3
  recordAction(`筛选状态切换为：${filterLabel.value}`, { withToast: true })
}

// 点击表格行后切换选中行，模拟列表查看和详情联动。
function selectTableRow(rowId) {
  selectedTableRowId.value = rowId
  const row = tableRows.find((item) => item.id === rowId)
  recordAction(`表格已选中：${row?.name || rowId}`)
}

// 判断树分支是否展开，用于控制子节点渲染。
function isTreeExpanded(nodeKey) {
  return expandedTreeKeys.value.includes(nodeKey)
}

// 点击树分支箭头后展开或收起子节点，模拟层级浏览。
function toggleTreeBranch(nodeKey) {
  if (expandedTreeKeys.value.includes(nodeKey)) {
    expandedTreeKeys.value = expandedTreeKeys.value.filter((key) => key !== nodeKey)
    recordAction(`树分支已收起：${nodeKey}`)
    return
  }
  expandedTreeKeys.value = [...expandedTreeKeys.value, nodeKey]
  recordAction(`树分支已展开：${nodeKey}`)
}

// 点击树节点后记录当前焦点，模拟目录选中和详情联动。
function selectTreeNode(label) {
  selectedTreeNode.value = label
  recordAction(`树节点已定位：${label}`, { withToast: true })
}

// 提示气泡点击后展开或收起，验证弱提示不是静态文本。
function toggleBubble() {
  bubbleExpanded.value = !bubbleExpanded.value
  recordAction(`提示气泡已${bubbleExpanded.value ? '展开' : '收起'}`)
}

// 指标卡点击后刷新一个新值，模拟概览数据变动。
function refreshMetric() {
  metricValue.value = metricValue.value >= 97 ? 84 : metricValue.value + 3
  recordAction(`指标刷新为 ${metricValue.value}%`, { withToast: true })
}

// 资料卡点击后轮转加入日期，模拟成员资料更新。
function toggleProfile() {
  profileJoined.value = profileJoined.value === 'March 2023' ? 'May 2026' : 'March 2023'
  recordAction(`资料卡已切换到 ${profileJoined.value}`)
}

// 模态框按钮点击后打开正式浮层。
function openModal() {
  modalOpen.value = true
  recordAction('已打开模态框')
}

// 模态框关闭动作统一收口，避免状态散落。
function closeModal() {
  modalOpen.value = false
  recordAction('已关闭模态框')
}

// 模态框确认动作会关闭浮层并弹出 toast，模拟阻断式确认流程。
function confirmModal() {
  modalOpen.value = false
  recordAction('已确认模态框动作', { withToast: true })
}

// Toast 样例点击后显式弹出轻提示。
function triggerToast() {
  recordAction('已触发轻提示', { withToast: true })
}

// 打开 toast 时先清理旧定时器，避免快速多次点击导致状态错乱。
function openToast(message) {
  toastMessage.value = message
  toastVisible.value = true
  if (toastTimer.value) {
    window.clearTimeout(toastTimer.value)
  }
  toastTimer.value = window.setTimeout(() => {
    toastVisible.value = false
    toastTimer.value = null
  }, 1800)
}

// 页面级重置按钮负责把 demo 状态恢复到初始值，便于重复演示。
function resetDemo() {
  primaryClicks.value = 0
  iconClicks.value = 0
  switchEnabled.value = true
  activeTabIndex.value = 0
  textFieldValue.value = ''
  searchFieldValue.value = ''
  selectedRadio.value = 'Workspace'
  selectedCheckboxes.value = ['Roles', 'Members']
  filterIndex.value = 0
  panelSuggestion.value = 'Focus field suggestion'
  selectedTableRowId.value = 'mem-102'
  expandedTreeKeys.value = ['workspace', 'workspace.members']
  selectedTreeNode.value = 'Workspace / Members / Product'
  bubbleExpanded.value = false
  metricValue.value = 92
  profileJoined.value = 'March 2023'
  modalOpen.value = false
  toastVisible.value = false
  if (toastTimer.value) {
    window.clearTimeout(toastTimer.value)
    toastTimer.value = null
  }
  recordAction('已重置主题 demo')
}

// 组件卸载时清掉计时器，避免 toast 计时器泄漏到页面外。
onBeforeUnmount(() => {
  if (toastTimer.value) {
    window.clearTimeout(toastTimer.value)
  }
})
</script>

<template>
  <section class="theme-workspace">
    <article class="theme-workspace-hero glass-panel">
      <div>
        <div class="eyebrow">SELTHEME SHOWCASE</div>
        <h3>把主题样例升级成正式分层工作区</h3>
        <p>
          左侧保留主题控件分类导航，右侧把 Buttons、Forms、Tabs、Radio、Checkbox、Table、Tree、Overlays 拆成并列层。
          这样后续再扩按钮、输入框、页签、表格、树和弹层时，不会继续混到一个总类里。
        </p>
      </div>

      <div class="theme-workspace-meta">
        <span class="glass-chip">Buttons</span>
        <span class="glass-chip">Forms</span>
        <span class="glass-chip">Tabs</span>
        <span class="glass-chip">Table</span>
        <span class="glass-chip">Tree</span>
        <span class="glass-chip">Overlays</span>
        <button class="glass-button theme-meta-action" type="button" @click="resetDemo">Reset demo</button>
      </div>
    </article>

    <div class="theme-workspace-layout">
      <aside class="theme-catalog glass-panel">
        <div class="theme-catalog-title">Theme Catalog</div>

        <button
          v-for="section in sections"
          :key="section.key"
          type="button"
          class="theme-catalog-item"
          :class="{ active: section.key === activeSection?.key }"
          @click="selectSection(section.key)"
        >
          <strong>{{ section.label }}</strong>
          <span>{{ section.desc }}</span>
        </button>

        <div class="theme-live-panel glass-panel">
          <div class="theme-catalog-title">Live State</div>
          <strong>{{ lastActionLabel }}</strong>
          <p class="fine-print">{{ interactionSummary }}</p>
        </div>
      </aside>

      <section v-if="activeSection" class="theme-gallery glass-panel">
        <header class="theme-gallery-header">
          <div>
            <div class="eyebrow">Layered Samples</div>
            <h4>{{ activeSection.label }}</h4>
            <p class="fine-print" style="margin-top: 10px; max-width: 720px;">
              {{ activeSection.hero }}
            </p>
          </div>

          <span class="glass-badge">{{ activeSection.samples.length }} 个样例</span>
        </header>

        <div class="theme-gallery-grid">
          <article
            v-for="sample in activeSection.samples"
            :key="sample.key"
            class="theme-gallery-card glass-panel"
          >
            <div class="theme-gallery-card-top">
              <strong>{{ sample.label }}</strong>
              <span class="theme-tone" :class="sample.tone">{{ sample.tone }}</span>
            </div>

            <div class="theme-gallery-demo">
              <button
                v-if="!sample.kind"
                type="button"
                class="theme-demo-button"
                :class="sample.tone"
                @click="handlePrimaryAction(sample.preview)"
              >
                {{ sample.preview }}
              </button>

              <button
                v-else-if="sample.kind === 'icon'"
                type="button"
                class="theme-demo-circle"
                @click="handleIconAction"
              >
                {{ sample.preview }}
              </button>

              <button
                v-else-if="sample.kind === 'switch'"
                type="button"
                class="theme-demo-switch"
                :class="{ active: switchEnabled }"
                :aria-pressed="switchEnabled"
                @click="toggleSwitch"
              >
                <span class="theme-demo-switch-dot" />
              </button>

              <div v-else-if="sample.kind === 'tabs'" class="theme-demo-tabs">
                <button
                  v-for="(tab, index) in tabs"
                  :key="tab"
                  type="button"
                  class="theme-demo-tab"
                  :class="{ active: activeTabIndex === index }"
                  @click="selectTab(index)"
                >
                  {{ tab }}
                </button>
              </div>

              <label v-else-if="sample.kind === 'input'" class="theme-demo-input theme-demo-input-shell">
                <span>⌕</span>
                <input :value="textFieldValue" :placeholder="sample.preview" type="text" @input="handleTextInput">
              </label>

              <div v-else-if="sample.kind === 'search'" class="theme-demo-search theme-demo-search-shell">
                <span>⌕</span>
                <input :value="searchFieldValue" :placeholder="sample.preview" type="text" @input="handleSearchInput" @keydown.enter="submitSearch">
                <button type="button" class="theme-demo-mini-action" @click="submitSearch">Go</button>
              </div>

              <div v-else-if="sample.kind === 'radio'" class="theme-demo-choice-group">
                <button
                  v-for="option in radioOptions"
                  :key="option"
                  type="button"
                  class="theme-demo-radio"
                  :class="{ active: selectedRadio === option }"
                  @click="selectRadio(option)"
                >
                  <span class="theme-demo-radio-dot" />
                  <span>{{ option }}</span>
                </button>
              </div>

              <div v-else-if="sample.kind === 'checkbox-group'" class="theme-demo-choice-group">
                <button
                  v-for="option in checkboxOptions"
                  :key="option"
                  type="button"
                  class="theme-demo-checkbox"
                  :class="{ active: hasCheckboxOption(option) }"
                  :aria-pressed="hasCheckboxOption(option)"
                  @click="toggleCheckboxOption(option)"
                >
                  <span class="theme-demo-checkbox-mark">{{ hasCheckboxOption(option) ? '✓' : '' }}</span>
                  <span>{{ option }}</span>
                </button>
              </div>

              <div v-else-if="sample.kind === 'panel'" class="theme-demo-panel">
                <div class="theme-demo-panel-title">{{ sample.preview }}</div>
                <ul>
                  <li>
                    <button type="button" class="theme-demo-link" @click="chooseSuggestion('Focus field suggestion')">
                      Focus field suggestion
                    </button>
                  </li>
                  <li>
                    <button type="button" class="theme-demo-link" @click="chooseSuggestion('Tabs task suggestions')">
                      Tabs task suggestions
                    </button>
                  </li>
                  <li>
                    <button type="button" class="theme-demo-link" @click="chooseSuggestion('Status filter presets')">
                      Status filter presets
                    </button>
                  </li>
                </ul>
                <div class="fine-print">当前采用：{{ panelSuggestion }}</div>
              </div>

              <button
                v-else-if="sample.kind === 'chip'"
                type="button"
                class="theme-demo-chip"
                @click="cycleFilter"
              >
                {{ filterLabel }}
              </button>

              <div v-else-if="sample.kind === 'table'" class="theme-demo-data-card">
                <div class="table-shell">
                  <table>
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Role</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr
                        v-for="row in tableRows"
                        :key="row.id"
                        class="table-row"
                        :class="{ active: selectedTableRowId === row.id }"
                        @click="selectTableRow(row.id)"
                      >
                        <td>{{ row.name }}</td>
                        <td>{{ row.role }}</td>
                        <td>
                          <span class="status-pill" :class="row.status === 'ACTIVE' ? 'active' : 'disabled'">
                            {{ row.status }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div class="fine-print">当前选中：{{ selectedTableRow.name }} / {{ selectedTableRow.role }}</div>
              </div>

              <div v-else-if="sample.kind === 'tree'" class="theme-demo-data-card theme-demo-tree">
                <ul class="theme-tree">
                  <li v-for="node in treeNodes" :key="node.key">
                    <div class="theme-tree-line">
                      <button
                        v-if="node.children"
                        type="button"
                        class="theme-tree-toggle"
                        @click="toggleTreeBranch(node.key)"
                      >
                        {{ isTreeExpanded(node.key) ? '−' : '+' }}
                      </button>
                      <button type="button" class="theme-tree-node" @click="selectTreeNode(node.label)">
                        {{ node.label }}
                      </button>
                    </div>
                    <ul v-if="node.children && isTreeExpanded(node.key)" class="theme-tree-child">
                      <li v-for="child in node.children" :key="child.key">
                        <div class="theme-tree-line">
                          <button
                            v-if="child.children"
                            type="button"
                            class="theme-tree-toggle"
                            @click="toggleTreeBranch(child.key)"
                          >
                            {{ isTreeExpanded(child.key) ? '−' : '+' }}
                          </button>
                          <span v-else class="theme-tree-spacer" />
                          <button type="button" class="theme-tree-node" @click="selectTreeNode(`${node.label} / ${child.label}`)">
                            {{ child.label }}
                          </button>
                        </div>
                        <ul v-if="child.children && isTreeExpanded(child.key)" class="theme-tree-child">
                          <li v-for="leaf in child.children" :key="leaf.key">
                            <div class="theme-tree-line">
                              <span class="theme-tree-spacer" />
                              <button
                                type="button"
                                class="theme-tree-node"
                                @click="selectTreeNode(`${node.label} / ${child.label} / ${leaf.label}`)"
                              >
                                {{ leaf.label }}
                              </button>
                            </div>
                          </li>
                        </ul>
                      </li>
                    </ul>
                  </li>
                </ul>
                <div class="fine-print">当前节点：{{ selectedTreeNode }}</div>
              </div>

              <button
                v-else-if="sample.kind === 'bubble'"
                type="button"
                class="theme-demo-bubble theme-demo-bubble-button"
                @click="toggleBubble"
              >
                {{ bubbleExpanded
                  ? 'Toquid glass keeps guidance visible and can be expanded for richer task hints.'
                  : 'Toquid glass keeps guidance visible without turning the business page into a heavy dialog.' }}
              </button>

              <button
                v-else-if="sample.kind === 'stat'"
                type="button"
                class="theme-demo-stat theme-demo-stat-button"
                @click="refreshMetric"
              >
                <span class="theme-demo-stat-value">{{ metricValue }}%</span>
                <span class="theme-demo-stat-copy">{{ sample.preview }}</span>
              </button>

              <button
                v-else-if="sample.kind === 'profile'"
                type="button"
                class="theme-demo-profile theme-demo-profile-button"
                @click="toggleProfile"
              >
                <div class="theme-demo-profile-mark">SV</div>
                <div>
                  <strong>{{ sample.preview }}</strong>
                  <p class="fine-print" style="margin-top: 4px;">Joined {{ profileJoined }}</p>
                </div>
              </button>

              <button
                v-else-if="sample.kind === 'modal'"
                type="button"
                class="theme-demo-modal theme-demo-modal-button"
                @click="openModal"
              >
                <strong>{{ sample.preview }}</strong>
                <span>Click to open a floating confirmation layer.</span>
              </button>

              <button
                v-else-if="sample.kind === 'toast'"
                type="button"
                class="theme-demo-toast theme-demo-toast-button"
                @click="triggerToast"
              >
                <strong>{{ sample.preview }}</strong>
                <span>Click to raise a soft live feedback card.</span>
              </button>
            </div>

            <p class="fine-print">
              {{ sample.desc || `${activeSection.label} 组件样例，适合继续沉淀到 SELTHEME 主题包中。` }}
            </p>
          </article>
        </div>
      </section>
    </div>

    <div v-if="modalOpen" class="theme-floating-backdrop">
      <article class="theme-floating-modal glass-panel">
        <div class="eyebrow">INTERACTIVE MODAL</div>
        <h4>确认把这个主题样例用于业务页？</h4>
        <p class="fine-print">
          这类浮层适合承接高风险确认、升级提示和阻断式业务动作。
        </p>
        <div class="theme-floating-actions">
          <button class="glass-button" type="button" @click="closeModal">Cancel</button>
          <button class="glass-button primary" type="button" @click="confirmModal">Confirm</button>
        </div>
      </article>
    </div>

    <div v-if="toastVisible" class="theme-floating-toast glass-panel">
      <strong>Theme feedback</strong>
      <p class="fine-print">{{ toastMessage }}</p>
    </div>
  </section>
</template>
