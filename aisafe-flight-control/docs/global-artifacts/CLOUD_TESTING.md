# US090 — Cloud Testing Guide

## Overview

This guide covers testing the US090 External Logging system with the Logging Server deployed on a DEI Cloud Virtual Server (Alpine Linux v3.23).

**🔒 Accessing from home?** See [`CLOUD_VPN_ACCESS.md`](CLOUD_VPN_ACCESS.md) for VPN setup (SoftEther/SSTP/OpenVPN) — the secure way to access the Cloud VS without enabling External SSH.

**Architecture:**

```
Local Machine (Windows/Linux/Mac)          Cloud VS (Alpine Linux v3.23)
┌────────────────────────────┐              ┌────────────────────────────┐
│  Air Transport Company App │   TCP :2002  │                            │
│  (TCP Client)              │◄────────────►│  AtccTcpServer (US078)     │
│                            │              │  - Auth, operations        │
│                            │   UDP :9999  │  - Sends UDP log events    │
│                            │─────────────►│                            │
│                            │              │  RemoteAccessesLogging     │
│                            │   HTTP :8080 │  Server (US090)            │
│                            │◄────────────►│  - UDP receiver            │
│                            │              │  - Log file storage        │
│                            │              │  - HTTP visualization      │
└────────────────────────────┘              └────────────────────────────┘
```

## Quick Start (Windows Workflow)

Run the complete cloud workflow with one command:
```cmd
cd aisafe-flight-control
scripts\run-cloud-workflow.bat
```

This will:
1. Detect VPN connection
2. Build all modules
3. Deploy JAR to Cloud VS
4. Start TCP Server (cloud logging)
5. Start TCP Client (interactive login)
6. Open browser dashboard (via SSH tunnel)

**To stop everything:**
```cmd
scripts\cleanup-all.bat
```

## Prerequisites

