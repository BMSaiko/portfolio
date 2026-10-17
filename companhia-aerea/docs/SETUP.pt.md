# Configuração — Companhia Aérea (versão inicial)

> Documentação adicionada em 2026 (portfolio). Os comandos **não foram testados** (sem JDK nem Maven na máquina onde isto foi escrito). Versão inglesa: [SETUP.md](SETUP.md).

## Estado

**Não se espera que este projeto compile tal como está commitado.** `Passageiro.java`, `Voo.java` e `Main.java` referenciam `Pessoa` e `PilotoTripulante`, que não estão no repositório (ver [ARCHITECTURE](ARCHITECTURE.pt.md#4-problemas-verificados-lidos-no-código), problema 1). O `mvn compile` deve por isso falhar com erros de símbolo não resolvido para essas duas classes (previsão feita a ler o código, não uma execução observada). Para ver um programa a funcionar, usar [`apoo-lp1-companhia-aerea`](../../apoo-lp1-companhia-aerea/docs/SETUP.pt.md).

## Pré-requisitos

| Ferramenta | Versão |
|---|---|
| JDK | 17+ (o `pom.xml` define source/target 17) |
| Maven | 3.x |

Sem base de dados, variáveis de ambiente ou segredos.

## Comandos (do README)

```bash
cd companhia-aerea/CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```

Para confirmar o estado descrito acima, correr só `mvn compile` e procurar os erros de classes em falta.

## Se as classes em falta fossem repostas

Mesmo assim o programa só executaria uma ação: o menu não tem ciclo, só a opção 1 está implementada, `Main.voos` é `null` (NPE ao adicionar), as datas não se conseguem ler e a lista de tripulação fica vazia (problemas 2–4 em [ARCHITECTURE](ARCHITECTURE.pt.md)).

## Testes

Nenhum.
