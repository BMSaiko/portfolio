RCOMP 2025-2026 Project 1 - Sprint 2 - Member 1240904 (Terminal 3)
===========================================

This folder contains all the outputs and artifacts produced by the team member with number 1240904 for Sprint 2. The work focuses on the development of the Packet Tracer simulation for **Terminal 3** and ** campus backbone**, as defined in the sprint planning.

### Author
- **Student Number:** 1240904
- **Assigned Task:** T.2.2
- **Terminal:** 3

---

## 1. Bloco de Endereços Atribuído à Equipa

A equipa dispõe do seguinte bloco de endereços IPv4:

| Parâmetro | Valor |
|---|---|
| Bloco da equipa | `10.61.128.0/17` |
| Intervalo total | 10.61.128.0 – 10.61.255.255 |
| Total de endereços | 32 768 |

---

## 2. Metodologia de Endereçamento (VLSM)

O método utilizado foi o **VLSM (Variable Length Subnet Masking)**, que consiste em alocar sub-redes de tamanho variável, ajustadas ao número real de nós necessários em cada rede, evitando desperdício de endereços.

### Princípios aplicados

1. **Ordenação por tamanho decrescente** — as redes com maior número de nós são alocadas primeiro, garantindo alinhamento de blocos e evitando fragmentação.
2. **Potência de 2** — o tamanho de cada sub-rede é a menor potência de 2 que acomoda o número de nós requeridos mais 2 (endereço de rede e broadcast).
3. **Não sobreposição** — cada sub-rede começa imediatamente após o fim da anterior, garantindo que não há sobreposição entre blocos.
4. **Redes partilhadas primeiro** — o Campus Backbone e a DMZ de Switches foram alocados em coordenação com toda a equipa antes de distribuir os blocos individuais por terminal.

---

## 3. Alocação Global de Sub-redes (toda a equipa)

As seguintes sub-redes foram definidas em reunião de planeamento e são partilhadas por todos os terminais:

| Rede | Nós necessários | Sub-rede | Máscara | Intervalo utilizável | Broadcast |
|---|---|---|---|---|---|
| Campus Backbone | 190 | 10.61.174.0/24 | 255.255.255.0 | 10.61.174.1 – 10.61.174.254 | 10.61.174.255 |
| DMZ de Switches | 410 | 10.61.170.0/23 | 255.255.254.0 | 10.61.170.1 – 10.61.171.254 | 10.61.171.255 |

### Cálculo — Campus Backbone (`/24`)
- Nós necessários: 190 (um endereço de backbone por router de terminal + margem)
- 2⁸ = 256 endereços → 254 utilizáveis → **prefixo /24** ✓

### Cálculo — DMZ de Switches (`/23`)
- Nós necessários: 410 (endereços de gestão para todos os switches de todos os terminais)
- 2⁸ = 256 → insuficiente; 2⁹ = 512 → 510 utilizáveis → **prefixo /23** ✓

---

## 4. Sub-redes do Terminal 3

O bloco atribuído ao Terminal 3 para as suas redes internas é composto pelas seguintes sub-redes, calculadas por VLSM:

### 4.1. Tabela de sub-redes

| VLAN | Rede | Nós necessários | Hosts disponíveis | Sub-rede | Máscara | Intervalo utilizável | Broadcast |
|---|---|---|---|---|---|---|---|
| t3-wifi (776) | Wi-Fi | 1500 | 2046 | 10.61.136.0/21 | 255.255.248.0 | 10.61.136.1 – 10.61.143.254 | 10.61.143.255 |
| t3-outlets (775) | Tomadas utilizador | 950 | 1022 | 10.61.156.0/22 | 255.255.252.0 | 10.61.156.1 – 10.61.159.254 | 10.61.159.255 |
| t3-voip (777) | VoIP | 350 | 510 | 10.61.166.0/23 | 255.255.254.0 | 10.61.166.1 – 10.61.167.254 | 10.61.167.255 |
| t3-servers (778) | DMZ de Servidores | 150 | 254 | 10.61.172.0/24 | 255.255.255.0 | 10.61.172.1 – 10.61.172.254 | 10.61.172.255 |

### 4.2. Cálculo detalhado por sub-rede

