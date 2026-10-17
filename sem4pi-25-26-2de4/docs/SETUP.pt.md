# Setup — Plataforma AISafe Flight Control

> Documentação adicionada em 2026 (portfolio). Os comandos **não foram testados** (esta máquina não tem JDK, Maven nem GCC). Tudo vem dos POMs, dos scripts, do workflow de CI e dos READMEs da equipa. Versão inglesa: [SETUP.md](SETUP.md).

## Pré-requisitos

| Ferramenta | Versão / origem |
|---|---|
| JDK | 21 (`maven.compiler.source/target` e `<release>` nos POMs; a CI usa Temurin 21) |
| Maven | 3.x (os scripts usam `mvnw.cmd` se existir, senão `mvn`) |
| GCC + make + threads/semáforos POSIX | para o `scomp` (Linux/WSL) e para `lprog/src/main/c/flight_tester` |
| Internet | Dependências Maven; o plugin ANTLR4 gera o parser da DSL durante o build |

Base de dados: **base H2 embebida em ficheiro** (`./database/aisafe`), criada automaticamente; nada a instalar. De notar que o esquema é recriado a cada arranque ([INDEX §4](INDEX.pt.md#4-achados-verificados), achado 4).

## Build (do README)

```bash
cd sem4pi-25-26-2de4
mvn clean install                      # POM da raiz: eapli.base + lprog (gramática ANTLR)
cd aisafe-flight-control && mvn clean install
cd ../scomp && make                    # simulador em C
```

A pasta `libs/` está vazia de propósito. Um ficheiro de teste de aceitação do back-office com caminho muito longo não está no repositório (notas do [README](../README.pt.md)).

## Executar

### Back office (Java, consola)

```bash
cd aisafe-flight-control
mvn -DskipTests install
mvn exec:java -pl aisafe.app.backoffice.console
```

É o que o `scripts/run-backoffice.bat` faz no Windows. Carregar primeiro dados de exemplo com `scripts/bootstrap.bat` ou com o módulo `aisafe.bootstrap` (utilizadores, áreas, aeroportos, modelos de aeronave, companhias e 12 meses de dados segundo o README).

Outros pontos de entrada (nomes das classes, encontradas por `main`): `AirTransportCompanyApp` (consola da companhia), `AtccTcpServer` e `WeatherServerMain` (servidor, `aisafe.app.server`), `RemoteAccessesLoggingServer` (`aisafe-logging-server`). Os scripts `run-us078-us090.bat` e os ficheiros `phase1`…`phase6` ligam servidor, cliente, registo e dashboard no Windows; as variantes cloud (`run-cloud.sh`, `deploy-to-cloud.sh`, `setup-cloud-vs.sh`) precisam da VPN do ISEP e de uma VM cloud (ver [testes na cloud](global-artifacts/CLOUD_TESTING.md) e [acesso VPN](global-artifacts/CLOUD_VPN_ACCESS.md)). TODO: confirmar com o BMS a sequência local mínima (bootstrap → servidor → cliente → servidor de registo) e as portas TCP/UDP; `LOGGING_UDP_PORT=9999` e `LOGGING_HTTP_PORT=8080` aparecem em `run-cloud.sh`.

### DSL de voo (`lprog`)

A gramática é `lprog/src/main/antlr4/pt/isep/sem4pi/lprog/FlightDSL.g4`; o `mvn clean install` na raiz gera o parser. O tester em C compila com o seu Makefile em `lprog/src/main/c/flight_tester/`.

### Simulador SCOMP (C)

Do [README-sprint3](../scomp/README-sprint3.md) §4:

```bash
cd scomp
make clean && make
./air_sim config/opo_mad.cfg            # 2 voos, sem violações
./air_sim config/opo_mad_stress.cfg     # 8 voos, violações e terminações
```

Os outros cenários estão em `scomp/config/` (`collisions.cfg`, `invalid_params.cfg`, `progressive_collisions.cfg`, `single_flight.cfg`, `three_violations.cfg`). Os relatórios são escritos junto ao executável (por exemplo `simulation_report_<timestamp>.txt`).

## Testes

```bash
cd aisafe-flight-control && mvn test     # ~880 métodos de teste JUnit 5 nos módulos AISafe
```

Não executado. O workflow de CI só corre testes do `eapli.base` ([INDEX §3](INDEX.pt.md#3-testes-e-ci)). O simulador em C não tem testes automáticos.

## Diagramas

O `generate-plantuml-diagrams.sh` na raiz do repositório regenera os diagramas PlantUML (precisa do PlantUML; não executado).
