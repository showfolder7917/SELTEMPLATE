#!/bin/zsh
set -e

# 计算脚本所在目录，确保从任意位置双击都能回到 SELVUE 工程根目录。
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# 明确切换到前端工程目录，保证 npm 和 vite 都使用当前项目上下文。
cd "$SCRIPT_DIR"

# 统一固定本地前端端口，和工程内 dev:local 脚本保持一致。
PORT=5176
# 启动成功后对外展示的访问地址。
URL="http://127.0.0.1:${PORT}/"
# 把 vite 输出收进临时日志，便于启动失败时回看最近错误。
LOG_FILE="/tmp/selfvue_admin.log"
# 用于在脚本退出时回收本次启动的前端进程。
SERVER_PID=""

stop_port_processes() {
  # 先找出当前端口上遗留的旧前端进程，避免复用旧服务导致生命周期失控。
  local pids
  pids="$(/usr/sbin/lsof -ti tcp:${PORT} 2>/dev/null || true)"
  if [ -n "$pids" ]; then
    echo "检测到 ${PORT} 端口已有旧进程，先停止后再由当前脚本接管..."
    for pid in $pids; do
      kill "$pid" >/dev/null 2>&1 || true
    done
    sleep 1
    pids="$(/usr/sbin/lsof -ti tcp:${PORT} 2>/dev/null || true)"
    if [ -n "$pids" ]; then
      for pid in $pids; do
        kill -9 "$pid" >/dev/null 2>&1 || true
      done
    fi
  fi
}

cleanup() {
  # 若当前脚本已经拉起了 vite，则关闭窗口时同步停止它和它的子进程。
  if [ -n "$SERVER_PID" ]; then
    kill "$SERVER_PID" >/dev/null 2>&1 || true
    pkill -TERM -P "$SERVER_PID" >/dev/null 2>&1 || true
    sleep 1
    pkill -KILL -P "$SERVER_PID" >/dev/null 2>&1 || true
  fi
  # 再次回收端口，避免下次双击时还残留旧进程。
  stop_port_processes
}

# 绑定退出钩子，让脚本真正接管前端生命周期。
trap cleanup EXIT INT TERM HUP

# 首次运行若依赖还没装，则先自动安装，避免用户手工补环境。
if [ ! -d "node_modules" ]; then
  echo "正在安装 SELVUE 前端依赖..."
  npm install
fi

echo ""
echo "SELVUE 用户管理台正在启动..."
echo "项目目录: ${SCRIPT_DIR}"
echo "打开地址: ${URL}"
echo ""

# 启动前先清掉旧端口，确保当前脚本是唯一服务拥有者。
stop_port_processes

# 正式启动 vite 本地服务，并把输出写入日志文件。
npm run dev:local >"$LOG_FILE" 2>&1 &
SERVER_PID=$!

# 循环探测页面地址，确保浏览器打开的是已可访问的真实页面。
for _ in {1..60}; do
  if curl -fsS "$URL" >/dev/null 2>&1; then
    open "$URL"
    echo "前端已启动。关闭这个终端窗口时，前端服务会一并停止。"
    wait "$SERVER_PID"
    exit 0
  fi
  sleep 1
done

echo "启动超时：浏览器未能在 60 秒内连接到 ${URL}"
if [ -f "$LOG_FILE" ]; then
  echo ""
  echo "最近日志："
  tail -n 20 "$LOG_FILE" || true
fi
exit 1
