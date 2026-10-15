# Companhia Aérea — versão inicial

[🇬🇧 English](README.md) · 🇵🇹 Português

Aplicação de consola em Java para gestão dos voos de uma companhia aérea (UC de LP1, ISEP). É a versão inicial; a versão refatorizada do grupo está em [`../apoo-lp1-companhia-aerea`](../apoo-lp1-companhia-aerea).

## Modelo de domínio
`Voo` · `Aviao` (matrícula, marca/modelo, lugares turística/executiva) · `Aeroporto` (sigla, país, cidade mais próxima) · `Passageiro` (email, tipo de bilhete, lugar) · tripulação.

## Menu previsto
Adicionar/remover voo, pesquisar por rota e data, mostrar/alterar passageiros, pesquisar voos num intervalo de datas, voo com mais passageiros, total e média de passageiros por rota, guardar/ler ficheiro.

> Estado: em desenvolvimento — apenas "adicionar voo" está implementado no menu.

## Stack
Java · Maven

## Como executar
```bash
cd CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```
