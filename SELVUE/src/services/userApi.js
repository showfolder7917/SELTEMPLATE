// 统一复用底层请求包装，保持后端联调和错误处理一致。
import { requestJson } from './request'

// 本地缓存键用于在后端离线时维持可演示的用户数据源。
const LOCAL_STORAGE_KEY = 'selfvue-user-directory'

// 默认种子数据用于冷启动时快速生成可见页面。
const DEFAULT_USERS = [
  { id: 1, name: 'Luna Chen', email: 'luna.chen@selfvue.ai', status: 'ACTIVE', createdAt: '2026-05-01 10:12:00', updatedAt: '2026-05-23 09:48:00' },
  { id: 2, name: 'Mika Sato', email: 'mika.sato@selfvue.ai', status: 'ACTIVE', createdAt: '2026-05-03 14:20:00', updatedAt: '2026-05-22 11:16:00' },
  { id: 3, name: 'River Tan', email: 'river.tan@selfvue.ai', status: 'DISABLED', createdAt: '2026-05-05 17:30:00', updatedAt: '2026-05-20 16:04:00' },
  { id: 4, name: 'Aster Li', email: 'aster.li@selfvue.ai', status: 'ACTIVE', createdAt: '2026-05-10 09:08:00', updatedAt: '2026-05-23 15:26:00' }
]

/**
 * 获取用户列表。
 *
 * @param {{ keyword: string, status: string }} filters 页面筛选条件
 * @returns {Promise<{ mode: string, records: object[] }>} 数据来源模式和用户列表
 */
export async function listUsers(filters) {
  try {
    // 联调模式优先命中正式后端接口，保证后端在线时直接读真实数据。
    const params = new URLSearchParams()
    if (filters.keyword) {
      params.set('name', filters.keyword)
    }
    if (filters.status && filters.status !== 'all') {
      params.set('status', filters.status)
    }
    const query = params.toString()
    const records = await requestJson(`/api/users${query ? `?${query}` : ''}`)
    return { mode: 'remote', records: normalizeUsers(records || []) }
  } catch (error) {
    // 后端不可达时立即回退本地缓存，保证页面和 CRUD 主流程仍可预览。
    return { mode: 'local', records: queryLocalUsers(filters) }
  }
}

/**
 * 创建用户。
 *
 * @param {{ name: string, email: string, status: string }} payload 表单载荷
 * @returns {Promise<{ mode: string, record: object }>} 数据来源模式和新用户
 */
