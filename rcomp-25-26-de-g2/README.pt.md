# RCOMP 2025/26 — Airport campus network

[🇬🇧 English](README.md) · 🇵🇹 Português

> Desenho e simulação da rede de um campus aeroportuário com quatro terminais, em três sprints.

| | |
|---|---|
| **Contexto** | LEI — Redes de Computadores (RCOMP) |
| **Ano** | 2025/26 (Feb–May 2026) |
| **Equipa** | Grupo de 3 (turma DE, grupo 2) |
| **Stack** | Cisco Packet Tracer · structured cabling (T568B) · VLAN/VTP/STP · OSPF · DHCPv4 · DNS · VoIP · NAT · ACLs |

## A minha contribuição
- Responsável pelo **Terminal 2** em todos os sprints (cablagem, simulação, serviços).
- Sprint master do Sprint 1; coordenei as decisões técnicas globais (p. ex. norma T568B) e o inventário global.

## O que este projeto demonstra
- Projeto de cablagem estruturada com plantas, medições e normas.
- Engenharia de rede L2/L3: **VLANs, VTP, trunking, redundância STP**, router-on-a-stick, **OSPF**.
- Serviços de rede: **DHCPv4, DNS (domínio raiz), telefonia VoIP, NAT estático, ACLs de firewall**.
- Prática de equipa: plannings, reviews e documentação por membro.

## Funcionalidades
- **Sprint 1** — cablagem estruturada dos terminais do campus (tomadas, APs Wi-Fi, cross-connects, backbone).
- **Sprint 2** — simulação em Packet Tracer do Terminal 2: 10 switches, router Cisco 2811, ligações HCC redundantes, ligação ao backbone do campus e ao ISP.
- **Sprint 3** — OSPF (área 2) a substituir rotas estáticas, DHCPv4 em todas as redes, VoIP com reencaminhamento, DNS, NAT estático para servidor HTTP/HTTPS, ACLs.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `doc/sprint1/` | Projeto de cablagem, planeamento, review, inventário global |
| `doc/sprint2/` | Simulações Packet Tracer e configurações |
| `doc/sprint3/` | Configuração de routing e serviços |

## Execução
Abrir os ficheiros `.pkt` no Cisco Packet Tracer (p. ex. `doc/sprint2/1221514/Terminal2.pkt`). As configurações estão em `docs-running-config/`.

## Notas
- A documentação mistura inglês e português, tal como entregue.

## Documentação

- [Índice e rastreabilidade](docs/INDEX.pt.md) — sprints, tarefa → entregável → revisão, achados
- [Setup](docs/SETUP.pt.md) — Packet Tracer 9.0.0, que ficheiro abrir, verificações (não executadas)
- [Glossário](docs/GLOSSARY.pt.md)
- [Auditoria da documentação](../docs/AUDIT.pt.md#rcomp-25-26-de-g2) — estado e lacunas deste projeto
- [Sprint 1 (planeamento, revisão, esquemas)](doc/sprint1/)
- [Sprint 2 (planeamento, revisão)](doc/sprint2/)
- [Sprint 3 (planeamento, revisão, registos DNS)](doc/sprint3/)
