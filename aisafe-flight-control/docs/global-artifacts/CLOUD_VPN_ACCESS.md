# US090 — Cloud Access via VPN (From Home)

## Overview

This guide explains how to access the Cloud VS from home **without enabling External SSH access** on the VS. Instead, you connect through the ISEP VPN, which gives you a virtual network interface directly on the ISEP internal network.

**Why VPN instead of External SSH?**
- External SSH on the VS exposes port 22 to the entire internet
- VPN encrypts all traffic and requires DEI credentials
- Once connected via VPN, you access the VS as if you were physically at ISEP
- External SSH on the VS can remain **DISABLED** for security

**Architecture:**

```
Home PC
┌──────────────────────────────────────────────────────────────┐
│                                                              │
│  ┌─────────────┐    VPN Tunnel (SSL/TLS)    ┌────────────┐  │
│  │ VPN Client   │═══════════════════════════►│ ISEP VPN   │  │
│  │ (SoftEther/  │    UDP 443 or TCP 443     │ Server     │  │
│  │  SSTP/       │                            │ deinet.dei │  │
│  │  OpenVPN)    │                            │ .isep.ipp  │  │
│  └──────┬───────┘                            │ .pt        │  │
│         │                                     └─────┬──────┘  │
│         │ Virtual NIC (10.9.x.x)                   │         │
│         │                                           │         │
│         ▼                                           ▼         │
│  ┌─────────────┐    SSH :22 (internal)    ┌──────────────┐  │
│  │ ssh/scp     │─────────────────────────►│ Cloud VS     │  │
│  │ (key-based) │    Encrypted via VPN     │ vs489.dei    │  │
│  └─────────────┘                          │ .isep.ipp.pt │  │
│                                           │ VNET1        │  │
│                                           │ 10.9.0.0/16  │  │
│                                           └──────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

---

## Phase 1: VPN Connection (From Home)

### 1.1 Choose a VPN Protocol

| Protocol | Recommendation | Port | Notes |
|----------|---------------|------|-------|
| **SoftEther** | ✅ **Best option** | 443/TCP+UDP | Most evolved, works through most firewalls |
| **SSTP** | ✅ Good (Windows) | 443/TCP | Native Windows client, SSL/TLS |
| **OpenVPN** | ✅ Good (cross-platform) | 1194/UDP or 443/TCP | Needs separate client install |
| **PPTP** | ❌ Avoid | 1723/TCP | Considered insecure |

### 1.2 Option A: SoftEther VPN (Recommended)

**Windows:**

1. Download SoftEther VPN Client from https://www.softether.org/
2. Install and open "VPN Gate Public VPN Relay Servers" → "SoftEther VPN Client"
3. Create a new connection:
   - **Host Name:** `deinet.dei.isep.ipp.pt` (or `deinet1.dei.isep.ipp.pt` if round-robin fails)
   - **Port Number:** `443`
   - **Virtual Hub Name:** `DEFAULT`
   - **Auth Type:** `RADIUS or NT Domain Authentication`
   - **Username:** Your DEI username
   - **Password:** Your DEI password
4. Click "Connect"
5. Verify: You should get a virtual IP in the 10.9.x.x range

**Linux:**

```bash
# Install SoftEther VPN client
git clone https://github.com/SoftEtherVPN/SoftEtherVPN.git
cd SoftEtherVPN
make

# Configure connection
vpncmd /CLIENT localhost /CMD AccountCreate deinet /SERVER:deinet.dei.isep.ipp.pt:443 /HUB:DEFAULT /USERNAME:your_dei_username
vpncmd /CLIENT localhost /CMD AccountConnect deinet
```

**macOS:**

Use Tunnelblick (OpenVPN) — see Option C below.

### 1.3 Option B: SSTP VPN (Windows Native)

1. Open **Settings → Network & Internet → VPN → Add a VPN connection**
2. Configure:
   - **VPN provider:** Windows (built-in)
   - **Connection name:** `ISEP VPN`
   - **Server name:** `deinet.dei.isep.ipp.pt`
   - **VPN type:** **SSTP**
   - **Username:** Your DEI username
   - **Password:** Your DEI password
3. Click "Connect"
4. Verify: Check your IP with `ipconfig` — you should see a new adapter with 10.9.x.x

### 1.4 Option C: OpenVPN (Cross-Platform)

1. Download config files from ISEP: `openvpn-dei-config.zip`
2. Install OpenVPN client:
   - **Windows:** https://openvpn.net/community-downloads/
   - **macOS:** Tunnelblick (https://tunnelblick.net/)
   - **Linux:** `sudo apt install openvpn` or `sudo dnf install openvpn`
3. Import the config file for `deinet.dei.isep.ipp.pt`
4. Connect using your DEI credentials
5. For TCP mode (if UDP is blocked): edit config, change `proto udp` to `proto tcp`

### 1.5 Verify VPN Connection

After connecting, verify you're on the ISEP network:

```bash
# Check your virtual IP (should be 10.9.x.x)
ipconfig          # Windows
ip addr show      # Linux
ifconfig          # macOS

