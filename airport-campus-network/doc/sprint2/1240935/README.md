RCOMP 2025-2026 Project 1 - Sprint 2 - Member 1240935 (Terminal 4)
===========================================

This folder contains all the outputs and artifacts produced by the team member with number 1240935 for Sprint 2. The work focuses on the development of the Packet Tracer simulation for **Terminal 4** and the **campus backbone**, as defined in the sprint planning.

### Author
- **Student Number:** 1240935
- **Assigned Task:** T.2.4
- **Terminal:** 4

---

## 1. Bloco de Endereços Atribuído à Equipa

A equipa dispõe do seguinte bloco de endereços IPv4:

| Parâmetro          | Valor                       |
|--------------------|-----------------------------|
| Bloco da equipa    | `10.61.128.0/17`            |
| Intervalo total    | 10.61.128.0 – 10.61.255.255 |
| Total de endereços | 32 768                      |

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

| Rede            | Nós necessários | Sub-rede       | Máscara       | Intervalo utilizável        | Broadcast     |
|-----------------|-----------------|----------------|---------------|-----------------------------|---------------|
| Campus Backbone | 190             | 10.61.174.0/24 | 255.255.255.0 | 10.61.174.1 – 10.61.174.254 | 10.61.174.255 |
| DMZ de Switches | 410             | 10.61.170.0/23 | 255.255.254.0 | 10.61.170.1 – 10.61.171.254 | 10.61.171.255 |

### Cálculo — Campus Backbone (`/24`)
- Nós necessários: 190 (um endereço de backbone por router de terminal + margem)
- 2⁸ = 256 endereços → 254 utilizáveis → **prefixo /24** ✓

### Cálculo — DMZ de Switches (`/23`)
- Nós necessários: 410 (endereços de gestão para todos os switches de todos os terminais)
- 2⁸ = 256 → insuficiente; 2⁹ = 512 → 510 utilizáveis → **prefixo /23** ✓

---

## 4. Sub-redes do Terminal 4

O bloco atribuído ao Terminal 4 para as suas redes internas é composto pelas seguintes sub-redes, calculadas por VLSM:

### 4.1. Tabela de sub-redes

| VLAN             | Rede               | Nós necessários | Hosts disponíveis | Sub-rede       | Máscara         | Intervalo utilizável        | Broadcast     |
|------------------|--------------------|-----------------|-------------------|----------------|-----------------|-----------------------------|---------------|
| t4-wifi (780)    | Wi-Fi              | 1800            | 2046              | 10.61.144.0/21 | 255.255.248.0   | 10.61.144.1 – 10.61.151.254 | 10.61.151.255 |
| t4-outlets (779) | Tomadas utilizador | 800             | 1022              | 10.61.160.0/22 | 255.255.252.0   | 10.61.160.1 – 10.61.163.254 | 10.61.163.255 |
| t4-voip (781)    | VoIP               | 400             | 510               | 10.61.168.0/23 | 255.255.254.0   | 10.61.168.1 – 10.61.169.254 | 10.61.169.255 |
| t4-servers (782) | DMZ de Servidores  | 120             | 126               | 10.61.173.0/25 | 255.255.255.128 | 10.61.173.1 – 10.61.173.126 | 10.61.173.127 |

### 4.2. Cálculo detalhado por sub-rede

#### Wi-Fi — `10.61.144.0/21`
- Nós necessários: **1800**
- 2¹¹ = 2048 → 2046 utilizáveis → **prefixo /21** ✓
- Máscara: 256 – 8 = 248 → `255.255.248.0`

#### Tomadas de utilizador — `10.61.160.0/22`
- Nós necessários: **800**
- 2¹⁰ = 1024 → 1022 utilizáveis → **prefixo /22** ✓
- Máscara: 256 – 4 = 252 → `255.255.252.0`

