# Setup — opening and checking the simulations

> Documentation added in 2026 (portfolio). **Not tested**: this machine has no Cisco Packet Tracer. Everything below is taken from the team's plannings and READMEs. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Item | Source |
|---|---|
| Cisco Packet Tracer **9.0.0** — required by the team so the individual simulations merge into one `campus.pkt` | [sprint 2 planning §2.1](../doc/sprint2/planning.md) |
| Cisco Networking Academy account (to download Packet Tracer) | general; not in the repo |

No code, build, database or environment variables.

## Which file to open

| Goal | File |
|---|---|
| Whole campus, final state | [`doc/sprint3/1240904/campus.pkt`](../doc/sprint3/1240904/campus.pkt) |
| Whole campus, Layer 2/3 only (no services) | [`doc/sprint2/1240904/campus.pkt`](../doc/sprint2/1240904/campus.pkt) |
| One terminal, final state | `doc/sprint3/1221514/Terminal2.pkt`, `doc/sprint3/1240904/Terminal3.pkt`, `doc/sprint3/1240935/Terminal4.pkt` |
| Device configuration as text | `*_running-config.txt` in each member's `docs-running-config/` (1240904 keeps them next to the `.pkt`; in Sprint 2 1240935 uses `docs/`) |
| DNS record evidence | `T2-DNS-records.png`, `T3-DNS-records.png`, `T4-DNS-records.png` (Sprint 3) |

## Checks the team documented (not run here)

From the [Terminal 2 Sprint 3 README](../doc/sprint3/1221514/README.md); run them in the Packet Tracer CLI/PC command prompt.

| Service | Command | Expected (per README) |
|---|---|---|
| OSPF | `show ip ospf neighbor` on `T2-Router` | Neighbours `3.3.3.3` (10.61.174.3) and `4.4.4.4` (10.61.174.4) on `Fa0/0.769` |
| OSPF | `show ip route ospf` | `O IA` routes to the T3 and T4 subnets via `10.61.174.3` / `10.61.174.4` |
| DHCPv4 | `show ip dhcp pool`, `show ip dhcp binding`; `ipconfig` on `T2-PC-Outlets` | IP `10.61.152.x`, gateway `10.61.152.1`, DNS `10.61.173.130` |
| DNS | `nslookup server1.rcomp-25-26-de-g2` | `10.61.173.131` |
| DNS | `nslookup ns.terminal-3.rcomp-25-26-de-g2` | `10.61.172.10` |

Other services (VoIP, NAT, ACLs) have their own verification sections in the same README and in the Terminal 3 and 4 READMEs.

## Troubleshooting

- A different Packet Tracer version may refuse to open or may alter the files. Use 9.0.0.
- The `.pkt` files are binary: do not merge or diff them in git.
- Reading the configs needs no tool; open the `.txt` files. They contain password hashes (see [INDEX §4](INDEX.md#4-verified-findings), finding 7); the devices are lab simulations.

## Tests

None beyond the manual checks above.