#### Wi-Fi — `10.61.136.0/21`
- Nós necessários: **1500**
- 2¹¹ = 2048 → 2046 utilizáveis → **prefixo /21** ✓
- Máscara: 256 – 8 = 248 → `255.255.248.0`

#### Tomadas de utilizador — `10.61.156.0/22`
- Nós necessários: **950**
- 2¹⁰ = 1024 → 1022 utilizáveis → **prefixo /22** ✓
- Máscara: 256 – 4 = 252 → `255.255.252.0`

#### VoIP — `10.61.166.0/23`
- Nós necessários: **350**
- 2⁹ = 512 → 510 utilizáveis → **prefixo /23** ✓
- Máscara: 256 – 2 = 254 → `255.255.254.0`

#### DMZ de Servidores — `10.61.172.0/24`
- Nós necessários: **150**
- 2⁸ = 256 → 254 utilizáveis → **prefixo /24** ✓
- Máscara: `255.255.255.0`

---

## 5. Endereços IP dos Routers no Campus Backbone

Cada terminal tem um router com um endereço único na rede do Campus Backbone (`10.61.174.0/24`), definido em reunião de planeamento:

| Terminal | Router | IP no backbone |
|---|---|---|
| Terminal 2 | T2-Router | 10.61.174.2 |
| **Terminal 3** | **T3-Router** | **10.61.174.3** |
| Terminal 4 | T4-Router | 10.61.174.4 |

---

## 6. Endereços IP de Gestão dos Switches (DMZ de Switches)

A rede `10.61.170.0/23` é partilhada por todos os terminais para gestão remota dos switches. O intervalo atribuído ao Terminal 3 é `10.61.170.51 – 10.61.170.100`.

| Switch | IP de gestão (Vlan770) |
|---|---|
| T3-IC | 10.61.170.51 |
| T3-HC-1A | 10.61.170.52 |
| T3-HC-1B | 10.61.170.53 |
| T3-HC-1C | 10.61.170.54 |
| T3-HC-2A | 10.61.170.55 |
| T3-HC-2B | 10.61.170.56 |
| T3-HC-2C | 10.61.170.57 |
| T3-HC-2D | 10.61.170.58 |
| T3-CP-1A-1 | 10.61.170.62 |
| T3-CP-1A-2 | 10.61.170.63 |
| T3-CP-1A-3 | 10.61.170.64 |
| T3-CP-1A-4 | 10.61.170.65 |
| T3-CP-1A-5 | 10.61.170.66 |
| T3-CP-1B-1 | 10.61.170.67 |
| T3-CP-1B-2 | 10.61.170.68 |
| T3-CP-1B-3 | 10.61.170.69 |
| T3-CP-1B-4 | 10.61.170.70 |
| T3-CP-1C-1 | 10.61.170.71 |
| T3-CP-2A-1 | 10.61.170.72 |
| T3-CP-2B-1 | 10.61.170.73 |
| T3-CP-2C-1 | 10.61.170.74 |
| T3-CP-2D-1 | 10.61.170.75 |
| T3-CP-2D-2 | 10.61.170.76 |
| T3-CP-2D-3 | 10.61.170.77 |

---

## 7. Configuração das Subinterfaces do T3-Router

O router do Terminal 3 (modelo Cisco 2811) utiliza uma única interface física `FastEthernet0/0` em modo trunk, com subinterfaces lógicas para cada VLAN — técnica designada por **router-on-a-stick**. Cada subinterface corresponde a uma VLAN e o seu endereço IP actua como **gateway** para os dispositivos dessa rede.

| Subinterface | VLAN ID | VLAN Name | Endereço IP | Máscara | Função |
|---|---|---|---|---|---|
| Fa0/0.769 | 769 | backbone | 10.61.174.3 | 255.255.255.0 | IP do T3-Router no Campus Backbone |
| Fa0/0.775 | 775 | t3-outlets | 10.61.156.1 | 255.255.252.0 | Gateway das tomadas de utilizador |
| Fa0/0.776 | 776 | t3-wifi | 10.61.136.1 | 255.255.248.0 | Gateway da rede Wi-Fi |
| Fa0/0.777 | 777 | t3-voip | 10.61.166.1 | 255.255.254.0 | Gateway da rede VoIP |
| Fa0/0.778 | 778 | t3-servers | 10.61.172.1 | 255.255.255.0 | Gateway da DMZ de Servidores |

