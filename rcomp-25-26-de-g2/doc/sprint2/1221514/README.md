RCOMP 2025-2026 Project 1 - Sprint 2 - Member 1221514 (Terminal 2)
===================================================================

This folder contains all the outputs and artifacts produced by the team member with number 1221514 for Sprint 2. The work focuses on the development of the Packet Tracer simulation for **Terminal 2**, as defined in the sprint planning.

### Author
- **Student Number:** 1221514
- **Assigned Task:** T.2.1
- **Terminal:** 2
- **Sprint Master:** 1240904

---

## 1. Sprint 2 Overview for Terminal 2

The goal for this task was to create a Packet Tracer simulation representing the logical network infrastructure for Terminal 2, based on the structured cabling project from Sprint 1. This simulation includes:

- **Layer 2 configuration** (VLANs, VTP, trunking) for all 10 switches within the terminal.
- **Layer 3 configuration** (IPv4 addressing, static routing) for the terminal's router (Cisco 2811).
- **End devices** to demonstrate connectivity on all required VLANs (PC, Laptop+AP, Server, VoIP).
- **Connection to the campus backbone** and the **internet** (via ISP router).
- **Redundancy links** between Horizontal Cross-Connects (HCCs) of the same level, with Spanning Tree Protocol (STP) ensuring loop-free operation.

This simulation was developed in isolation and is intended to be integrated with other terminals' simulations by the sprint master into `campus.pkt`.

---

## 2. Network Configuration Files

The core outcomes of this sprint are the configuration files for all network devices within Terminal 2. These files can be used to restore the configuration of a device or to review the exact commands used.

### 2.1. Packet Tracer Simulation File
- **File:** `Terminal2.pkt`
- **Description:** The complete Packet Tracer simulation file for Terminal 2, including all 10 switches, the router, end devices, and the internet connection.
- **Packet Tracer Version:** 9.0.0

### 2.2. Device Configuration Dumps

The following text files contain the `running-config` export for each network device in the simulation. They were exported directly from Packet Tracer.

#### Switches (10 total)

| File | Device | Role | VTP Mode | IP (VLAN 770) |
|------|--------|------|----------|---------------|
| `T2-MCC.txt` | T2-MCC | Main Cross-Connect (Sala 12) | Server | 10.61.170.1/23 |
| `T2-L1-HC-A.txt` | T2-L1-HC-A | HC-A Nivel 1 - Zona Oeste (Sala 11) | Client | 10.61.170.3/23 |
| `T2-L1-HC-C.txt` | T2-L1-HC-C | HC-C Nivel 1 - Zona Este (Sala 6) | Client | 10.61.170.4/23 |
| `T2-L4-HC-A.txt` | T2-L4-HC-A | HC-A Nivel 4 - Zona Oeste (Sala 17) | Client | 10.61.170.5/23 |
| `T2-L4-HC-B.txt` | T2-L4-HC-B | HC-B Nivel 4 - Zona Central/Leste (Sala 8) | Client | 10.61.170.6/23 |
| `T2-CP-L1-5.txt` | T2-CP-L1-5 | CP-5 Nivel 1 (Sala 5) | Client | 10.61.170.7/23 |
| `T2-CP-L1-7.txt` | T2-CP-L1-7 | CP-7 Nivel 1 (Sala 7) | Client | 10.61.170.8/23 |
| `T2-CP-L1-8.txt` | T2-CP-L1-8 | CP-8 Nivel 1 (Sala 8) | Client | 10.61.170.9/23 |
| `T2-CP-L4-3.txt` | T2-CP-L4-3 | CP-3 Nivel 4 (Sala 3) | Client | 10.61.170.10/23 |
| `T2-CP-L4-5.txt` | T2-CP-L4-5 | CP-5 Nivel 4 (Sala 5) | Client | 10.61.170.11/23 |

> **Note:** The configuration of each HCC includes an additional trunk port (`Fa9/1`) for redundancy to the other HCC on the same floor, with STP cost set to 100 to ensure the link to the MCC is preferred.

#### Routers

| File | Device | Description |
|------|--------|-------------|
| `T2-Router.txt` | T2-Router | Terminal 2 Router (Cisco 2811) |
| `ISP-Router.txt` | ISP-Router | Internet Service Provider Router |

