RCOMP 2025-2026 Project 1 - Sprint 3 - Member 1240935 (Terminal 4)
===================================================================

This folder contains all outputs and artifacts produced by student 1240935 for Sprint 3.
The work focuses on updating the Packet Tracer simulation for **Terminal 4**, adding OSPF dynamic
routing, DHCPv4, VoIP, DNS, NAT, and static firewall (ACLs), as defined in the sprint planning.

### Author
- **Student Number:** 1240935
- **Assigned Task:** T.3.4
- **Terminal:** 4
- **Sprint Master:** 1240935

---

## 1. Sprint 3 Overview for Terminal 4

The goal for this task was to update the Packet Tracer simulation from Sprint 2 to include all new
features required in Sprint 3:

- **OSPF dynamic routing** replacing static routing (Area 4 for Terminal 4)
- **DHCPv4 service** for all internal networks (outlets, Wi-Fi and VoIP)
- **VoIP service** with Cisco Telephony, prefix 4, and call forwarding to T2 and T3
- **Second HTTP server** added to the Servers DMZ (T4-Server-HTTP)
- **DNS service** — subdomain `terminal-4.airport-campus-network`
- **NAT** — static redirect of HTTP/HTTPS from backbone interface to DNS server
- **Static Firewall (ACLs)** — traffic access policies enforced last, after all features tested

This simulation was developed based on the `campus.pkt` from Sprint 2, working on Terminal 4.

---

## 2. OSPF Dynamic Routing

Static routing was fully removed from T4-Router and replaced with OSPF dynamic routing.
Terminal 4 was assigned **OSPF Area 4**. The backbone VLAN 769 belongs to Area 0, shared by
all terminal routers.

### 2.1. Static Routes Removed

All previously configured static routes were removed from T4-Router. No default route was kept
on this router — the default route is redistributed via OSPF by T2-Router using
`default-information originate` and learned automatically by T4-Router through OSPF.

### 2.2. OSPF Configuration on T4-Router

```
router ospf 1
 router-id 4.4.4.4
 network 10.61.174.0 0.0.0.255 area 0        ! backbone VLAN 769 - area 0
 network 10.61.160.0 0.0.3.255 area 4        ! t4-outlets VLAN 779 - area 4
 network 10.61.144.0 0.0.7.255 area 4        ! t4-wifi VLAN 780 - area 4
 network 10.61.168.0 0.0.1.255 area 4        ! t4-voip VLAN 781 - area 4
 network 10.61.173.0 0.0.0.127 area 4        ! t4-servers VLAN 782 - area 4
```

### 2.3. OSPF Configuration on Other Routers

As per sprint planning, static routing was also replaced with OSPF on T2-Router and T3-Router.
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

**T3-Router (Area 3):**
```
router ospf 1
 router-id 3.3.3.3
 network 10.61.174.0 0.0.0.255 area 0
 network 10.61.156.0 0.0.3.255 area 3
 network 10.61.136.0 0.0.7.255 area 3
 network 10.61.166.0 0.0.1.255 area 3
 network 10.61.172.0 0.0.0.255 area 3
```

---

## 3. DHCPv4 Service

T4-Router was configured to provide DHCPv4 to all internal networks within Terminal 4, except
the Servers DMZ (VLAN 782), the Switches DMZ (VLAN 770), and the backbone (VLAN 769),
which retain static addressing.

### 3.1. Excluded Addresses

The following addresses were excluded from DHCP pools to protect static assignments:

```
ip dhcp excluded-address 10.61.160.1          ! gateway t4-outlets
ip dhcp excluded-address 10.61.144.1          ! gateway t4-wifi
ip dhcp excluded-address 10.61.168.1          ! gateway t4-voip
ip dhcp excluded-address 10.61.173.1          ! gateway t4-servers
ip dhcp excluded-address 10.61.173.10         ! T4-Server (DNS)
ip dhcp excluded-address 10.61.173.11         ! T4-Server-HTTP
```

### 3.2. DHCP Pools

