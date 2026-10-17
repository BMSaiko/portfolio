#!/bin/sh
# ============================================================
#  US090 — Cloud VS Setup Script (Alpine Linux v3.23)
#  Run ONCE on the Cloud VS via SSH
#
#  Usage:
#    ssh root@vs489.dei.isep.ipp.pt 'bash -s' < setup-cloud-vs.sh
#
#  Or copy to VS first:
#    scp setup-cloud-vs.sh root@vs489.dei.isep.ipp.pt:/tmp/
#    ssh root@vs489.dei.isep.ipp.pt 'sh /tmp/setup-cloud-vs.sh'
#
#  SSH Authentication:
#    This script sets up SSH key-based authentication.
#    You need to provide your RSA public key as $SSH_PUB_KEY.
#    If not provided, password auth will be used (get password
#    from DEI Cloud Web Interface).
# ============================================================

set -e

echo "============================================"
echo "  US090 — Cloud VS Setup (Alpine Linux v3.23)"
echo "============================================"

# --- 1. Update package index ---
echo "[1/7] Updating package index..."
apk update

# --- 2. Install Java 21 ---
echo "[2/7] Installing OpenJDK 21..."
apk add openjdk21
java -version

# --- 3. Create directories ---
echo "[3/7] Creating directories..."
mkdir -p /var/log/aisafe
mkdir -p /opt/aisafe-logging-server

# --- 4. Set permissions ---
echo "[4/7] Setting permissions..."
chmod 755 /var/log/aisafe
chmod 755 /opt/aisafe-logging-server

# --- 5. SSH Key Setup (optional) ---
echo "[5/7] SSH configuration..."
if [ -n "$SSH_PUB_KEY" ]; then
    mkdir -p /root/.ssh
    chmod 700 /root/.ssh
    echo "$SSH_PUB_KEY" >> /root/.ssh/authorized_keys
    chmod 600 /root/.ssh/authorized_keys
    echo "  OK: SSH public key installed"
    echo "  Connect from local: ssh -i ~/.ssh/id_rsa root@$(hostname)"
else
    echo "  No SSH_PUB_KEY provided — using password authentication"
    echo "  Get root password from DEI Cloud Web Interface"
    echo "  WARNING: Password resets on every VS boot!"
fi

# --- 6. Verify network ---
echo "[6/7] Network configuration..."
echo "  Hostname: $(hostname)"
echo "  IP addresses:"
ip addr show eth0 2>/dev/null | grep 'inet ' || echo "  (eth0 not found — check network)"

# --- 7. Verify SSH ---
echo "[7/7] SSH service status..."
if pgrep -x sshd > /dev/null 2>&1; then
    echo "  SSH daemon: running"
else
    echo "  SSH daemon: NOT running (may need to be enabled via Cloud panel)"
fi

echo ""
echo "============================================"
echo "  Setup complete!"
echo "============================================"
echo ""
echo "Directories created:"
echo "  /var/log/aisafe           — log files"
echo "  /opt/aisafe-logging-server — JAR deployment"
echo ""
echo "SSH Access:"
echo "  ssh root@$(hostname)"
echo "  ssh root@$(hostname 2>/dev/null || echo 'vs489.dei.isep.ipp.pt')"
echo ""
echo "Next steps:"
echo "  1. Deploy JAR from local machine:"
echo "     ./scripts/deploy-to-cloud.sh"
echo ""
echo "  2. Or manually:"
echo "     scp target/aisafe-logging-server-1.0.0.jar root@$(hostname):/opt/aisafe-logging-server/"
echo "     ssh root@$(hostname) 'cd /opt/aisafe-logging-server && nohup java -jar aisafe-logging-server-1.0.0.jar > /var/log/aisafe/server.log 2>&1 &'"
echo ""
echo "  3. Open firewall ports via DEI Cloud Web Interface:"
echo "     UDP 9999 — Logging events"
echo "     TCP 8080 — HTTP visualization"
