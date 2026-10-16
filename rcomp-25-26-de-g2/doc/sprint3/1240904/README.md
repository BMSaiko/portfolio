RCOMP 2025-2026 Project 1 - Sprint 3 - Member 1240904 (Terminal 3)
===================================================================

This folder contains all outputs and artifacts produced by student 1240904 for Sprint 3.
The work focuses on updating the Packet Tracer simulation for **Terminal 3**, adding OSPF dynamic
routing, DHCPv4, VoIP, DNS, NAT, and static firewall (ACLs), as defined in the sprint planning.
The additional final task of integrating all members' work into `campus.pkt` is also included.

### Author
- **Student Number:** 1240904
- **Assigned Task:** T.3.2
- **Terminal:** 3
- **Sprint Master:** 1240935

---

## 1. Sprint 3 Overview for Terminal 3

The goal for this task was to update the Packet Tracer simulation from Sprint 2 to include all new
features required in Sprint 3:

- **OSPF dynamic routing** replacing static routing (Area 3 for Terminal 3)
- **DHCPv4 service** for all internal networks (outlets, Wi-Fi and VoIP)
- **VoIP service** with Cisco Telephony, prefix 3, and call forwarding to T2 and T4
- **Second HTTP server** added to the Servers DMZ (T3-Server-HTTP)
- **DNS service** — subdomain `terminal-3.rcomp-25-26-de-g2`
- **NAT** — static redirect of HTTP/HTTPS from backbone interface to DNS server
- **Static Firewall (ACLs)** — traffic access policies enforced last, after all features tested

This simulation was developed based on the `campus.pkt` from Sprint 2, working on Terminal 3.
The final integration into `campus.pkt` was also carried out by this team member.

---

## 2. OSPF Dynamic Routing

Static routing was fully removed from T3-Router and replaced with OSPF dynamic routing.
Terminal 3 was assigned **OSPF Area 3**. The backbone VLAN 769 belongs to Area 0, shared by
all terminal routers.

### 2.1. Static Routes Removed

All previously configured static routes were removed from T3-Router. No default route was kept
on this router — the default route is redistributed via OSPF by T2-Router using
`default-information originate` and learned automatically by T3-Router through OSPF.

### 2.2. OSPF Configuration on T3-Router

```
router ospf 1
 router-id 3.3.3.3
 network 10.61.174.0 0.0.0.255 area 0        ! backbone VLAN 769 - area 0
 network 10.61.156.0 0.0.3.255 area 3        ! t3-outlets VLAN 775 - area 3
 network 10.61.136.0 0.0.7.255 area 3        ! t3-wifi VLAN 776 - area 3
 network 10.61.166.0 0.0.1.255 area 3        ! t3-voip VLAN 777 - area 3
 network 10.61.172.0 0.0.0.255 area 3        ! t3-servers VLAN 778 - area 3
```

### 2.3. OSPF Configuration on Other Routers

As per sprint planning, static routing was also replaced with OSPF on T2-Router and T4-Router.
No other changes were made to those routers.

**T2-Router (Area 2):**
```
router ospf 1
 router-id 2.2.2.2
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.152.0 0.0.3.255 area 2
 network 10.61.128.0 0.0.7.255 area 2
 network 10.61.164.0 0.0.1.255 area 2
 network 10.61.173.128 0.0.0.127 area 2
 default-information originate
```

**T4-Router (Area 4):**
```
router ospf 1
 router-id 4.4.4.4
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.160.0 0.0.3.255 area 4
 network 10.61.144.0 0.0.7.255 area 4
 network 10.61.168.0 0.0.1.255 area 4
 network 10.61.173.0 0.0.0.127 area 4
```

---

## 3. DHCPv4 Service

T3-Router was configured to provide DHCPv4 to all internal networks within Terminal 3, except
the Servers DMZ (VLAN 778), the Switches DMZ (VLAN 770), and the backbone (VLAN 769),
which retain static addressing.

### 3.1. Excluded Addresses

The following addresses were excluded from DHCP pools to protect static assignments:

