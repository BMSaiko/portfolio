RCOMP 2025-2026 Projeto 1 - Planeamento do Sprint 2
===================================================

### Sprint master: 1240904

**Data:** 2026-02-26

**Duração do Sprint:** 2026-03-16 a 2026-04-19

---

# 1. Backlog do Sprint


| Tarefa | Descrição                                                                                                                                | Atribuído a |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------ | ------------ |
| T.2.1  | Simulação Packet Tracer de camada 2 e 3 para o**Terminal 2**, backbone do campus e ligação à internet (DSL/ISP).                      | 1221514      |
| T.2.2  | Simulação Packet Tracer de camada 2 e 3 para o**Terminal 3**, backbone do campus. Integração de todas as simulações em `campus.pkt`. | 1240904      |
| T.2.4  | Simulação Packet Tracer de camada 2 e 3 para o**Terminal 4**, backbone do campus.                                                        | 1240935      |
| T.2.3  | ~~Terminal 5~~ — **Não aplicável** (a equipa cobre os Terminais 2, 3 e 4).                                                              | —           |

> As tarefas que no Sprint 1 não foram avaliadas como *Totalmente implementadas sem problemas* devem ser concluídas e os problemas corrigidos pelo mesmo elemento da equipa durante este sprint.

---

# 2. Decisões técnicas e coordenação

## 2.1 Versão do Packet Tracer

Todos os membros da equipa devem utilizar o **Cisco Packet Tracer 9.0.0**.
A utilização da mesma versão é obrigatória para permitir a junção de todas as simulações individuais em `campus.pkt` sem problemas de compatibilidade.

## 2.2 Convenção de nomenclatura dos dispositivos

### Nomes de apresentação (Packet Tracer)

O formato é: `T<terminal>-<função>[-<qualificador>]`


| Tipo de dispositivo         | Padrão de nomenclatura | Exemplos                  |
| --------------------------- | ----------------------- | ------------------------- |
| Main Cross-Connect          | `T<N>-MCC`              | `T2-MCC`                  |
| Intermediate Cross-Connect  | `T<N>-ICC`              | `T2-ICC`, `T3-ICC`        |
| Horizontal Cross-Connect    | `T<N>-HCC-<piso/zona>`  | `T2-HCC-F0`, `T3-HCC-F1A` |
| Ponto de Consolidação     | `T<N>-CP-<zona>`        | `T2-CP-A`, `T4-CP-B`      |
| Router                      | `T<N>-Router`           | `T2-Router`, `T4-Router`  |
| PC (Outlets de utilizador) | `T<N>-PC-Outlets`       | `T2-PC-Outlets`           |
| PC (DMZ de switches)        | `T<N>-PC-DMZ`           | `T3-PC-DMZ`               |
| Laptop (Wi-Fi)              | `T<N>-Laptop-WiFi`      | `T4-Laptop-WiFi`          |
| Ponto de Acesso             | `T<N>-AP`               | `T2-AP`                   |
| Servidor                    | `T<N>-Server`           | `T3-Server`               |
| Telefone VoIP               | `T<N>-VoIP`             | `T2-VoIP`                 |
| Router ISP (apenas T2)      | `ISP-Router`            | `ISP-Router`              |
| Modem DSL (apenas T2)       | `T2-DSL-Modem`          | `T2-DSL-Modem`            |

### Hostnames (nível de sistema operativo, via comando `hostname` — sem espaços)

Mesmo padrão dos nomes de apresentação: `T2-MCC`, `T3-Router`, `T4-HCC-F1`, etc.

---

## 2.3 VLANs

**Nome do domínio VTP:** `rc2526deg2`
**Intervalo de VLAN IDs disponível:** 769–789
**VLAN predefinida:** 1 (mantida como padrão Cisco — não utilizada para tráfego específico)


