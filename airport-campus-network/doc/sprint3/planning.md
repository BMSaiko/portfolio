RCOMP 2025-2026 Projeto 1 - Planeamento do Sprint 3
===================================================

### Sprint master: 1240935

**Duração do Sprint:** 2026-04-19 a 2026-05-17

---

# 1. Backlog do Sprint

| Tarefa | Descrição                                                                                                                                          | Atribuído a |
|--------|----------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
| T.3.1  | Atualização da simulação Packet Tracer do **Terminal 2** com OSPF, DHCP, VoIP, DNS, NAT e ACLs.                                                    | 1221514     |
| T.3.2  | Atualização da simulação Packet Tracer do **Terminal 3** com OSPF, DHCP, VoIP, DNS, NAT e ACLs. Integração de todas as simulações em `campus.pkt`. | 1240904     |
| T.3.4  | Atualização da simulação Packet Tracer do **Terminal 4** com OSPF, DHCP, VoIP, DNS, NAT e ACLs.                                                    | 1240935     |
| T.3.3  | ~~Terminal 5~~ — **Não aplicável** (a equipa cobre os Terminais 2, 3 e 4).                                                                         | —           |

> Ponto de partida: cada membro deve usar como base o ficheiro `campus.pkt` resultante do Sprint 2, trabalhando sobre o mesmo terminal de sempre.
> As tarefas de ACL (firewall estática) devem ser **as últimas a ser implementadas**, apenas depois de todas as outras funcionalidades estarem testadas e a funcionar.

---

# 2. Decisões técnicas e coordenação

## 2.1 OSPF — Áreas e configuração

O routing estático é eliminado e substituído por OSPF dinâmico. A infra-estrutura passa a constituir um único domínio OSPF (Sistema Autónomo).

### Áreas OSPF

| Terminal / Rede            | OSPF Area ID | Redes abrangidas              |
|----------------------------|--------------|-------------------------------|
| Campus Backbone (VLAN 769) | **0**        | 10.61.174.0/24                |
| Terminal 2                 | **2**        | Todas as redes internas do T2 |
| Terminal 3                 | **3**        | Todas as redes internas do T3 |
| Terminal 4                 | **4**        | Todas as redes internas do T4 |

> A area 0 é obrigatória para o backbone. Cada terminal usa um ID de área único e diferente.

### Comandos de referência para T2-Router (Terminal 2 - Raiz DNS)

```
router ospf 1
 router-id 2.2.2.2
 network 10.61.174.0 0.0.0.255 area 0        ! backbone — área 0
 network 10.61.152.0 0.0.3.255 area 2        ! t2-outlets
 network 10.61.128.0 0.0.7.255 area 2        ! t2-wifi
 network 10.61.164.0 0.0.1.255 area 2        ! t2-voip
 network 10.61.173.128 0.0.0.127 area 2      ! t2-servers
 default-information originate                   ! redistribuir rota default via OSPF
```

### DHCPv4 no T2-Router

```
! Excluir IPs estáticos dos pools
ip dhcp excluded-address 10.61.152.1        ! gateway t2-outlets
ip dhcp excluded-address 10.61.128.1        ! gateway t2-wifi
ip dhcp excluded-address 10.61.164.1        ! gateway t2-voip
ip dhcp excluded-address 10.61.173.129      ! gateway t2-servers (DNS)
ip dhcp excluded-address 10.61.173.131      ! server1 HTTP

ip dhcp pool t2-outlets-pool
 network 10.61.152.0 255.255.252.0
 default-router 10.61.152.1
 dns-server 10.61.173.129
 domain-name terminal-2.airport-campus-network

ip dhcp pool t2-wifi-pool
 network 10.61.128.0 255.255.248.0
 default-router 10.61.128.1
 dns-server 10.61.173.129
 domain-name terminal-2.airport-campus-network

ip dhcp pool t2-voip-pool
 network 10.61.164.0 255.255.254.0
 default-router 10.61.164.1
 dns-server 10.61.173.129
 domain-name terminal-2.airport-campus-network
 option 150 ip 10.61.164.1                  ! TFTP para Cisco 7960
```