```
ip dhcp excluded-address 10.61.156.1          ! gateway t3-outlets
ip dhcp excluded-address 10.61.136.1          ! gateway t3-wifi
ip dhcp excluded-address 10.61.166.1          ! gateway t3-voip
ip dhcp excluded-address 10.61.172.1          ! gateway t3-servers
ip dhcp excluded-address 10.61.172.10         ! T3-Server (DNS)
ip dhcp excluded-address 10.61.172.11         ! T3-Server-HTTP
```

### 3.2. DHCP Pools

Three pools were configured, one per dynamic VLAN. The VoIP pool includes option 150 with
the address of the TFTP server (T3-Router itself at 10.61.166.1) required by Cisco 7960 phones
to download their configuration file.

```
ip dhcp pool t3-outlets-pool
 network 10.61.156.0 255.255.252.0
 default-router 10.61.156.1
 dns-server 10.61.172.10
 domain-name terminal-3.rcomp-25-26-de-g2

ip dhcp pool t3-wifi-pool
 network 10.61.136.0 255.255.248.0
 default-router 10.61.136.1
 dns-server 10.61.172.10
 domain-name terminal-3.rcomp-25-26-de-g2

ip dhcp pool t3-voip-pool
 network 10.61.166.0 255.255.254.0
 default-router 10.61.166.1
 dns-server 10.61.172.10
 domain-name terminal-3.rcomp-25-26-de-g2
 option 150 ip 10.61.166.1
```

### 3.3. DHCP Client Table

| Device | VLAN | IP Assignment | DNS Server | Domain |
|---|---|---|---|---|
| T3-PC-Outlets | 775 | DHCP (t3-outlets-pool) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |
| T3-Laptop-WiFi | 776 | DHCP (t3-wifi-pool) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |
| T3-VoIP | 777 | DHCP (t3-voip-pool) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |
| T3-VoIP-2 | 777 | DHCP (t3-voip-pool) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |

---

## 4. VoIP Service

### 4.1. Numbering Plan

| Terminal | Prefix | Local Numbers |
|---|---|---|
| Terminal 2 | 2 | 2001 – 2999 |
| **Terminal 3** | **3** | **3001 – 3002** |
| Terminal 4 | 4 | 4001 – 4999 |

### 4.2. Cisco Telephony Service

Cisco CME (Call Manager Express) was configured on T3-Router to provide VoIP service for the
local VoIP network. Two IP phones (model 7960) were registered: T3-VoIP and T3-VoIP-2,
assigned numbers 3001 and 3002 respectively.

```
telephony-service
 max-ephones 2
 max-dn 2
 ip source-address 10.61.166.1 port 2000
 auto assign 1 to 2

ephone-dn 1
 number 3001

ephone-dn 2
 number 3002
```

### 4.3. Inter-Terminal Call Forwarding

Two dial-peers were configured to forward calls with prefixes 2 and 4 to the respective
terminal routers via the Campus Backbone:

```
dial-peer voice 2 voip
 destination-pattern 2...
 session target ipv4:10.61.174.2

dial-peer voice 4 voip
 destination-pattern 4...
 session target ipv4:10.61.174.4
```

### 4.4. Switch Port Configuration for VoIP Phones

All switch ports connected to Cisco 7960 phones were configured with voice VLAN enabled
and access VLAN disabled, as required:

```
interface FastEthernet X/X
 switchport mode access
 no switchport access vlan
 switchport voice vlan 777
```

---

## 5. HTTP Server

A second server, `T3-Server-HTTP`, was added to the Servers DMZ (VLAN 778) with static
IP address `10.61.172.11/24`. The HTTP and HTTPS services were enabled on this server, and
a simple HTML page was created identifying it as Terminal 3's HTTP server.

The existing T3-Server (DNS/ns at 10.61.172.10) also had HTTP and HTTPS services enabled,
as required for the NAT redirection described in section 7.

---

## 6. DNS Service

### 6.1. Domain Hierarchy