| VLAN ID | Nome da VLAN | Âmbito / Propósito                              |
| ------- | ------------ | ------------------------------------------------- |
| 1       | default      | VLAN predefinida (padrão Cisco, não utilizada)  |
| 769     | backbone     | Campus Backbone — encaminhamento entre terminais |
| 770     | switches-dmz | DMZ de Switches — gestão remota dos switches    |
| 771     | t2-outlets   | Terminal 2 — Tomadas de utilizador               |
| 772     | t2-wifi      | Terminal 2 — Wi-Fi                               |
| 773     | t2-voip      | Terminal 2 — VoIP                                |
| 774     | t2-servers   | Terminal 2 — DMZ de Servidores                   |
| 775     | t3-outlets   | Terminal 3 — Tomadas de utilizador               |
| 776     | t3-wifi      | Terminal 3 — Wi-Fi                               |
| 777     | t3-voip      | Terminal 3 — VoIP                                |
| 778     | t3-servers   | Terminal 3 — DMZ de Servidores                   |
| 779     | t4-outlets   | Terminal 4 — Tomadas de utilizador               |
| 780     | t4-wifi      | Terminal 4 — Wi-Fi                               |
| 781     | t4-voip      | Terminal 4 — VoIP                                |
| 782     | t4-servers   | Terminal 4 — DMZ de Servidores                   |

> Total: 2 VLANs partilhadas + (3 terminais × 4 VLANs) = **14 VLANs** (+ VLAN predefinida 1).
> Todas as VLANs devem estar presentes em **todos os switches** de todos os terminais.
> Todas as ligações entre switches devem ser configuradas em modo **trunk** transportando todas as VLANs.

---

## 2.4 Configuração VTP

- **Domínio VTP:** `rc2526deg2`
- **Servidor VTP:** o switch IC em cada simulação individual (pelo menos um por simulação)
- **Clientes VTP:** todos os outros switches dentro da mesma simulação

Comandos de configuração:

```
vtp domain rc2526deg2
vtp mode server     ! apenas no switch IC
vtp mode client     ! em todos os outros switches
```

---

## 2.5 Endereçamento IPv4

**Bloco de endereços da equipa: `10.61.128.0/17`** (10.61.128.0 – 10.61.255.255 — 32768 endereços no total)
**Endereço IPv4 do router ISP (Terminal 2):** `89.73.67.118/30`

### Alocação de sub-redes (maior para menor, para evitar desperdício)


| Rede                     | Nós necessários | Sub-rede         | Máscara        | Intervalo utilizável          | Broadcast     |
| ------------------------ | ----------------- | ---------------- | --------------- | ------------------------------ | ------------- |
| T2 — Wi-Fi              | 2000              | 10.61.128.0/21   | 255.255.248.0   | 10.61.128.1 – 10.61.135.254   | 10.61.135.255 |
| T3 — Wi-Fi              | 1500              | 10.61.136.0/21   | 255.255.248.0   | 10.61.136.1 – 10.61.143.254   | 10.61.143.255 |
| T4 — Wi-Fi              | 1800              | 10.61.144.0/21   | 255.255.248.0   | 10.61.144.1 – 10.61.151.254   | 10.61.151.255 |
| T2 — Tomadas utilizador | 1000              | 10.61.152.0/22   | 255.255.252.0   | 10.61.152.1 – 10.61.155.254   | 10.61.155.255 |
| T3 — Tomadas utilizador | 950               | 10.61.156.0/22   | 255.255.252.0   | 10.61.156.1 – 10.61.159.254   | 10.61.159.255 |
| T4 — Tomadas utilizador | 800               | 10.61.160.0/22   | 255.255.252.0   | 10.61.160.1 – 10.61.163.254   | 10.61.163.255 |
| T2 — VoIP               | 500               | 10.61.164.0/23   | 255.255.254.0   | 10.61.164.1 – 10.61.165.254   | 10.61.165.255 |
| T3 — VoIP               | 350               | 10.61.166.0/23   | 255.255.254.0   | 10.61.166.1 – 10.61.167.254   | 10.61.167.255 |
| T4 — VoIP               | 400               | 10.61.168.0/23   | 255.255.254.0   | 10.61.168.1 – 10.61.169.254   | 10.61.169.255 |
| DMZ de Switches          | 410               | 10.61.170.0/23   | 255.255.254.0   | 10.61.170.1 – 10.61.171.254   | 10.61.171.255 |
| T3 — DMZ de Servidores  | 150               | 10.61.172.0/24   | 255.255.255.0   | 10.61.172.1 – 10.61.172.254   | 10.61.172.255 |
| T4 — DMZ de Servidores  | 120               | 10.61.173.0/25   | 255.255.255.128 | 10.61.173.1 – 10.61.173.126   | 10.61.173.127 |
| T2 — DMZ de Servidores  | 100               | 10.61.173.128/25 | 255.255.255.128 | 10.61.173.129 – 10.61.173.254 | 10.61.173.255 |
| Campus Backbone          | 190               | 10.61.174.0/24   | 255.255.255.0   | 10.61.174.1 – 10.61.174.254   | 10.61.174.255 |

