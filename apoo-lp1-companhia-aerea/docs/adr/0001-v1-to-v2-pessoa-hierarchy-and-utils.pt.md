# ADR 0001 — Da versão inicial para a refatoração: hierarquia `Pessoa`, `ler()` no domínio, `utils` e persistência em ficheiro

> Documentação adicionada em 2026 (portfolio). Reconstruída por comparação deste projeto com [`companhia-aerea`](../../../companhia-aerea) (a versão inicial); o raciocínio da própria equipa não está registado, por isso as motivações estão marcadas como inferidas. Versão inglesa: [0001-v1-to-v2-pessoa-hierarchy-and-utils.md](0001-v1-to-v2-pessoa-hierarchy-and-utils.md).

- **Estado:** Aceite (tal como implementado no repositório)
- **Evidência:** `companhia-aerea/CompanhiaAerea/src/main/java/org/example/*.java` (5 ficheiros), `grupo3-apoo-lp1-projeto-companhiaarea/src/main/java/org/companhiaaerea/**` (10 ficheiros)
- **Confirmar com o autor:** TODO: confirmar com o BMS (porque se fez a reescrita, quem decidiu a organização)

## Contexto

A versão inicial (`org.example`, 5 classes) modela voos, aviões, aeroportos e passageiros, e lê toda a entrada dentro do `Main` (`lerNumVoo`, `lerAviao`, `lerAeroporto`, `lerDataHora`, `lerPassageiros`, `lerTripulacao`). O seu `Passageiro` estende `Pessoa` e o voo guarda objetos `PilotoTripulante`, mas **nenhuma das classes está no repositório** (o `git ls-files` não encontra `Pessoa.java` nem `PilotoTripulante.java` aí), pelo que essa pasta não compila tal como está commitada.

## Decisão

| Aspeto | Versão inicial | Refatoração | Porquê (inferido) |
|---|---|---|---|
| Pacote | `org.example` | `org.companhiaaerea` | Espaço de nomes com significado |
| Pessoas | `Passageiro extends Pessoa`, `PilotoTripulante` (ambas em falta no repo) | Superclasse concreta `Pessoa`; `Passageiro` e `Tripulante` estendem-na; a função da tripulação é o enum `Categoria` (5 valores) | Eliminar duplicação de campos de identidade/contacto; uma classe de tripulação em vez de só pilotos |
| Entrada | Funções estáticas no `Main` | `static ler()` em cada classe de domínio + `utils.Console`/`utils.Data` com perguntas validadas por regex | O `Main` deixa de crescer; a validação é reutilizável |
| Persistência | O menu lista "guardar ficheiro" / "ler ficheiro" (opções 10–11) mas os `case` estão vazios | `utils.File`: texto separado por `;`, um voo por linha, em `<home>/companhia_aerea/` | Os dados sobrevivem entre execuções sem base de dados |
| Funcionalidades | 11 opções impressas, só a opção 1 (adicionar voo) implementada; as 2–11 são ramos `case` vazios | 10 opções de menu funcionais num ciclo: pesquisa por data/rota, voo mais cheio, listas de passageiros, editar passageiros, média e total de passageiros, guardar/carregar | Concluir o enunciado (a versão inicial é um esqueleto do mesmo menu) |

## Consequências

**Positivas**

- Uma hierarquia de herança real (`Pessoa` → `Passageiro`, `Tripulante`) e encapsulamento, que é o objetivo da unidade curricular.
- O projeto compila sozinho (as 10 classes estão presentes) e corre com o comando do README.
- A validação e o código de ficheiros vivem fora do `Main`.

**Negativas / dívida (verificada, ver [ARCHITECTURE](../ARCHITECTURE.pt.md#5-problemas-verificados-lidos-no-código))**

- As classes de domínio misturam modelo e I/O de consola (`ler()`), e `Voo`/`File` dependem do estático `Main.voos`.
- O formato com `;` é frágil (texto livre com `;`, ficheiros malformados fazem o carregamento falhar).
- Sem testes automáticos.

## Alternativas não tomadas (inferido)

- Serialização Java ou bibliotecas JSON/CSV para a persistência — eliminaria o parser escrito à mão, mas exige uma dependência ou `Serializable`.
- Uma classe de repositório/serviço em vez de uma lista estática no `Main`.
- Manter `PilotoTripulante` e acrescentar uma classe irmã para a restante tripulação em vez de um enum `Categoria`.