#### VoIP — `10.61.168.0/23`
- Nós necessários: **400**
- 2⁹ = 512 → 510 utilizáveis → **prefixo /23** ✓
- Máscara: 256 – 2 = 254 → `255.255.254.0`

#### DMZ de Servidores — `10.61.173.0/25`
- Nós necessários: **120**
- 2⁷ = 128 → 126 utilizáveis → **prefixo /25** ✓
- Máscara: `255.255.255.128`

---

## 5. Endereços IP dos Routers no Campus Backbone

Cada terminal tem um router com um endereço único na rede do Campus Backbone (`10.61.174.0/24`), definido em reunião de planeamento:

| Terminal       | Router        | IP no backbone  |
|----------------|---------------|-----------------|
| Terminal 2     | T2-Router     | 10.61.174.2     |
| Terminal 3     | T3-Router     | 10.61.174.3     |
| **Terminal 4** | **T4-Router** | **10.61.174.4** |

---

## 6. Endereços IP de Gestão dos Switches (DMZ de Switches)

A rede `10.61.170.0/23` é partilhada por todos os terminais para gestão remota dos switches. O intervalo atribuído ao Terminal 4 é `10.61.170.101 – 10.61.170.150`.

| Switch     | IP de gestão (Vlan770) |
|------------|------------------------|
| T4-ICC     | 10.61.170.101          |
| T4-HCC-F0  | 10.61.170.102          |
| T4-HCC-F2  | 10.61.170.103          |
| T4-CP-F0-1 | 10.61.170.104          |
| T4-CP-F0-2 | 10.61.170.105          |
| T4-CP-F0-3 | 10.61.170.106          |
| T4-CP-F0-4 | 10.61.170.107          |
| T4-CP-F2-1 | 10.61.170.108          |
| T4-CP-F2-2 | 10.61.170.109          |
| T4-CP-F2-3 | 10.61.170.110          |
| T4-CP-F2-4 | 10.61.170.111          |

> !! — Confirm the exact CP names and count match your Sprint 1 design. Add or remove rows as needed and assign sequential IPs from the range .119 onwards if you have additional switches.

---

## 7. Configuração das Subinterfaces do T4-Router

O router do Terminal 4 (modelo Cisco 2811) utiliza uma única interface física `FastEthernet0/0` em modo trunk, com subinterfaces lógicas para cada VLAN — técnica designada por **router-on-a-stick**. Cada subinterface corresponde a uma VLAN e o seu endereço IP actua como **gateway** para os dispositivos dessa rede.

| Subinterface | VLAN ID | VLAN Name  | Endereço IP | Máscara         | Função                             |
|--------------|---------|------------|-------------|-----------------|------------------------------------|
| Fa0/0.769    | 769     | backbone   | 10.61.174.4 | 255.255.255.0   | IP do T4-Router no Campus Backbone |
| Fa0/0.779    | 779     | t4-outlets | 10.61.160.1 | 255.255.252.0   | Gateway das tomadas de utilizador  |
| Fa0/0.780    | 780     | t4-wifi    | 10.61.144.1 | 255.255.248.0   | Gateway da rede Wi-Fi              |
| Fa0/0.781    | 781     | t4-voip    | 10.61.168.1 | 255.255.254.0   | Gateway da rede VoIP               |
| Fa0/0.782    | 782     | t4-servers | 10.61.173.1 | 255.255.255.128 | Gateway da DMZ de Servidores       |

> A VLAN 770 (DMZ de Switches) **não tem subinterface no router** — é uma rede isolada sem gateway, acessível apenas localmente.

---

## 8. Tabela de Routing Estático do T4-Router

O T4-Router apenas conhece diretamente as redes às quais está ligado. Para alcançar as redes dos outros terminais, são configuradas rotas estáticas que indicam o próximo salto (next-hop) no Campus Backbone.

### 8.1. Redes diretamente ligadas (aprendidas automaticamente)

