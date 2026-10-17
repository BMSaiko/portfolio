# Configuração — Gestão de Companhia Aérea (refatoração OOP, Grupo 3)

> Documentação adicionada em 2026 (portfolio). Os comandos abaixo **não foram testados** na máquina onde isto foi escrito (sem JDK nem Maven); decorrem do `pom.xml` e do README. Versão inglesa: [SETUP.md](SETUP.md).

## Pré-requisitos

| Ferramenta | Versão | Porquê |
|---|---|---|
| JDK | 17 ou superior | o `pom.xml` define `maven.compiler.source/target` a 17 |
| Maven | 3.x | Compilação; o POM não tem plugins nem dependências |

Sem base de dados, sem variáveis de ambiente, sem segredos.

## Compilar e executar

```bash
cd airline-management-oop/grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```

O `exec:java` é invocado pelo prefixo curto (o POM não configura o plugin), por isso o Maven descarrega o `exec-maven-plugin` na primeira utilização — é preciso internet uma vez. Alternativa sem o plugin:

```bash
mvn compile
java -cp target/classes org.companhiaaerea.Main
```

## Utilização

1. Escolher `1` para adicionar um voo; responder às perguntas (voo `VXXX`, avião, aeroportos, datas `dd/MM/yyyy HH:mm`, passageiros — `0` no número de identificação termina — e tripulação).
2. Usar `3`–`8` para pesquisar e calcular estatísticas (datas `dd/MM/yyyy`).
3. Usar `9` para guardar e `10` para carregar. Os ficheiros ficam em `<a tua home>/companhia_aerea/<nome>.voos` (por exemplo `C:\Users\<tu>\companhia_aerea\` no Windows). Carregar **substitui** os voos que estão em memória.
4. `0` sai e oferece guardar quando existem voos.

## Resolução de problemas

| Sintoma | Causa provável / solução |
|---|---|
| `error: release version 17 not supported` | JDK anterior à 17; instalar JDK 17+ e verificar `java -version` / `mvn -version` |
| `No plugin found for prefix 'exec'` | Sem rede na primeira execução; ligar uma vez ou usar a alternativa `java -cp` |
| Exceção ao carregar um ficheiro | Ficheiro editado à mão ou campo com `;` (ver [ARCHITECTURE](ARCHITECTURE.pt.md#5-problemas-verificados-lidos-no-código), problemas 1–2); apagar ou regenerar |
| `ArrayIndexOutOfBoundsException` após a opção 10 | Foi escrito um número fora dos ficheiros listados |
| `NaN` como média (opção 7) | Não há voos no intervalo escolhido |
| Acentos errados na consola | Code page da consola; as fontes são UTF-8 (`project.build.sourceEncoding`) — experimentar `chcp 65001` no Windows |

## Testes

Nenhum. Não existe a pasta `src/test` e o POM não declara framework de testes, por isso o `mvn test` não tem nada para executar.
