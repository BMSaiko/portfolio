# RCOMP 2025-2026 Project 1 - Sprint 3 - Member 1221514 (Terminal 2)

**Student:** 1221514
**Terminal:** 2 (Root DNS Domain)
**Team:** airport-campus-network
**Sprint Duration:** 2026-04-19 to 2026-05-17
**Sprint Master:** 1240935

---

## 1. Sprint 3 Overview

The goal for this task was to update the Packet Tracer simulation from Sprint 2 to include all new features required in Sprint 3:

- **OSPF dynamic routing** replacing static routing (except default route kept on T2-Router)
- **DHCPv4 service** for all internal networks (outlets, wifi, voip)
- **VoIP service** with Cisco Telephony, prefix 2, and call forwarding to other terminals
- **DNS service** ” T2 is the root domain `airport-campus-network`
- **NAT (Network Address Translation)** ” static NAT redirecting HTTP/HTTPS to HTTP server
- **Static Firewall (ACLs)** ” traffic access policies on the router
- **Second server added** to DMZ for HTTP service

This simulation was developed based on the `campus.pkt` from Sprint 2, working on Terminal 2. The final integration into `campus.pkt` is done by the Sprint Master (1240904).

---

## 2. Network Topology

```
[PC-Outside] --- Fa0/1 [ISP-Router] Fa0/0 --- Eth0/0/0 [T2-Router]
                                                          |
                                    Fa0/0.769 (backbone) --- T3/T4 routers
                                    Fa0/0.771 (outlets)  --- PC-Outlets
                                    Fa0/0.772 (wifi)     --- Laptop-WiFi
                                    Fa0/0.773 (voip)     --- IP Phones 2001, 2002
                                    Fa0/0.774 (servers)  --- T2-Server (DNS) .130
                                                        --- T2-Server-HTTP .131
```

---

## 3. IPv4 Addressing Plan

### 3.1 VLAN Table

| VLAN Name | VLAN ID | Purpose | Network | Subnet Mask | Gateway |
|-----------|---------|---------|---------|-------------|---------|
| backbone | 769 | Campus Backbone | 10.61.174.0/24 | 255.255.255.0 | 10.61.174.2 |
| switches-dmz | 770 | Switch Management DMZ | 10.61.170.0/23 | 255.255.254.0 | ” (isolated) |
| t2-outlets | 771 | User Workstations | 10.61.152.0/22 | 255.255.252.0 | 10.61.152.1 |
| t2-wifi | 772 | Wireless Network | 10.61.128.0/21 | 255.255.248.0 | 10.61.128.1 |
| t2-voip | 773 | IP Phones | 10.61.164.0/23 | 255.255.254.0 | 10.61.164.1 |
| t2-servers | 774 | Server DMZ | 10.61.173.128/25 | 255.255.255.128 | 10.61.173.129 |

### 3.2 Key Device IP Addresses

**ISP-Router (2811):**
| Interface | IP | Mask | Connected to |
|-----------|-----|------|--------------|
| Fa0/0 | 89.73.67.118 | /30 | T2-Router Eth0/0/0 |
| Fa0/1 | 209.165.200.225 | /30 | PC-Outside |

**T2-Router:**
| Interface | IP | Mask | VLAN |
|-----------|-----|------|------|
| Eth0/0/0 | 89.73.67.117 | /30 | ISP Link |
| Fa0/0.769 | 10.61.174.2 | /24 | 769 (backbone) |
| Fa0/0.771 | 10.61.152.1 | /22 | 771 (outlets) |
| Fa0/0.772 | 10.61.128.1 | /21 | 772 (wifi) |
| Fa0/0.773 | 10.61.164.1 | /23 | 773 (voip) |
| Fa0/0.774 | 10.61.173.129 | /25 | 774 (servers) |

**DMZ Servers (VLAN 774):**
| Server | IP | Gateway | Services |
|--------|-----|---------|----------|
| T2-Server (DNS) | 10.61.173.130/25 | 10.61.173.129 | DNS |
| T2-Server-HTTP | 10.61.173.131/25 | 10.61.173.129 | HTTP, HTTPS |

**PC-Outside:**
| Setting | Value |
|---------|-------|
| IP Address | 209.165.200.226 |
| Subnet Mask | 255.255.255.252 |
| Default Gateway | 209.165.200.225 |
| DNS Server | 10.61.173.130 |
## 4. OSPF Dynamic Routing Configuration