export async function createUser(payload) {
  try {
    // 联调模式把新增动作提交到正式接口。
    const record = await requestJson('/api/users', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
    return { mode: 'remote', record: normalizeUser(record) }
  } catch (error) {
    // 本地回退模式在 localStorage 中补一条记录。
    return { mode: 'local', record: createLocalUser(payload) }
  }
}

/**
 * 更新用户。
 *
 * @param {number} id 用户主键
 * @param {{ name: string, email: string, status: string }} payload 更新载荷
 * @returns {Promise<{ mode: string, record: object }>} 数据来源模式和更新结果
 */
export async function updateUser(id, payload) {
  try {
    // 联调模式把编辑动作写回正式接口。
    const record = await requestJson(`/api/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload)
    })
    return { mode: 'remote', record: normalizeUser(record) }
  } catch (error) {
    // 本地回退模式在缓存中更新用户，保证演示链路闭环。
    return { mode: 'local', record: updateLocalUser(id, payload) }
  }
}

/**
 * 删除用户。
 *
 * @param {number} id 用户主键
 * @returns {Promise<{ mode: string }>} 数据来源模式
 */
export async function deleteUser(id) {
  try {
    // 联调模式优先删除正式后端记录。
    await requestJson(`/api/users/${id}`, {
      method: 'DELETE'
    })
    return { mode: 'remote' }
  } catch (error) {
    // 本地回退模式从缓存中删除记录。
    deleteLocalUser(id)
    return { mode: 'local' }
  }
}

/**
 * 读取并规范化本地用户列表。
 *
 * @returns {object[]} 本地用户
 */
function readLocalUsers() {
  // 本地缓存不存在时先种一份默认数据，保证页面首次打开就有内容。
  const raw = window.localStorage.getItem(LOCAL_STORAGE_KEY)
  if (!raw) {
    window.localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(DEFAULT_USERS))
    return normalizeUsers(DEFAULT_USERS)
  }
  // 历史缓存读取后统一做字段规范化，避免结构漂移影响页面。
  return normalizeUsers(JSON.parse(raw))
}

/**
 * 写回本地用户列表。
 *
 * @param {object[]} users 最新用户列表
 */
function writeLocalUsers(users) {
  // 每次本地 CRUD 后都统一持久化，保证刷新后状态还原。
  window.localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(users))
}

/**
 * 查询本地用户。
 *
 * @param {{ keyword: string, status: string }} filters 页面筛选条件
 * @returns {object[]} 过滤后的本地用户
 */
function queryLocalUsers(filters) {
  // 先拿完整列表，再按关键字和状态做页面侧过滤。
  return readLocalUsers()
    .filter((record) => {
      if (!filters.keyword) {
        return true
      }
      const keyword = filters.keyword.toLowerCase()
      return record.name.toLowerCase().includes(keyword) || record.email.toLowerCase().includes(keyword)
    })
    .filter((record) => filters.status === 'all' || !filters.status ? true : record.status === filters.status)
    .sort((left, right) => Number(right.id) - Number(left.id))
}

/**
 * 在本地创建用户。
 *
 * @param {{ name: string, email: string, status: string }} payload 创建载荷
 * @returns {object} 新用户
 */
function createLocalUser(payload) {
  // 通过已有最大主键生成新的本地主键，保持和后端自增语义相近。
  const users = readLocalUsers()
  const nextId = users.reduce((maxId, item) => Math.max(maxId, Number(item.id)), 0) + 1
  const now = formatNow()
  const created = normalizeUser({
    id: nextId,
    ...payload,
    createdAt: now,
    updatedAt: now
  })
  // 新纪录写到列表前部，和后端倒序结果保持一致。
  writeLocalUsers([created, ...users])
  return created
}

/**
 * 在本地更新用户。
 *
 * @param {number} id 用户主键
 * @param {{ name: string, email: string, status: string }} payload 更新载荷
 * @returns {object} 更新后的用户
 */
function updateLocalUser(id, payload) {
  // 遍历本地列表替换目标项，并更新更新时间。
  const users = readLocalUsers().map((record) => {
    if (Number(record.id) !== Number(id)) {
      return record
    }
    return normalizeUser({
      ...record,
      ...payload,
      updatedAt: formatNow()
    })
  })
  writeLocalUsers(users)
  return users.find((record) => Number(record.id) === Number(id))
}

/**
 * 在本地删除用户。
 *
 * @param {number} id 用户主键
 */
function deleteLocalUser(id) {
  // 删除时只保留非目标记录，避免页面层再做重复过滤。
  const nextUsers = readLocalUsers().filter((record) => Number(record.id) !== Number(id))
  writeLocalUsers(nextUsers)
}

/**
 * 批量规范化用户数据。
 *
 * @param {any[]} records 原始列表
 * @returns {object[]} 规范化后的列表
 */
function normalizeUsers(records) {
  // 所有列表项统一走单条规范化逻辑，保证展示字段和状态派生一致。
  return records.map((record) => normalizeUser(record))
}

/**
 * 规范化单条用户数据。
 *
 * @param {any} record 原始用户
 * @returns {object} 规范化后的用户
 */
function normalizeUser(record) {
  // 统一把后端状态或本地状态映射成可直接显示的标签与色调。
  const status = record?.status === 'DISABLED' ? 'DISABLED' : 'ACTIVE'
  return {
    id: Number(record?.id),
    name: String(record?.name || ''),
    email: String(record?.email || ''),
    status,
    statusLabel: status === 'ACTIVE' ? '启用中' : '已停用',
    statusTone: status === 'ACTIVE' ? 'active' : 'disabled',
    createdAt: String(record?.createdAt || ''),
    updatedAt: String(record?.updatedAt || ''),
    joinedLabel: String(record?.createdAt || '').slice(0, 10) || '--'
  }
}

/**
 * 生成当前时间字符串。
 *
 * @returns {string} 时间文案
 */
function formatNow() {
  // 使用本地时间生成和后端接近的时间格式，保持两种模式的视觉统一。
  return new Date().toLocaleString('sv-SE').replace('T', ' ')
}
