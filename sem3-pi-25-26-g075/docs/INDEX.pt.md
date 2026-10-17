# Índice da documentação — Logística Ferroviária (Projeto Integrador do 3.º semestre LEI, grupo 075)

> Documentação adicionada em 2026 (portfolio). Isto é um **mapa** dos documentos originais da equipa; nada foi editado. Os factos foram lidos do repositório (contagens medidas por comando); o que não é verificável está marcado `TODO: confirmar com o BMS`. Versão inglesa: [INDEX.md](INDEX.md). Ver também [SETUP](SETUP.pt.md), o [ponto de entrada](README.md) da equipa (só descreve USEI01–03, ver achado 1) e o [glossário](global-artifacts/glossary.md), as [regras de negócio](global-artifacts/business-rules.md) e o [modelo de domínio](global-artifacts/domain-model.puml) existentes, que são ligados, não duplicados.

## 1. Estrutura

| Caminho | UC | Conteúdo |
|---|---|---|
| [`docs/ESINF/`](ESINF/) | ESINF (estruturas de dados) | USEI01–03, 06–08, 11–13, no template da cadeira: `01.requirements-engineering`, `02.analysis`, `03.design` (PlantUML), `04.tests-and-implementation`, mais análises de complexidade |
| [`docs/LAPR3/`](LAPR3/) | LAPR3 (programação/Java + BD) | USLP07–10, mesmo template |
| [`docs/BDDAD/`](BDDAD/) | BDDAD (base de dados) | USBD28, 30, 33, 39–42 (um Markdown cada) |
| [`docs/ARQCP/`](ARQCP/) | ARQCP (arquitetura de computadores) | USAC01–09, funções em Assembly RISC-V |
| [`docs/global-artifacts/`](global-artifacts/) | todas | glossário, regras de negócio, modelo de domínio, diagrama de classes global, justificação das associações |
| [`../warehouseMngt/`](../warehouseMngt/) | ESINF, LAPR3 | Aplicação Java 17 / Maven `logistics-on-rails` |
| [`../stationMngt/`](../stationMngt/) | ARQCP | Assembly RISC-V + C, uma pasta por história |
| [`../sql-scripts/`](../sql-scripts/) | BDDAD | Scripts Oracle e modelos por sprint |
| [`../scrum/`](../scrum/) | LAPR3 | relatórios de sprint (`.pptx`), autoavaliações e burndown (`.xlsx`) |

## 2. Rastreabilidade: user story → documentos → código → testes

"Pelo nome" = a classe tem o nome do assunto da história mas não cita o id; caso contrário o ficheiro que cita é indicado. Neste projeto os ids são citados sobretudo nos testes e quase nunca no código de produção.

### ESINF (Java, `warehouseMngt/src`, pacote `pt.isep.dei.esinf`)

| US | Assunto | Docs | Código | Testes |
|---|---|---|---|---|
| USEI01 | Alocação de caixas, FEFO/FIFO | [USEI01](ESINF/USEI01/USEI01-README.md) | `service/warehouse/WarehouseAllocator` *(pelo nome)* | `warehouse/WarehouseAllocatorTest`, `EdgeCasesWarehouseTest`, `WarehouseIntegrationTest`, `PerformanceWarehouseTest` (citam USEI01) |
| USEI02 | Alocação de encomendas STRICT / PARTIAL | [USEI02](ESINF/USEI02/USEI02-README.md) | `service/warehouse/OrderAllocationService` *(pelo nome)* | `orders/OrderAllocationServiceTest`, `EdgeCasesOrderAllocationTest`, `OrderAllocationIntegrationTest`, `PerformanceOrderAllocationTest` (citam USEI02) |
| USEI03 | Empacotamento em carrinhos (FF / FFD / BFD) | [USEI03](ESINF/USEI03/USEI03-README.md) | `service/warehouse/TrolleyPackingService` *(pelo nome)* | `warehouse/TrolleyPackingServiceTest` |
| USEI06 | Pesquisa de estações | [USEI06](ESINF/USEI06/USEI06-README.md), [complexidade](ESINF/USEI06/complexity.md) | `service/StationSearchService`, `ui/SearchStationsMenu` *(pelo nome)* | `stations/StationSearchServiceTest` (cita USEI06); o `BalancedTreeBuilder` também menciona USEI06 |
| USEI07 | Árvore 2D balanceada de estações | [USEI07](ESINF/USEI07/USEI07-README.md), [complexidade](ESINF/USEI07/Complexity-Analysis.md) | `service/spatial/BalancedTreeBuilder` *(pelo nome)* | `service/spatial/BalancedTreeTest` (cita USEI07), `PerformanceTests` |
| USEI08 | Consulta por intervalo 2D | [USEI08](ESINF/USEI08/USEI08-README.md), [complexidade](ESINF/USEI08/complexity.md) | classe de implementação não identificada | `stations/StationRangeSearchTest` (cita USEI08) |
| USEI11 | Ordem de melhoria de estações (dependências) | [USEI11](ESINF/USEI11/USEI11-README.md), [complexidade](ESINF/USEI11/complexity.pdf) | `service/railway/RailwayUpgradeService`, `controller/UpgradePlanController`, `ui/UpgradePlanMenu` *(pelo nome)* | `upgrade/UpgradePlanServiceTest`, `UpgradePlanPerformanceTest` (citam USEI11) |
| USEI12 | Rede de backbone mínima | [USEI12](ESINF/USEI12/USEI12-README.md), [complexidade](ESINF/USEI12/complexity.pdf) | `service/railway/MinimalBackboneService` *(pelo nome)* | `service/railway/MinimalBackboneServiceTest` |
| USEI13 | Centralidade de hubs | [USEI13](ESINF/USEI13/USEI13-README.md), [complexidade](ESINF/USEI13/complexity.md) | `service/railway/HubCentralityAnalysisService` *(pelo nome)* | `service/railway/HubCentralityAnalysisServiceTest` |