Static routing was removed and replaced with OSPF dynamic routing. Terminal 2 uses **OSPF Area 2**.

### 4.1 OSPF Areas

| Area | Network | Description |
|------|---------|-------------|
| 0 | 10.61.174.0/24 | Campus Backbone (VLAN 769) |
| 2 | All T2 internal networks | Terminal 2 |
| 3 | All T3 internal networks | Terminal 3 |
| 4 | All T4 internal networks | Terminal 4 |

### 4.2 OSPF Configuration on T2-Router

```cisco
router ospf 1
 router-id 2.2.2.2
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.152.0 0.0.3.255 area 2
 network 10.61.128.0 0.0.7.255 area 2
 network 10.61.164.0 0.0.1.255 area 2
 network 10.61.173.128 0.0.0.127 area 2
 default-information originate
```

### 4.3 OSPF Configuration on T3-Router

```cisco
router ospf 1
 router-id 3.3.3.3
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.156.0 0.0.3.255 area 3
 network 10.61.136.0 0.0.7.255 area 3
 network 10.61.166.0 0.0.1.255 area 3
 network 10.61.172.0 0.0.0.255 area 3
```

### 4.4 OSPF Configuration on T4-Router

```cisco
router ospf 1
 router-id 4.4.4.4
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.160.0 0.0.3.255 area 4
 network 10.61.144.0 0.0.7.255 area 4
 network 10.61.168.0 0.0.1.255 area 4
 network 10.61.173.0 0.0.0.127 area 4
```

### 4.5 Default Route

The default route to ISP is kept on T2-Router and redistributed via OSPF:

```cisco
ip route 0.0.0.0 0.0.0.0 89.73.67.118
```

### 4.6 Verification Commands

```cisco
show ip ospf neighbor
show ip route ospf
show ip ospf database
```

**Expected OSPF Neighbors (T2-Router):**
```
Neighbor ID  Pri  State        Dead Time  Address        Interface
3.3.3.3        1  FULL/BDR     00:00:xx   10.61.174.3    Fa0/0.769
4.4.4.4        1  FULL/DROTHER 00:00:xx   10.61.174.4    Fa0/0.769
```

**Expected OSPF IA Routes (T2-Router):**
```
O IA  10.61.136.0/21 [110/2] via 10.61.174.3
O IA  10.61.144.0/21 [110/2] via 10.61.174.4
O IA  10.61.156.0/22 [110/2] via 10.61.174.3
O IA  10.61.160.0/22 [110/2] via 10.61.174.4
O IA  10.61.166.0/23 [110/2] via 10.61.174.3
O IA  10.61.168.0/23 [110/2] via 10.61.174.4
O IA  10.61.172.0/24 [110/2] via 10.61.174.3
O IA  10.61.173.0/25 [110/2] via 10.61.174.4
```
## 5. DHCPv4 Service

The T2-Router provides DHCPv4 service to all internal networks except DMZ and backbone.

### 5.1 Exclude Static IP Addresses

```cisco
ip dhcp excluded-address 10.61.152.1
ip dhcp excluded-address 10.61.128.1
ip dhcp excluded-address 10.61.164.1
ip dhcp excluded-address 10.61.173.129
ip dhcp excluded-address 10.61.173.130
ip dhcp excluded-address 10.61.173.131
```

### 5.2 DHCP Pools

```cisco
ip dhcp pool t2-outlets-pool
 network 10.61.152.0 255.255.252.0
 default-router 10.61.152.1
 dns-server 10.61.173.130
 domain-name terminal-2.airport-campus-network

ip dhcp pool t2-wifi-pool
 network 10.61.128.0 255.255.248.0
 default-router 10.61.128.1
 dns-server 10.61.173.130
 domain-name terminal-2.airport-campus-network

ip dhcp pool t2-voip-pool
 network 10.61.164.0 255.255.254.0
 default-router 10.61.164.1
 dns-server 10.61.173.130
 domain-name terminal-2.airport-campus-network
 option 150 ip 10.61.164.1
```

### 5.3 Verification Commands

```cisco
show ip dhcp pool
show ip dhcp binding
```

**From PC-Outlets:**
```
C:\>ipconfig
```
Expected: IP 10.61.152.x, GW 10.61.152.1, DNS 10.61.173.130

