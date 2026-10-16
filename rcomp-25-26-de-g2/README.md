# RCOMP 2025/26 — Airport campus network

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Design and simulation of the network of an airport campus with four terminals, over three sprints.

| | |
|---|---|
| **Context** | LEI — Computer Networks (RCOMP) |
| **Year** | 2025/26 (Feb–May 2026) |
| **Team** | Group of 3 (class DE, group 2) |
| **Stack** | Cisco Packet Tracer · structured cabling (T568B) · VLAN/VTP/STP · OSPF · DHCPv4 · DNS · VoIP · NAT · ACLs |

## My contribution
- **Terminal 2** owner in all sprints (cabling design, simulation, services).
- Sprint master of Sprint 1; coordinated the global technical decisions (e.g. T568B standard) and the global inventory.

## What this project shows
- Structured cabling design with schematics, measurements and standards.
- Layer 2/3 network engineering: **VLANs, VTP, trunking, STP redundancy**, router-on-a-stick, **OSPF**.
- Network services: **DHCPv4, DNS (root domain), VoIP telephony, static NAT, firewall ACLs**.
- Team practice: sprint plannings, reviews and per-member documentation.

## Features
- **Sprint 1** — structured cabling for the campus terminals (outlets, Wi-Fi APs, cross-connects, backbone).
- **Sprint 2** — Packet Tracer simulation of Terminal 2: 10 switches, Cisco 2811 router, redundant HCC links, connection to the campus backbone and ISP.
- **Sprint 3** — OSPF (area 2) replacing static routes, DHCPv4 for all networks, VoIP with call forwarding, DNS, static NAT to an HTTP/HTTPS server, ACLs.

## Structure
| Path | Content |
|---|---|
| `doc/sprint1/` | Cabling design, planning, review, global inventory |
| `doc/sprint2/` | Packet Tracer simulations and configs |
| `doc/sprint3/` | Routing and services configuration |

## Running
Open the `.pkt` files in Cisco Packet Tracer (e.g. `doc/sprint2/1221514/Terminal2.pkt`). Running configs are in `docs-running-config/`.

## Notes
- Documentation is a mix of English and Portuguese, as delivered.
