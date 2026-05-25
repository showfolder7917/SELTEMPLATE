# admin-workbench-dark

`SELTHEME` 下的正式跨项目通用后台深色玻璃主题包。

包含：

- `tokens.css`：通用后台工作台颜色、阴影、圆角和间距变量
- `components.css`：主题聚合入口
- `components/layout.css`：页面壳层、工作区网格、面板和列表骨架
- `components/buttons.css`：主按钮、次按钮、标签按钮和状态芯片
- `components/forms.css`：表单、筛选栏、输入框和子区块
- `components/tabs.css`：页签导航、模式切换和语言切换
- `components/tables.css`：表格、行内操作和摘要文字
- `components/trees.css`：树型导航与层级节点
- `components/overlays.css`：空态、提示条和加载遮罩
- `assets/`：背景雾化和光效 SVG 资产
- `preview/index.html`：通用后台主题静态预览入口

适用场景：

- 主数据工作台
- 配置类后台页面
- 审批、列表、录入混合布局
- 需要在同一主题语言下承载中日双语后台项目

预览方式：

- 以主题包根目录启动静态服务后访问 `/preview/index.html`
- `preview/index.html` 依赖主题包根目录下的 `components.css` 和 `assets/`，不要把静态服务根直接指到 `preview/`

使用原则：

- 业务工程优先直接消费 `admin-workbench-dark/components.css`
- 业务页面自己的领域语义类名应保留在业务工程内，不再反向写回主题包
- 若某个项目需要独立品牌皮肤，应基于本主题新增业务主题包，而不是直接污染通用主题
