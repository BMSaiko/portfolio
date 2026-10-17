# ADR 0001 — Manter a versão inicial como retrato histórico não reparado

> Documentação adicionada em 2026 (portfolio). Ao contrário dos outros ADRs, este regista uma **decisão de curadoria do portfolio**, não uma decisão da equipa original. Versão inglesa: [0001-keep-early-version-as-unrepaired-snapshot.md](0001-keep-early-version-as-unrepaired-snapshot.md).

- **Estado:** Proposto — TODO: confirmar com o BMS
- **Evidência:** `CompanhiaAerea/src/main/java/org/example/*.java`; [ARCHITECTURE](../ARCHITECTURE.pt.md#4-problemas-verificados-lidos-no-código); o README já diz "Work in progress: only 'add flight' is implemented"
- **Relacionado:** [ADR 0001 de `airline-management-oop`](../../../airline-management-oop/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md) (o que a refatoração mudou)

## Contexto

O projeto é a primeira iteração do exercício da companhia aérea. Não compila (faltam duas classes referenciadas), só uma ação de menu está esboçada e há vários erros (lista nula, tripulação vazia, data-hora ilegível). Existe uma versão concluída num projeto irmão.

## Decisão

Deixar o código-fonte intacto e documentar o seu estado com honestidade. Não acrescentar as classes em falta nem corrigir os erros.

## Consequências

- **Positivas:** o portfolio mostra iteração genuína (rascunho → refatoração) sem reescrever o histórico de uma equipa; sem código inventado.
- **Negativas:** quem visita não consegue correr este projeto; o README e o SETUP têm de o dizer claramente (dizem).

## Alternativas não tomadas

- Reconstruir `Pessoa` / `PilotoTripulante` para compilar — seria autoria inventada; reconsiderar só se os originais forem encontrados.
- Remover o projeto — perde a comparação antes/depois em que o ADR da refatoração se apoia.