Three pools were configured, one per dynamic VLAN. The VoIP pool includes option 150 with
the address of the TFTP server (T4-Router itself at 10.61.168.1) required by Cisco 7960 phones
to download their configuration file.

```
ip dhcp pool T4-outlets-pool
 network 10.61.160.0 255.255.252.0
 default-router 10.61.160.1
 dns-server 10.61.173.10
 domain-name terminal-4.airport-campus-network

ip dhcp pool T4-wifi-pool
 network 10.61.144.0 255.255.248.0
 default-router 10.61.144.1
 dns-server 10.61.173.10
 domain-name terminal-4.airport-campus-network

ip dhcp pool T4-voip-pool
 network 10.61.168.0 255.255.254.0
 default-router 10.61.168.1
 dns-server 10.61.173.10
 domain-name terminal-4.airport-campus-network
 option 150 ip 10.61.168.1
```

### 3.3. DHCP Client Table

| Device         | VLAN | IP Assignment          | DNS Server   | Domain                       |
|----------------|------|------------------------|--------------|------------------------------|
| T4-PC-Outlets  | 779  | DHCP (T4-outlets-pool) | 10.61.173.10 | terminal-4.airport-campus-network |
| T4-Laptop-WiFi | 780  | DHCP (T4-wifi-pool)    | 10.61.173.10 | terminal-4.airport-campus-network |
| T4-VoIP        | 781  | DHCP (T4-voip-pool)    | 10.61.173.10 | terminal-4.airport-campus-network |
| T4-VoIP-2      | 781  | DHCP (T4-voip-pool)    | 10.61.173.10 | terminal-4.airport-campus-network |

---

## 4. VoIP Service

### 4.1. Numbering Plan

| Terminal       | Prefix | Local Numbers   |
|----------------|--------|-----------------|
| Terminal 2     | 2      | 2001 – 2999     |
| Terminal 3     | 3      | 3001 – 3999     |
| **Terminal 4** | **4**  | **4001 – 4002** |

### 4.2. Cisco Telephony Service

Cisco CME (Call Manager Express) was configured on T4-Router to provide VoIP service for the
local VoIP network. Two IP phones (model 7960) were registered: T4-VoIP and T4-VoIP-2,
assigned numbers 4001 and 4002 respectively.

```
telephony-service
 max-ephones 2
 max-dn 2
 ip source-address 10.61.168.1 port 2000
 auto assign 1 to 2

ephone-dn 1
 number 4001

ephone-dn 2
 number 4002
```

### 4.3. Inter-Terminal Call Forwarding

Two VoIP dial-peers were configured to forward calls with prefixes 2 and 3 to the respective
terminal routers via the Campus Backbone, and two POTS dial-peers for local phones:

```
dial-peer voice 1 pots
 destination-pattern 4...
 port 50/0/0

dial-peer voice 2 pots
 destination-pattern 4...
 port 50/0/1

dial-peer voice 10 voip
 destination-pattern 2...
 session target ipv4:10.61.174.2

dial-peer voice 20 voip
 destination-pattern 3...
 session target ipv4:10.61.174.3
```

### 4.4. Switch Port Configuration for VoIP Phones

All switch ports connected to Cisco 7960 phones were configured with voice VLAN enabled
and access VLAN disabled, as required:

```
interface FastEthernet X/X
 switchport mode access
 no switchport access vlan
 switchport voice vlan 781
```

---

## 5. HTTP Server

A second server, `T4-Server-HTTP`, was added to the Servers DMZ (VLAN 782) with static
IP address `10.61.173.11/25`. The HTTP and HTTPS services were enabled on this server, and
a simple HTML page was created identifying it as Terminal 4's HTTP server.

The existing T4-Server (DNS/ns at 10.61.173.10) also had HTTP and HTTPS services enabled,
as required for the NAT redirection described in section 7.

---

## 6. DNS Service

### 6.1. Domain Hierarchy