**From Laptop-WiFi:**
```
C:\>ipconfig
```
Expected: IP 10.61.128.x, GW 10.61.128.1, DNS 10.61.173.130

**DHCP Release/Renew Test:**
```
C:\>ipconfig /release
C:\>ipconfig /renew
C:\>ipconfig
```
Expected: IP is re-assigned correctly after release/renew.
## 6. VoIP Service

### 6.1 Numbering Plan

| Terminal | Prefix | Local Numbers | Examples |
|----------|--------|---------------|----------|
| Terminal 2 | **2** | 2001 “ 2999 | 2001, 2002 |
| Terminal 3 | **3** | 3001 “ 3999 | 3001, 3002 |
| Terminal 4 | **4** | 4001 “ 4999 | 4001, 4002 |

### 6.2 Cisco Telephony Service (CME) on T2-Router

```cisco
telephony-service
 max-ephones 2
 max-dn 2
 ip source-address 10.61.164.1 port 2000
 auto assign 1 to 2

ephone-dn 1
 number 2001

ephone-dn 2
 number 2002
```

### 6.3 Dial-Peers for Inter-Terminal Calling

```cisco
dial-peer voice 3 voip
 destination-pattern 3...
 session target ipv4:10.61.174.3

dial-peer voice 4 voip
 destination-pattern 4...
 session target ipv4:10.61.174.4
```

### 6.4 Switch Port Configuration for VoIP Phones

```cisco
interface FastEthernet X/X
 switchport mode access
 no switchport access vlan
 switchport voice vlan 773
 no shutdown
```

### 6.5 IP Phones

| Phone | Extension | Switch | Port | Level |
|-------|-----------|--------|------|-------|
| T2-VoIP-1 | 2001 | T2-CP-L4-3 | Fa2/1 | Level 4, Sala 3 |
| T2-VoIP-2 | 2002 | T2-CP-L1-8 | Fa3/1 | Level 1, Sala 8 |

### 6.6 Verification Commands

```cisco
show telephony-service
show ephone
show dial-peer voice
```

**Test local call:** Pick up 2001, dial 2002 â†’ should connect.
**Test inter-terminal:** Pick up 2001, dial 3001 â†’ should forward to T3-Router.
## 7. DNS Service

Terminal 2 hosts the **root DNS domain** for the team: `airport-campus-network`.

### 7.1 DNS Domain Hierarchy

| Terminal | DNS Domain | Name Server (ns) | IP Address |
|----------|-------------|-------------------|------------|
| T2 (root) | `airport-campus-network` | `ns.airport-campus-network` | 10.61.173.130 |
| T3 | `terminal-3.airport-campus-network` | `ns.terminal-3.airport-campus-network` | 10.61.172.10 |
| T4 | `terminal-4.airport-campus-network` | `ns.terminal-4.airport-campus-network` | 10.61.173.10 |

### 7.2 T2-Server (DNS) Configuration

- **IP Address:** 10.61.173.130
- **Subnet Mask:** 255.255.255.128
- **Default Gateway:** 10.61.173.129
- **DNS Server:** 127.0.0.1 (itself)
- **Domain:** airport-campus-network

### 7.3 DNS Records on T2-Server

| Type | Name | Value | Description |
|------|------|-------|-------------|
| A | `ns.airport-campus-network` | 10.61.173.130 | Root DNS server |
| A | `server1.airport-campus-network` | 10.61.173.131 | HTTP server |
| CNAME | `www.airport-campus-network` | `server1.airport-campus-network` | Web alias |
| CNAME | `web.airport-campus-network` | `server1.airport-campus-network` | Web alias |
| CNAME | `dns.airport-campus-network` | `ns.airport-campus-network` | DNS alias |
| NS | `terminal-3.airport-campus-network` | `ns.terminal-3.airport-campus-network` | T3 subdomain |
| A (glue) | `ns.terminal-3.airport-campus-network` | 10.61.172.10 | T3 glue record |
| NS | `terminal-4.airport-campus-network` | `ns.terminal-4.airport-campus-network` | T4 subdomain |
| A (glue) | `ns.terminal-4.airport-campus-network` | 10.61.173.10 | T4 glue record |

### 7.4 Verification Commands

**From PC-Outlets:**
```
C:\>nslookup server1.airport-campus-network
```
Expected: `Address: 10.61.173.131`

