#!/bin/sh
# ============================================================
#  US090 — Manage Logging Server on Cloud VS
#  Run ON the Cloud VS (Alpine Linux v3.23)
#
#  Usage:
#    ./start-logging-server-vs.sh start
#    ./start-logging-server-vs.sh stop
#    ./start-logging-server-vs.sh restart
#    ./start-logging-server-vs.sh status
#    ./start-logging-server-vs.sh logs
#    ./start-logging-server-vs.sh test
#
#  Or from local machine via SSH:
#    ssh root@vs489.dei.isep.ipp.pt 'bash -s' < start-logging-server-vs.sh start
# ============================================================

REMOTE_DIR="/opt/aisafe-logging-server"
JAR_NAME="aisafe-logging-server-1.0.0.jar"
LOG_FILE="/var/log/aisafe/server.log"
ACCESS_LOG="/var/log/aisafe/remote_accesses.log"

# --- Helper functions ---
get_pid() {
    pgrep -f "$JAR_NAME" 2>/dev/null | head -1
}

is_running() {
    PID=$(get_pid)
    if [ -n "$PID" ] && kill -0 "$PID" 2>/dev/null; then
        return 0
    fi
    return 1
}

show_status() {
    PID=$(get_pid)
    if is_running; then
        echo "  Status: RUNNING (PID: $PID)"
        echo "  Uptime: $(ps -o etime= -p "$PID" 2>/dev/null | tr -d ' ')"
    else
        echo "  Status: STOPPED"
    fi
    
    # Check ports
    if command -v ss >/dev/null 2>&1; then
        ss -tlnp | grep 8080 >/dev/null 2>&1 && echo "  HTTP  :8080  LISTENING" || echo "  HTTP  :8080  NOT LISTENING"
        ss -ulnp | grep 9999 >/dev/null 2>&1 && echo "  UDP   :9999  LISTENING" || echo "  UDP   :9999  NOT LISTENING"
    fi
    
    # Log file sizes
    if [ -f "$ACCESS_LOG" ]; then
        SIZE=$(wc -c < "$ACCESS_LOG")
        LINES=$(wc -l < "$ACCESS_LOG")
        echo "  Access log: $ACCESS_LOG ($LINES entries, $SIZE bytes)"
    else
        echo "  Access log: $ACCESS_LOG (empty)"
    fi
}

# --- Main ---
case "${1:-status}" in
    start)
        if is_running; then
            echo "Logging Server is already running (PID: $(get_pid))"
            exit 0
        fi
        echo "Starting Logging Server..."
        cd "$REMOTE_DIR"
        nohup java -jar "$JAR_NAME" > "$LOG_FILE" 2>&1 &
        sleep 3
        if is_running; then
            echo "  OK: Started (PID: $(get_pid))"
        else
            echo "  ERROR: Failed to start"
            echo "  Check log: tail $LOG_FILE"
            exit 1
        fi
        ;;
    
    stop)
        PID=$(get_pid)
        if [ -z "$PID" ]; then
            echo "Logging Server is not running"
            exit 0
        fi
        echo "Stopping Logging Server (PID: $PID)..."
        kill "$PID" 2>/dev/null
        sleep 2
        if is_running; then
            echo "  Force killing..."
            kill -9 "$PID" 2>/dev/null
        fi
        echo "  OK: Stopped"
        ;;
    
    restart)
        echo "Restarting Logging Server..."
        $0 stop
        sleep 1
        $0 start
        ;;
    
    status)
        echo "============================================"
echo "  US090 — Logging Server Status"
        echo "============================================"
        show_status
        ;;
    
    logs)
        if [ -f "$LOG_FILE" ]; then
            echo "=== Server Log (last 50 lines) ==="
            tail -50 "$LOG_FILE"
        else
            echo "No server log found at $LOG_FILE"
        fi
        ;;
    
    access)
        if [ -f "$ACCESS_LOG" ]; then
            echo "=== Access Log (last 50 entries) ==="
            tail -50 "$ACCESS_LOG"
        else
            echo "No access log found at $ACCESS_LOG"
        fi
        ;;
    
    test)
        echo "=== Testing Logging Server ==="
        
        # Test UDP reception
        TEST_MSG="2026-01-01T00:00:00|testuser|127.0.0.1|54321|US078|LOGIN_SUCCESS"
        echo "Sending test UDP message..."
        echo "$TEST_MSG" | nc -u -w1 localhost 9999 2>/dev/null
        sleep 1
        
        # Check if it was stored
        if [ -f "$ACCESS_LOG" ]; then
            if tail -5 "$ACCESS_LOG" | grep -q "testuser"; then
                echo "  OK: Test message stored in access log"
                tail -1 "$ACCESS_LOG"
            else
                echo "  WARNING: Test message not found in access log"
            fi
        else
            echo "  WARNING: Access log file not created yet"
        fi
        
        # Test HTTP
        echo ""
        echo "Testing HTTP endpoints..."
        if command -v curl >/dev/null 2>&1; then
            HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/logs 2>/dev/null)
            echo "  /api/logs  -> HTTP $HTTP_CODE"
            HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/active 2>/dev/null)
            echo "  /api/active -> HTTP $HTTP_CODE"
        else
            echo "  (curl not installed — skipping HTTP test)"
        fi
        ;;
    
    *)
        echo "Usage: $0 {start|stop|restart|status|logs|access|test}"
        echo ""
        echo "Commands:"
        echo "  start    — Start the Logging Server"
        echo "  stop     — Stop the Logging Server"
        echo "  restart  — Restart the Logging Server"
        echo "  status   — Show server status and port info"
        echo "  logs     — Show last 50 lines of server log"
        echo "  access   — Show last 50 access log entries"
        echo "  test     — Send test UDP message and verify"
        exit 1
        ;;
esac
