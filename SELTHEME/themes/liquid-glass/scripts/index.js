// 主题层索引：统一导出各个正式组件层，供 preview 和业务页共享同一份结构描述。
import { BUTTON_LAYER_META } from './buttons.js'
import { FORM_LAYER_META } from './forms.js'
import { TAB_LAYER_META } from './tabs.js'
import { TABLE_LAYER_META } from './tables.js'
import { TREE_LAYER_META } from './trees.js'
import { OVERLAY_LAYER_META } from './overlays.js'

// 主题层顺序显式收口，避免不同页面各自硬编码分类顺序。
export const THEME_LAYER_ORDER = [
  BUTTON_LAYER_META,
  FORM_LAYER_META,
  TAB_LAYER_META,
  TABLE_LAYER_META,
  TREE_LAYER_META,
  OVERLAY_LAYER_META
]
