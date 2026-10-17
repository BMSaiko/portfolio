# Índice da documentação — Plataforma AISafe Flight Control (Projeto Integrador do 4.º semestre LEI, grupo 2DE4)

> Documentação adicionada em 2026 (portfolio). Isto é um **mapa** dos documentos originais da equipa; nada foi editado. Os factos foram lidos do repositório (contagens medidas por comando); o que não é verificável está marcado `TODO: confirmar com o BMS`. Versão inglesa: [INDEX.md](INDEX.md). Ver também [SETUP](SETUP.pt.md) e as notas existentes de [glossário](global-artifacts/glossary.md), [testes na cloud](global-artifacts/CLOUD_TESTING.md) e [acesso VPN à cloud](global-artifacts/CLOUD_VPN_ACCESS.md), que são ligadas, não duplicadas.

O README do projeto remete para `docs/readme.md`, que não existe (achado 1); este índice é o ponto de entrada de `docs/`.

## 1. Estrutura

| Caminho | UC | Conteúdo |
|---|---|---|
| [`docs/us_NNN/`](./) | EAPLI, LPROG, RCOMP | 46 pastas por história (`readme.md`, muitas vezes com diagrama de sequência PlantUML e PNG; `tests.md` na US071/US072) |
| [`docs/global-artifacts/`](global-artifacts/) | todas | glossário, composição do grupo, modelo de domínio (final e versões anteriores), diagrama de casos de uso, reuniões (`Meetings/`), PDFs de requisitos (`sem4/`), notas da cloud, template de peer-assessment |
| [`docs/Peer-Assessement/`](Peer-Assessement/) | LAPR4 | peer-assessments individuais |
| [`../scomp/docs/us_NNN/`](../scomp/docs/) | SCOMP | 11 histórias (US100–US112), mais os READMEs do [Sprint 2](../scomp/README-sprint2.md) e do [Sprint 3](../scomp/README-sprint3.md) |
| [`../aisafe-flight-control/`](../aisafe-flight-control/) | EAPLI | aplicação Java Maven multi-módulo (7 módulos) |
| [`../aisafe-logging-server/`](../aisafe-logging-server/) | RCOMP | servidor de registo de acessos remotos (UDP, HTTP) |
| [`../lprog/`](../lprog/) | LPROG | `FlightDSL.g4` (ANTLR4), validador/importador, `flight_tester` em C |
| [`../scomp/`](../scomp/) | SCOMP | simulador de voos em C (processos, memória partilhada, threads, semáforos) |
| [`../eapli.base/`](../eapli.base/) | EAPLI | cópia do projeto-exemplo da cadeira (módulos `exemplo.*`) |

## 2. Rastreabilidade: família de user stories → documentos → código → testes

As contagens de testes são ocorrências de `@Test`/`@ParameterizedTest` em `src/test/java` por módulo: `aisafe.core` 656 (62 ficheiros), `aisafe.app.backoffice.console` 89 (9), `aisafe.app.server` 6 (1), `aisafe-logging-server` 39 (3), `lprog` 93 (9), `eapli.base` 6 (2). Os ids das histórias são citados no código (comentários, nomes de testes); "citado em" indica onde.

| Histórias | Assunto | Docs | Código (módulos onde o id é citado) |
|---|---|---|---|
| US001–US005 | Restrições técnicas, repositório, estrutura, servidor de CI, deploy automático | `docs/us_001`…`us_005` | [`.github/workflows/maven-ci.yml`](../.github/workflows/maven-ci.yml), `pom.xml` da raiz, [`aisafe-flight-control/scripts/`](../aisafe-flight-control/scripts/) |
| US010–US011 | Modelo de domínio, justificação dos agregados | [`us_010`](us_010/), [`us_011`](us_011/) (39 ficheiros de diagramas), [`global-artifacts/US10-DomainModel-FINAL`](global-artifacts/US10-DomainModel-FINAL.puml) | `aisafe.core` |
| US030–US033 | Autenticação/autorização, registar/desativar/listar utilizadores | `us_030`…`us_033` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ testes) |
| US041–US043 | Dados meteorológicos: registar, importação em massa, consultar | `us_041`…`us_043` | `aisafe.core`, `aisafe.app.backoffice.console` (+ testes) |
| US050–US058 | Áreas de controlo aéreo, aeroportos, modelos de aeronave e de motor | `us_050`, `us_052`, `us_055`…`us_058` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ testes) |
| US060–US064 | Companhias de transporte aéreo e colaboradores | `us_060`…`us_064` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ testes) |
| US070–US078 | Frota, rotas de voo, pilotos, acesso remoto dos colaboradores | `us_070`…`us_078` | `aisafe.core`, `aisafe.app.company.console`, `aisafe.persistence.impl`; a US078 também em `aisafe.app.server` e nos testes de `aisafe-logging-server` |
| US080–US085 | Planos de voo (manual, a partir de ficheiro), meteorologia nos voos, especificação da DSL, validação | `us_080`…`us_085` | `lprog` (gramática, validador, importador), `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.app.server` |
| US090–US091 | Registo externo de acessos remotos, visualização dos registos | `us_090`, `us_091` | [`aisafe-logging-server`](../aisafe-logging-server/) |
| US100–US112 | Simulação SCOMP: memória partilhada, thread de violações, variável de condição, semáforos, relatórios | [`scomp/docs/us_100`…`us_112`](../scomp/docs/), `docs/us_107`, `us_111`, `us_112` (um segundo documento, diferente, por id) | [`scomp/*.c`](../scomp/); a US112 (relatório mensal) é citada em `aisafe.bootstrap`, implementada pelo `MonthlyReportGenerator` em `aisafe.core`; exemplos de saída em [`aisafe-flight-control/reports/monthly/`](../aisafe-flight-control/reports/monthly/) (jan–mar 2026) |
| US120–US121 | DSL de voo (Sprint 3), plano de voo a partir de ficheiro | `us_120`, `us_121` | testes de `lprog` |
| US666 | — (não é um id de história da cadeira) | [`global-artifacts/us_666`](global-artifacts/us_666/readme.md) | — |

