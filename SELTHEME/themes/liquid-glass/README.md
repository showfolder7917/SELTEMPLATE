# liquid-glass

`SELTHEME` 下的正式毛玻璃通用后台主题包。

包含：

- `tokens.css`：液态玻璃主题变量
- `components.css`：主题组件聚合入口，统一引入分层组件样式
- `components/buttons.css`：胶囊按钮与圆形图标按钮
- `components/forms.css`：输入框、建议面板、复选框、开关、列表交互
- `components/tabs.css`：页签切换层
- `components/tables.css`：表格层
- `components/trees.css`：树结构层
- `components/overlays.css`：气泡提示、统计卡、升级卡、展示浮层
- `components/admin-adapter.css`：把 liquid-glass 适配到当前 `seladmin-*` 后台页面语义
- `scripts/index.js`：主题层正式索引
- `scripts/buttons.js`：按钮层元数据
- `scripts/forms.js`：表单层元数据
- `scripts/tabs.js`：页签层元数据
- `scripts/tables.js`：表格层元数据
- `scripts/trees.js`：树结构层元数据
- `scripts/overlays.js`：浮层层元数据
- `assets/`：光效、雾化和高光 SVG 资产
- `preview/index.html`：稳定入口，会跳转到正式互动 demo
- `preview/demo.html`：正式互动 demo 壳页
- `preview/fragments/`：展示页、互动实验页、侧栏片段
- `preview/scripts/demo-shell.js`：互动 demo 的片段装配与状态逻辑
- `preview/styles/demo-shell.css`：preview 壳层专属样式
- `../../打开liquid-glass主题Demo.command`：本地双击启动 demo 的静态服务脚本

适用场景：

- 轻玻璃感后台工作台
- 品牌展示感更强的录入与管理页面
- 需要与 `seladmin-*` 通用后台结构直接对接的浅色主题