> A VLAN 770 (DMZ de Switches) **não tem subinterface no router** — é uma rede isolada sem gateway, acessível apenas localmente.

---

## 8. Tabela de Routing Estático do T3-Router

O T3-Router apenas conhece diretamente as redes às quais está ligado. Para alcançar as redes dos outros terminais, são configuradas rotas estáticas que indicam o próximo salto (next-hop) no Campus Backbone.

### 8.1. Redes diretamente ligadas (aprendidas automaticamente)

| Rede | Interface |
|---|---|
| 10.61.174.0/24 | Fa0/0.769 (backbone) |
| 10.61.156.0/22 | Fa0/0.775 (t3-outlets) |
| 10.61.136.0/21 | Fa0/0.776 (t3-wifi) |
| 10.61.166.0/23 | Fa0/0.777 (t3-voip) |
| 10.61.172.0/24 | Fa0/0.778 (t3-servers) |

### 8.2. Rotas estáticas configuradas

#### Para o Terminal 2 — next-hop: T2-Router (`10.61.174.2`)

| Destino | Máscara | Next-hop | Rede |
|---|---|---|---|
| 10.61.128.0 | 255.255.248.0 | 10.61.174.2 | T2 — Wi-Fi |
| 10.61.152.0 | 255.255.252.0 | 10.61.174.2 | T2 — Tomadas utilizador |
| 10.61.164.0 | 255.255.254.0 | 10.61.174.2 | T2 — VoIP |
| 10.61.173.128 | 255.255.255.128 | 10.61.174.2 | T2 — DMZ de Servidores |

#### Para o Terminal 4 — next-hop: T4-Router (`10.61.174.4`)

| Destino | Máscara | Next-hop | Rede |
|---|---|---|---|
| 10.61.144.0 | 255.255.248.0 | 10.61.174.4 | T4 — Wi-Fi |
| 10.61.160.0 | 255.255.252.0 | 10.61.174.4 | T4 — Tomadas utilizador |
| 10.61.168.0 | 255.255.254.0 | 10.61.174.4 | T4 — VoIP |
| 10.61.173.0 | 255.255.255.128 | 10.61.174.4 | T4 — DMZ de Servidores |

#### Rota por omissão (default route)

| Destino | Máscara | Next-hop | Função |
|---|---|---|---|
| 0.0.0.0 | 0.0.0.0 | 10.61.174.2 | Todo o tráfego desconhecido (internet) via T2 |

A rota por omissão (`0.0.0.0/0`) encaminha para o T2-Router todo o tráfego cujo destino não corresponda a nenhuma rota conhecida — nomeadamente o tráfego para a internet, uma vez que é o Terminal 2 que possui a ligação ao ISP.

### 8.3. Comandos de configuração aplicados

```
ip route 10.61.128.0 255.255.248.0 10.61.174.2
ip route 10.61.152.0 255.255.252.0 10.61.174.2
ip route 10.61.164.0 255.255.254.0 10.61.174.2
ip route 10.61.173.128 255.255.255.128 10.61.174.2
ip route 10.61.144.0 255.255.248.0 10.61.174.4
ip route 10.61.160.0 255.255.252.0 10.61.174.4
ip route 10.61.168.0 255.255.254.0 10.61.174.4
ip route 10.61.173.0 255.255.255.128 10.61.174.4
ip route 0.0.0.0 0.0.0.0 10.61.174.2
```

---

## 9. End-devices configurados no Terminal 3

| Dispositivo | VLAN | IP | Máscara | Gateway |
|---|---|---|---|---|
| T3-PC-Outlets | 775 — t3-outlets | 10.61.156.10 | 255.255.252.0 | 10.61.156.1 |
| T3-PC-DMZ | 770 — switches-dmz | 10.61.170.100 | 255.255.254.0 | — (rede isolada) |
| T3-Server | 778 — t3-servers | 10.61.172.10 | 255.255.255.0 | 10.61.172.1 |
| T3-Laptop-WiFi | 776 — t3-wifi | 10.61.136.10 | 255.255.248.0 | 10.61.136.1 |
| T3-VoIP | 777 — t3-voip | — | — | — (configurado no Sprint 3) |

## 10. Testing and Verification
The following tests were performed to ensure the simulation is functioning correctly.