#### End Devices

| Device | Type | VLAN | IP Address | Gateway |
|--------|------|------|------------|---------|
| `T2-PC-Outlets` | PC | 771 (t2-outlets) | 10.61.152.10/22 | 10.61.152.1 |
| `T2-PC-DMZ` | PC | 770 (switches-dmz) | 10.61.170.200/23 | — (isolated) |
| `T2-Laptop-WiFi` | Laptop | 772 (t2-wifi) | 10.61.128.10/21 | 10.61.128.1 |
| `T2-Server` | Server | 774 (t2-servers) | 10.61.173.130/25 | 10.61.173.129 |
| `T2-VoIP` | IP Phone 7960 | 773 (t2-voip) | DHCP (Sprint 3) | DHCP |
| `T2-AP` | Access Point | 772 (t2-wifi) | 10.61.128.2/21 | 10.61.128.1 |

---

## 3. IPv4 Addressing Plan for Terminal 2

Based on the team's planning, the following IPv4 networks are used within Terminal 2.

### 3.1. VLAN Table

| VLAN Name | VLAN ID | Purpose | IPv4 Network | Subnet Mask | Usable Range | Default Gateway |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Shared VLANs** |
| backbone | 769 | Campus Backbone | `10.61.174.0/24` | `255.255.255.0` | `10.61.174.1` - `10.61.174.254` | N/A |
| switches-dmz | 770 | Switch Management DMZ | `10.61.170.0/23` | `255.255.254.0` | `10.61.170.1` - `10.61.171.254` | N/A (isolated) |
| **Terminal 2 Specific VLANs** |
| t2-outlets | 771 | User Workstations | `10.61.152.0/22` | `255.255.252.0` | `10.61.152.1` - `10.61.155.254` | `10.61.152.1` |
| t2-wifi | 772 | Wireless Network | `10.61.128.0/21` | `255.255.248.0` | `10.61.128.1` - `10.61.135.254` | `10.61.128.1` |
| t2-voip | 773 | IP Phones | `10.61.164.0/23` | `255.255.254.0` | `10.61.164.1` - `10.61.165.254` | `10.61.164.1` |
| t2-servers | 774 | Server DMZ | `10.61.173.128/25`| `255.255.255.128` | `10.61.173.129` - `10.61.173.254` | `10.61.173.129` |

### 3.2. Key Device IP Addresses

- **Terminal 2 Router (T2-Router):**
  - VLAN 769 (Backbone): `10.61.174.2/24`
  - VLAN 771 (Outlets): `10.61.152.1/22` (Gateway)
  - VLAN 772 (Wi-Fi): `10.61.128.1/21` (Gateway)
  - VLAN 773 (VoIP): `10.61.164.1/23` (Gateway)
  - VLAN 774 (Servers): `10.61.173.129/25` (Gateway)

- **Terminal 2 Switches (on VLAN 770 - Switches DMZ):** as per table in section 2.2.

- **Internet Connection:**
  - **T2-Router (Eth0/0/0 to DSL Modem):** `89.73.67.117/30`
  - **ISP-Router (Fa0/0 to DSL Modem):** `89.73.67.118/30`
  - **ISP-Router (to Internet):** `209.165.200.225/30` (simulated)

---

## 4. Topology — Device Interconnections

### 4.1. Trunk Links (Fiber Optic — PT-SWITCH-NM-1FFE Multimode)

**Backbone: MCC → HCCs e outros terminais**

| # | Source Device | Source Port | Destination Device | Destination Port | VLANs |
|---|---------------|-------------|-------------------|------------------|-------|
| 1 | T2-MCC | Fa0/1 | T3-IC | Fa9/1 | 769-782 |
| 2 | T2-MCC | Fa1/1 | T4-IC | Fa9/1 | 769-782 |
| 3 | T2-MCC | Fa2/1 | T2-L1-HC-A | Fa1/1 | 769-782 |
| 4 | T2-MCC | Fa3/1 | T2-L1-HC-C | Fa1/1 | 769-782 |
| 5 | T2-MCC | Fa4/1 | T2-L4-HC-A | Fa1/1 | 769-782 |
| 6 | T2-MCC | Fa5/1 | T2-L4-HC-B | Fa1/1 | 769-782 |
| 7 | T2-MCC | Fa9/1 | T2-Router | Fa0/0 | 769-782 |

