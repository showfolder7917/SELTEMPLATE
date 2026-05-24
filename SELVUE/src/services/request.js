// 默认基地址改为相对路径，让开发态优先走 Vite 代理，避免跨端口联调被浏览器 CORS 拦截。
const DEFAULT_API_BASE_URL = ''
// 超时设置用于在后端离线时快速触发本地回退，而不是长时间等待。
const REQUEST_TIMEOUT_MS = 5000

/**
 * 执行标准 JSON 请求。
 *
 * @param {string} path 接口路径
 * @param {RequestInit} [options] 请求参数
 * @returns {Promise<any>} 解析后的结果
 */
export async function requestJson(path, options = {}) {
  // 允许通过环境变量覆盖联调地址；未覆盖时优先走相对路径代理，而不是直接跨域打 8080。
  const baseUrl = import.meta.env.VITE_API_BASE_URL || DEFAULT_API_BASE_URL
  // 每次请求都创建独立 AbortController，便于超时主动终止。
  const controller = new AbortController()
  // 开启超时计时器，防止接口无响应拖慢页面。
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  try {
    // 发起正式请求，并统一附带 JSON 头和超时信号。
    const response = await fetch(`${baseUrl}${path}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      },
      ...options,
      signal: controller.signal
    })
    // 非 2xx 直接抛错，避免页面把失败响应误当成功。
    if (!response.ok) {
      throw new Error(`请求失败：${response.status} ${response.statusText}`)
    }
    // 用户接口统一按 JSON 解析。
    const payload = await response.json()
    // 兼容后端 CommonResponse 响应壳。
    if (typeof payload?.code !== 'undefined' && payload.code !== 0) {
      throw new Error(payload.message || '接口返回失败')
    }
    // 优先返回 data，保证服务层消费结构统一。
    return typeof payload?.data !== 'undefined' ? payload.data : payload
  } finally {
    // 请求结束后清理超时计时器，避免遗留无效任务。
    window.clearTimeout(timeoutId)
  }
}