> Todas as sub-redes são não sobrepostas e pertencem ao bloco da equipa `10.61.128.0/17`. ✓

### Endereços IPv4 dos routers no Campus Backbone (VLAN 769 — 10.61.174.0/24)


| Terminal   | IP do router no backbone |
| ---------- | ------------------------ |
| Terminal 2 | 10.61.174.2              |
| Terminal 3 | 10.61.174.3              |
| Terminal 4 | 10.61.174.4              |

### Intervalos de IP dos switches na DMZ (VLAN 770 — 10.61.170.0/23)


| Terminal   | Intervalo de IPs dos switches  |
| ---------- | ------------------------------ |
| Terminal 2 | 10.61.170.1  – 10.61.170.50   |
| Terminal 3 | 10.61.170.51 – 10.61.170.100  |
| Terminal 4 | 10.61.170.101 – 10.61.170.150 |

### Blocos de endereços atribuídos por membro da equipa


| Membro  | Terminal | Wi-Fi          | Tomadas utilizador | VoIP           | DMZ de Servidores |
| ------- | -------- | -------------- | ------------------ | -------------- | ----------------- |
| 1221514 | T2       | 10.61.128.0/21 | 10.61.152.0/22     | 10.61.164.0/23 | 10.61.173.128/25  |
| 1240904 | T3       | 10.61.136.0/21 | 10.61.156.0/22     | 10.61.166.0/23 | 10.61.172.0/24    |
| 1240935 | T4       | 10.61.144.0/21 | 10.61.160.0/22     | 10.61.168.0/23 | 10.61.173.0/25    |

> As redes partilhadas (Campus Backbone `10.61.174.0/24` e DMZ de Switches `10.61.170.0/23`) são geridas coletivamente e não pertencem a nenhum bloco individual.

---

## 2.6 Redundância de caminhos entre switches

A equipa decidiu implementar redundância de caminhos entre HCs do mesmo nível em cada terminal, conforme permitido pelo enunciado. O STP (Spanning Tree Protocol) é mantido ativo em todos os switches para gerir os loops resultantes.

### Regras aplicadas

- Ligações redundantes são criadas **apenas entre HCs do mesmo nível** (horizontal).
- Ligações entre HCs e o IC (níveis distintos) são sempre representadas por **um único cabo**, mesmo que fisicamente existam dois.
- O STP **nunca deve ser desativado** em nenhum switch — é ele que bloqueia os caminhos redundantes em operação normal e os ativa em caso de falha.
### Topologia de redundância por terminal

Cada equipa define ligações em anel entre os HCs do mesmo nível.

# 3. Atribuição de subtarefas


| Número de aluno | Tarefa | Terminal                                                            |
| ---------------- | ------ | ------------------------------------------------------------------- |
| 1221514          | T.2.1  | Terminal 2 (+ ligação à internet via DSL/ISP)                    |
| 1240904          | T.2.2  | Terminal 3 (+ integração de todas as simulações em`campus.pkt`) |
| 1240935          | T.2.4  | Terminal 4                                                          |

> Sprint master: **1240904**
