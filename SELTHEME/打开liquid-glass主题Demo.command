#!/bin/zsh
set -e

# 计算脚本所在目录，保证双击后也能回到 SELTHEME 工程根目录。
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
# 切到主题工程根目录，确保静态服务从正确目录暴露 themes 路径。
cd "$SCRIPT_DIR"

# 固定 demo 静态服务端口，便于浏览器地址和后续验证保持稳定。
PORT=8790
# 对外访问地址直接指向正式互动 demo。
URL="http://127.0.0.1:${PORT}/themes/liquid-glass/preview/demo.html"
# 静态服务日志统一写到临时目录，启动失败时可直接回看。
LOG_FILE="/tmp/seltheme_liquid_glass_demo.log"
# 用于在脚本退出时回收本轮拉起的静态服务。
SERVER_PID=""

stop_port_processes() {
  # 若端口上已有旧服务，先停止，避免复用旧进程导致脚本无法托管生命周期。
  local pids
  pids="$(/usr/sbin/lsof -ti tcp:${PORT} 2>/dev/null || true)"
  if [ -n "$pids" ]; then
    echo "检测到 ${PORT} 端口已有旧进程，先停止后由当前脚本接管..."
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
  # 若本轮已拉起静态服务，则在终端关闭时一并停止它和子进程。
  if [ -n "$SERVER_PID" ]; then
    kill "$SERVER_PID" >/dev/null 2>&1 || true
    pkill -TERM -P "$SERVER_PID" >/dev/null 2>&1 || true
    sleep 1
    pkill -KILL -P "$SERVER_PID" >/dev/null 2>&1 || true
  fi
  # 再收一次端口，避免下次双击时残留旧服务。
  stop_port_processes
}

# 绑定退出钩子，让脚本真正托管静态服务生命周期。
trap cleanup EXIT INT TERM HUP

echo ""
echo "liquid-glass 主题 Demo 正在启动..."
echo "项目目录: ${SCRIPT_DIR}"
echo "打开地址: ${URL}"
echo ""

# 启动前先清理旧端口，保证当前脚本是唯一服务拥有者。
stop_port_processes

# 通过 Python 静态服务暴露 SELTHEME 根目录，让 fragments 和 assets 都能被正常加载。
python3 -m http.server "${PORT}" --directory "$SCRIPT_DIR" >"$LOG_FILE" 2>&1 &
SERVER_PID=$!

# 循环探测正式 demo 地址，确保打开的是已经可访问的互动页面。
for _ in {1..60}; do
  if curl -fsS "$URL" >/dev/null 2>&1; then
    # 正常双击场景下直接打开浏览器；测试场景可通过 SKIP_OPEN=1 跳过 GUI。
    if [ "${SKIP_OPEN:-0}" = "1" ]; then
      echo "检测到 SKIP_OPEN=1，已跳过浏览器打开。"
    else
      open "$URL"
    fi
    echo "主题 demo 已启动。关闭这个终端窗口时，静态服务会一并停止。"
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
