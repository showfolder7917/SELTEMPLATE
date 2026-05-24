#!/bin/zsh
set -e

# 先计算前端工程目录，用于拼出前后端脚本的稳定相对路径。
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# SELVUE 所在的 SELTEMPLATE 目录，便于联动 SELSP 后端工程。
SELTEMPLATE_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
# 后端命令脚本统一放在 SELSP 工程根目录，由本全套脚本调起。
BACKEND_SCRIPT="${SELTEMPLATE_DIR}/SELSP/启动SELSP用户后台服务.command"
# 前端命令脚本复用本工程的单独启动脚本，避免逻辑重复。
FRONTEND_SCRIPT="${SCRIPT_DIR}/打开SELVUE用户管理台.command"
# 以后端真实接口地址作为服务就绪判定，确保前端不是在空后端上打开。
BACKEND_URL="http://127.0.0.1:8080/api/users"
# 前端页面地址用于最终页面就绪校验。
FRONTEND_URL="http://127.0.0.1:5176/"
# 记录后台脚本包装进程，方便退出时统一停止。
BACKEND_WRAPPER_PID=""
# 记录前台脚本包装进程，方便退出时统一停止。
FRONTEND_WRAPPER_PID=""

stop_wrapper() {
  # 通过包装进程 PID 回收由当前脚本拉起的子脚本。
  local pid="$1"
  if [ -z "$pid" ]; then
    return
  fi
  if ps -p "$pid" >/dev/null 2>&1; then
    kill -TERM "$pid" >/dev/null 2>&1 || true
    wait "$pid" 2>/dev/null || true
  fi
  if ps -p "$pid" >/dev/null 2>&1; then
    kill -KILL "$pid" >/dev/null 2>&1 || true
    wait "$pid" 2>/dev/null || true
  fi
}

wait_for_url() {
  # 统一轮询服务地址，确保按依赖顺序启动并反馈明确结果。
  local url="$1"
  local label="$2"
  local retries="${3:-90}"
  for _ in $(seq 1 "$retries"); do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "${label} 已就绪：${url}"
      return 0
    fi
    sleep 1
  done
  echo "${label} 启动超时：${url}"
  return 1
}

cleanup() {
  # 关闭窗口时，按前端后端顺序停止本轮接管的服务。
  echo ""
  echo "正在停止 SELVUE 用户管理全套服务..."
  stop_wrapper "$FRONTEND_WRAPPER_PID"
  stop_wrapper "$BACKEND_WRAPPER_PID"
}

# 绑定退出钩子，确保一键启动器真正拥有整个启动链生命周期。
trap cleanup EXIT INT TERM HUP

echo ""
echo "正在一键启动 SELVUE 用户管理全套服务..."
echo "后端脚本: ${BACKEND_SCRIPT}"
echo "前端脚本: ${FRONTEND_SCRIPT}"
echo ""

# 先启动后端并等待接口可访问，避免前端打开后立即进入本地回退模式。
"$BACKEND_SCRIPT" &
BACKEND_WRAPPER_PID=$!
wait_for_url "$BACKEND_URL" "SELSP 用户后台服务" 120

# 后端就绪后再启动前端，并等待真实页面地址可访问。
"$FRONTEND_SCRIPT" &
FRONTEND_WRAPPER_PID=$!
wait_for_url "$FRONTEND_URL" "SELVUE 用户管理台" 90

echo "前后端已按依赖顺序启动完成。关闭这个窗口后，两套服务会一并停止。"

# 保持当前脚本驻留，持续托管前后端子脚本生命周期。
wait "$BACKEND_WRAPPER_PID" "$FRONTEND_WRAPPER_PID"