### 10.1. Connectivity within Terminal 3

```text
! From T3-PC-Outlets (10.61.156.10 - VLAN 775):
ping 10.61.156.1        ! Gateway T3-Router (must respond) ✓
ping 10.61.136.10       ! T3-Laptop-WiFi (inter-VLAN routing) ✓
ping 10.61.172.10       ! T3-Server (inter-VLAN routing) ✓

! Security / Isolation tests (VLAN 770 is not routed):
ping 10.61.170.100      ! T3-PC-DMZ (must NOT respond - isolated management network) ❌
ping 10.61.170.51       ! T3-IC (must NOT respond - isolated management network) ❌
```

### 10.2. Connectivity to other Terminals (via Campus Backbone campus.pkt)
```text
! From T3-PC-Outlets (10.61.156.10):
ping 10.61.174.2        ! T2-Router (must respond) ✓
ping 10.61.174.4        ! T4-Router (must respond) ✓
ping 10.61.152.10       ! T2-PC-Outlets (must respond) ✓
ping 10.61.160.10       ! T4-PC-Outlets (must respond) ✓
```

### 10.3. Connectivity to the Internet
```text
! From T3-PC-Outlets (10.61.156.10):
ping 89.73.67.118       ! ISP-Router via T2 (must respond) ✓
```

### 10.4. Routing Verification
```text
! On T3-Router:
show ip route
show ip interface brief
show running-config

! Expected routes:
! C - 10.61.174.0/24 (directly connected - VLAN 769 backbone)
! C - 10.61.156.0/22 (directly connected - VLAN 775 t3-outlets)
! C - 10.61.136.0/21 (directly connected - VLAN 776 t3-wifi)
! C - 10.61.166.0/23 (directly connected - VLAN 777 t3-voip)
! C - 10.61.172.0/24 (directly connected - VLAN 778 t3-servers)
! S - 10.61.128.0/21 via 10.61.174.2 (T2 Wi-Fi)
! S - 10.61.152.0/22 via 10.61.174.2 (T2 Outlets)
! S - 10.61.164.0/23 via 10.61.174.2 (T2 VoIP)
! S - 10.61.173.128/25 via 10.61.174.2 (T2 Servers)
! S - 10.61.144.0/21 via 10.61.174.4 (T4 Wi-Fi)
! S - 10.61.160.0/22 via 10.61.174.4 (T4 Outlets)
! S - 10.61.168.0/23 via 10.61.174.4 (T4 VoIP)
! S - 10.61.173.0/25 via 10.61.174.4 (T4 Servers)
! S* - 0.0.0.0/0 via 10.61.174.2 (default route to internet via T2)
```

### 10.5. VLAN and VTP Verification
```text
! On T3-IC (VTP Server):
show vlan brief
show vtp status
show interfaces trunk

! Expected VLANs: 769 (backbone), 770 (switches-dmz),
!   771-774 (T2), 775-778 (T3), 779-782 (T4)

! On any VTP client (e.g. T3-HC-1A):
show vlan brief      ! must show all 14 VLANs propagated by VTP
show vtp status      ! VTP Mode must be Client, domain rc2526deg2
```

# 10.6. Switch Management Verification (Switches DMZ)
```text
! Note: For this test, T3-PC-DMZ must be connected to an access port assigned to VLAN 770.

! From T3-PC-DMZ (10.61.170.100 - VLAN 770):
ping 10.61.170.51    ! T3-IC ✓
ping 10.61.170.52    ! T3-HC-1A ✓
ping 10.61.170.53    ! T3-HC-1B ✓
ping 10.61.170.54    ! T3-HC-1C ✓
ping 10.61.170.55    ! T3-HC-2A ✓
ping 10.61.170.56    ! T3-HC-2B ✓
ping 10.61.170.57    ! T3-HC-2C ✓
ping 10.61.170.58    ! T3-HC-2D ✓

! Confirm T3-PC-DMZ cannot reach outside the DMZ network (isolated network)
ping 10.61.156.10    ! T3-PC-Outlets (must NOT respond) ❌
ping 10.61.174.3     ! T3-Router (must NOT respond) ❌
```

# 10.7. STP Verification
```text
! On any switch:
show spanning-tree
show spanning-tree vlan 769
show spanning-tree vlan 775
```