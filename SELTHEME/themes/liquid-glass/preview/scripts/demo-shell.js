// 统一读取主题包正式分类元数据，避免 preview 继续手写一套层级口径。
import { THEME_LAYER_ORDER } from '../../scripts/index.js'

// 片段路径表：把 sidebar、showcase、playground 的文件路径集中声明，便于壳页按需加载。
const fragmentPaths = {
  sidebar: './fragments/sidebar.html',
  showcase: './fragments/showcase.html',
  playground: './fragments/playground.html',
};

// 初始交互状态：承接 demo 页的关键字、开关和复选框，用来驱动右侧实时摘要卡。
const demoState = {
  activeView: 'showcase',
  keyword: '',
  invite: true,
  alert: false,
  depth: true,
  workspace: true,
};

// 片段缓存：避免 tab 切换时重复请求相同 html 文件，保证 demo 切换足够轻。
const fragmentCache = new Map();

// 通用节点引用：集中保存壳页容器，便于后续挂载片段和更新摘要。
const shellRefs = {
  sidebarSlot: document.getElementById('demo-sidebar-slot'),
  viewSlot: document.getElementById('demo-view-slot'),
  summarySlot: document.getElementById('demo-summary-slot'),
};

// 渲染主题层目录：把主题包正式 css/js 分层同步到左侧目录，保证主题源结构对外可见。
function renderThemeLayerToc() {
  // 目录节点依赖 sidebar 片段，因此每次都在挂载后重新获取。
  const tocNode = document.getElementById('seltheme-layer-toc');

  // 若目录节点不存在，说明 sidebar 尚未挂载完成，本轮不做渲染。
  if (!tocNode) {
    return;
  }

  // 统一按正式层级元数据生成目录项，避免 tabs、tables、trees 被遗漏。
  tocNode.innerHTML = THEME_LAYER_ORDER.map((layer) => `
    <div class="seltheme-toc-item seltheme-glass">
      <div>
        <strong>${layer.label}</strong>
        <div class="seltheme-copy">${layer.desc}</div>
      </div>
      <code>${layer.styleFile}</code>
    </div>
  `).join('');
}

// 加载 html 片段：统一从 preview/fragments 读取，失败时直接抛错，避免页面静默缺块。
async function loadFragment(fragmentKey) {
  // 若当前片段已加载过，直接复用缓存内容，减少重复网络开销。
  if (fragmentCache.has(fragmentKey)) {
    return fragmentCache.get(fragmentKey);
  }

  // 按片段键拿到正式路径，保证壳页与片段路径映射只有这一处来源。
  const fragmentPath = fragmentPaths[fragmentKey];
  // 通过 fetch 获取片段原文，让 demo.html 不再堆满所有模块标记。
  const response = await fetch(fragmentPath);

  // 若片段请求失败，直接抛错阻断挂载，避免出现半残的预览页。
  if (!response.ok) {
    throw new Error(`加载片段失败: ${fragmentPath}`);
  }

  // 读取片段 html 文本并写入缓存，后续切页时直接复用。
  const html = await response.text();
  fragmentCache.set(fragmentKey, html);
  return html;
}

// 渲染左侧摘要：把当前关键交互状态汇总成稳定卡片，便于观察“互动是否真的生效”。
function renderSummary() {
  // 根据当前状态拼出最重要的四个观察项，减少用户切换页面时的信息损失。
  shellRefs.summarySlot.innerHTML = `
    <div class="seltheme-eyebrow">Live demo summary</div>
    <div class="seltheme-demo-summary-grid">
      <div class="seltheme-glass seltheme-demo-summary-item">
        <span>Current View</span>
        <strong>${demoState.activeView === 'showcase' ? 'Showcase' : 'Playground'}</strong>
      </div>
      <div class="seltheme-glass seltheme-demo-summary-item">
        <span>Keyword</span>
        <strong>${demoState.keyword || 'None'}</strong>
      </div>
      <div class="seltheme-glass seltheme-demo-summary-item">
        <span>Invite</span>
        <strong>${demoState.invite ? 'On' : 'Off'}</strong>
      </div>
      <div class="seltheme-glass seltheme-demo-summary-item">
        <span>Glass Depth</span>
        <strong>${demoState.depth ? 'Deep' : 'Soft'}</strong>
      </div>
    </div>
  `;
}

