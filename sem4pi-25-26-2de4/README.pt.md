# AISafe — Flight Control Platform (Integrative Project, 4th semester)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Gestão de tráfego aéreo e planos de voo com back office Java em DDD, DSL para planos de voo, simulação em C e servidores de rede.

| | |
|---|---|
| **Contexto** | LEI — Projeto Integrador do 4.º semestre (EAPLI · SCOMP · RCOMP · LPROG) |
| **Ano** | 2025/26 (Mar–Jun 2026) |
| **Equipa** | Grupo 2DE4, 5 elementos, 3 sprints |
| **Stack** | Java · EAPLI framework · Maven · JPA · ANTLR4 · C (processes, threads, shared memory) · TCP/UDP · PlantUML · JUnit 5 |

## A minha contribuição
- Camadas de **domínio e persistência** (`aisafe.core`, `aisafe.persistence.impl`) e consola de back office.
- **US121** — importação de planos de voo a partir de ficheiro DSL com camada de validação antes do parsing ANTLR; testes migrados para JUnit 5.
- **US112** relatórios mensais e **US111** relatórios de simulação; **US107** notificação entre threads com variáveis de condição; **US090** logging externo UDP de acessos remotos.
- Modelo de domínio **DDD**, justificação de agregados e diagramas de sequência; dados de bootstrap para 12 meses e casos limite.

## O que este projeto demonstra
- **Domain-Driven Design** com a framework EAPLI: agregados, repositórios, serviços de aplicação, arquitetura em camadas.
- **Processamento de linguagens**: DSL de planos de voo com gramática ANTLR4.
- **Programação de sistemas em C**: processos (`fork`), threads POSIX, mutexes/variáveis de condição e memória partilhada num simulador de voos que deteta violações de segurança.
- **Redes**: servidores TCP de acesso remoto e logging UDP; notas de deploy em cloud/VPN.
- Documentação rigorosa: user stories com análise, desenho e testes; atas de reuniões.

## Funcionalidades
- `aisafe-flight-control` — módulos Java: core, persistência, bootstrap, consola de back office, consola de empresa, servidor.
- `aisafe-logging-server` — servidor central de logging.
- `lprog/` — gramática `FlightDSL.g4` e um testador de voos em C.
- `scomp/` — simulador em C: processos de voo, memória partilhada, thread de relatórios (ver `README-sprint2.md`, `README-sprint3.md`).
- `docs/` — documentação por US, modelo de domínio e reuniões.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `aisafe-flight-control/` | Aplicação Java Maven multi-módulo |
| `aisafe-logging-server/` | Servidor de logging |
| `lprog/` | Gramática ANTLR + testador C |
| `scomp/` | Simulação em C (Makefile) |
| `docs/` | Documentação |

## Execução
```bash
mvn clean install                 # raiz: eapli.base + lprog (gramática ANTLR)
cd aisafe-flight-control && mvn clean install
cd ../scomp && make               # simulador em C
```
Ver `docs/readme.md` para o planeamento e a documentação técnica.

## Notas
- Um ficheiro de teste com caminho muito longo (`InsertWeatherDataInFlightPlanUiAcceptanceTest.java`) não pôde ser extraído no Windows e não está incluído.
- Outputs de build, `libs/*.jar` e pastas de IDE ficaram de fora.

## Documentação

- [Auditoria da documentação](../docs/AUDIT.pt.md#sem4pi-25-26-2de4) — estado e lacunas deste projeto
- [Artefactos globais (casos de uso, reuniões, requisitos)](docs/global-artifacts/)
- [Glossário](docs/global-artifacts/glossary.md)
- [Testes em cloud](docs/global-artifacts/CLOUD_TESTING.md)
- [Acesso VPN à cloud](docs/global-artifacts/CLOUD_VPN_ACCESS.md)