```
C:\>nslookup www.airport-campus-network
```
Expected: CNAME â†’ `server1.airport-campus-network` â†’ `10.61.173.131`

```
C:\>nslookup dns.airport-campus-network
```
Expected: CNAME â†’ `ns.airport-campus-network` â†’ `10.61.173.130`

```
C:\>nslookup ns.terminal-3.airport-campus-network
```
Expected: `Address: 10.61.172.10`

```
C:\>nslookup ns.terminal-4.airport-campus-network
```
Expected: `Address: 10.61.173.10`
## 8. NAT (Network Address Translation)

Static NAT redirects HTTP/HTTPS traffic from the backbone interface to the T2-Server-HTTP.

### 8.1 NAT Interface Configuration

```cisco
interface Ethernet0/0/0
 ip nat outside

interface FastEthernet0/0.769
 ip nat outside

interface FastEthernet0/0.771
 ip nat inside

interface FastEthernet0/0.772
 ip nat inside

interface FastEthernet0/0.773
 ip nat inside

interface FastEthernet0/0.774
 ip nat inside
```

> **Note:** `Ethernet0/0/0` (ISP link) must also have `ip nat outside` for NAT to work with external traffic.

### 8.2 Static NAT Rules

```cisco
ip nat inside source static tcp 10.61.173.131 80 10.61.174.2 80
ip nat inside source static tcp 10.61.173.131 443 10.61.174.2 443
```

### 8.3 T2-Server-HTTP Configuration

- **IP Address:** 10.61.173.131
- **Subnet Mask:** 255.255.255.128
- **Default Gateway:** 10.61.173.129
- **DNS Server:** 10.61.173.130
- **Services:** HTTP (enabled), HTTPS (enabled)
- **Page:** `index.html` with Terminal 2 identification

### 8.4 Verification Commands

```cisco
show ip nat translations
show ip nat statistics
```

**Expected NAT Translations:**
```
Pro  Inside global     Inside local       Outside local      Outside global
tcp 10.61.174.2:80     10.61.173.131:80   ---                ---
tcp 10.61.174.2:443    10.61.173.131:443  ---                ---
```

### 8.5 NAT Testing

**Test HTTP Direct (from PC-Outside):**
```
http://10.61.173.131
```
Expected: Terminal 2 HTTP Server page loads.

**Test HTTP via NAT (from PC-Outside):**
```
http://10.61.174.2
```
Expected: Terminal 2 HTTP Server page loads (NAT redirect to 10.61.173.131).

**Test HTTPS via NAT (from PC-Outside):**
```
https://10.61.174.2
```
Expected: Terminal 2 HTTP Server page loads.

> **Note:** NAT redirect only works when accessing from **outside** the T2 network (e.g., from PC-Outside via ISP-Router). From inside networks (PC-Outlets), the traffic does not hit the NAT rule ” use direct access instead.
## 9. Static Firewall (ACLs)

> âš ï¸ **IMPORTANT:** ACLs are the **LAST** configuration step. Only apply after ALL other features are confirmed working.

### 9.1 ACL 100 ” Configuration

```cisco
access-list 100 deny ip 10.61.152.0 0.0.3.255 any
access-list 100 deny ip 10.61.128.0 0.0.7.255 any
access-list 100 deny ip 10.61.164.0 0.0.1.255 any
access-list 100 deny ip 10.61.173.128 0.0.0.127 any
access-list 100 permit icmp any any echo
access-list 100 permit icmp any any echo-reply
access-list 100 permit udp any host 10.61.173.130 eq 53
access-list 100 permit tcp any host 10.61.173.130 eq 53
access-list 100 permit tcp any host 10.61.173.131 eq 80
access-list 100 permit tcp any host 10.61.173.131 eq 443
access-list 100 permit ospf any any
access-list 100 permit udp any eq bootpc any eq bootps
access-list 100 permit udp any any eq 69
access-list 100 permit tcp any any eq 2000
access-list 100 permit ip any any
```

### 9.2 Apply ACL to Backbone Interface

```cisco
interface FastEthernet0/0.769
 ip access-group 100 in
```

### 9.3 ACL Rule Priority

