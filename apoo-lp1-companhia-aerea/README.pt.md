# APOO / LP1 — Gestão de Companhia Aérea (Grupo 3)

[🇬🇧 English](README.md) · 🇵🇹 Português

Aplicação de consola em Java para gestão de voos de uma companhia aérea, feita pelo grupo 3 para as UCs de APOO e LP1 (ISEP). Estende a [versão inicial](../companhia-aerea) com um desenho orientado a objetos mais limpo.

## Desenho
- Domínio: `Voo`, `Aviao`, `Aeroporto`, `Passageiro`, `Tripulante`, com `Pessoa` como superclasse comum (herança).
- Pacote `utils`: `Console` (leitura de dados), `Data` (datas), `File` (guardar/ler ficheiros).

## Stack
Java · Maven

## Como executar
```bash
cd grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```
