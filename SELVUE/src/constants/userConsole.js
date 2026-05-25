// 统一注入键，保证侧边栏、页面和弹层都通过同一上下文访问用户管理状态。
export const userConsoleKey = Symbol('userConsole')

// 左侧导航当前先保留正式业务入口和主题说明入口，为未来多页面扩展留口。
export const NAV_ITEMS = [
  { key: 'users', label: '用户管理', desc: '列表、筛选、详情和增删改查' },
  { key: 'theme', label: '主题样例', desc: '液态玻璃卡片与控件预览' }
]

// 页面状态枚举统一在常量层收口，避免组件和服务散落魔法字符串。
export const USER_STATUS_OPTIONS = [
  { value: 'all', label: '全部状态' },
  { value: 'ACTIVE', label: '启用中' },
  { value: 'DISABLED', label: '已停用' }
]

// 主题样例数据用于展示 SELTHEME 的独立组件方向。
export const THEME_PREVIEW_ITEMS = [
  { key: 'workspace', label: 'Workspace Button', tone: 'cyan' },
  { key: 'member', label: 'Invite Member', tone: 'lilac' },
  { key: 'search', label: 'Search Field', tone: 'cream' },
  { key: 'upgrade', label: 'Upgrade Plan', tone: 'rose' }
]