### VoIP no T2-Router (Cisco Telephony Service)

```
telephony-service
 max-ephones 2
 max-dn 2
 ip source-address 10.61.164.1 port 2000
 auto assign 1 to 2

ephone-dn 1
 number 2001

ephone-dn 2
 number 2002

! Dial-peers para encaminhamento inter-terminal
dial-peer voice 3 voip
 destination-pattern 3...
 session target ipv4:10.61.174.3

dial-peer voice 4 voip
 destination-pattern 4...
 session target ipv4:10.61.174.4
```

### NAT no T2-Router

```
interface FastEthernet0/0.769
 description Campus Backbone (VLAN 769)
 encapsulation dot1Q 769
 ip address 10.61.174.2 255.255.255.0
 ip nat outside

interface FastEthernet0/0.771
 encapsulation dot1Q 771
 ip address 10.61.152.1 255.255.252.0
 ip nat inside

interface FastEthernet0/0.772
 encapsulation dot1Q 772
 ip address 10.61.128.1 255.255.248.0
 ip nat inside

interface FastEthernet0/0.773
 encapsulation dot1Q 773
 ip address 10.61.164.1 255.255.254.0
 ip nat inside

interface FastEthernet0/0.774
 encapsulation dot1Q 774
 ip address 10.61.173.129 255.255.255.128
 ip nat inside

! Redirecionar HTTP/HTTPS para o servidor DNS (T2-Server - 10.61.173.129)
ip nat inside source static tcp 10.61.173.129 80 10.61.174.2 80
ip nat inside source static tcp 10.61.173.129 443 10.61.174.2 443
```

### ACLs no T2-Router (exemplo)

```
access-list 100 deny ip 10.61.128.0 0.0.127.255 any
access-list 100 deny ip 10.61.160.0 0.0.15.255 any
access-list 100 permit icmp any any echo
access-list 100 permit icmp any any echo-reply
access-list 100 permit udp any host 10.61.173.129 eq 53
access-list 100 permit tcp any host 10.61.173.129 eq 53
access-list 100 permit tcp any host 10.61.173.129 eq 80
access-list 100 permit tcp any host 10.61.173.129 eq 443
access-list 100 permit 89 any any
access-list 100 permit udp any eq bootpc any eq bootps
access-list 100 permit udp any any eq 69
access-list 100 permit tcp any any eq 2000
access-list 100 permit ip any any

interface FastEthernet0/0.769
 ip access-group 100 in
```

> A area 0 é obrigatória para o backbone. Cada terminal usa um ID de área único e diferente.

### Regras de configuração OSPF

- Eliminar **todas** as rotas estáticas em todos os routers, **exceto** a rota por omissão (`0.0.0.0/0`) no T2-Router, que deve ser mantida.
- O T2-Router deve redistribuir a rota por omissão no domínio OSPF com o comando `default-information originate`.
- Cada router anuncia as suas redes diretamente ligadas na área correspondente ao seu terminal, **exceto** a rede do backbone (VLAN 769), que é anunciada na área 0.

### Comandos de referência (exemplo para T4-Router)

```
router ospf 1
 router-id 4.4.4.4
 network 10.61.174.0 0.0.0.255 area 0        ! backbone — área 0
 network 10.61.160.0 0.0.3.255 area 4        ! t4-outlets
 network 10.61.144.0 0.0.7.255 area 4        ! t4-wifi
 network 10.61.168.0 0.0.1.255 area 4        ! t4-voip
 network 10.61.173.0 0.0.0.127 area 4        ! t4-servers
```

---

## 2.2 Servidores HTTP — novos servidores nas DMZ

Em cada terminal é adicionado um **segundo servidor** à rede DMZ de Servidores. Este servidor executa o serviço HTTP/HTTPS e apresenta uma página HTML simples que identifica o terminal.

