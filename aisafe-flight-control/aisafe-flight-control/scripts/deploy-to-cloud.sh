#!/bin/sh
# ============================================================
#  US090 — Deploy Logging Server to Cloud VS
#  Run from LOCAL development machine (aisafe-flight-control dir)
#
#  Prerequisites:
#    - Maven installed locally
#    - SSH access to Cloud VS (key-based or password)
#    - Cloud VS has been set up (run setup-cloud-vs.sh first)
#
#  Usage:
#    ./scripts/deploy-to-cloud.sh
#    SSH_KEY_PATH=~/.ssh/id_rsa_aisafe ./scripts/deploy-to-cloud.sh
#
#  SSH Authentication:
#    Set SSH_KEY_PATH env var to use a specific private key.
#    If not set, uses default SSH agent / ~/.ssh/id_rsa.
#    Password auth also works (get password from DEI Cloud panel).
# ============================================================

set -e

# --- Configuration ---
VS_HOST="vs489.dei.isep.ipp.pt"
VS_USER="root"
VS_SSH_PORT=22
REMOTE_DIR="/opt/aisafe-logging-server"
JAR_NAME="aisafe-logging-server-1.0.0.jar"

# --- SSH Key (optional) ---
# Set SSH_KEY_PATH to use a specific private key
# Example: SSH_KEY_PATH="$HOME/.ssh/id_rsa_aisafe"
SSH_KEY_PATH="${SSH_KEY_PATH:-}"
SSH_OPTS=""
SCP_OPTS=""
if [ -n "$SSH_KEY_PATH" ] && [ -f "$SSH_KEY_PATH" ]; then
    SSH_OPTS="-i $SSH_KEY_PATH"
    SCP_OPTS="-i $SSH_KEY_PATH"
fi

# --- Resolve paths ---
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
PARENT_DIR="$(dirname "$PROJECT_DIR")"
LOGGING_SERVER_DIR="$PARENT_DIR/aisafe-logging-server"

echo "============================================"
echo "  US090 — Deploy to Cloud VS"
echo "============================================"
echo "  VS:       $VS_HOST"
echo "  SSH port: $VS_SSH_PORT"
echo "  JAR:      $JAR_NAME"
echo ""

# --- Pre-flight checks ---
if [ ! -f "$LOGGING_SERVER_DIR/pom.xml" ]; then
    echo "ERROR: aisafe-logging-server not found at: $LOGGING_SERVER_DIR"
    echo "Make sure aisafe-logging-server is alongside aisafe-flight-control"
    exit 1
fi

# --- 1. Build the Logging Server JAR ---
echo "[1/3] Building Logging Server JAR..."
cd "$LOGGING_SERVER_DIR"
mvn clean package -DskipTests -q
if [ $? -ne 0 ]; then
    echo "ERROR: Maven build failed"
    exit 1
fi
echo "  OK: $JAR_NAME built"

# --- 2. Copy JAR to Cloud VS ---
echo "[2/3] Copying JAR to Cloud VS..."
scp $SCP_OPTS -P $VS_SSH_PORT target/$JAR_NAME $VS_USER@$VS_HOST:$REMOTE_DIR/
if [ $? -ne 0 ]; then
    echo "ERROR: SCP failed — check SSH connectivity to $VS_HOST:$VS_SSH_PORT"
    exit 1
fi
echo "  OK: JAR copied to $VS_HOST:$REMOTE_DIR/"

# --- 3. Restart the Logging Server on VS ---
echo "[3/3] Restarting Logging Server on VS..."
ssh $SSH_OPTS -p $VS_SSH_PORT $VS_USER@$VS_HOST << 'REMOTE_SCRIPT'
    cd /opt/aisafe-logging-server
    
    # Kill existing instance
    pkill -f "aisafe-logging-server" 2>/dev/null || true
    sleep 1
    
    # Start new instance
    nohup java -jar aisafe-logging-server-1.0.0.jar > /var/log/aisafe/server.log 2>&1 &
    sleep 3
    
    # Verify process is running
    if pgrep -f "aisafe-logging-server" > /dev/null 2>&1; then
        PID=$(pgrep -f "aisafe-logging-server" | head -1)
        echo "  OK: Logging Server started (PID: $PID)"
    else
        echo "  ERROR: Failed to start Logging Server"
        echo "  Server log:"
        cat /var/log/aisafe/server.log 2>/dev/null || echo "  (no log file)"
        exit 1
    fi
    
    # Show listening ports
    echo "  Listening ports:"
    ss -tlnp | grep 8080 && echo "    HTTP  :8080 OK" || echo "    HTTP  :8080 NOT LISTENING"
    ss -ulnp | grep 9999 && echo "    UDP   :9999 OK" || echo "    UDP   :9999 NOT LISTENING"
REMOTE_SCRIPT

echo ""
echo "============================================"
echo "  Deployment complete!"
echo "============================================"
echo ""
echo "  Cloud Logging Server:"
echo "    UDP:   $VS_HOST:9999"
echo "    HTTP:  http://$VS_HOST:8080"
echo "    Logs:  ssh $SSH_OPTS -p $VS_SSH_PORT $VS_USER@$VS_HOST 'tail -f /var/log/aisafe/remote_accesses.log'"
echo ""
echo "  To test UDP reception:"
echo "    ssh $SSH_OPTS -p $VS_SSH_PORT $VS_USER@$VS_HOST"
echo "    echo \"2026-01-01T00:00:00|test|10.9.0.1|54321|US078|LOGIN_SUCCESS\" | nc -u localhost 9999"
echo "    tail -1 /var/log/aisafe/remote_accesses.log"
echo ""
echo "  To run local TCP Server with cloud logging:"
echo "    ./scripts/run-cloud.sh"
