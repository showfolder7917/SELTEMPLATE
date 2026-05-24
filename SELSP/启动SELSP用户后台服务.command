#!/bin/zsh
set -e

# 锁定 SELSP 工程根目录，保证 gradle 和资源加载都在正确上下文里运行。
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 当前用户管理后端统一监听 8080，脚本也按这个端口进行接管和探测。
PORT=8080
# 用真实用户列表接口判定后端是否已经可服务。
URL="http://127.0.0.1:${PORT}/api/users"
# 把 Spring Boot 启动日志收集到临时文件，便于失败时直接定位。
LOG_FILE="/tmp/selsp_backend.log"
# 保存 bootRun 的进程号，退出时统一回收。
SERVER_PID=""

stop_port_processes() {
  # 先回收占用 8080 的旧后端，避免脚本退出后仍遗留不可控旧服务。
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
  # 若本轮脚本拉起了后端，则关闭终端时连同 bootRun 和其子进程一起停止。
  if [ -n "$SERVER_PID" ]; then
    kill "$SERVER_PID" >/dev/null 2>&1 || true
    pkill -TERM -P "$SERVER_PID" >/dev/null 2>&1 || true
    sleep 1
    pkill -KILL -P "$SERVER_PID" >/dev/null 2>&1 || true
  fi
  # 最后再兜底回收端口，避免下次启动冲突。
  stop_port_processes
}

# 绑定退出钩子，让当前脚本接管后端生命周期。
trap cleanup EXIT INT TERM HUP

echo ""
echo "SELSP 用户后台服务正在启动..."
echo "项目目录: ${SCRIPT_DIR}"
echo "访问地址: ${URL}"
echo ""

# 启动前先清理旧进程，确保当前脚本拥有 8080。
stop_port_processes

# 用 gradle 原生命令拉起 Spring Boot 服务，避免引入额外守护方式。
./gradlew bootRun >"$LOG_FILE" 2>&1 &
SERVER_PID=$!

# 轮询真实接口地址，确认应用和数据层都已完成初始化。
for _ in {1..120}; do
  if curl -fsS "$URL" >/dev/null 2>&1; then
    echo "后端已启动。关闭这个终端窗口时，后端服务会一并停止。"
    wait "$SERVER_PID"
    exit 0
  fi
  sleep 1
done

echo "启动超时：后端未能在 120 秒内连接到 ${URL}"
if [ -f "$LOG_FILE" ]; then
  echo ""
  echo "最近日志："
  tail -n 30 "$LOG_FILE" || true
fi
exit 1