| Terminal | Servidor HTTP  | IP               | Máscara         | Gateway       |
|----------|----------------|------------------|-----------------|---------------|
| T2       | T2-Server-HTTP | 10.61.173.131/25 | 255.255.255.128 | 10.61.173.129 |
| T3       | T3-Server-HTTP | 10.61.172.11     | 255.255.255.0   | 10.61.172.1   |
| T4       | T4-Server-HTTP | 10.61.173.11     | 255.255.255.128 | 10.61.173.1   |

> O servidor existente de cada terminal (adicionado no Sprint 2) passa a ser o **servidor DNS** (ns). O novo servidor é o **servidor HTTP** (server1).

---

## 2.3 DHCPv4

O router de cada terminal fornece DHCP para as redes internas do terminal, **exceto** as redes DMZ e o backbone, cujos endereços são estáticos.

### Pools DHCP por terminal

#### Terminal 4 (T4-Router)

| Pool            | Rede           | Gateway     | DNS Server   | Domain name                  | Obs.              |
|-----------------|----------------|-------------|--------------|------------------------------|-------------------|
| T4-outlets-pool | 10.61.160.0/22 | 10.61.160.1 | 10.61.173.10 | terminal-4.airport-campus-network | —                 |
| T4-wifi-pool    | 10.61.144.0/21 | 10.61.144.1 | 10.61.173.10 | terminal-4.airport-campus-network | —                 |
| T4-voip-pool    | 10.61.168.0/23 | 10.61.168.1 | 10.61.173.10 | terminal-4.airport-campus-network | Inclui option 150 |

> **Option 150** (TFTP para IP phones 7960): apontar para o IP do router na VLAN VoIP — `10.61.168.1`

### Endereços excluídos dos pools (IPs estáticos a reservar)

```
ip dhcp excluded-address 10.61.160.1        ! gateway t4-outlets
ip dhcp excluded-address 10.61.144.1        ! gateway t4-wifi
ip dhcp excluded-address 10.61.168.1        ! gateway t4-voip
```

### Comandos de referência (T4-Router — pool outlets)

```
ip dhcp pool T4-outlets-pool
 network 10.61.160.0 255.255.252.0
 default-router 10.61.160.1
 dns-server 10.61.173.10
 domain-name terminal-4.airport-campus-network

ip dhcp pool T4-voip-pool
 network 10.61.168.0 255.255.254.0
 default-router 10.61.168.1
 dns-server 10.61.173.10
 domain-name terminal-4.airport-campus-network
 option 150 ip 10.61.168.1
```

---

## 2.4 VoIP — Numeração e encaminhamento de chamadas

### Esquema de prefixos e numeração

Cada terminal usa um dígito de prefixo único. Os números de telefone têm 4 dígitos.

| Terminal | Prefixo | Números locais | Exemplos |
| -------- | ------- | -------------- | -------- |
| Terminal 2 | **2** | 2001 – 2999 | 2001, 2002 |
| Terminal 3 | **3** | 3001 – 3999 | 3001, 3002 |
| Terminal 4 | **4** | 4001 – 4999 | 4001, 4002 |

> Cada terminal deve ter **pelo menos 2 telemóveis IP 7960** ligados e configurados.

### Encaminhamento de chamadas entre terminais (dial-peers)

Chamadas cujo prefixo não é local são encaminhadas para o router do terminal destino via backbone.

#### T4-Router — encaminhamento de saída

| Destino    | Padrão | Session Target (IP do router destino) |
|------------|--------|---------------------------------------|
| Terminal 2 | `2...` | `10.61.174.2`                         |
| Terminal 3 | `3...` | `10.61.174.3`                         |

```
dial-peer voice 2 voip
 destination-pattern 2...
 session target ipv4:10.61.174.2

dial-peer voice 3 voip
 destination-pattern 3...
 session target ipv4:10.61.174.3
```

### Configuração Cisco Telephony Service (CME) em T4-Router

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

### Configuração das portas de switch para os telefones VoIP

```
interface FastEthernet X/X
 switchport mode access
 no switchport access vlan
 switchport voice vlan 781
 no shutdown
```

---