**Redundancy links between same-level HCCs (Fiber MM)**

| # | Source Device | Source Port | Destination Device | Destination Port | VLANs | STP Cost |
|---|---------------|-------------|-------------------|------------------|-------|----------|
| 8 | T2-L1-HC-A | Fa9/1 | T2-L1-HC-C | Fa9/1 | 769-782 | 100 |
| 9 | T2-L4-HC-A | Fa9/1 | T2-L4-HC-B | Fa9/1 | 769-782 | 100 |

These redundant links provide an alternative path in case the primary link to the MCC fails. The STP cost is set to 100 on these ports, making them less preferred than the direct links to the MCC (which have default cost 19). Therefore, the `Fa9/1` ports will be in `Alternate/Blocking` state under normal operation.

**Distribution: HCC → CPs (Fiber MM)**

| # | Source Device | Source Port | Destination Device | Destination Port | VLANs |
|---|---------------|-------------|-------------------|------------------|-------|
| 10 | T2-L1-HC-A | Fa2/1 | T2-CP-L1-8 | Fa1/1 | 769-782 |
| 11 | T2-L1-HC-C | Fa2/1 | T2-CP-L1-5 | Fa1/1 | 769-782 |
| 12 | T2-L1-HC-C | Fa3/1 | T2-CP-L1-7 | Fa1/1 | 769-782 |
| 13 | T2-L4-HC-A | Fa2/1 | T2-CP-L4-3 | Fa1/1 | 769-782 |
| 14 | T2-L4-HC-B | Fa2/1 | T2-CP-L4-5 | Fa1/1 | 769-782 |

### 4.2. Access Links (Switch to End Device — Copper Straight-Through)

| # | Switch | Port | Device | VLAN |
|---|--------|------|--------|------|
| 20 | T2-MCC | Fa8/1 | T2-PC-DMZ | 770 |
| 21 | T2-CP-L1-5 | Fa2/1 | T2-PC-Outlets | 771 |
| 22 | T2-CP-L1-7 | Fa2/1 | T2-AP | 772 |
| 23 | T2-CP-L1-8 | Fa2/1 | T2-Server | 774 |
| 24 | T2-CP-L4-3 | Fa2/1 | T2-VoIP | 773 (voice) |

### 4.3. WAN Links (Router to Internet)

| # | Source | Port | Destination | Port | Network |
|---|--------|------|-------------|------|---------|
| 30 | T2-Router | Eth0/0/0 | DSL-Modem | Ethernet | 89.73.67.117/30 |
| 31 | DSL-Modem | Coaxial | ISP-Router | Fa0/0 | 89.73.67.118/30 |
| 32 | ISP-Router | Fa0/1 | Internet | — | Simulated |

---

## 5. Testing and Verification

The following tests were performed to validate the correct operation of Terminal 2. All tests were executed after the final topology and configurations were applied, including the redundant links between HCCs.

### 5.1. Connectivity within Terminal 2

#### From T2-PC-Outlets (10.61.152.10)

| Command | Expected Result | Actual Result |
|---------|----------------|----------------|
| `ping 10.61.152.1` | Gateway (outlets) | ✅ Reply (after ARP) |
| `ping 10.61.128.1` | Gateway (Wi-Fi) | ✅ Reply |
| `ping 10.61.164.1` | Gateway (VoIP) | ✅ Reply |
| `ping 10.61.173.129` | Gateway (servers) | ✅ Reply |
| `ping 10.61.174.2` | T2-Router backbone IP | ✅ Reply |
| `ping 10.61.128.10` | Laptop Wi-Fi | ✅ Reply |
| `ping 10.61.173.130` | T2-Server | ✅ Reply |
| `ping 10.61.170.200` | PC-DMZ (different VLAN) | ❌ Timeout (expected – PC-DMZ is isolated) |
| `ping 10.61.170.1` | T2-MCC switch | ❌ Timeout (router not in VLAN 770) |

