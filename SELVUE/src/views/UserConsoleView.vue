<script setup>
// 主页面通过注入拿到统一上下文，只负责编排区块。
import { inject } from 'vue'
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
</script>

<template>
  <div class="console-page">
    <section class="top-banner">
      <article class="hero-card glass-panel">
        <div class="eyebrow">SELVUE USER CONSOLE</div>
        <h2 v-if="consoleState.currentPage === 'users'">用一套液态玻璃管理台，承接用户增删改查的正式交互。</h2>
        <h2 v-else>主题样例页负责陈列可复用控件，不再把样例压成一张静态卡。</h2>
        <p>
          <template v-if="consoleState.currentPage === 'users'">
            当前页面优先直连 SELSP 用户接口；若后端暂时不可达，则自动回退到本地种子数据。
            左侧保持导航壳，右侧按“概览 + 列表 + 详情 + 表单”收口，便于后续扩展更多业务模块。
          </template>
          <template v-else>
            当前主题页按 Buttons、Forms、Tabs、Radio、Checkbox、Table、Tree、Overlays 并列陈列 SELTHEME 样例，
            方便把每类控件拆开查看、单独验证并继续沉淀到主题包。
          </template>
        </p>
        <div class="hero-meta">
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
