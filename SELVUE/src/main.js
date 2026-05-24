// 创建 Vue 应用实例，作为 SELVUE 用户管理台的正式入口。
import { createApp } from 'vue'
// 全局样式统一承接液态玻璃主题、布局和组件视觉。
import './style.css'
// 根组件只负责应用壳和页面编排，不直接堆业务细节。
import App from './App.vue'

// 把应用挂到根节点，完成页面启动。
createApp(App).mount('#app')