#### From T2-Laptop-WiFi (10.61.128.10)

| Command | Expected Result | Actual Result |
|---------|----------------|----------------|
| `ping 10.61.128.1` | Gateway | ✅ Reply |
| `ping 10.61.152.1` | Gateway outlets T2 | ✅ Reply |
| `ping 10.61.152.10` | PC-Outlets | ✅ Reply |
| `ping 10.61.173.130` | T2-Server | ✅ Reply |
| `ping 10.61.174.3` | T3-Router | ✅ Reply |

#### From T2-Server (10.61.173.130)

| Command | Expected Result | Actual Result |
|---------|----------------|----------------|
| `ping 10.61.173.129` | Gateway | ✅ Reply |
| `ping 10.61.152.10` | PC-Outlets | ✅ Reply |
| `ping 10.61.128.10` | Laptop Wi-Fi | ✅ Reply |
| `ping 10.61.174.3` | T3-Router | ✅ Reply |
| `ping 10.61.172.1` | Gateway servers T3 | ✅ Reply |
| `ping 10.61.173.1` | Gateway servers T4 | ✅ Reply |

#### From T2-PC-DMZ (10.61.170.200) – isolated management network

| Command | Expected Result | Actual Result |
|---------|----------------|----------------|
| `ping 10.61.170.1` | T2-MCC | ✅ Reply |
| `ping 10.61.170.3` | T2-L1-HC-A | ✅ Reply |
| `ping 10.61.170.51` | T3-IC | ✅ Reply |
| `ping 10.61.170.101` | T4-IC | ✅ Reply |
| `ping 10.61.152.1` | Any IP outside VLAN 770 | ❌ Timeout (correct) |

### 5.2. Connectivity to other Terminals (via Campus Backbone)

From **T2-PC-Outlets** (10.61.152.10):

| Command | Destination | Expected Result | Actual Result |
|---------|-------------|----------------|----------------|
| `ping 10.61.174.3` | T3-Router (backbone) | ✅ Reply | ✅ Reply |
| `ping 10.61.174.4` | T4-Router (backbone) | ✅ Reply | ✅ Reply |
| `ping 10.61.156.1` | Gateway outlets T3 | ✅ Reply | ✅ Reply |
| `ping 10.61.136.1` | Gateway Wi-Fi T3 | ✅ Reply | ✅ Reply |
| `ping 10.61.166.1` | Gateway VoIP T3 | ✅ Reply | ✅ Reply |
| `ping 10.61.172.1` | Gateway servers T3 | ✅ Reply | ✅ Reply |
| `ping 10.61.160.1` | Gateway outlets T4 | ✅ Reply | ✅ Reply |
| `ping 10.61.144.1` | Gateway Wi-Fi T4 | ✅ Reply | ✅ Reply |
| `ping 10.61.168.1` | Gateway VoIP T4 | ✅ Reply | ✅ Reply |
| `ping 10.61.173.1` | Gateway servers T4 | ✅ Reply | ✅ Reply |

> **Note:** All gateways of T3 and T4 responded, confirming that inter-terminal routing is fully functional.

### 5.3. Connectivity to the Internet

From **T2-PC-Outlets** or **T2-Laptop-WiFi**:

| Command | Destination | Expected Result | Actual Result |
|---------|-------------|----------------|----------------|
| `ping 89.73.67.118` | ISP Router | ✅ Reply | ✅ Reply |
| `ping 209.165.200.226` | Internet gateway (simulated) | ❌ No device with this IP | ❌ Destination unreachable (expected) |
| `ping 8.8.8.8` | External DNS | ❌ No route in ISP | ❌ Destination unreachable (expected) |

The ISP connectivity requirement is satisfied by the successful ping to `89.73.67.118`.

### 5.4. Router Verification (T2-Router)

| Command | Expected Outcome | Actual Result |
|---------|------------------|----------------|
| `show ip interface brief` | Sub-interfaces Fa0/0.769–774 up/up with correct IPs | ✅ All up/up |
| `show ip route` | Connected routes for T2 networks + static routes to T3/T4 + default route | ✅ Complete routing table |
| `ping 10.61.152.10` | Success | ✅ 100% success |
| `ping 10.61.128.10` | Success | ✅ 100% success |
| `ping 10.61.173.130` | Success | ✅ 100% success |
| `ping 10.61.174.3` | Success | ✅ 100% success |
| `ping 10.61.156.1` | Success | ✅ 100% success |
| `ping 89.73.67.118` | Success | ✅ 100% success |

