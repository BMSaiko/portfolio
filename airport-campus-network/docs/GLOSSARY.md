# Glossary — RCOMP airport campus network

> Documentation added in 2026 (portfolio). Terms as used in the team's documents ([INDEX](INDEX.md)); definitions are general networking knowledge, with the project-specific usage noted. Portuguese version: [GLOSSARY.pt.md](GLOSSARY.pt.md).

## Cabling (Sprint 1)

| Term | Meaning |
|---|---|
| MC / MCC | Main Cross-Connect — campus distribution point; placed in Terminal 2 |
| IC / ICC | Intermediate Cross-Connect — per-building distribution point |
| HC / HCC | Horizontal Cross-Connect — floor/zone distribution to the outlets |
| CP | Consolidation Point — passive point between an HC and the outlets |
| Outlet / TO | Wall socket for user devices (keystone CAT7) |
| T568B | Colour order of the pairs on RJ45 connectors; the team's chosen standard |
| CAT7 S/FTP | Shielded twisted-pair copper cable used for horizontal cabling (max 90 m) |
| OM4 | Multimode fibre used for building and campus backbones, 24 fibres |
| LSZH | Low-smoke zero-halogen cable jacket |
| Patch panel | Passive panel that terminates cables in a cabinet |
| 42U / 24U / 18U … | Rack cabinet heights in rack units |
| Backbone | Link between cross-connects: building backbone (HC↔IC) or campus backbone (between terminals) |

## Layer 2 / 3 (Sprint 2)

| Term | Meaning |
|---|---|
| VLAN | Virtual LAN; this project uses IDs 769–782 |
| VTP | VLAN Trunking Protocol; domain `rc2526deg2`, the IC switch is the server |
| Trunk | Switch link carrying all VLANs |
| STP | Spanning Tree Protocol; blocks loops from the redundant rings and is never disabled |
| Router-on-a-stick | One router interface split into sub-interfaces per VLAN (for example `Fa0/0.769`) |
| DMZ | Segment for servers (or switch management) with restricted access |
| `.pkt` | Cisco Packet Tracer simulation file |
| Running config | Text dump of a device's active configuration |

## Services (Sprint 3)

| Term | Meaning |
|---|---|
| OSPF / area | Link-state routing protocol; area 0 = campus backbone, areas 2, 3, 4 = terminals T2, T3, T4 |
| `default-information originate` | Makes a router advertise its default route in OSPF (T2 does this) |
| DHCPv4 | Automatic IPv4 addressing; one router-based pool per network |
| DNS: A / CNAME / NS / glue | Address record / alias / name-server delegation / address of the delegated name server |
| FQDN | Fully qualified domain name, for example `server1.airport-campus-network` |
| Root domain | `airport-campus-network`, served by T2; `terminal-3` and `terminal-4` are delegated sub-domains |
| VoIP / telephony service | IP phones registered on the router (SCCP, TCP 2000); numbering plan and dial-peers route calls between terminals |
| Dial-peer | Call-routing rule on a Cisco voice router |
| Static NAT | Fixed translation, here HTTP/HTTPS (ports 80/443) from the backbone address to the local server |
| ACL | Access control list; used as a static firewall (anti-spoofing, ICMP allowed, DMZ and router protection) |