## 2.5 DNS — Domínios e servidores de nomes

### Hierarquia de domínios

| Terminal  | Domínio DNS                    | Servidor de nomes (ns)            | IP do servidor ns                         |
|-----------|--------------------------------|-----------------------------------|-------------------------------------------|
| T2 (raiz) | `airport-campus-network`            | `ns.airport-campus-network`            | 10.61.173.129 (T2-Server)                |
| T3        | `terminal-3.airport-campus-network` | `ns.terminal-3.airport-campus-network` | 10.61.172.10                              |
| T4        | `terminal-4.airport-campus-network` | `ns.terminal-4.airport-campus-network` | 10.61.173.10                              |

> O servidor DNS de cada terminal é o servidor já existente do Sprint 2 (T3-Server, T4-Server, etc.).
> O domínio raiz `airport-campus-network` é tratado como se fosse o domínio DNS root da infraestrutura.

### Registos DNS obrigatórios em cada servidor

#### Servidor T4 — `terminal-4.airport-campus-network`

| Tipo     | Nome                                   | Valor                                  | Descrição                                 |
|----------|----------------------------------------|----------------------------------------|-------------------------------------------|
| A        | `ns.terminal-4.airport-campus-network`      | 10.61.173.10                           | Servidor DNS/ns do T4                     |
| A        | `server1.terminal-4.airport-campus-network` | 10.61.173.11                           | Servidor HTTP do T4                       |
| CNAME    | `www.terminal-4.airport-campus-network`     | `server1.terminal-4.airport-campus-network` | Alias web                                 |
| CNAME    | `web.terminal-4.airport-campus-network`     | `server1.terminal-4.airport-campus-network` | Alias web                                 |
| CNAME    | `dns.terminal-4.airport-campus-network`     | `ns.terminal-4.airport-campus-network`      | Alias dns                                 |
| NS       | `terminal-4.airport-campus-network`         | `ns.terminal-4.airport-campus-network`      | Registo NS do domínio                     |
| A (glue) | `ns.airport-campus-network`                 | 10.61.173.129                         | Servidor raiz (necessário para resolução) |

> Todos os nomes nos registos DNS devem ser **FQDNs** terminando em `airport-campus-network` (sem ponto final, de forma consistente em toda a equipa).

### Registos DNS obrigatórios no T2 (raiz `airport-campus-network`)

O Terminal 2 é o responsável pela raiz DNS. O servidor T2-Server (10.61.173.129) deve conter:

| Tipo     | Nome                                   | Valor                                  | Descrição                                 |
|----------|----------------------------------------|----------------------------------------|-------------------------------------------|
| A        | `ns.airport-campus-network`                | 10.61.173.129                         | Servidor DNS raiz (T2)                   |
| A        | `server1.airport-campus-network`           | 10.61.173.131                         | Servidor HTTP (novo)                      |
| CNAME    | `www.airport-campus-network`               | `server1.airport-campus-network`           | Alias web                                 |
| CNAME    | `web.airport-campus-network`               | `server1.airport-campus-network`           | Alias web                                 |
| CNAME    | `dns.airport-campus-network`               | `ns.airport-campus-network`                | Alias dns                                 |
| NS       | `terminal-3.airport-campus-network`       | `ns.terminal-3.airport-campus-network`      | Subdomínio NS record                      |
| A (glue) | `ns.terminal-3.airport-campus-network`    | 10.61.172.10                          | Glue record para T3                       |
| NS       | `terminal-4.airport-campus-network`       | `ns.terminal-4.airport-campus-network`      | Subdomínio NS record                      |
| A (glue) | `ns.terminal-4.airport-campus-network`    | 10.61.173.10                          | Glue record para T4                       |

### Configuração DNS dos end-devices com IP estático (servidores)

Adicionar manualmente em cada servidor:
- DNS Server: IP do servidor ns local (ex: `10.61.173.10` para T4)
- Domain: nome do domínio local (ex: `terminal-4.airport-campus-network`)

---

## 2.6 NAT — Redirecionamento HTTP/HTTPS para servidor DNS