// 同步右侧实时摘要卡：把 playground 内的输入和选择状态映射成可见反馈。
function syncPlaygroundCard() {
  // 只在 playground 已经渲染后更新实时卡，避免切到 showcase 时拿不到目标节点。
  const titleNode = document.getElementById('demo-live-title');
  const copyNode = document.getElementById('demo-live-copy');
  const keywordNode = document.getElementById('demo-metric-keyword');
  const inviteNode = document.getElementById('demo-metric-invite');
  const alertNode = document.getElementById('demo-metric-alert');
  const depthNode = document.getElementById('demo-metric-depth');

  // 若当前视图没有这些节点，说明 playground 还没挂载，直接结束本轮同步。
  if (!titleNode || !copyNode || !keywordNode || !inviteNode || !alertNode || !depthNode) {
    return;
  }

  // 按当前开关状态生成标题，让交互结果能立刻被视觉感知。
  titleNode.textContent = `Current mode: ${demoState.workspace ? 'Workspace' : 'Focus'} / ${demoState.alert ? 'Alert' : 'Calm'}`;
  // 把输入关键字与复选框组合成一段解释性文案，证明 demo 不再只是静态陈列。
  copyNode.textContent = demoState.keyword
    ? `当前关键字为 “${demoState.keyword}”，邀请状态为 ${demoState.invite ? '开启' : '关闭'}，玻璃深度为 ${demoState.depth ? 'Deep' : 'Soft'}。`
    : `输入关键字、切换开关或勾选成员邀请，右侧摘要会即时更新。`;

  // 将状态映射到指标卡，便于对照检查每个控制项是否真的驱动了界面。
  keywordNode.textContent = demoState.keyword || 'None';
  inviteNode.textContent = demoState.invite ? 'On' : 'Off';
  alertNode.textContent = demoState.alert ? 'On' : 'Off';
  depthNode.textContent = demoState.depth ? 'Deep' : 'Soft';
}

// 绑定 playground 交互：把输入框、复选框、按钮和 switch 的动作接进统一状态。
function bindPlaygroundInteractions() {
  // 关键字输入框负责驱动实时摘要里的 keyword 与提示文案。
  const searchInput = document.getElementById('demo-search-input');
  // 邀请复选框用于切换成员邀请状态。
  const inviteCheckbox = document.getElementById('demo-checkbox-invite');
  // 通知复选框映射为 alert 状态的辅助入口。
  const notifyCheckbox = document.getElementById('demo-checkbox-notify');
  // 三个 switch 负责切换 workspace、alert、depth 三类布尔状态。
  const switches = Array.from(document.querySelectorAll('.seltheme-switch[data-control]'));
  // 互动按钮负责制造可见反馈，证明 demo 已具备最小动作闭环。
  const actionButtons = Array.from(document.querySelectorAll('[data-action=\"apply\"], [data-action=\"suggest\"]'));

  // 如果当前页面还没挂载 playground，就不做任何绑定，避免空节点报错。
  if (!searchInput || !inviteCheckbox || !notifyCheckbox || switches.length === 0) {
    return;
  }

  // 输入框每次变化都回写状态，并刷新顶部摘要和右侧卡片。
  searchInput.addEventListener('input', (event) => {
    demoState.keyword = event.target.value.trim();
    renderSummary();
    syncPlaygroundCard();
  });

  // 邀请复选框变化时，直接影响摘要里的 invite 指标。
  inviteCheckbox.addEventListener('change', (event) => {
    demoState.invite = event.target.checked;
    renderSummary();
    syncPlaygroundCard();
  });

  // 通知复选框变化时，同步到 alert 状态，保持“复选框和开关都能驱动状态”的演示目的。
  notifyCheckbox.addEventListener('change', (event) => {
    demoState.alert = event.target.checked;
    renderSummary();
    syncPlaygroundCard();
  });

  // 为每个 switch 绑定点击行为，通过 data-control 决定该切换哪个状态字段。
  switches.forEach((switchButton) => {
    switchButton.addEventListener('click', () => {
      // 读取当前 switch 对应的状态键，确保按钮与状态映射稳定。
      const controlKey = switchButton.dataset.control;
      // 取反状态值，形成最小交互反馈。
      demoState[controlKey] = !demoState[controlKey];
      // 让 DOM 上的 data-active 与状态保持一致，交由 CSS 决定视觉位置。
      switchButton.dataset.active = String(demoState[controlKey]);

      // alert 状态还要兼容通知复选框的视觉结果，所以顺带同步复选框。
      if (controlKey === 'alert') {
        notifyCheckbox.checked = demoState.alert;
      }

      // 每次切换后都刷新摘要，证明开关已经真正生效。
      renderSummary();
      syncPlaygroundCard();
    });
  });

  // 互动按钮用来触发两类显式动作：应用预览与生成建议。
  actionButtons.forEach((button) => {
    button.addEventListener('click', () => {
      // 读取按钮动作类型，决定要向状态里写什么反馈。
      const action = button.dataset.action;

      // Apply preview 让 workspace 状态被强制开启，模拟“应用当前主题方案”。
      if (action === 'apply') {
        demoState.workspace = true;
      }

      // Generate suggestion 在没有输入时补一个默认关键字，模拟系统建议反馈。
      if (action === 'suggest' && !demoState.keyword) {
        demoState.keyword = 'Liquid member';
        searchInput.value = demoState.keyword;
      }

      // 动作执行后统一刷新摘要卡，保持动作结果可见。
      renderSummary();
      syncPlaygroundCard();
    });
  });

  // 初始挂载完成后立刻同步一次，保证默认态可见。
  syncPlaygroundCard();
}

