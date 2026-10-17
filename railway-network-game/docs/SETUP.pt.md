# Setup — Jogo de Rede Ferroviária

> Documentação adicionada em 2026 (portfolio). Os comandos **não foram testados** (a máquina onde isto foi escrito não tem JDK nem Maven); tudo vem do `pom.xml`, do `Main.java` e dos READMEs. Versão inglesa: [SETUP.md](SETUP.md).

## Pré-requisitos

| Ferramenta | Versão / origem |
|---|---|
| JDK | 17 (`java.version` no `pom.xml`) |
| Maven | 3.x |
| Acesso à internet | As dependências incluem `AuthLib` (`pt.isep.lei.esoft`, de `https://repo.repsy.io/mvn/pmaio/lei-soft`, declarado no POM), GraphStream `gs-core`, JUnit Jupiter, commons-lang3 |
| Jupyter (opcional) | Só para os notebooks MATCP (`matcp.ipynb`, `MATCP/US30`, `MATCP/US31`) |

Sem base de dados, variáveis de ambiente nem segredos. Se o repositório Maven externo já não estiver disponível, a resolução do `AuthLib` falha (previsão a partir do POM, não uma execução observada).

## Correr (do README)

```bash
cd railway-network-game
mvn compile exec:java -Dexec.mainClass=pt.ipp.isep.dei.Main
```

O programa pede um cenário (1–4, por omissão 1) carregado de `src/main/resources/scenario<N>_stations.csv` / `scenario<N>_lines.csv` e mostra um menu de consola:

1. Verificar se um comboio pode viajar entre duas estações (US13)
2. Encontrar estações de um tipo alcançáveis a partir de uma origem
3. Encontrar uma rota de manutenção por todas as linhas uma só vez (US14)
4. Encontrar o caminho mais curto com pontos intermédios (US27)
5. Conhecer a equipa
6. Sair

As funcionalidades de editor/jogador de US01–US12 **não são alcançáveis** a partir deste menu ([INDEX §4](INDEX.pt.md#4-achados-verificados), achado 1).

## Modo de desempenho (US29)

O `Main` aceita `PERFORMANCE_TEST <ficheiroEstações> <ficheiroLinhas> <US13|US14> [param]` e imprime `EXECUTION_TIME:<ms>` e `RESULT:<bool>`; `param` é o tipo de comboio no US13 (por omissão `DIESEL`) ou um booleano (`electrifiedOnly`) no US14. O formato de saída destina-se a ser lido por um script ([doc US29](system-documentation/sprint3/US29/us29_documentation.md)). Não executado aqui.

## Testes

```bash
mvn test
```

Não executado. As únicas fontes de teste são o harness de desempenho e o `US29PerformanceTestRunner` ([INDEX §3](INDEX.pt.md#3-testes)); os testes de desempenho geram cenários grandes e podem demorar muito. TODO: confirmar com o BMS a forma pretendida de correr as medições do US29.

## Empacotar

O POM configura o `maven-assembly-plugin` com `jar-with-dependencies` e `mainClass` `pt.ipp.isep.dei.Main`:

```bash
mvn package
```

Não executado; o nome exato do resultado depende do `finalName`/`artifactId` do POM (`project-template`).