Terminal 3 was assigned the subdomain `terminal-3.rcomp-25-26-de-g2`, a subdomain of the
root domain `rcomp-25-26-de-g2` hosted by Terminal 2. The DNS name server for Terminal 3
is the existing T3-Server at `10.61.172.10`, identified as `ns.terminal-3.rcomp-25-26-de-g2`.

| Terminal | Domain | Name Server | NS IP |
|---|---|---|---|
| T2 (root) | `rcomp-25-26-de-g2` | `ns.rcomp-25-26-de-g2` | 10.61.173.129 |
| **T3** | **`terminal-3.rcomp-25-26-de-g2`** | **`ns.terminal-3.rcomp-25-26-de-g2`** | **10.61.172.10** |
| T4 | `terminal-4.rcomp-25-26-de-g2` | `ns.terminal-4.rcomp-25-26-de-g2` | 10.61.173.10 |

### 6.2. DNS Records Configured on T3-Server

The following records were added to the DNS database on T3-Server:

| Type | Name | Value | Description |
|---|---|---|---|
| A | `ns.terminal-3.rcomp-25-26-de-g2` | `10.61.172.10` | T3 name server |
| A | `server1.terminal-3.rcomp-25-26-de-g2` | `10.61.172.11` | HTTP server |
| CNAME | `www.terminal-3.rcomp-25-26-de-g2` | `server1.terminal-3.rcomp-25-26-de-g2` | Web alias |
| CNAME | `web.terminal-3.rcomp-25-26-de-g2` | `server1.terminal-3.rcomp-25-26-de-g2` | Web alias |
| CNAME | `dns.terminal-3.rcomp-25-26-de-g2` | `ns.terminal-3.rcomp-25-26-de-g2` | DNS alias |
| NS | `terminal-3.rcomp-25-26-de-g2` | `ns.terminal-3.rcomp-25-26-de-g2` | NS record for this domain |
| A (glue) | `ns.rcomp-25-26-de-g2` | `10.61.173.129` | Root DNS server glue record |

All names are FQDNs ending in `rcomp-25-26-de-g2`, without a trailing dot, consistently across
the team. The glue record for the root domain is required so that T3's DNS server can delegate
resolution of names outside its domain to the root server at T2.

### 6.3. DNS Configuration on Static Devices

The DNS server address and domain name were manually configured on both servers with static IPs:

| Device | DNS Server | Domain |
|---|---|---|
| T3-Server (10.61.172.10) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |
| T3-Server-HTTP (10.61.172.11) | 10.61.172.10 | terminal-3.rcomp-25-26-de-g2 |

DHCP clients (T3-PC-Outlets, T3-Laptop-WiFi, VoIP phones) receive DNS configuration
automatically through their respective DHCP pools.

---

## 7. NAT — Static HTTP/HTTPS Redirect

Static NAT was configured on T3-Router to redirect HTTP (port 80) and HTTPS (port 443)
requests received on the backbone interface (10.61.174.3) to T3-Server (10.61.172.10),
the local DNS server that has HTTP/HTTPS enabled.

The backbone subinterface was marked as NAT outside, and all internal subinterfaces
were marked as NAT inside:

```
interface FastEthernet0/0.769
 ip nat outside

interface FastEthernet0/0.775
 ip nat inside
interface FastEthernet0/0.776
 ip nat inside
interface FastEthernet0/0.777
 ip nat inside
interface FastEthernet0/0.778
 ip nat inside

ip nat inside source static tcp 10.61.172.10 80 10.61.174.3 80
ip nat inside source static tcp 10.61.172.10 443 10.61.174.3 443
```

---

## 8. Static Firewall (ACLs)

ACLs were configured last, after all other features were verified as fully operational.
Access list 100 was applied inbound on the backbone interface (FastEthernet0/0.769),
enforcing the following policies in priority order:

| Priority | Policy Applied |
|---|---|
| 1st | Anti-spoofing: blocks traffic arriving from backbone with source IPs belonging to T3 internal networks |
| 2nd | ICMP echo and echo-reply always permitted |
| 3rd | All traffic to DMZ blocked except DNS (UDP/TCP 53) and HTTP/HTTPS (TCP 80/443) to T3-Server; all traffic from DMZ permitted |
| 4th | Traffic directed to the router blocked, with exceptions for OSPF (protocol 89), DHCP (UDP 67), TFTP (UDP 69), SCCP/VoIP (TCP 2000), and NAT-translated HTTP/HTTPS |
| 5th | All remaining pass-through traffic permitted |