# Test connectivity to the Cloud VS
ping 10.9.21.233
ping vs489.dei.isep.ipp.pt

# Test SSH connectivity (should connect, may prompt for auth)
ssh root@vs489.dei.isep.ipp.pt
```

**Expected:** You should be able to reach `10.9.21.233` (or whatever IP the VS has on VNET1).


---

## Phase 2: SSH Key Setup (One-Time)

### 2.1 Generate SSH Key Pair (on your local machine)

```bash
ssh-keygen -t rsa -b 4096 -C "your_dei_email" -f ~/.ssh/id_rsa_aisafe
```

This creates:
- **Private key:** `~/.ssh/id_rsa_aisafe` ← NEVER share this
- **Public key:** `~/.ssh/id_rsa_aisafe.pub` ← this goes on the VS

### 2.2 Copy Public Key to Cloud VS (via VPN)

```bash
# Make sure VPN is connected first!
ssh-copy-id -i ~/.ssh/id_rsa_aisafe.pub root@vs489.dei.isep.ipp.pt
# Enter the root password from DEI Cloud panel when prompted
```

Or manually:

```bash
# Copy the public key
scp -i ~/.ssh/id_rsa_aisafe ~/.ssh/id_rsa_aisafe.pub root@vs489.dei.isep.ipp.pt:/tmp/

# SSH into the VS and install it
ssh root@vs489.dei.isep.ipp.pt
mkdir -p /root/.ssh
chmod 700 /root/.ssh
cat /tmp/id_rsa_aisafe.pub >> /root/.ssh/authorized_keys
chmod 600 /root/.ssh/authorized_keys
exit
```

### 2.3 Configure SSH Client (Optional, Recommended)

Edit `~/.ssh/config` on your local machine:

```
Host vs489
    HostName vs489.dei.isep.ipp.pt
    User root
    IdentityFile ~/.ssh/id_rsa_aisafe
    Port 22
    ServerAliveInterval 60
    ServerAliveCountMax 3
```

Now you can simply use: `ssh vs489`

---

## Phase 3: Cloud VS Setup (One-Time, via VPN)

### 3.1 Connect to VS via VPN

```bash
# Make sure VPN is connected!
ssh vs489
```

### 3.2 Run Setup Script

```bash
# From local machine, copy and run the setup script (VPN connected)
scp -i ~/.ssh/id_rsa_aisafe scripts/setup-cloud-vs.sh root@vs489.dei.isep.ipp.pt:/tmp/
ssh vs489 'sh /tmp/setup-cloud-vs.sh'
```

Or manually on the VS:

```bash
apk update
apk add openjdk21
mkdir -p /var/log/aisafe /opt/aisafe-logging-server
java -version
```

### 3.3 Open Firewall Ports (Internal Only!)

Via the **DEI Cloud Web Interface**, open these ports on the VS:

| Port | Protocol | Source | Purpose |
|------|----------|--------|---------|
| 9999 | UDP | VNET1 (10.9.0.0/16) | Logging events |
| 8080 | TCP | VNET1 (10.9.0.0/16) | HTTP visualization |

**⚠️ DO NOT enable "External SSH/SFTP access" on the VS!** The VPN provides secure access.

---

## Phase 4: Deploy and Test (Via VPN)

### 4.1 Deploy the Logging Server

```bash
# From local machine (VPN connected)
cd aisafe-flight-control
SSH_KEY_PATH=~/.ssh/id_rsa_aisafe ./scripts/deploy-to-cloud.sh
```

### 4.2 Verify on Cloud VS

```bash
ssh -i ~/.ssh/id_rsa_aisafe root@vs489.dei.isep.ipp.pt
cd /opt/aisafe-logging-server
./start-logging-server-vs.sh status
```

Expected:
```
============================================
  US090 — Logging Server Status
============================================
  Status: RUNNING (PID: 12345)
  HTTP  :8080  LISTENING
  UDP   :9999  LISTENING
```

### 4.3 Run Local System with Cloud Logging

```bash
# Linux/Mac (VPN connected)
SSH_KEY_PATH=~/.ssh/id_rsa_aisafe ./scripts/run-cloud.sh

