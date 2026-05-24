<script setup>
// provide 用于把统一用户上下文下发给导航、页面和弹层。
import { provide } from 'vue'
// 左侧导航壳只负责品牌和主导航。
import AppSidebar from './components/AppSidebar.vue'
// 主页面视图负责用户 CRUD 的整体编排。
import UserConsoleView from './views/UserConsoleView.vue'
// 注入键统一约束所有子组件访问上下文的方式。
import { userConsoleKey } from './constants/userConsole'
// 组合式状态入口统一承接列表、表单、删除确认和提示消息。
import { useUserDirectory } from './composables/user/useUserDirectory'

// 初始化整套用户管理台上下文。
const userConsole = useUserDirectory()
// 把上下文提供给整个应用壳，避免子组件各自重建状态。
provide(userConsoleKey, userConsole)
</script>

<template>
  <div class="app-shell">
    <AppSidebar />
    <main class="main-shell">
      <UserConsoleView />
    </main>
  </div>
</template>
