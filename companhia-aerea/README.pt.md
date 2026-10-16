# Companhia Aérea — early version

[🇬🇧 English](README.md) · 🇵🇹 Português

> Aplicação de consola para gerir voos de uma companhia aérea — a primeira iteração, depois refeita em `apoo-lp1-companhia-aerea`.

| | |
|---|---|
| **Contexto** | CTESP — Laboratório de Programação 1 (LP1) |
| **Ano** | 2022/23 |
| **Equipa** | Projeto de equipa |
| **Stack** | Java · Maven |

## O que este projeto demonstra
- Modelação de um domínio real com classes (voo, avião, aeroporto, passageiro, tripulação).
- Menus de consola e tratamento de input em Java.
- Iteração honesta: esta é a versão inicial; o refactor OO está no projeto seguinte.

## Funcionalidades
- Domínio: `Voo`, `Aviao` (matrícula, modelo, lugares turística/executiva), `Aeroporto` (código, país, cidade mais próxima), `Passageiro` (email, tipo de bilhete, lugar), tripulação.
- Menu planeado: adicionar/remover voo, pesquisar por rota e data, listar/editar passageiros, voos num intervalo de datas, voo com mais passageiros, totais e médias por rota, guardar/carregar.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `CompanhiaAerea/` | Projeto Maven |

## Execução
```bash
cd CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```

## Notas
- Trabalho em curso: no menu só está implementado “adicionar voo”. A versão concluída está em [`apoo-lp1-companhia-aerea`](../apoo-lp1-companhia-aerea).

## Documentação

- [Auditoria da documentação](../docs/AUDIT.pt.md#companhia-aerea) — estado e lacunas deste projeto