```
! Anti-spoofing
access-list 100 deny ip 10.61.156.0 0.0.3.255 any
access-list 100 deny ip 10.61.136.0 0.0.7.255 any
access-list 100 deny ip 10.61.166.0 0.0.1.255 any
access-list 100 deny ip 10.61.172.0 0.0.0.255 any
access-list 100 deny ip 10.61.170.0 0.0.1.255 any
! ICMP
access-list 100 permit icmp any any echo
access-list 100 permit icmp any any echo-reply
! DMZ access
access-list 100 permit udp any host 10.61.172.10 eq 53
access-list 100 permit tcp any host 10.61.172.10 eq 53
access-list 100 permit tcp any host 10.61.172.10 eq 80
access-list 100 permit tcp any host 10.61.172.10 eq 443
access-list 100 permit tcp any host 10.61.172.11 eq 80
access-list 100 permit tcp any host 10.61.172.11 eq 443
access-list 100 deny ip any 10.61.172.0 0.0.0.255
! Router services
access-list 100 permit ospf any any
access-list 100 permit udp host 0.0.0.0 host 255.255.255.255 eq 67
access-list 100 permit udp any any eq 69
access-list 100 permit tcp any any eq 2000
access-list 100 permit tcp any host 10.61.174.3 eq 80
access-list 100 permit tcp any host 10.61.174.3 eq 443
! Deny router
access-list 100 deny ip any host 10.61.174.3
access-list 100 deny ip any host 10.61.156.1
access-list 100 deny ip any host 10.61.136.1
access-list 100 deny ip any host 10.61.166.1
access-list 100 deny ip any host 10.61.172.1
! Pass-through
access-list 100 permit ip any any

interface FastEthernet0/0.769
 ip access-group 100 in
```

---

## 9. IPv4 Addressing Plan for Terminal 3

### 9.1. VLAN Table

| VLAN Name | VLAN ID | IPv4 Network | Mask | Gateway | Address Assignment |
|---|---|---|---|---|---|
| backbone | 769 | 10.61.174.0/24 | 255.255.255.0 | — | Static |
| switches-dmz | 770 | 10.61.170.0/23 | 255.255.254.0 | — | Static (isolated) |
| t3-outlets | 775 | 10.61.156.0/22 | 255.255.252.0 | 10.61.156.1 | DHCP |
| t3-wifi | 776 | 10.61.136.0/21 | 255.255.248.0 | 10.61.136.1 | DHCP |
| t3-voip | 777 | 10.61.166.0/23 | 255.255.254.0 | 10.61.166.1 | DHCP + option 150 |
| t3-servers | 778 | 10.61.172.0/24 | 255.255.255.0 | 10.61.172.1 | Static |

### 9.2. Key Device IP Addresses

| Device | Role | IP | VLAN |
|---|---|---|---|
| T3-Router Fa0/0.769 | Backbone | 10.61.174.3/24 | 769 |
| T3-Router Fa0/0.775 | Gateway Outlets | 10.61.156.1/22 | 775 |
| T3-Router Fa0/0.776 | Gateway Wi-Fi | 10.61.136.1/21 | 776 |
| T3-Router Fa0/0.777 | Gateway VoIP | 10.61.166.1/23 | 777 |
| T3-Router Fa0/0.778 | Gateway Servers | 10.61.172.1/24 | 778 |
| T3-Server | DNS / ns / HTTP | 10.61.172.10/24 | 778 |
| T3-Server-HTTP | HTTP (server1) | 10.61.172.11/24 | 778 |

---

## 10. Testing and Verification

### 10.1. OSPF Connectivity