### Cloud VS
- **VS:** vs489.dei.isep.ipp.pt (Template #127, Alpine Linux v3.23)
- **Network:** VNET1 (10.9.0.0/16), static IPv4 via DHCP
- **SSH:** `ssh root@vs489.dei.isep.ipp.pt` (port 22 internal, 10489 external)
- **Java 21:** `apk add openjdk21`
- **Firewall ports:** UDP 9999, TCP 8080 (open via DEI Cloud Web Interface)

### Local Machine
- Java 21, Maven 3.9+
- SSH client (OpenSSH, PuTTY, or Git Bash)
- `scp` for file transfer

---

## Phase 1: Cloud VS Setup (One-Time)
### 1.2 Configure SSH Authentication

The Cloud VS supports two authentication methods. **Key-based is strongly recommended** because the root password resets on every VS boot.

#### Option A: SSH Key-Based Authentication (Recommended)

**Step 1 — Generate an RSA key pair (on your local machine):**

```bash
ssh-keygen -t rsa -b 4096 -C "your_email" -f ~/.ssh/id_rsa_aisafe
```

This creates:
- **Private key:** `~/.ssh/id_rsa_aisafe` ← keep this SECRET, never share
- **Public key:** `~/.ssh/id_rsa_aisafe.pub` ← this goes on the VS

**Step 2 — Copy the public key to the Cloud VS:**

```bash
# Method 1: Using ssh-copy-id (if you have password access)
ssh-copy-id -i ~/.ssh/id_rsa_aisafe.pub root@vs489.dei.isep.ipp.pt

# Method 2: Manual copy (paste the public key content)
cat ~/.ssh/id_rsa_aisafe.pub
# Then SSH into the VS and add it manually:
ssh root@vs489.dei.isep.ipp.pt
mkdir -p /root/.ssh
chmod 700 /root/.ssh
echo "PASTE_PUBLIC_KEY_HERE" >> /root/.ssh/authorized_keys
chmod 600 /root/.ssh/authorized_keys
```

**Step 3 — Connect using the private key:**

```bash
ssh -i ~/.ssh/id_rsa_aisafe root@vs489.dei.isep.ipp.pt
```

**Step 4 — (Optional) Add to SSH config for convenience:**

Edit `~/.ssh/config` on your local machine:

```
Host vs489
    HostName vs489.dei.isep.ipp.pt
    User root
    IdentityFile ~/.ssh/id_rsa_aisafe
    Port 22
```

Then connect simply with: `ssh vs489`

#### Option B: Password Authentication

The root password is obtained from the **DEI Cloud Web Interface** when the VS is created.

```bash
ssh root@vs489.dei.isep.ipp.pt
# Enter password when prompted
```

**⚠️ IMPORTANT:** The root password **resets every time the VS boots**. You must retrieve the new password from the DEI Cloud Web Interface after each reboot.

#### Option C: External SSH (from outside ISEP network)

```bash
ssh -p 10489 root@vsgate-ssh.dei.isep.ipp.pt
# Uses the same authentication (key or password)
```

---

### 1.3 Run the Setup Script

From your **local machine** (after SSH key is configured):

```bash
cd aisafe-flight-control
scp -i ~/.ssh/id_rsa_aisafe scripts/setup-cloud-vs.sh root@vs489.dei.isep.ipp.pt:/tmp/
ssh -i ~/.ssh/id_rsa_aisafe root@vs489.dei.isep.ipp.pt 'sh /tmp/setup-cloud-vs.sh'
```

Or with the VS's setup script that also installs your key:

```bash
SSH_PUB_KEY="$(cat ~/.ssh/id_rsa_aisafe.pub)" ssh root@vs489.dei.isep.ipp.pt 'sh' < scripts/setup-cloud-vs.sh
```

Or manually on the VS:

```bash
apk update
apk add openjdk21
mkdir -p /var/log/aisafe /opt/aisafe-logging-server
java -version
```

### 1.4 Open Firewall Ports

### 1.1 Connect to the Cloud VS

```bash
ssh root@vs489.dei.isep.ipp.pt
```

### 1.2 Run the Setup Script

From your **local machine**:

```bash
cd aisafe-flight-control
scp scripts/setup-cloud-vs.sh root@vs489.dei.isep.ipp.pt:/tmp/
ssh root@vs489.dei.isep.ipp.pt 'sh /tmp/setup-cloud-vs.sh'
```

Or manually on the VS:

```bash
apk update
apk add openjdk21
mkdir -p /var/log/aisafe /opt/aisafe-logging-server
java -version
```

### 1.3 Open Firewall Ports

Via the **DEI Cloud Web Interface**, open:
| Port | Protocol | Purpose |
|------|----------|---------|
| 9999 | UDP | Receive log events from AISafe |
| 8080 | TCP | HTTP visualization dashboard |


---

## Phase 2: Deploy the Logging Server

### 2.1 Deploy from Local Machine

```bash
cd aisafe-flight-control
./scripts/deploy-to-cloud.sh
```

This script will:
1. Build the `aisafe-logging-server-1.0.0.jar` via Maven
2. Copy the JAR to the Cloud VS via `scp`
3. Restart the Logging Server on the VS

### 2.2 Verify Deployment on Cloud VS

```bash
ssh root@vs489.dei.isep.ipp.pt
cd /opt/aisafe-logging-server
./start-logging-server-vs.sh status
```

Expected output:
```
============================================
  US090 — Logging Server Status
============================================
  Status: RUNNING (PID: 12345)
  HTTP  :8080  LISTENING
  UDP   :9999  LISTENING
  Access log: /var/log/aisafe/remote_accesses.log (0 entries, 0 bytes)
```

### 2.3 Test UDP Reception on Cloud VS

```bash
ssh root@vs489.dei.isep.ipp.pt
./start-logging-server-vs.sh test
```

Or manually:

```bash
echo "2026-01-01T00:00:00|testuser|10.9.0.1|54321|US078|LOGIN_SUCCESS" | nc -u localhost 9999
tail -1 /var/log/aisafe/remote_accesses.log
```

Expected:
```
2026-01-01T00:00:00|testuser|10.9.0.1|54321|US078|LOGIN_SUCCESS
```

---

## Phase 3: Run Local System with Cloud Logging

### 3.1 Start with Cloud Logging (Linux/Mac)

```bash
cd aisafe-flight-control
./scripts/run-cloud.sh
```

This will:
1. Check if the Cloud Logging Server is reachable
2. Start the local TCP Server with `-Dlogging.host=<CLOUD_VS_IP>`
3. Open the cloud HTTP dashboard in your browser
4. Start the TCP Client (Air Transport Company App)

### 3.2 Start with Cloud Logging (Windows)

**Option A: Complete Workflow (Recommended)**

Run the unified script that does everything end-to-end:
```cmd
cd aisafe-flight-control
scripts
un-cloud-workflow.bat
```

Script structure:
- `run-cloud-workflow.bat` — Master script (invokes phases 1-6)
- `phase1-detect-vpn.bat` — Detect VPN IP and resolve Cloud VS IP
- `phase2-build.bat` — Build all modules (Maven compile + package)
- `phase3-deploy.bat` — Deploy JAR to Cloud VS (scp + restart via SSH)
- `phase4-start-server.bat` — Start TCP Server locally (cloud logging)
- `phase5-start-client.bat` — Start TCP Client (interactive)
- `phase6-dashboard.bat` — Open browser dashboard (SSH tunnel)

Each phase checks errorlevel and kills Java processes on failure.
Uses SSH config (`~/.ssh/config`) for key-based authentication.

**Option B: Manual Step-by-Step**

Start the TCP Server:
```cmd
cd aisafe-flight-control
mvn -pl aisafe.app.server exec:java -Dlogging.host=10.9.21.233
```

Then in a second terminal:
```cmd
mvn -pl aisafe.app.company.console exec:java
```

### 3.3 Verify Logs Appear on Cloud

After performing login/logout operations in the client:

```bash
ssh root@vs489.dei.isep.ipp.pt
tail -10 /var/log/aisafe/remote_accesses.log
```

Expected:
```
2026-06-03T14:30:22.123|atcc@airline.com|127.0.0.1|54321|US078|LOGIN_SUCCESS
2026-06-03T14:35:10.456|atcc@airline.com|127.0.0.1|54321|US078|LOGOUT
```

---

## Phase 4: HTTP Visualization Dashboard

Access from your browser:

| URL | Description |
|-----|-------------|
| `http://localhost:8080/` | All log events (via SSH tunnel) |
| `http://localhost:8080/active` | Active sessions (via SSH tunnel) |
| `http://localhost:8080/api/logs` | JSON API — all events |


---

## Phase 5: Test Scenarios

### Test 1: Successful Login

1. Start the TCP Client
2. Enter email: `atcc@airline.com`
3. Enter password: `Pass123`
4. **Expected on Cloud VS:**
   ```
   ...|atcc@airline.com|<client_ip>|<port>|US078|LOGIN_SUCCESS
   ```
5. **Expected in browser:** Entry appears in the logs table

### Test 2: Failed Login

1. Start the TCP Client
2. Enter email: `unknown@airline.com`
3. Enter password: `wrongpassword`
4. **Expected on Cloud VS:**
   ```
   ...|unknown@airline.com|<client_ip>|<port>|US078|LOGIN_FAILURE
   ```

### Test 3: Logout

1. Login successfully
2. Select option `0` (Logout)
3. **Expected on Cloud VS:**
   ```
   ...|atcc@airline.com|<client_ip>|<port>|US078|LOGOUT
   ```
4. **Expected in browser:** Session removed from active sessions

### Test 4: Disconnect

1. Login successfully
2. Close the client window (or Ctrl+C)
3. **Expected on Cloud VS:**
   ```
   ...|atcc@airline.com|<client_ip>|<port>|US078|DISCONNECT
   ```

### Test 5: Multiple Concurrent Clients

1. Start two TCP Clients simultaneously
2. Login with different users
3. **Expected:** Both sessions appear in `/active` endpoint
4. **Expected:** All events appear in log file

---

## Phase 6: Management Commands

### On the Cloud VS

```bash
# Check status
./start-logging-server-vs.sh status

# View server logs
./start-logging-server-vs.sh logs

# View access logs
./start-logging-server-vs.sh access

# Restart after code changes
./start-logging-server-vs.sh restart

# Run self-test
./start-logging-server-vs.sh test

# Stop
./start-logging-server-vs.sh stop
```

### From Local Machine

```bash
# Full redeploy cycle
./scripts/deploy-to-cloud.sh

# Quick status check
ssh root@vs489.dei.isep.ipp.pt './start-logging-server-vs.sh status'

# Tail access logs remotely
ssh root@vs489.dei.isep.ipp.pt 'tail -f /var/log/aisafe/remote_accesses.log'

# Check HTTP endpoints
curl http://10.9.21.233:8080/api/logs | python -m json.tool
curl http://10.9.21.233:8080/api/active | python -m json.tool
```

---

## Quick Reference

| Action | Command |
|--------|---------|
| **Setup VS** (one-time) | `scp scripts/setup-cloud-vs.sh root@vs489:/tmp/ && ssh root@vs489 'sh /tmp/setup-cloud-vs.sh'` |
| **Deploy JAR** | `./scripts/deploy-to-cloud.sh` |
| **Start with cloud** (Linux/Mac) | `./scripts/run-cloud.sh` |
| **Start with cloud** (Windows) | `scripts
un-cloud-workflow.bat` (unified) or `mvn -pl aisafe.app.server exec:java -Dlogging.host=10.9.21.233` (manual) |
| **Check VS status** | `ssh root@vs489 './start-logging-server-vs.sh status'` |
| **View cloud logs** | `ssh root@vs489 'tail -f /var/log/aisafe/remote_accesses.log'` |
| **HTTP dashboard** | `http://10.9.21.233:8080/` |
| **Restart VS server** | `ssh root@vs489 './start-logging-server-vs.sh restart'` |

---

## Troubleshooting

### "Cannot reach Cloud Logging Server"

```bash
ssh root@vs4899 'pgrep -f aisafe-logging-server'
ssh root@vs489 'ss -tlnp | grep 8080'
ssh root@vs489 'tail -50 /var/log/aisafe/server.log'
```

### "No logs appear in Cloud VS"

1. Check firewall: UDP 9999 open in DEI Cloud Web Interface
2. Check client: `-Dlogging.host` points to correct Cloud VS IP
3. Check server: `./start-logging-server-vs.sh status`
4. Test UDP: `echo "test" | nc -u <CLOUD_VS_IP> 9999`

### "Logs appear locally but not on Cloud"

`DEFAULT_LOGGING_HOST` defaults to `127.0.0.1`. Override:

```bash
# System property
mvn -pl aisafe.app.server exec:java -Dlogging.host=10.9.21.233

# Environment variable
export AISAFE_LOGGING_HOST=10.9.21.233
mvn -pl aisafe.app.server exec:java
```
