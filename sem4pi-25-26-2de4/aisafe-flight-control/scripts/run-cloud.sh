#!/bin/sh
# ============================================================
#  US078 & US090 — Run with CLOUD Logging Server
#  For Linux/Mac development machines
#
#  Starts the local TCP Server (US078) configured to send
#  UDP log events to the Cloud VS, then starts the TCP Client.
#
#  Prerequisites:
#    - Cloud VS Logging Server is running (deploy-to-cloud.sh)
#    - Maven, Java 21 installed locally
#
#  Usage:
#    ./scripts/run-cloud.sh
#
#  Configuration:
#    Override CLOUD_VS_HOST / CLOUD_VS_IP to point to a different VS
#    Set SSH_KEY_PATH to use a specific SSH private key
#
#  Usage:
#    ./scripts/run-cloud.sh
#    SSH_KEY_PATH=~/.ssh/id_rsa_aisafe ./scripts/run-cloud.sh
#    CLOUD_VS_IP=10.9.0.50 ./scripts/run-cloud.sh
# ============================================================

set -e

# --- Configuration ---
CLOUD_VS_HOST="${CLOUD_VS_HOST:-vs489.dei.isep.ipp.pt}"
CLOUD_VS_IP="${CLOUD_VS_IP:-10.9.21.233}"
LOGGING_UDP_PORT=9999
LOGGING_HTTP_PORT=8080

# --- SSH Key (optional) ---
SSH_KEY_PATH="${SSH_KEY_PATH:-}"
SSH_OPTS=""
if [ -n "$SSH_KEY_PATH" ] && [ -f "$SSH_KEY_PATH" ]; then
    SSH_OPTS="-i $SSH_KEY_PATH"
fi

# --- Resolve paths ---
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "============================================"
echo "  US078 & US090 — Cloud Mode"
echo "============================================"
echo "  Logging Server: $CLOUD_VS_HOST"
echo "    UDP  : $CLOUD_VS_IP:$LOGGING_UDP_PORT"
echo "    HTTP : http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT"
echo "  TCP Server:     localhost:2002"
echo ""

# --- Check if Cloud Logging Server is reachable ---
echo "[1/3] Checking Cloud Logging Server..."
HTTP_OK=0
if command -v curl >/dev/null 2>&1; then
    if curl -s --connect-timeout 5 "http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/api/logs" >/dev/null 2>&1; then
        HTTP_OK=1
    fi
elif command -v wget >/dev/null 2>&1; then
    if wget -q --timeout=5 -O /dev/null "http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/api/logs" 2>/dev/null; then
        HTTP_OK=1
    fi
fi

if [ "$HTTP_OK" -eq 1 ]; then
    echo "  OK: Cloud Logging Server is reachable"
else
    echo "  WARNING: Cannot reach Cloud Logging Server at $CLOUD_VS_IP:$LOGGING_HTTP_PORT"
    echo ""
    echo "  Make sure the server is running on the Cloud VS:"
    echo "    ssh $SSH_OPTS root@$CLOUD_VS_HOST"
    echo "    cd /opt/aisafe-logging-server"
    echo "    nohup java -jar aisafe-logging-server-1.0.0.jar > /var/log/aisafe/server.log 2>&1 &"
    echo ""
    printf "  Continue anyway? (y/n) "
    read -r REPLY
    echo ""
    if [ "$REPLY" != "y" ] && [ "$REPLY" != "Y" ]; then
        echo "Aborted."
        exit 1
    fi
fi

# --- Cleanup function ---
cleanup() {
    echo ""
    echo "Cleaning up..."
    if [ -n "$SERVER_PID" ] && kill -0 "$SERVER_PID" 2>/dev/null; then
        kill "$SERVER_PID" 2>/dev/null
        wait "$SERVER_PID" 2>/dev/null
    fi
    echo "Done."
}
trap cleanup EXIT INT TERM

# --- Start TCP Server with cloud logging ---
echo "[2/3] Starting US078 TCP Server (cloud logging to $CLOUD_VS_IP)..."
cd "$PROJECT_DIR"
mvn exec:java -pl aisafe.app.server -Dlogging.host="$CLOUD_VS_IP" -q &
SERVER_PID=$!

# Wait for port 2002
echo "  Waiting for TCP port 2002..."
PORT_READY=0
for i in $(seq 1 60); do
    if command -v ss >/dev/null 2>&1; then
        if ss -tlnp | grep -q ":2002 "; then
            PORT_READY=1
            break
        fi
    elif command -v netstat >/dev/null 2>&1; then
        if netstat -tlnp 2>/dev/null | grep -q ":2002 "; then
            PORT_READY=1
            break
        fi
    fi
    sleep 1
done

if [ "$PORT_READY" -ne 1 ]; then
    echo "  ERROR: Timeout waiting for TCP port 2002"
    exit 1
fi
echo "  OK: TCP Server ready on port 2002"

# --- Open browser ---
echo "[3/3] Opening browser..."
if command -v xdg-open >/dev/null 2>&1; then
    xdg-open "http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/" 2>/dev/null &
elif command -v open >/dev/null 2>&1; then
    open "http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/" 2>/dev/null &
else
    echo "  (no browser opener found — open manually)"
fi

echo ""
echo "============================================"
echo "  Cloud mode active!"
echo ""
echo "  Logs:     http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/"
echo "  Sessions: http://$CLOUD_VS_IP:$LOGGING_HTTP_PORT/active"
echo ""
echo "  Press Ctrl+C to stop"
echo "============================================"
echo ""

# --- Run client in foreground ---
cd "$PROJECT_DIR"
mvn exec:java -pl aisafe.app.company.console