### 5.5. Switch Verification (T2-MCC)

| Command | Expected Outcome | Actual Result |
|---------|------------------|----------------|
| `show vlan brief` | VLANs 769-782 active; Fa8/1 in VLAN 770 | ✅ Correct |
| `show interfaces trunk` | Ports Fa0/1, Fa1/1, Fa2/1, Fa3/1, Fa4/1, Fa5/1, Fa9/1 trunking, allowed VLANs 769-782 | ✅ Correct |
| `show vtp status` | VTP Server, domain `rc2526deg2`, revision 238 | ✅ Correct |
| `show spanning-tree` | Root bridge for all VLANs (lowest priority) | ✅ Correct (T2-MCC is root) |

### 5.6. VTP Client Verification (e.g., T2-L1-HC-A)

| Command | Expected Outcome | Actual Result |
|---------|------------------|----------------|
| `show vtp status` | VTP Client, same domain and revision as server | ✅ Correct |
| `show vlan brief` | All VLANs 769-782 present | ✅ Correct |

### 5.7. Redundancy and STP Verification

After adding the redundant links between HCCs, verify that Spanning Tree is blocking the redundant path to avoid loops.

On **T2-L1-HC-A** (or any HCC), check the STP state for VLAN 769:

```
show spanning-tree vlan 769
```

```text
Expected output (example for T2-L1-HC-A):
Interface Role Sts Cost Prio.Nbr Type

Fa1/1 Root FWD 19 128.1 P2p
Fa9/1 Altn BLK 100 128.9 P2p
```

```text
- The `Fa1/1` (link to MCC) is the Root Port and is Forwarding (cost 19).
- The `Fa9/1` (redundant link to the other HCC) is in Alternate/Blocking state (cost 100), preventing a loop.

To test failover, manually shut down the active trunk port (e.g., `Fa1/1` on T2-L1-HC-A) and observe that after ~30-50 seconds the redundant link transitions to Forwarding, restoring connectivity. This confirms the redundancy is operational.
```

---

## 6. Summary of Test Results

| Component / Functionality | Status |
|---------------------------|--------|
| Layer 2 (VLANs, VTP, trunks) | ✅ Fully operational |
| Layer 3 (Router-on-a-stick, sub-interfaces) | ✅ Fully operational |
| Static routing (T2 ↔ T3, T2 ↔ T4) | ✅ Fully operational |
| Internal endpoint connectivity (PC, Laptop, Server) | ✅ Fully operational |
| Isolated management VLAN 770 | ✅ Correctly isolated |
| Internet link (to ISP router) | ✅ Operational |
| Redundancy links between HCCs (same level) | ✅ Implemented with STP cost 100, blocking redundant path |
| Integration readiness (naming, trunking) | ✅ Ready for campus.pkt |

All mandatory requirements for Sprint 2 (Terminal 2) have been met. The simulation is stable and ready to be merged with the other terminals' simulations by the sprint master.

---

## 7. Reflections and Challenges

- **Redundancy between HCCs:** Two fiber links were added between `T2-L1-HC-A` ↔ `T2-L1-HC-C` and `T2-L4-HC-A` ↔ `T2-L4-HC-B` using port `Fa9/1` on each switch. The STP cost on these ports was increased to 100 to ensure the direct links to the MCC (cost 19) remain preferred. This provides physical redundancy without causing loops.
- **VTP domain consistency:** All switches in the campus must share the same VTP domain (`rc2526deg2`) to propagate VLANs. T2-MCC is the VTP server.
- **Fiber optic backbone:** All inter-switch links use multimode fiber (PT-SWITCH-NM-1FFE) as per structured cabling design.
- **Endpoint minimisation:** Only mandatory endpoints (one per required VLAN) are present, following the sprint plan.

---

*Document generated for Sprint 2 — Student 1221514 — Terminal 2*  