// 主题样例分组用于把控件从“混在一张卡里”升级成按能力分层陈列。
export const THEME_SHOWCASE_SECTIONS = [
  {
    key: 'buttons',
    label: 'Buttons',
    desc: '按钮、胶囊和触发器样例',
    hero: '把主要动作控件沉淀成统一的液态玻璃按钮层，方便业务页直接复用。',
    samples: [
      { key: 'primary', label: 'Primary CTA', preview: 'Create workspace', tone: 'cyan', desc: '主操作按钮适合承接新增、创建和进入工作区动作。' },
      { key: 'secondary', label: 'Secondary Action', preview: 'Primary button', tone: 'mint', desc: '次级按钮适合承接辅助跳转和低风险操作。' },
      { key: 'search', label: 'Search Trigger', preview: 'Search button', tone: 'amber', desc: '搜索按钮适合和筛选、搜索框组合使用。' },
      { key: 'icon', label: 'Icon Trigger', preview: '＋', tone: 'lilac', kind: 'icon', desc: '圆形触发器适合快捷新增、更多操作和悬浮入口。' },
      { key: 'toggle', label: 'Switch Trigger', preview: 'On / Off', tone: 'rose', kind: 'switch', desc: '胶囊开关适合场景切换和启停类操作。' }
    ]
  },
  {
    key: 'forms',
    label: 'Forms',
    desc: '输入、筛选和选择控件样例',
    hero: '把搜索、输入、联想和选择操作拆成稳定表单层，便于后台表单和筛选区直接接入。',
    samples: [
      { key: 'text', label: 'Text Field', preview: 'Search projects...', tone: 'cyan', kind: 'input', desc: '文本输入框适合作为列表筛选和弹层表单的基础输入控件。' },
      { key: 'searchline', label: 'Search Field', preview: 'Text field', tone: 'cream', kind: 'search', desc: '搜索框样式适合放在工具条和资源检索场景。' },
      { key: 'dropdown', label: 'Suggestion Panel', preview: 'Invite projects...', tone: 'lilac', kind: 'panel', desc: '建议面板适合搜索联想、邀请成员和筛选提示。' },
      { key: 'status', label: 'Status Filter', preview: 'Select dropdown', tone: 'rose', kind: 'chip', desc: '胶囊筛选器适合状态切换和轻量级条件筛选。' }
    ]
  },
  {
    key: 'tabs',
    label: 'Tabs',
    desc: '页签切换样例',
    hero: '把 tabs 单独抽成一层，方便工作台、详情页和设置页复用同一套分段导航交互。',
    samples: [
      { key: 'tabs', label: 'Segmented Tabs', preview: 'Overview / Members / Access', tone: 'cyan', kind: 'tabs', desc: '分段标签适合工作台、详情页和二级功能切换。' }
    ]
  },
  {
    key: 'radios',
    label: 'Radio',
    desc: '单选按钮样例',
    hero: '把单选按钮从通用表单里拆出来，方便策略切换、模式选择和单一决策场景单独评估。',
    samples: [
      { key: 'radio', label: 'Radio Group', preview: 'Single select', tone: 'amber', kind: 'radio', desc: '单选按钮适合模式切换、状态选择和单一决策。' }
    ]
  },
  {
    key: 'checkboxes',
    label: 'Checkbox',
    desc: '多选按钮样例',
    hero: '把多选按钮独立出来，便于批量配置、权限选择和组合条件的交互评估。',
    samples: [
      { key: 'checkbox', label: 'Checkbox Group', preview: 'Multiple select', tone: 'mint', kind: 'checkbox-group', desc: '多选按钮适合批量配置、权限勾选和条件组合。' }
    ]
  },
  {
    key: 'tables',
    label: 'Table',
    desc: '表格样例',
    hero: '把表格从其他数据控件中拆开，方便列表页、日志页和后台管理页单独复用和验证。',
    samples: [
      // 基础数据表用于展示标准列表浏览和行选中反馈。
      { key: 'table', label: 'Data Table', preview: 'Workspace members', tone: 'cyan', kind: 'table', desc: '表格样例用于展示成员、订单、日志等结构化数据。' },
      // 表格演示补一张带摘要和批量动作的卡，方便单独观察后台表格区块。
      { key: 'table-demo', label: 'Table Demo', preview: 'Orders dashboard', tone: 'mint', kind: 'table-demo', desc: '表格演示适合承接订单、审批和批量操作列表。' }
    ]
  },
  {
    key: 'trees',
    label: 'Tree',
    desc: '树结构样例',
    hero: '把树结构独立成单层，方便权限树、目录树和资源层级浏览的交互单独维护。',
    samples: [
      { key: 'tree', label: 'Tree Navigation', preview: 'Workspace tree', tone: 'lilac', kind: 'tree', desc: '树结构样例适合权限树、目录树和资源层级浏览。' }
    ]
  },
  {
    key: 'overlays',
    label: 'Overlays',
    desc: '卡片、提示和展示浮层样例',
    hero: '把展示型组件提升到独立浮层层级，方便概览卡、提示层和升级卡统一复用。',
    samples: [
      // tip 单独保留在浮层层，承接短提示和悬浮说明场景。
      { key: 'tip', label: 'Tip', preview: 'Hover or pin tip', tone: 'cyan', kind: 'tip', desc: 'tip 适合字段说明、轻提示和局部引导。' },
      // 右键菜单样例用于评估列表行和资源卡的操作菜单密度。
      { key: 'context-menu', label: 'Context Menu', preview: 'Quick row actions', tone: 'mint', kind: 'context-menu', desc: '右键菜单适合列表行、资源卡和树节点的快捷操作。' },
      // 展示气泡继续保留，作为长说明型提示卡。
      { key: 'bubble', label: 'Speech Bubble', preview: 'Liquid glass tip', tone: 'cyan', kind: 'bubble', desc: '提示气泡适合承接弱提示、指导语和局部说明。' },
      { key: 'stats', label: 'Metric Card', preview: 'Upgrade plan', tone: 'mint', kind: 'stat', desc: '指标卡适合总量、占比和状态概览展示。' },
      { key: 'profile', label: 'Profile Card', preview: 'User info', tone: 'amber', kind: 'profile', desc: '资料卡适合用户概览、成员名片和详情摘要。' },
      { key: 'modal', label: 'Dialog Box', preview: 'Modal dialog', tone: 'lilac', kind: 'modal', desc: '弹出提示框适合二次确认、升级提示和阻断式操作。' },
      { key: 'toast', label: 'Toast Card', preview: 'Toast notification', tone: 'rose', kind: 'toast', desc: '轻提示卡适合保存成功、同步模式和状态反馈。' }
    ]
  }
]
