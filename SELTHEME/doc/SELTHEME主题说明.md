# SELTHEME 主题说明

## 目标
- 提供一套可独立复用的液态玻璃主题源。
- 不把主题只埋在某一个业务页面里。

## 组成
- `themes/liquid-glass/tokens.css`：颜色、阴影、圆角和模糊变量
- `themes/liquid-glass/components.css`：玻璃卡片、按钮、输入框、标签组件样式
- `themes/liquid-glass/assets/*.svg`：背景雾化、高光球体和网格光效图
- `themes/liquid-glass/preview/index.html`：毛玻璃主题的独立预览入口

## 目录收敛原则
- `SELTHEME` 根目录只保留说明、文档和主题集合入口。
- 每一套主题都必须放在 `themes/<theme-name>/` 下独立成包。
- 主题包内部至少应包含 `tokens.css`、`components.css`、`assets/` 和 `preview/`。

## 风格特征
- 半透明磨砂面板
- 柔和高光边缘
- 冷暖混合渐变
- 轻盈投影和漂浮层次
