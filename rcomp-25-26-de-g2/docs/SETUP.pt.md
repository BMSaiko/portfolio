# Setup — abrir e verificar as simulações

> Documentação adicionada em 2026 (portfolio). **Não testado**: esta máquina não tem Cisco Packet Tracer. Tudo o que se segue vem dos plannings e READMEs da equipa. Versão inglesa: [SETUP.md](SETUP.md).

## Pré-requisitos

| Item | Fonte |
|---|---|
| Cisco Packet Tracer **9.0.0** — exigido pela equipa para as simulações individuais se juntarem num só `campus.pkt` | [planning sprint 2 §2.1](../doc/sprint2/planning.md) |
| Conta Cisco Networking Academy (para descarregar o Packet Tracer) | geral; não está no repositório |

Sem código, build, base de dados nem variáveis de ambiente.

## Que ficheiro abrir

| Objetivo | Ficheiro |
|---|---|
| Campus completo, estado final | [`doc/sprint3/1240904/campus.pkt`](../doc/sprint3/1240904/campus.pkt) |
| Campus completo, só camadas 2/3 (sem serviços) | [`doc/sprint2/1240904/campus.pkt`](../doc/sprint2/1240904/campus.pkt) |
| Um terminal, estado final | `doc/sprint3/1221514/Terminal2.pkt`, `doc/sprint3/1240904/Terminal3.pkt`, `doc/sprint3/1240935/Terminal4.pkt` |
| Configuração dos dispositivos em texto | `*_running-config.txt` em `docs-running-config/` de cada membro (o 1240904 guarda-os junto ao `.pkt`; no Sprint 2 o 1240935 usa `docs/`) |
| Prova dos registos DNS | `T2-DNS-records.png`, `T3-DNS-records.png`, `T4-DNS-records.png` (Sprint 3) |

## Verificações documentadas pela equipa (não executadas aqui)

Do [README do Terminal 2, Sprint 3](../doc/sprint3/1221514/README.md); correr na CLI do Packet Tracer ou na linha de comandos dos PCs.

| Serviço | Comando | Esperado (segundo o README) |
|---|---|---|
| OSPF | `show ip ospf neighbor` no `T2-Router` | Vizinhos `3.3.3.3` (10.61.174.3) e `4.4.4.4` (10.61.174.4) em `Fa0/0.769` |
| OSPF | `show ip route ospf` | Rotas `O IA` para as sub-redes do T3 e T4 via `10.61.174.3` / `10.61.174.4` |
| DHCPv4 | `show ip dhcp pool`, `show ip dhcp binding`; `ipconfig` no `T2-PC-Outlets` | IP `10.61.152.x`, gateway `10.61.152.1`, DNS `10.61.173.130` |
| DNS | `nslookup server1.rcomp-25-26-de-g2` | `10.61.173.131` |
| DNS | `nslookup ns.terminal-3.rcomp-25-26-de-g2` | `10.61.172.10` |

Os outros serviços (VoIP, NAT, ACLs) têm secções de verificação próprias no mesmo README e nos READMEs dos Terminais 3 e 4.

## Resolução de problemas

- Outra versão do Packet Tracer pode recusar abrir ou alterar os ficheiros. Usar a 9.0.0.
- Os `.pkt` são binários: não fazer merge nem diff em git.
- Ler as configurações não precisa de ferramenta; abrir os `.txt`. Contêm hashes de passwords (ver [INDEX §4](INDEX.pt.md#4-achados-verificados), achado 7); os dispositivos são simulações de laboratório.

## Testes

Nenhum além das verificações manuais acima.
