# SELVUE 前端设计需求文档

## 0. 当前实现问题与优先修正项

### 0.1 当前实现现状
- `SELTEMPLATE` 目录下当前只有后端工程 `SELSP`，还没有对应的 Vue 前端工程。
- 用户管理后端接口已经存在，可提供列表、详情、新增、更新、删除的基本能力。
- 目前没有统一的前端架构标准，也没有可复用的液态玻璃主题目录。

### 0.2 当前核心问题
- 问题 1：缺少 `SELVUE` 用户管理台，无法承接用户 CRUD 的前端交互。
- 问题 2：缺少一套与 `java架构-AI` 对齐的 `vue架构-AI` 规范，后续新增页面容易继续散乱实现。
- 问题 3：缺少独立的液态玻璃主题目录，视觉资产和组件样式无法沉淀为可复用前端主题源。

### 0.3 当前字段 / 结构是否可以复用
- 可以复用 `SELSP` 现有用户接口和字段：`id`、`name`、`email`、`status`、`createdAt`、`updatedAt`。
- 可以复用 [ai_dual_teacher_admin](/Users/showfolder/Documents/workSpace/SELF/SELFCOMMON/SELFVUE/ai_dual_teacher_admin) 的工程分层思路：
  `views / components / composables / services / constants`。
- 可以复用 `SELFSP / SELSP` 的文档治理方式：项目文档放工程目录，通用规范放 `SELFMEMORY/MEMORIES/human/需求/`。

### 0.4 开发前必须先修正的事项
1. 明确 `SELVUE`、`SELTHEME` 和 `vue架构-AI` 三者的落点与边界。
2. 先定义前端当前阶段是“可独立预览 + 可切后端联调”的模式，避免页面一开始就完全依赖后端在线。
3. 先定义主题源目录与项目消费关系，避免样式只落在单个页面而无法复用。

## 1. 目标
- 在 `SELTEMPLATE/SELVUE` 下建立一套 Vue 3 + Vite 前端工程。
- 实现用户管理台，覆盖用户列表、搜索筛选、详情查看、新增、更新、删除。
- 整体采用左右布局：左侧为品牌与导航，右侧为用户管理主内容区。
- 在 `SELTEMPLATE/SELTHEME` 下单独沉淀液态玻璃主题、组件样式和自生成图片资产。
- 在 `SELFMEMORY/MEMORIES/human/需求/vue架构-AI` 下建立一套与 `java架构-AI` 平行的 Vue 架构规范。

## 2. 输入与输出边界

### 2.1 输入
- 参考工程：
  [ai_dual_teacher_admin](/Users/showfolder/Documents/workSpace/SELF/SELFCOMMON/SELFVUE/ai_dual_teacher_admin)
- 后端接口来源：
  [SELSP](/Users/showfolder/Documents/workSpace/SELF/SELTEMPLATE/SELSP)
- 架构规范参考：
  [java架构-AI](/Users/showfolder/Documents/workSpace/SELF/SELFMEMORY/MEMORIES/human/需求/java架构-AI)
- 视觉参考：
  用户提供的液态玻璃 / 毛玻璃 UI 截图

### 2.2 输出
- `SELTEMPLATE/SELVUE/`：正式 Vue 工程
- `SELTEMPLATE/SELTHEME/`：独立液态玻璃主题目录
- `SELFMEMORY/MEMORIES/human/需求/vue架构-AI/`：Vue 架构规范组
- `SELTEMPLATE/SELVUE/doc/SELVUE前端设计需求文档.md`：本需求文档

## 3. 核心设计原则
- 原则 1：工程结构优先稳定，先分层再写页面细节。
- 原则 2：主题源独立沉淀，项目使用主题，但不把主题和业务页面混成一团。
- 原则 3：用户 CRUD 页面要先做到可独立运行，再接正式后端联调。
- 原则 4：当前阶段优先落地单页面用户管理台，但目录结构要为未来多页面扩展留口。

## 4. 方案设计

### 4.1 SELVUE 工程方案
- 技术基线：Vue 3 + Vite。
- 目录结构采用：
  - `src/views/`
  - `src/components/`
  - `src/composables/`
  - `src/services/`
  - `src/constants/`
- 根组件只负责应用壳、上下文提供和主布局编排。
- 用户页面由以下几个区块组成：
  - 左侧导航壳
  - 顶部概览卡片与搜索区
  - 中部用户列表表格
  - 右侧用户详情与操作卡片
  - 弹层式新增 / 编辑表单

### 4.2 数据接入方案
- 默认采用“后端联调优先，本地回退兜底”模式。
- 若 `SELSP` 后端可访问，则通过 `/api/users` 系列接口读写数据。
- 若当前后端不可访问，则自动回退到浏览器本地存储种子数据，保证页面可预览和可演示。
- 为避免前端双击启动后直接落入本地回退模式，需要补一套本地启动器：单前端脚本、单后端脚本和前后端全套脚本。

### 4.3 SELTHEME 主题方案
- 单独建立 `SELTHEME` 目录，沉淀：
  - 主题变量
  - 液态玻璃卡片样式
  - 按钮、输入框、标签和开关样式
  - 背景氛围图与高光 SVG 资产
- 主题风格特征：
  - 低饱和冷暖渐变
  - 半透明磨砂玻璃
  - 高亮内发光边缘
  - 柔和投影和流动背景

### 4.4 vue架构-AI 规范方案
- 参照 `java架构-AI`，建立以下文档：
  - `Vue工程架构.md`
  - `页面命名与目录强制规范.md`
  - `新模块创建清单.md`
  - `自动bug修复接入规范.md`
- 规范重点覆盖：
  - 目录和分层
  - 状态与服务边界
  - 文档与测试要求
  - 自动纠错与自动回归接入条件

## 5. 数据与字段设计
- 用户字段采用后端现有结构：
  - `id`
  - `name`
  - `email`
  - `status`
  - `createdAt`
  - `updatedAt`
- 前端新增辅助字段：
  - `statusLabel`
  - `statusTone`
  - `joinedLabel`
- 当前阶段不新增后端字段。

## 6. 代码改动点
- 前端改动点：
  - 新建 `SELVUE` 工程
  - 新建用户管理页面、组件、状态和服务层
  - 新建液态玻璃主题样式和自生成图片资产
- 文档改动点：
  - 新建本需求文档
  - 新建 `vue架构-AI` 规范组
- 主题改动点：
  - 新建 `SELTHEME` 独立目录与主题源文件

## 7. 风险点
- 风险 1：若前端完全绑定后端在线，页面验证容易被联调环境阻塞。
- 风险 2：若主题只写在业务页面里，后续无法复用到其他 Vue 页面。
- 风险 3：若没有独立规范，后续页面扩展又会回到单文件堆逻辑的状态。

## 8. 验证方案
- 构建验证：
  - `npm install`
  - `npm run build`
- 页面验证：
  - 启动本地开发服务
  - 打开真实页面验证左右布局、主题样式和 CRUD 主流程
- 启动器验证：
  - 运行 `启动SELSP用户后台服务.command` 验证后端接口可达
  - 运行 `打开SELVUE用户管理台.command` 验证前端脚本能接管 `5176`
  - 运行 `一键启动SELVUE用户管理全套.command` 验证前后端按依赖顺序启动并由脚本统一托管生命周期
- 主题验证：
  - 检查主题目录中的样式和图片资产是否能独立展示
- 文档验证：
  - 检查 `vue架构-AI` 文档组是否齐全且可直接复用