| Test | Expected Result | Result |
|---|---|---|
| `show ip ospf neighbor` on T3-Router | T2-Router and T4-Router listed as neighbors | ✅ |
| `show ip route ospf` on T3-Router | O routes for T2 and T4 networks + O*E2 default route | ✅ |
| `ping 10.61.174.2` from T3-Router | T2-Router responds | ✅ |
| `ping 10.61.174.4` from T3-Router | T4-Router responds | ✅ |
| `ping 10.61.152.1` from T3-Router | T2 outlets gateway responds | ✅ |
| `ping 10.61.160.1` from T3-Router | T4 outlets gateway responds | ✅ |

### 10.2. DHCP Service

| Test | Expected Result | Result |
|---|---|---|
| T3-PC-Outlets `ipconfig` | IP: 10.61.156.x, GW: 10.61.156.1, DNS: 10.61.172.10 | ✅ |
| T3-Laptop-WiFi `ipconfig` | IP: 10.61.136.x, GW: 10.61.136.1, DNS: 10.61.172.10 | ✅ |
| `show ip dhcp binding` on T3-Router | Active leases for PC and laptop | ✅ |

### 10.3. VoIP Service

| Test                     | Expected Result | Result |
|--------------------------|---|---|
| T3-VoIP dials 3001       | T3-VoIP-2 rings (local call) | ✅ |
| T3-VoIP dials 2001       | Forwarded to T2-VoIP | ✅ |
| T3-VoIP dials 4001       | Forwarded to T4-VoIP | ✅ |
| `show telephony-service` | 2 ephones, source 10.61.166.1 port 2000 | ✅ |
| `show ephone registered` | 2 phones registered | ✅ |

### 10.4. DNS Service

From T3-PC-Outlets (DNS auto-configured via DHCP):

| Query | Expected Result | Result |
|---|---|---|
| `nslookup ns.terminal-3.rcomp-25-26-de-g2` | 10.61.172.10 | ✅ |
| `nslookup server1.terminal-3.rcomp-25-26-de-g2` | 10.61.172.11 | ✅ |
| `nslookup www.terminal-3.rcomp-25-26-de-g2` | 10.61.172.11 | ✅ |
| `nslookup ns.rcomp-25-26-de-g2` | 10.61.173.129 | ✅ |
| `nslookup ns.terminal-4.rcomp-25-26-de-g2` | 10.61.173.10 (via root T2) | ✅ |

### 10.5. NAT and HTTP

| Test | Expected Result | Result |
|---|---|---|
| Browser to `http://10.61.174.3` from T2-PC-Outlets | T3-Server HTML page displayed | ✅ |
| `show ip nat translations` on T3-Router | TCP entries for ports 80 and 443 | ✅ |

### 10.6. ACL Verification

| Test | Expected Result | Result |
|---|---|---|
| `ping` from T2 to T3-PC-Outlets | Allowed (ICMP permitted) | ✅ |
| DNS query to 10.61.172.10 | Allowed | ✅ |
| HTTP to 10.61.174.3 | Allowed and redirected by NAT | ✅ |
| VoIP call T3 → T2 after ACL enforcement | Still works | ✅ |
| Packet with spoofed T3 source IP from backbone | Blocked | ✅ |
| Traffic to T3 DMZ not matching DNS/HTTP rules | Blocked | ✅ |

---

## 11. Summary of Test Results

| Component | Status |
|---|---|
| OSPF dynamic routing (Area 3) | ✅ Fully operational |
| Static routes removed | ✅ Confirmed |
| DHCPv4 (outlets, Wi-Fi, VoIP) | ✅ Fully operational |
| VoIP with call forwarding (T2, T4) | ✅ Fully operational |
| DNS subdomain `terminal-3.rcomp-25-26-de-g2` | ✅ Fully operational |
| HTTP server (T3-Server-HTTP) | ✅ Active with HTML page |
| NAT HTTP/HTTPS redirect to T3-Server | ✅ Fully operational |
| Static Firewall (ACLs) | ✅ Configured and tested |
| campus.pkt integration | ✅ Delivered |

All mandatory requirements for Sprint 3 (Terminal 3) have been met. The simulation is stable
and the final campus.pkt integration was completed successfully.

---