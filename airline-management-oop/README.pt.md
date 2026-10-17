# Airline Management — OOP refactor (Group 3)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Aplicação de consola Java para voos de uma companhia aérea, refeita com um design orientado a objetos mais limpo.

> Anteriormente `apoo-lp1-companhia-aerea`.

| | |
|---|---|
| **Contexto** | CTESP — Programação Orientada a Objetos (APOO) / LP1 |
| **Ano** | 2022/23 |
| **Equipa** | Grupo 3 |
| **Stack** | Java · Maven |

## O que este projeto demonstra
- **Herança** (`Pessoa` → `Passageiro`, `Tripulante`) e encapsulamento.
- Separação entre domínio e utilitários (`Console`, `Data`, `File`).
- Persistência em ficheiro (guardar/carregar) em Java puro.

## Funcionalidades
- Domínio: `Voo`, `Aviao`, `Aeroporto`, `Passageiro`, `Tripulante`, `Pessoa` (superclasse).
- `utils`: leitura de input, datas e persistência em ficheiro.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `grupo3-apoo-lp1-projeto-companhiaarea/` | Projeto Maven |

## Execução
```bash
cd grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```

## Notas
- Evolução da [versão inicial](../airline-management-v1).

## Documentação

- [Arquitetura](docs/ARCHITECTURE.pt.md) — diagrama de classes, menu, fluxos, formato de persistência, problemas verificados
- [Configuração](docs/SETUP.pt.md) — pré-requisitos, compilar e executar, resolução de problemas
- [ADR 0001](docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md) — da versão inicial para esta refatoração
- [Auditoria da documentação](../docs/AUDIT.pt.md#airline-management-oop) — estado e lacunas deste projeto