| Priority | Rule | Purpose |
|----------|------|---------|
| 1st | Block spoofing (4 rules) | Anti-spoofing for T2 internal networks |
| 2nd | Allow ICMP echo/reply | Network diagnostics |
| 3rd | Allow DNS (53) to T2-Server | DNS resolution |
| 4th | Allow HTTP/HTTPS to T2-Server-HTTP | Web access |
| 5th | Allow OSPF | Dynamic routing |
| 6th | Allow DHCP | IP address assignment |
| 7th | Allow TFTP (69) | IP phone firmware |
| 8th | Allow VoIP SCCP (2000) | Cisco telephony |
| 9th | Permit all remaining | Transit traffic |

### 9.4 Anti-Spoofing Rules Detail

| Rule | Network | Wildcard | Covers |
|------|---------|----------|--------|
| deny ip 10.61.152.0 0.0.3.255 | t2-outlets /22 | 0.0.3.255 | 10.61.152.0 - 10.61.155.255 |
| deny ip 10.61.128.0 0.0.7.255 | t2-wifi /21 | 0.0.7.255 | 10.61.128.0 - 10.61.135.255 |
| deny ip 10.61.164.0 0.0.1.255 | t2-voip /23 | 0.0.1.255 | 10.61.164.0 - 10.61.165.255 |
| deny ip 10.61.173.128 0.0.0.127 | t2-servers /25 | 0.0.0.127 | 10.61.173.128 - 10.61.173.255 |

### 9.5 Verification Commands

```cisco
show access-lists 100
show ip interface FastEthernet0/0.769
```

**Expected ACL output:**
```
Extended IP access list 100
    deny ip 10.61.152.0 0.0.3.255 any
    deny ip 10.61.128.0 0.0.7.255 any
    deny ip 10.61.164.0 0.0.1.255 any
    deny ip 10.61.173.128 0.0.0.127 any
    permit icmp any any echo
    permit icmp any any echo-reply
    permit udp any host 10.61.173.130 eq domain
    permit tcp any host 10.61.173.130 eq domain
    permit tcp any host 10.61.173.131 eq www
    permit tcp any host 10.61.173.131 eq 443
    permit ospf any any
    permit udp any eq bootpc any eq bootps
    permit udp any any eq tftp
    permit tcp any any eq 2000
    permit ip any any
```

**Expected interface output:**
```
Inbound  access list is 100
```

### 9.6 Important Notes