Terminal 4 was assigned the subdomain `terminal-4.airport-campus-network`, a subdomain of the
root domain `airport-campus-network` hosted by Terminal 2. The DNS name server for Terminal 4
is the existing T4-Server at `10.61.173.10`, identified as `ns.terminal-4.airport-campus-network`.

| Terminal  | Domain                             | Name Server                           | NS IP            |
|-----------|------------------------------------|---------------------------------------|------------------|
| T2 (root) | `airport-campus-network`                | `ns.airport-campus-network`                | 10.61.173.129    |
| T3        | `terminal-3.airport-campus-network`     | `ns.terminal-3.airport-campus-network`     | 10.61.172.10     |
| **T4**    | **`terminal-4.airport-campus-network`** | **`ns.terminal-4.airport-campus-network`** | **10.61.173.10** |

### 6.2. DNS Records Configured on T4-Server

The following records were added to the DNS database on T4-Server:

| Type     | Name                                   | Value                                  | Description                 |
|----------|----------------------------------------|----------------------------------------|-----------------------------|
| A        | `ns.terminal-4.airport-campus-network`      | `10.61.173.10`                         | T4 name server              |
| A        | `server1.terminal-4.airport-campus-network` | `10.61.173.11`                         | HTTP server                 |
| CNAME    | `www.terminal-4.airport-campus-network`     | `server1.terminal-4.airport-campus-network` | Web alias                   |
| CNAME    | `web.terminal-4.airport-campus-network`     | `server1.terminal-4.airport-campus-network` | Web alias                   |
| CNAME    | `dns.terminal-4.airport-campus-network`     | `ns.terminal-4.airport-campus-network`      | DNS alias                   |
| NS       | `terminal-4.airport-campus-network`         | `ns.terminal-4.airport-campus-network`      | NS record for this domain   |
| A (glue) | `ns.airport-campus-network`                 | `10.61.173.129`                        | Root DNS server glue record |

All names are FQDNs ending in `airport-campus-network`, without a trailing dot, consistently across
the team. The glue record for the root domain is required so that T4's DNS server can delegate
resolution of names outside its domain to the root server at T2.

### 6.3. DNS Configuration on Static Devices

The DNS server address and domain name were manually configured on both servers with static IPs:

| Device                        | DNS Server   | Domain                       |
|-------------------------------|--------------|------------------------------|
| T4-Server (10.61.173.10)      | 10.61.173.10 | terminal-4.airport-campus-network |
| T4-Server-HTTP (10.61.173.11) | 10.61.173.10 | terminal-4.airport-campus-network |

DHCP clients (T4-PC-Outlets, T4-Laptop-WiFi, VoIP phones) receive DNS configuration
automatically through their respective DHCP pools.

---

## 7. NAT — Static HTTP/HTTPS Redirect

Static NAT was configured on T4-Router to redirect HTTP (port 80) and HTTPS (port 443)
requests received on the backbone interface (10.61.174.4) to T4-Server (10.61.173.10),
the local DNS server that has HTTP/HTTPS enabled.

The backbone subinterface was marked as NAT outside, and all internal subinterfaces
were marked as NAT inside:

```
interface FastEthernet0/0.769
 ip nat outside

interface FastEthernet0/0.779
 ip nat inside
interface FastEthernet0/0.780
 ip nat inside
interface FastEthernet0/0.781
 ip nat inside
interface FastEthernet0/0.782
 ip nat inside

ip nat inside source static tcp 10.61.173.10 80 10.61.174.4 80
ip nat inside source static tcp 10.61.173.10 443 10.61.174.4 443
```

---

## 8. Static Firewall (ACLs)

ACLs were configured last, after all other features were verified as fully operational.
The named extended ACL `BACKBONE-IN` was applied inbound on the backbone interface
(FastEthernet0/0.769), enforcing the following policies in priority order:

| Priority | Policy Applied                                                                                                                                                    |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1st      | Anti-spoofing: blocks traffic arriving from backbone with source IPs belonging to T4 internal networks                                                            |
| 2nd      | ICMP echo and echo-reply always permitted                                                                                                                         |
| 3rd      | All traffic to DMZ blocked except DNS (UDP/TCP 53) and HTTP/HTTPS (TCP 80/443) to T4-Server; all traffic from DMZ permitted                                       |
| 4th      | Traffic directed to the router blocked, with exceptions for OSPF (protocol 89), DHCP (UDP 67), TFTP (UDP 69), SCCP/VoIP (TCP 2000), and NAT-translated HTTP/HTTPS |
| 5th      | All remaining pass-through traffic permitted                                                                                                                      |

```
ip access-list extended BACKBONE-IN
 ! Anti-spoofing
 deny ip 10.61.160.0 0.0.3.255 any
 deny ip 10.61.144.0 0.0.7.255 any
 deny ip 10.61.168.0 0.0.1.255 any
 deny ip 10.61.173.0 0.0.0.127 any
 deny ip 10.61.170.0 0.0.1.255 any
 ! ICMP
 permit icmp any any echo
 permit icmp any any echo-reply
 ! DMZ access
 permit udp any host 10.61.173.10 eq 53
 permit tcp any host 10.61.173.10 eq 53
 permit tcp any host 10.61.173.10 eq 80
 permit tcp any host 10.61.173.10 eq 443
 permit tcp any host 10.61.173.11 eq 80
 permit tcp any host 10.61.173.11 eq 443
 deny ip any 10.61.173.0 0.0.0.127
 ! Router services
 permit ospf any any
 permit udp host 0.0.0.0 host 255.255.255.255 eq 67
 permit udp any any eq 69
 permit tcp any any eq 2000
 permit tcp any host 10.61.174.4 eq 80
 permit tcp any host 10.61.174.4 eq 443
 ! Deny router
 deny ip any host 10.61.174.4
 deny ip any host 10.61.160.1
 deny ip any host 10.61.144.1
 deny ip any host 10.61.168.1
 deny ip any host 10.61.173.1
 ! Pass-through
 permit ip any any

interface FastEthernet0/0.769
 ip access-group BACKBONE-IN in
```

---

## 9. IPv4 Addressing Plan for Terminal 4

### 9.1. VLAN Table

| VLAN Name    | VLAN ID | IPv4 Network   | Mask            | Gateway     | Address Assignment |
|--------------|---------|----------------|-----------------|-------------|--------------------|
| backbone     | 769     | 10.61.174.0/24 | 255.255.255.0   | —           | Static             |
| switches-dmz | 770     | 10.61.170.0/23 | 255.255.254.0   | —           | Static (isolated)  |
| t4-outlets   | 779     | 10.61.160.0/22 | 255.255.252.0   | 10.61.160.1 | DHCP               |
| t4-wifi      | 780     | 10.61.144.0/21 | 255.255.248.0   | 10.61.144.1 | DHCP               |
| t4-voip      | 781     | 10.61.168.0/23 | 255.255.254.0   | 10.61.168.1 | DHCP + option 150  |
| t4-servers   | 782     | 10.61.173.0/25 | 255.255.255.128 | 10.61.173.1 | Static             |

### 9.2. Key Device IP Addresses

| Device              | Role            | IP              | VLAN |
|---------------------|-----------------|-----------------|------|
| T4-Router Fa0/0.769 | Backbone        | 10.61.174.4/24  | 769  |
| T4-Router Fa0/0.779 | Gateway Outlets | 10.61.160.1/22  | 779  |
| T4-Router Fa0/0.780 | Gateway Wi-Fi   | 10.61.144.1/21  | 780  |
| T4-Router Fa0/0.781 | Gateway VoIP    | 10.61.168.1/23  | 781  |
| T4-Router Fa0/0.782 | Gateway Servers | 10.61.173.1/25  | 782  |
| T4-Server           | DNS / ns / HTTP | 10.61.173.10/25 | 782  |
| T4-Server-HTTP      | HTTP (server1)  | 10.61.173.11/25 | 782  |

---