// 更新 tab 和左侧导航高亮：让壳层、侧栏和主视图三处状态保持一致。
function updateNavigationState() {
  // 顶部 tab 通过 activeView 高亮当前视图，减少左右区域状态不一致。
  document.querySelectorAll('.seltheme-demo-tab').forEach((tabButton) => {
    tabButton.classList.toggle('is-active', tabButton.dataset.target === demoState.activeView);
  });

  // 左侧导航卡也同步高亮，确保从侧栏点击和顶部切页得到相同反馈。
  document.querySelectorAll('.seltheme-demo-nav').forEach((navButton) => {
    navButton.classList.toggle('is-active', navButton.dataset.target === demoState.activeView);
  });

  // 主区域片段只显示当前目标视图，其余面板全部隐藏。
  document.querySelectorAll('.seltheme-demo-panel').forEach((panel) => {
    panel.classList.toggle('is-active', panel.dataset.view === demoState.activeView);
  });
}

// 切换主视图：统一由这里改变 activeView，避免顶部 tab 和左侧导航各写一套逻辑。
function switchView(nextView) {
  // 当目标视图非法或与当前相同，直接返回，避免无意义重复渲染。
  if (!fragmentPaths[nextView] || demoState.activeView === nextView) {
    return;
  }

  // 更新当前活动视图，供顶部摘要和 tab 状态复用。
  demoState.activeView = nextView;
  renderSummary();
  updateNavigationState();
}

// 绑定壳层导航：顶部 tab 和左侧导航都走统一的数据目标切换。
function bindShellNavigation() {
  // 统一监听所有携带 data-target 的导航按钮，避免分别写多套选择器。
  document.querySelectorAll('[data-target]').forEach((button) => {
    button.addEventListener('click', () => {
      switchView(button.dataset.target);
    });
  });

  // 顶部 Highlight 按钮触发切换到 playground，便于快速看到互动层。
  const highlightButton = document.querySelector('[data-action=\"highlight\"]');
  // 顶部 Reset 按钮负责恢复默认状态。
  const resetButton = document.querySelector('[data-action=\"reset\"]');

  // Highlight 行为直接进入互动实验页，方便立即检查交互结果。
  highlightButton?.addEventListener('click', () => {
    switchView('playground');
  });

  // Reset 行为恢复默认状态并刷新当前视图。
  resetButton?.addEventListener('click', () => {
    demoState.activeView = 'showcase';
    demoState.keyword = '';
    demoState.invite = true;
    demoState.alert = false;
    demoState.depth = true;
    demoState.workspace = true;
    renderSummary();
    updateNavigationState();
    bindPlaygroundInteractions();
  });
}

// 挂载所有片段：把 demo 拆开的 sidebar、showcase、playground 分别注入到壳页容器。
async function mountFragments() {
  // 并行加载 sidebar、showcase、playground 三个片段，缩短首屏等待时间。
  const [sidebarHtml, showcaseHtml, playgroundHtml] = await Promise.all([
    loadFragment('sidebar'),
    loadFragment('showcase'),
    loadFragment('playground'),
  ]);

  // 左侧容器只承接侧栏片段，保持壳层职责单一。
  shellRefs.sidebarSlot.innerHTML = sidebarHtml;
  // 主视图容器按顺序注入展示页和互动页，后续只通过类切换控制显隐。
  shellRefs.viewSlot.innerHTML = `${showcaseHtml}${playgroundHtml}`;
  // 侧栏挂载后立刻同步主题层目录，让 preview 目录和主题包正式结构保持一致。
  renderThemeLayerToc();
}

// 启动 demo：先挂载片段，再渲染摘要，最后绑定导航和互动逻辑。
async function bootstrapDemo() {
  // 完成片段注入后，页面才具备绑定导航和交互所需的 DOM。
  await mountFragments();
  // 初次渲染顶部摘要，让用户一打开页面就能看到当前状态。
  renderSummary();
  // 同步初始导航高亮和默认可见面板。
  updateNavigationState();
  // 绑定左侧导航和顶部 tab 行为。
  bindShellNavigation();
  // 绑定互动实验页内的最小交互闭环。
  bindPlaygroundInteractions();
}

// 启动入口：把所有初始化集中在一个明确的异步流程里。
bootstrapDemo().catch((error) => {
  // 若任一片段加载失败，直接把错误写回页面，避免空白白屏难以判断。
  shellRefs.viewSlot.innerHTML = `<section class=\"seltheme-glass seltheme-panel\"><h3>Demo 加载失败</h3><p class=\"seltheme-copy\">${error.message}</p></section>`;
});