Ids citados no código mas sem pasta de documentação: **US044, US086** (lado servidor de registo/meteorologia). Ids com pasta de documentação mas nunca citados no código: US001–US005, US010–US011, US041, US075–US076, US090–US091, US103, US121 (muitas são histórias de processo ou só de UI).

## 3. Testes e CI

- **CI** ([maven-ci.yml](../.github/workflows/maven-ci.yml)): em push/PR para `master`/`main` e todas as noites (02:00 UTC), JDK 21, corre `mvn -B clean verify --file eapli.base/pom.xml` e publica os relatórios Surefire e JaCoCo. Compila **só o `eapli.base`** (o framework de exemplo), não o `aisafe-flight-control`, o `lprog`, o `aisafe-logging-server` nem o `scomp` (achado 3).
- **Java**: ~880 métodos de teste nos módulos AISafe (acima). Há testes de aceitação do back-office (8 ficheiros versionados com "acceptance" no nome).
- **C**: sem testes automáticos; os `scomp/config/*.cfg` são entradas de cenários (colisões, stress, parâmetros inválidos) usadas manualmente ([README-sprint3 §4](../scomp/README-sprint3.md)).

## 4. Achados verificados

| # | Achado | Evidência |
|---|---|---|
| 1 | O README do projeto remete para `docs/readme.md`; esse ficheiro não existe em `docs/` | `ls docs/*.md` |
| 2 | A documentação do mesmo id de história está dividida por duas árvores: `docs/us_107`, `us_111`, `us_112` e `scomp/docs/us_107`, `us_111`, `us_112` têm conteúdo diferente | `diff` |
| 3 | A CI só compila o `eapli.base`; os módulos Java AISafe, a gramática ANTLR e o simulador em C não são compilados nem testados no pipeline | `maven-ci.yml` |
| 4 | A unidade de persistência usa uma base H2 em ficheiro (`jdbc:h2:./database/aisafe`) com `hibernate.hbm2ddl.auto=create`, ou seja o esquema é recriado a cada arranque; utilizador `sa` | `aisafe.persistence.impl/.../persistence.xml` |
| 5 | Os scripts de cloud usam por omissão a VM cloud do ISEP da equipa (nome e IP, acessíveis só na VPN da universidade); as passwords vêm do painel DEI Cloud e não são guardadas | `scripts/run-cloud.sh`, `deploy-to-cloud.sh` |
| 6 | `libs/` está vazia (o README diz que os `libs/*.jar` ficaram de fora) e um ficheiro de teste com caminho muito longo não foi extraído no Windows, por isso os builds em Windows não o têm | notas do README, `ls libs` |
| 7 | O `eapli.base` é uma cópia do framework de exemplo da cadeira (módulos `exemplo.*`) e não código do próprio projeto; é o que a CI exercita | `eapli.base/` |
| 8 | O Sprint 3 do SCOMP produz dois relatórios (por processo na US109 e por thread na US111) e usa interpolação linear entre waypoints em vez das fórmulas físicas dos requisitos; ambos são indicados pela equipa como limitações conhecidas | [README-sprint3](../scomp/README-sprint3.md) §3 |
| 9 | O texto mistura inglês e português (as notas do SCOMP e as atas de reunião estão em grande parte em português) | ficheiros |
| 10 | Vários ficheiros Markdown usam fins de linha CRLF (os READMEs e o glossário, por exemplo) enquanto o `.gitattributes` tem `* text=auto`, o que explica os avisos LF→CRLF do Git no Windows | `.gitattributes`, `grep -c $'\r'` |

## 5. Lista de melhorias (não aplicadas)

| Prioridade | Melhoria | Esforço |
|---|---|---|
| P1 | Apontar a CI aos módulos AISafe (`mvn -B verify` em `aisafe-flight-control` e na raiz) e acrescentar `make` para o `scomp` | 1 h |
| P1 | Criar `docs/readme.md` (ou corrigir a ligação do README) a apontar para este índice | 10 min |
| P2 | Fundir ou ligar entre si os dois conjuntos de documentos `us_107/111/112` | 30 min |
| P2 | Mudar `hbm2ddl.auto` de `create` para `update` (ou `validate`) em qualquer uso que não seja demo | 15 min |
| P2 | Acrescentar pastas de documentação para US044 e US086 | 1 h |
| P3 | Acrescentar um `Makefile`/script de topo a listar os comandos de build e execução por componente | 30 min |
| P3 | Acrescentar testes em C (por exemplo CTest ou Unity) ao simulador SCOMP | 1 dia |
