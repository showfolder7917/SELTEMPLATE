<script setup>
// 侧边栏通过注入读取共享上下文，不直接碰服务层实现。
import { inject } from 'vue'
// 注入键来自常量层，保证访问方式稳定。
import { userConsoleKey } from '../constants/userConsole'

// 当前组件只负责导航和静态说明。
const consoleState = inject(userConsoleKey)

/**
 * 切换当前页面。
 *
 * @param {string} key 页面标识
 */
function openPage(key) {
  // 导航只改当前页，不附带其他业务副作用。
  consoleState.currentPage = key
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand">
      <div class="brand-mark">SV</div>
      <div>
        <div class="brand-label">SELVUE CONSOLE</div>
        <h1>用户管理台</h1>
      </div>
    </div>

    <nav class="sidebar-nav">
      <button
        v-for="item in consoleState.navItems"
        :key="item.key"
        class="sidebar-nav-item"
        :class="{ active: consoleState.currentPage === item.key }"
        @click="openPage(item.key)"
      >
        <strong>{{ item.label }}</strong>
        <span>{{ item.desc }}</span>
      </button>
    </nav>

    <div class="sidebar-note glass-panel">
      <div class="eyebrow">THIS BUILD</div>
      <h3>当前交付范围</h3>
      <ul>
        <li>用户列表、详情、新增、编辑、删除</li>
        <li>正式接口优先，本地回退兜底</li>
        <li>液态玻璃主题卡片与控件样例</li>
        <li>已为未来多页面扩展保留分层目录</li>
      </ul>
    </div>
  </aside>
</template>
