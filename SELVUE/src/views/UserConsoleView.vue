<script setup>
// 主页面通过注入拿到统一上下文，只负责编排区块。
import { computed, inject, ref, watch } from 'vue'
// 注入键保证页面和侧边栏访问同一份共享状态。
import { userConsoleKey } from '../constants/userConsole'
// 工具条承接搜索、状态筛选和新增动作。
import UserToolbar from '../components/UserToolbar.vue'
// 概览卡展示总量、启用数和同步模式。
import UserStats from '../components/UserStats.vue'
// 列表表格负责渲染当前用户记录。
import UserTable from '../components/UserTable.vue'
// 详情卡负责查看和触发编辑删除。
import UserInspector from '../components/UserInspector.vue'
// 弹层表单负责新增和编辑。
import UserFormModal from '../components/UserFormModal.vue'
// 主题样例卡用于把 SELTHEME 预览植入业务页面。
import ThemeShowcaseCard from '../components/ThemeShowcaseCard.vue'
// 主题工作区负责把样例按类别分层陈列成正式展示页。
import ThemeShowcaseWorkspace from '../components/ThemeShowcaseWorkspace.vue'

// 页面层只消费状态，不重新创建第二套数据模型。
const consoleState = inject(userConsoleKey)
// 当前页是否处于主题工作区，用于只对主题页开放顶部说明区折叠。
const isThemePage = computed(() => consoleState.currentPage === 'theme')
// 主题页顶部说明区默认收起，优先把首屏空间让给下方样例和表格控件。
const themeBannerCollapsed = ref(true)

// 页面在 users 和 theme 之间切换时，同步校正折叠状态，避免用户页误用收起态。
watch(
  () => consoleState.currentPage,
  (pageKey) => {
    // 进入主题页时恢复为默认收起，保证每次回到主题页都先展示更紧凑的首屏。
    if (pageKey === 'theme') {
      themeBannerCollapsed.value = true
      return
    }
    // 离开主题页后清掉折叠状态，避免普通业务页沿用主题页的收起样式。
    themeBannerCollapsed.value = false
  },
  { immediate: true }
)

// 顶部说明区切换展开和收起，用于按需查看完整说明而不长期占用首屏高度。
function toggleThemeBanner() {
  themeBannerCollapsed.value = !themeBannerCollapsed.value
}
</script>

<template>
  <div class="console-page">
    <section class="top-banner" :class="{ 'theme-collapsed': isThemePage && themeBannerCollapsed }">
      <article class="hero-card glass-panel" :class="{ collapsed: isThemePage && themeBannerCollapsed }">
        <div class="hero-card-top" :class="{ 'theme-mode': isThemePage }">
          <div>
            <div class="eyebrow">SELVUE USER CONSOLE</div>
            <h2 v-if="consoleState.currentPage === 'users'">用一套液态玻璃管理台，承接用户增删改查的正式交互。</h2>
            <h2 v-else>主题样例页负责陈列可复用控件，不再把样例压成一张静态卡。</h2>
          </div>

          <button
            v-if="isThemePage"
            type="button"
            class="glass-button hero-collapse-toggle"
            @click="toggleThemeBanner"
          >
            {{ themeBannerCollapsed ? '展开说明' : '收起说明' }}
          </button>
        </div>
        <p v-if="!isThemePage || !themeBannerCollapsed">
          <template v-if="consoleState.currentPage === 'users'">
            当前页面优先直连 SELSP 用户接口；若后端暂时不可达，则自动回退到本地种子数据。
            左侧保持导航壳，右侧按“概览 + 列表 + 详情 + 表单”收口，便于后续扩展更多业务模块。
          </template>
          <template v-else>
            当前主题页按 Buttons、Forms、Tabs、Radio、Checkbox、Table、Tree、Overlays 并列陈列 SELTHEME 样例，
            方便把每类控件拆开查看、单独验证并继续沉淀到主题包。
          </template>
        </p>
        <div v-if="!isThemePage || !themeBannerCollapsed" class="hero-meta">
          <span class="glass-badge">{{ consoleState.syncLabel }}</span>
          <span class="glass-chip">左右布局</span>
          <span class="glass-chip">{{ consoleState.currentPage === 'users' ? '用户 CRUD' : 'Theme Gallery' }}</span>
          <span class="glass-chip">液态玻璃主题</span>
        </div>
      </article>

      <UserStats
        :stats="consoleState.stats"
        :sync-label="consoleState.syncLabel"
        :sync-tone="consoleState.syncTone"
        :compact="isThemePage && themeBannerCollapsed"
        :match-collapsed-height="isThemePage && themeBannerCollapsed"
      />
    </section>

    <template v-if="consoleState.currentPage === 'users'">
      <UserToolbar
        :filters="consoleState.filters"
        :status-options="consoleState.statusOptions"
        @keyword-change="consoleState.setKeyword"
        @status-change="consoleState.setStatus"
        @reset="consoleState.resetFilters"
        @create="consoleState.openCreateForm"
      />

      <div class="content-grid">
        <UserTable
          :users="consoleState.users"
          :selected-user-id="consoleState.selectedUserId"
          :loading="consoleState.loading"
          @select="consoleState.selectUser"
        />

        <div style="display: grid; gap: 20px; align-content: start;">
          <UserInspector
            :user="consoleState.selectedUser"
            :pending-delete-id="consoleState.pendingDeleteId"
            @edit="consoleState.openEditForm"
            @delete-request="consoleState.askDelete"
            @delete-cancel="consoleState.cancelDelete"
            @delete-confirm="consoleState.confirmDelete"
          />

          <ThemeShowcaseCard
            :items="consoleState.themePreviewItems"
            @open-theme="consoleState.openThemePage"
          />
        </div>
      </div>
    </template>

    <ThemeShowcaseWorkspace
      v-else
      :sections="consoleState.themeShowcaseSections"
    />

    <UserFormModal
      :open="consoleState.formOpen"
      :mode="consoleState.formMode"
      :draft="consoleState.formDraft"
      :saving="consoleState.saving"
      @close="consoleState.closeForm"
      @submit="consoleState.submitForm"
      @update:draft="(draft) => { consoleState.formDraft = draft }"
    />

    <div v-if="consoleState.flash" class="toast-card glass-panel">
      <strong>{{ consoleState.flash.title }}</strong>
      <p class="fine-print" style="margin-top: 6px;">{{ consoleState.flash.description }}</p>
    </div>
  </div>
</template>