### LAPR3 (Java + Oracle)

| US | Assunto | Docs | Código *(pelo nome)* |
|---|---|---|---|
| USLP07 | Despacho de comboios: escalonamento e cálculo de rota | [USLP07](LAPR3/USLP07/) | `service/TrainSchedulerService`, `service/TravelTimeCalculatorService`, `controller/TrainSchedulerMenuController`; SQL: [`sql-scripts/Sprint2/uslp07.sql`](../sql-scripts/Sprint2/uslp07.sql) |
| USLP08 | Planeador de rotas de carga | [USLP08](LAPR3/USLP08/) | `service/FreightRoutePlannerService`, `controller/FreightManagerMenuController` |
| USLP09 | Montagem de comboios: seleção de material circulante | [USLP09](LAPR3/USLP09/) | não identificado |
| USLP10 | Mesmo título que a USLP07 (achado 2) | [USLP10](LAPR3/USLP10/) (sem documento de testes) | não identificado |
| USLP03 | — (sem documentação) | — | só SQL: [`sql-scripts/Sprint1/uslp03.sql`](../sql-scripts/Sprint1/uslp03.sql) |

### BDDAD (Oracle, `sql-scripts/`)

| US | Assunto | Docs | Scripts |
|---|---|---|---|
| USBD09, USBD20 | Sprint 1 | nenhuma | [`Sprint1/User Stories/`](../sql-scripts/Sprint1/User%20Stories/) |
| USBD28 | Locomotivas que suportam várias bitolas | [USBD28](BDDAD/USBD28/README.md) | `Sprint2/User Stories/USBD28/` (função, inserts, teste) |
| USBD30 | Instalações com silos de cereais mas sem armazéns | [USBD30](BDDAD/USBD30/README.md) | `Sprint2/User Stories/USBD30/` |
| USBD33 | Validar comprimento máximo do comboio | [USBD33](BDDAD/USBD33/USBD33_documentation.md) | `Sprint3/User Stories/USBD33/` |
| USBD39 | Associar locomotiva a comboio planeado | [USBD39](BDDAD/USBD39/USBD39_documentation.md) | `Sprint3/User Stories/USBD39/` |
| USBD40 | Registar carga e vagões | [USBD40](BDDAD/USBD40/USBD40_documentation.md) | `Sprint3/User Stories/USBD40/` |
| USBD41 | Remover carga de comboio planeado | [USBD41](BDDAD/USBD41/USBD41_documentation.md) | `Sprint3/User Stories/USBD41/` |
| USBD42 | Registar movimento de comboio | [USBD42](BDDAD/USBD42/USBD42_documentation.md) | `Sprint3/User Stories/USBD42/` (lado Java *(pelo nome)*: `service/warehouse/TrainMovementService`) |

### ARQCP (Assembly RISC-V, `stationMngt/`)