1. **Do NOT use comments (!) in ACL commands** ” Packet Tracer rejects them
2. **Use `permit ospf any any`** ” not `permit 89 any any` (Packet Tracer doesn't accept protocol numbers)
3. **ACL must be created in correct order** ” anti-spoofing rules first, `permit ip any any` last
4. **If order is wrong**, delete and recreate: `no access-list 100` then re-add all rules
## 10. ISP-Router Configuration

The ISP-Router connects the T2-Router to the simulated external network (PC-Outside).

### 10.1 Interface Configuration

```cisco
hostname ISP-Router

interface FastEthernet0/0
 description Link to T2-Router Eth0/0/0
 ip address 89.73.67.118 255.255.255.252
 no shutdown

interface FastEthernet0/1
 description Simulated Internet Link
 ip address 209.165.200.225 255.255.255.252
 no shutdown
```

### 10.2 Static Routes

```cisco
ip route 10.61.128.0 255.255.128.0 89.73.67.117
ip route 0.0.0.0 0.0.0.0 209.165.200.226
```

### 10.3 Link Summary

| Link | Network | Device A | IP A | Device B | IP B |
|------|---------|----------|------|----------|------|
| ISP â†” T2 | 89.73.67.116/30 | ISP-Router Fa0/0 | 89.73.67.118 | T2-Router Eth0/0/0 | 89.73.67.117 |
| ISP â†” PC-Outside | 209.165.200.224/30 | ISP-Router Fa0/1 | 209.165.200.225 | PC-Outside Fa0 | 209.165.200.226 |

### 10.4 Verification Commands

```cisco
show ip route
show ip interface brief
ping 89.73.67.117
ping 10.61.174.2
ping 209.165.200.226
```

**Expected Routes:**
```
S  10.61.128.0/17 [1/0] via 89.73.67.117
C  89.73.67.116/30 is directly connected, FastEthernet0/0
C  209.165.200.224/30 is directly connected, FastEthernet0/1
S* 0.0.0.0/0 [1/0] via 209.165.200.226
```
## 11. Testing and Verification

### 11.1 OSPF Tests

```cisco
T2-Router#show ip ospf neighbor
```
Expected: T3 (3.3.3.3) FULL/BDR, T4 (4.4.4.4) FULL/DROTHER

```cisco
T2-Router#show ip route ospf
```
Expected: 8 OSPF IA routes for T3/T4 networks

```cisco
T2-Router#ping 10.61.174.3
T2-Router#ping 10.61.174.4
```
Expected: Both reply

### 11.2 DHCP Tests

**From PC-Outlets:**
```
C:\>ipconfig
```
Expected: IP 10.61.152.x, GW 10.61.152.1, DNS 10.61.173.130

```
C:\>ipconfig /release
C:\>ipconfig /renew
```
Expected: IP re-assigned correctly

**From Laptop-WiFi:**
```
C:\>ipconfig
```
Expected: IP 10.61.128.x, GW 10.61.128.1, DNS 10.61.173.130

### 11.3 VoIP Tests

```cisco
T2-Router#show telephony-service
```
Expected: 2 ephones registered

- **Local call:** 2001 â†’ 2002 (should connect)
- **Inter-terminal:** 2001 â†’ 3001 (should forward to T3-Router)

### 11.4 DNS Tests

**From PC-Outlets:**
```
C:\>nslookup server1.airport-campus-network
```
Expected: `Address: 10.61.173.131`

```
C:\>nslookup www.airport-campus-network
```
Expected: CNAME â†’ `10.61.173.131`

```
C:\>nslookup dns.airport-campus-network
```
Expected: CNAME â†’ `10.61.173.130`

```
C:\>nslookup ns.terminal-3.airport-campus-network
```
Expected: `Address: 10.61.172.10`

```
C:\>nslookup ns.terminal-4.airport-campus-network
```
Expected: `Address: 10.61.173.10`

### 11.5 NAT Tests

```cisco
T2-Router#show ip nat translations
```
Expected: tcp 10.61.174.2:80 â†’ 10.61.173.131:80, tcp 10.61.174.2:443 â†’ 10.61.173.131:443

**From PC-Outside:**
```
http://10.61.173.131
```
Expected: Terminal 2 HTTP page loads (direct)

```
http://10.61.174.2
```
Expected: Terminal 2 HTTP page loads (NAT redirect)

```
https://10.61.174.2
```
Expected: Terminal 2 HTTP page loads (NAT redirect)

### 11.6 ACL Tests

```cisco
T2-Router#show access-lists 100
```
Expected: 15 rules in correct order (deny spoofing first, permit ip any any last)

```cisco
T2-Router#show ip interface FastEthernet0/0.769
```
Expected: `Inbound access list is 100`

**After ACL, re-test all features:**
- OSPF neighbors still FULL
- DHCP release/renew works
- DNS resolution works
- HTTP/HTTPS via NAT works
- VoIP calls work
- Ping across all networks works

### 11.7 Connectivity Matrix

| From | To | Expected |
|------|-----|----------|
| PC-Outlets | 10.61.152.1 (GW) | Reply |
| PC-Outlets | 10.61.174.2 (backbone) | Reply |
| PC-Outlets | 10.61.174.3 (T3) | Reply |
| PC-Outlets | 10.61.174.4 (T4) | Reply |
| PC-Outlets | 10.61.173.130 (DNS) | Reply |
| PC-Outlets | 10.61.173.131 (HTTP) | Reply |
| PC-Outside | 10.61.174.2 (backbone) | Reply |
| PC-Outside | 10.61.173.131 (HTTP) | Reply |
| PC-Outside | 10.61.152.1 (outlets) | Reply |
| ISP-Router | 89.73.67.117 (T2) | Reply |
| ISP-Router | 10.61.174.2 (backbone) | Reply |
| T2-Server-HTTP | 10.61.173.129 (GW) | Reply |
| T2-Server-HTTP | 209.165.200.226 (PC-Outside) | Reply |

### 11.8 Summary of Test Results

| Component | Status |
|-----------|--------|
| OSPF dynamic routing (Area 2) | PASS |
| Default route redistribution | PASS |
| DHCPv4 service (outlets, wifi, voip) | PASS |
| VoIP service with call forwarding | PASS |
| DNS root domain | PASS |
| NAT (HTTP/HTTPS redirect) | PASS |
| Static Firewall (ACLs) | PASS |
| Internal endpoint connectivity | PASS |
| Internet link (to ISP-Router) | PASS |
| Inter-terminal connectivity (T3/T4) | PASS |