# Windows (VPN connected)
mvn -pl aisafe.app.server exec:java -Dlogging.host=10.9.21.233
```

### 4.4 Verify End-to-End

1. Login via the TCP Client
2. Check logs appear on Cloud VS:
   ```bash
   ssh -i ~/.ssh/id_rsa_aisafe root@vs489.dei.isep.ipp.pt
   tail -5 /var/log/aisafe/remote_accesses.log
   ```
3. Check HTTP dashboard: `http://10.9.21.233:8080/` (via VPN)

---

## Phase 5: Complete Workflow Summary

### Every Time You Work From Home (Windows)

```cmd
# Step 1: Connect VPN (SoftEther/SSTP/OpenVPN)
# VPN Server: deinet.dei.isep.ipp.pt:443

# Step 2: Run the complete workflow
cd aisafe-flight-control
scripts\run-cloud-workflow.bat

# Step 3: When done, stop everything
scripts\cleanup-all.bat
```

### Every Time You Work From Home (Linux/Mac)

```bash
# Step 1: Connect VPN
# Step 2: Verify VPN connectivity
ping vs489.dei.isep.ipp.pt

# Step 3: Deploy (after code changes)
cd aisafe-flight-control
./scripts/deploy-to-cloud.sh

# Step 4: Run and test
./scripts/run-cloud.sh

# Step 5: Disconnect VPN when done
```

### Quick SSH Access

```bash
# With SSH config (recommended)
ssh vs489

# Without config
ssh -i ~/.ssh/id_rsa_aisafe root@vs489.dei.isep.ipp.pt

# View logs remotely
ssh vs489 'tail -f /var/log/aisafe/remote_accesses.log'

# Restart server remotely
ssh vs489 '/opt/aisafe-logging-server/start-logging-server-vs.sh restart'
```


---

## Troubleshooting

### "Cannot connect to VPN"

| Problem | Solution |
|---------|----------|
| `deinet.dei.isep.ipp.pt` fails | Try specific server: `deinet1.dei.isep.ipp.pt` |
| Port 443 blocked | Use SSTP (also 443) or OpenVPN TCP 443 |
| Auth fails | Verify DEI username/password |
| macOS issues | Use OpenVPN + Tunnelblick (SoftEther macOS is experimental) |

### "Cannot ping VS after VPN connected"

```bash
# Check VPN IP
ipconfig | findstr 10.9    # Windows
ip addr | grep 10.9        # Linux

# Check routing
route print | findstr 10.9    # Windows
ip route | grep 10.9          # Linux

# Test step by step
ping 10.9.0.1              # VNET1 gateway (if known)
ping 10.9.21.233           # Cloud VS IP
ping vs489.dei.isep.ipp.pt # DNS resolution
```

### "SSH connection refused"

```bash
# Make sure VPN is connected!
ping vs489.dei.isep.ipp.pt

# Check SSH service on VS
ssh vs489 'pgrep -x sshd'

# Check if key is installed
ssh vs489 'cat /root/.ssh/authorized_keys'

# Try password auth (if key fails)
ssh -o PreferredAuthentications=password root@vs489.dei.isep.ipp.pt
```

### "SCP fails"

```bash
# Test with verbose output
scp -v -i ~/.ssh/id_rsa_aisafe test.txt root@vs489.dei.isep.ipp.pt:/tmp/

# Check disk space on VS
ssh vs489 'df -h'
```

---

## Security Checklist

- [ ] External SSH access on VS: **DISABLED** (in DEI Cloud panel)
- [ ] External SFTP access on VS: **DISABLED**
- [ ] VPN connection: **Required** for all access
- [ ] SSH key-based auth: **Configured** on VS
- [ ] Firewall ports: Only UDP 9999 + TCP 8080 open (internal VNET1 only)
- [ ] Root password: Changed via DEI Cloud panel (not inside VS)
- [ ] Private key: Stored securely on local machine (`chmod 600`)

---

## Quick Reference

| Action | Command |
|--------|---------|
| **Connect VPN** | SoftEther/SSTP/OpenVPN -> `deinet.dei.isep.ipp.pt:443` |
| **SSH to VS** | `ssh vs489` |
| **Deploy JAR** | `scripts\run-cloud-workflow.bat` (all-in-one) or `./scripts/deploy-to-cloud.sh` |
| **Run with cloud** | `scripts\run-cloud-workflow.bat` |
| **Check VS status** | `ssh vs489 './start-logging-server-vs.sh status'` |
| **View cloud logs** | `ssh vs489 "tail -f /var/log/aisafe/remote_accesses.log"` |
| **HTTP dashboard** | `http://localhost:8080/` (via SSH tunnel) or `http://10.9.21.233:8080/` (direct VPN) |
| **Disconnect VPN** | Close VPN client |