Em cada router, os pedidos HTTP (porta 80) e HTTPS (porta 443) recebidos na interface do backbone são redirecionados para o servidor DNS local (que deve ter o serviço HTTP/HTTPS ativo).

### NAT estático para T4-Router

```
! Definir interface interna e externa
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

! Redirecionar HTTP e HTTPS para o servidor DNS (T4-Server — 10.61.173.10)
ip nat inside source static tcp 10.61.173.10 80 10.61.174.4 80
ip nat inside source static tcp 10.61.173.10 443 10.61.174.4 443
```

> O servidor DNS de cada terminal deve ter o serviço HTTP/HTTPS ativado e uma página HTML de identificação criada.

---

## 2.7 ACLs — Firewall estática (a implementar por último)

> ⚠️ As ACLs devem ser **a última tarefa** deste sprint. Só devem ser configuradas depois de OSPF, DHCP, VoIP, DNS e NAT estarem todos testados e a funcionar corretamente.

As políticas de acesso, por ordem de prioridade, são:

| Prioridade | Política                                                                                                                                                        |
|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1ª         | Bloquear spoofing — tráfego de redes internas com IPs falsos; tráfego do backbone com IPs internos                                                              |
| 2ª         | Permitir sempre ICMP echo request e echo reply                                                                                                                  |
| 3ª         | Bloquear todo o tráfego para a DMZ, excepto DNS (UDP/TCP 53) e HTTP/HTTPS (TCP 80/443) para os respectivos servidores. Permitir todo o tráfego originado na DMZ |
| 4ª         | Bloquear tráfego dirigido ao próprio router, com excepções para serviços activos (ver abaixo)                                                                   |
| 5ª         | Permitir o restante tráfego de passagem                                                                                                                         |

### Exceções obrigatórias à política 4ª (tráfego permitido para o router)

- DHCP Discover/Request: `src 0.0.0.0`, `dst 255.255.255.255`, UDP porta 67
- TFTP (para IP phones): UDP porta 69
- OSPF: protocolo IP 89
- Cisco Telephony Service (SCCP/VoIP): TCP porta 2000
- Tráfego abrangido pelas regras NAT estático (HTTP/HTTPS)

---

## 2.8 Organização do trabalho

Cada membro usa como ponto de partida o ficheiro `campus.pkt` do Sprint 2.

- Cada membro é responsável pela configuração completa do seu terminal.
- Cada membro é também responsável por **substituir o routing estático por OSPF em todos os routers** do campus, mas **sem fazer mais nenhuma alteração** nos routers de outros terminais.
- O membro 1240904 (Terminal 3) tem a tarefa adicional de integrar todos os ficheiros individuais num único `campus.pkt` no final do sprint.

---

# 3. Atribuição de subtarefas

| Número de aluno | Tarefa | Terminal                                                                                                              |
|-----------------|--------|-----------------------------------------------------------------------------------------------------------------------|
| 1221514         | T.3.1  | Terminal 2 (+ manter default route + redistribuir via `default-information originate` + DNS raiz `airport-campus-network`) |
| 1240904         | T.3.2  | Terminal 3 (+ integração final em `campus.pkt`)                                                                       |
| 1240935         | T.3.4  | Terminal 4                                                                                                            |

> Sprint master: **1240935** (definir na reunião de planeamento)

---

# 4. Outputs do Sprint 3

Por membro, devem ser entregues na pasta pessoal do sprint:

| Ficheiro                            | Descrição                                                                               |
|-------------------------------------|-----------------------------------------------------------------------------------------|
| `Terminal4.pkt`                     | Simulação Packet Tracer actualizada                                                     |
| `T4-Router.txt`                     | Config dump do router                                                                   |
| `T4-ICC.txt`, `T4-HCC-F0.txt`, etc. | Config dump de cada switch                                                              |
| Documento DNS                       | Screenshot ou documento com os registos da base de dados DNS do servidor ns do terminal |

O membro 1240904 entrega também:
| `campus.pkt` | Integração de todas as simulações |