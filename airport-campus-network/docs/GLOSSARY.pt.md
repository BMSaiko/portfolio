# Glossário — Rede do campus aeroportuário RCOMP

> Documentação adicionada em 2026 (portfolio). Termos tal como usados nos documentos da equipa ([INDEX](INDEX.pt.md)); as definições são conhecimento geral de redes, com o uso específico do projeto anotado. Versão inglesa: [GLOSSARY.md](GLOSSARY.md).

## Cablagem (Sprint 1)

| Termo | Significado |
|---|---|
| MC / MCC | Main Cross-Connect — ponto de distribuição do campus; colocado no Terminal 2 |
| IC / ICC | Intermediate Cross-Connect — ponto de distribuição de cada edifício |
| HC / HCC | Horizontal Cross-Connect — distribuição por piso/zona até às tomadas |
| CP | Consolidation Point — ponto passivo entre um HC e as tomadas |
| Tomada / TO | Ficha de parede para dispositivos de utilizador (keystone CAT7) |
| T568B | Ordem de cores dos pares nos conectores RJ45; o padrão escolhido pela equipa |
| CAT7 S/FTP | Cabo de cobre de pares entrançados blindado para a cablagem horizontal (máx. 90 m) |
| OM4 | Fibra multimodo usada nos backbones de edifício e de campus, 24 fibras |
| LSZH | Bainha de cabo de baixa emissão de fumo e sem halogéneos |
| Patch panel | Painel passivo que termina cabos num armário |
| 42U / 24U / 18U … | Alturas de armários em unidades de rack |
| Backbone | Ligação entre cross-connects: de edifício (HC↔IC) ou de campus (entre terminais) |

## Camadas 2 / 3 (Sprint 2)

| Termo | Significado |
|---|---|
| VLAN | LAN virtual; este projeto usa os IDs 769–782 |
| VTP | VLAN Trunking Protocol; domínio `rc2526deg2`, o switch IC é o servidor |
| Trunk | Ligação entre switches que transporta todas as VLANs |
| STP | Spanning Tree Protocol; bloqueia ciclos dos anéis redundantes e nunca é desativado |
| Router-on-a-stick | Uma interface de router dividida em sub-interfaces por VLAN (por exemplo `Fa0/0.769`) |
| DMZ | Segmento para servidores (ou gestão de switches) com acesso restrito |
| `.pkt` | Ficheiro de simulação do Cisco Packet Tracer |
| Running config | Dump de texto da configuração ativa de um dispositivo |

## Serviços (Sprint 3)

| Termo | Significado |
|---|---|
| OSPF / área | Protocolo de encaminhamento link-state; área 0 = backbone do campus, áreas 2, 3, 4 = terminais T2, T3, T4 |
| `default-information originate` | Faz um router anunciar a sua rota por omissão em OSPF (o T2 fá-lo) |
| DHCPv4 | Endereçamento IPv4 automático; um pool no router por rede |
| DNS: A / CNAME / NS / glue | Registo de endereço / alias / delegação de servidor de nomes / endereço do servidor de nomes delegado |
| FQDN | Nome de domínio totalmente qualificado, por exemplo `server1.airport-campus-network` |
| Domínio raiz | `airport-campus-network`, servido pelo T2; `terminal-3` e `terminal-4` são subdomínios delegados |
| VoIP / telephony service | Telefones IP registados no router (SCCP, TCP 2000); plano de numeração e dial-peers encaminham chamadas entre terminais |
| Dial-peer | Regra de encaminhamento de chamadas num router de voz Cisco |
| NAT estático | Tradução fixa, aqui HTTP/HTTPS (portas 80/443) do endereço do backbone para o servidor local |
| ACL | Lista de controlo de acesso; usada como firewall estática (anti-spoofing, ICMP permitido, proteção da DMZ e do router) |