| Rede           | Interface              |
|----------------|------------------------|
| 10.61.174.0/24 | Fa0/0.769 (backbone)   |
| 10.61.160.0/22 | Fa0/0.779 (t4-outlets) |
| 10.61.144.0/21 | Fa0/0.780 (t4-wifi)    |
| 10.61.168.0/23 | Fa0/0.781 (t4-voip)    |
| 10.61.173.0/25 | Fa0/0.782 (t4-servers) |

### 8.2. Rotas estáticas configuradas

#### Para o Terminal 2 — next-hop: T2-Router (`10.61.174.2`)

| Destino       | Máscara         | Next-hop    | Rede                    |
|---------------|-----------------|-------------|-------------------------|
| 10.61.128.0   | 255.255.248.0   | 10.61.174.2 | T2 — Wi-Fi              |
| 10.61.152.0   | 255.255.252.0   | 10.61.174.2 | T2 — Tomadas utilizador |
| 10.61.164.0   | 255.255.254.0   | 10.61.174.2 | T2 — VoIP               |
| 10.61.173.128 | 255.255.255.128 | 10.61.174.2 | T2 — DMZ de Servidores  |

#### Para o Terminal 3 — next-hop: T3-Router (`10.61.174.3`)

| Destino     | Máscara       | Next-hop    | Rede                    |
|-------------|---------------|-------------|-------------------------|
| 10.61.136.0 | 255.255.248.0 | 10.61.174.3 | T3 — Wi-Fi              |
| 10.61.156.0 | 255.255.252.0 | 10.61.174.3 | T3 — Tomadas utilizador |
| 10.61.166.0 | 255.255.254.0 | 10.61.174.3 | T3 — VoIP               |
| 10.61.172.0 | 255.255.255.0 | 10.61.174.3 | T3 — DMZ de Servidores  |

#### Rota por omissão (default route)

| Destino | Máscara | Next-hop    | Função                                        |
|---------|---------|-------------|-----------------------------------------------|
| 0.0.0.0 | 0.0.0.0 | 10.61.174.2 | Todo o tráfego desconhecido (internet) via T2 |

A rota por omissão (`0.0.0.0/0`) encaminha para o T2-Router todo o tráfego cujo destino não corresponda a nenhuma rota conhecida — nomeadamente o tráfego para a internet, uma vez que é o Terminal 2 que possui a ligação ao ISP.

### 8.3. Comandos de configuração aplicados

```
ip route 10.61.128.0 255.255.248.0 10.61.174.2
ip route 10.61.152.0 255.255.252.0 10.61.174.2
ip route 10.61.164.0 255.255.254.0 10.61.174.2
ip route 10.61.173.128 255.255.255.128 10.61.174.2
ip route 10.61.136.0 255.255.248.0 10.61.174.3
ip route 10.61.156.0 255.255.252.0 10.61.174.3
ip route 10.61.166.0 255.255.254.0 10.61.174.3
ip route 10.61.172.0 255.255.255.0 10.61.174.3
ip route 0.0.0.0 0.0.0.0 10.61.174.2
```

---

## 9. End-devices configurados no Terminal 4

| Dispositivo    | VLAN               | IP            | Máscara         | Gateway                     |
|----------------|--------------------|---------------|-----------------|-----------------------------|
| T4-PC-Outlets  | 779 — t4-outlets   | 10.61.160.10  | 255.255.252.0   | 10.61.160.1                 |
| T4-PC-DMZ      | 770 — switches-dmz | 10.61.170.130 | 255.255.254.0   | — (rede isolada)            |
| T4-Server      | 782 — t4-servers   | 10.61.173.10  | 255.255.255.128 | 10.61.173.1                 |
| T4-Laptop-WiFi | 780 — t4-wifi      | 10.61.144.10  | 255.255.248.0   | 10.61.144.1                 |
| T4-VoIP        | 781 — t4-voip      | —             | —               | — (configurado no Sprint 3) |