| US | Função (segundo o seu doc) | Docs | Código | Testes |
|---|---|---|---|---|
| USAC01 | `encrypt_data` | [USAC01](ARQCP/USAC01/) | [`stationMngt/usac01/`](../stationMngt/usac01/) | driver `main.c` |
| USAC02 | `decrypt_data` | [USAC02](ARQCP/USAC02/) | `stationMngt/usac02/` | driver `main.c` |
| USAC03 | `extract_data` | [USAC03](ARQCP/USAC03/) | `stationMngt/usac03/` | driver `main.c` |
| USAC04 | `format_command` | [USAC04](ARQCP/USAC04/) | `stationMngt/usac04/` | driver `main.c` |
| USAC05 | `enqueue_value` (buffer circular) | [USAC05](ARQCP/USAC05/) | `stationMngt/usac05/` | driver `main.c` |
| USAC06 | `dequeue_value` | [USAC06](ARQCP/USAC06/) | `stationMngt/usac06/` | driver `main.c` |
| USAC07 | `move_n_to_array` | [USAC07](ARQCP/USAC07/) | `stationMngt/usac07/` | driver `main.c` |
| USAC08 | `sort_array` (bubble sort) | [USAC08](ARQCP/USAC08/) | `stationMngt/usac08/` | driver `main.c` |
| USAC09 | `median` | [USAC09](ARQCP/USAC09/) | `stationMngt/usac09/` | driver `main.c` |
| USAC11–12, 13–14, 16 | gestor, sketch de sensores, configuração | nenhuma | `stationMngt/usac11-12/`, `usac13-14/`, `usac16/` | `test_usac11.c`, `test_usac12.c`, `usac1314_test.c` |

## 3. Testes

`warehouseMngt/src/test` tem 17 classes de teste com 169 métodos `@Test` (JUnit 5, Mockito, AssertJ), misturando testes unitários, de casos limite, de integração e de desempenho. As funcionalidades de base de dados não têm testes Java; as histórias SQL trazem os seus `*_test.sql` e blocos anónimos. As histórias em Assembly são exercitadas por drivers em C, sem framework de testes.

## 4. Achados verificados

| # | Achado | Evidência |
|---|---|---|
| 1 | O `docs/README.md` ("Warehouse Management System") só detalha USEI01–03, mas `docs/ESINF` tem nove histórias (USEI01–03, 06–08, 11–13) e a pasta tem também documentos de LAPR3, BDDAD e ARQCP | [README](README.md) |
| 2 | USLP07 e USLP10 têm o mesmo título ("Train Dispatch - Scheduling and Route Calculation") e a USLP10 não tem pasta `04.tests-and-implementation` | [USLP07](LAPR3/USLP07/), [USLP10](LAPR3/USLP10/) |
| 3 | Os documentos ARQCP misturam `README.MD` / `README.md` / `REAME.md` (gralha na USAC03) | `docs/ARQCP/` |
| 4 | `stationMngt/usac11-12`, `usac13-14`, `usac16` têm código e testes mas não têm documentação; USAC10 e USAC15 não têm pasta | listagens |
| 5 | O URL JDBC do Oracle (nome de uma máquina da equipa) está fixo em 5 ficheiros Java e 5 ficheiros têm passwords literais, além da constante de password de acesso do menu. Valores não reproduzidos | `grep` em `warehouseMngt/src` |
| 6 | `stationMngt/usac11-12/config.txt` e `usac16/config.txt` têm um bloco `[USERS]` com nome, nome de utilizador, password e chave de Caesar (valores locais de desenvolvimento; não reproduzidos) | ficheiros |
| 7 | Os Makefiles de Assembly exigem o compilador cruzado `riscv32-linux-gcc` e `qemu-riscv32` com sysroot em `$HOME/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot`; o README não o diz | `stationMngt/*/Makefile` |
| 8 | O POM não tem o plugin exec; o README só dá `mvn test`, por isso o ponto de entrada (`pt.isep.dei.esinf.Main`) não tem comando de execução documentado | `pom.xml`, README |
| 9 | `usac13-14/raspberrycode.txt` é um sketch ao estilo Arduino (DHT11 no pino 28, 2 sensores de humidade do solo, 4 de humidade do ar e 4 de temperatura do ar), guardado como `.txt` | ficheiro |

## 5. Lista de melhorias (não aplicadas)

| Prioridade | Melhoria | Esforço |
|---|---|---|
| P1 | Tirar o URL JDBC e as credenciais do código Java (variáveis de ambiente ou ficheiro de propriedades não versionado com `.example`); rodar as passwords de desenvolvimento | 1–2 h |
| P1 | Atualizar o `docs/README.md` para listar todas as histórias ESINF/LAPR3/BDDAD/ARQCP, ou ligar este índice | 20 min |
| P2 | Documentar USAC11–16 e acrescentar ao README os pré-requisitos do toolchain | 1 h |
| P2 | Corrigir o título da USLP10 e acrescentar o seu documento de testes | 30 min |
| P2 | Acrescentar um job GitHub Actions: `mvn -B test` em `warehouseMngt` | 30 min |
| P3 | Renomear `REAME.md`, uniformizar `README.MD`; renomear `raspberrycode.txt` para `.ino` | 10 min |
