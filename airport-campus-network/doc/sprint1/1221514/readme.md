# Projeto de Cablagem Estruturada - Campus Aeroportuário

**Número de Aluno:** 1221514

**Sprint:** 1

**Data:** 2026-03-15

**Edifício:** Terminal 2 (T2) - Níveis 1 e 4

**Responsabilidades:** Campus Backbone (MC no T2) + T2 Interno

---

## Índice

- [Projeto de Cablagem Estruturada - Campus Aeroportuário](#projeto-de-cablagem-estruturada---campus-aeroportuário)
  - [Índice](#índice)
  - [1. Visão Geral do Projeto](#1-visão-geral-do-projeto)
    - [1.1. Introdução](#11-introdução)
    - [1.2. Normas e Decisões Técnicas](#12-normas-e-decisões-técnicas)
    - [1.3. Metodologia e Escala](#13-metodologia-e-escala)
  - [2. Campus Backbone (Ligação entre Terminais)](#2-campus-backbone-ligação-entre-terminais)
    - [2.1. Infraestrutura Geral do Campus](#21-infraestrutura-geral-do-campus)
    - [2.2. Medidas dos Terminais no Desenho](#22-medidas-dos-terminais-no-desenho)
    - [2.3. Distâncias do MC (T2) aos Terminais](#23-distâncias-do-mc-t2-aos-terminais)
    - [2.4. Dimensionamento da Cablagem (Fibra Ótica)](#24-dimensionamento-da-cablagem-fibra-ótica)
    - [2.5. Hardware - Campus Backbone](#25-hardware---campus-backbone)
  - [3. Terminal 2 (T2) - Nível 1 (Arrivals)](#3-terminal-2-t2---nível-1-arrivals)
    - [3.1. Infraestrutura Existente](#31-infraestrutura-existente)
    - [3.2. Cálculo de Tomadas por Sala](#32-cálculo-de-tomadas-por-sala)
    - [3.3. Solução Proposta - HCs e CPs](#33-solução-proposta---hcs-e-cps)
    - [3.4. Distâncias dos Backbones de Fibra](#34-distâncias-dos-backbones-de-fibra)
    - [3.5. Dimensionamento da Cablagem Horizontal](#35-dimensionamento-da-cablagem-horizontal)
    - [3.6. Hardware - Nível 1](#36-hardware---nível-1)
  - [4. Terminal 2 (T2) - Nível 4 (Departures)](#4-terminal-2-t2---nível-4-departures)
    - [4.1. Infraestrutura Existente](#41-infraestrutura-existente)
    - [4.2. Cálculo de Tomadas por Sala](#42-cálculo-de-tomadas-por-sala)
    - [4.3. Solução Proposta - HCs e CPs](#43-solução-proposta---hcs-e-cps)
    - [4.4. Distâncias dos Backbones de Fibra](#44-distâncias-dos-backbones-de-fibra)
    - [4.5. Dimensionamento da Cablagem Horizontal](#45-dimensionamento-da-cablagem-horizontal)
    - [4.6. Hardware - Nível 4](#46-hardware---nível-4)
  - [5. Resumo Consolidado - Terminal 2](#5-resumo-consolidado---terminal-2)
    - [5.1. Totais por Piso](#51-totais-por-piso)
    - [5.2. Resumo de Hardware T2](#52-resumo-de-hardware-t2)
  - [6. Conformidade e Conclusão](#6-conformidade-e-conclusão)
    - [6.1. Conformidade com as Normas](#61-conformidade-com-as-normas)
    - [6.2. Notas para os Responsáveis Individuais](#62-notas-para-os-responsáveis-individuais)
    - [6.3. Conclusão](#63-conclusão)

---

## 1. Visão Geral do Projeto

### 1.1. Introdução
Este documento consolida o projeto de cablagem estruturada para o Terminal 2 (T2) e para o campus backbone do aeroporto. O projeto abrange a infraestrutura interna dos Níveis 1 e 4 do T2, bem como as ligações de fibra ótica entre o Main Cross-connect (MC), localizado no T2, e os restantes terminais (T3, T4 e T5).

### 1.2. Normas e Decisões Técnicas
O projeto segue rigorosamente as normas **ISO/IEC 11801** e **EN 50173** para cablagem estruturada, incorporando as seguintes decisões técnicas:
- **Padrão de Terminação:** T568B para todos os conectores RJ45.
- **Cabo Horizontal:** Cobre **S/FTP CAT7** com bainha **LSZH (Low Smoke Zero Halogen)** , garantindo imunidade a EMI (essencial em ambiente aeroportuário) e segurança em caso de incêndio.
- **Backbones (Building e Campus):** Fibra ótica **Multimodo OM4 (50/125 µm)** , com **2 cabos de 24 fibras por percurso** em caminhos fisicamente separados para redundância.
- **Dimensionamento:**
    - Tomadas interior: 2 tomadas por cada 10 m² de área.
    - Tomadas paredes exteriores: 1 tomada a cada 5 metros lineares (nas paredes especificadas).
    - Access Points (APs): Distribuição uniforme para cobertura total (25 APs por piso).
    - Distância máxima do cabo horizontal: 90m (norma).

### 1.3. Metodologia e Escala
As medições foram realizadas sobre as plantas fornecidas, utilizando os seguintes fatores de conversão:
- **Campus Backbone (planta geral):** 0.8 cm = 100 metros → **1 cm = 125 metros**.
- **Níveis 1 e 4 do T2:** 3.1 cm = 50 metros → **1 cm = 16.13 metros**.

---

## 2. Campus Backbone (Ligação entre Terminais)

### 2.1. Infraestrutura Geral do Campus
- **Terminais:** T2, T3, T4, T5.
- **Localização do MC:** Terminal 2, Sala 12, Nível 1.
- **Infraestrutura:** Vala técnica subterrânea com ligação a todos os edifícios.

| Parâmetro | Valor |
|:----------|:------|
| **Dimensões T2** | 200m × 200m |
| **Dimensões T3** | 200m × 200m |
| **Dimensões T4** | 200m × 100m |
| **Dimensões T5** | 400m × 150m (aprox.) |
| **Distância vertical T2 à vala (N1)** | 10 m |
| **Distância vertical T2 à vala (N4)** | 25 m |
| **Distância vertical T3 à vala** | 10 m (N1) / 16 m (N2) |
| **Distância vertical T4 à vala** | 3 m (N0) / 15 m (N2) |
| **Distância vertical T5 à vala** | 3 m (N0) / 15 m (N2) |

### 2.2. Medidas dos Terminais no Desenho

| Terminal | Medidas no Desenho (cm) | Dimensões Reais (m) |
|:---------|:------------------------:|:-------------------:|
| **T2** | 1.6 × 1.6 cm | 200 × 200 m |
| **T3** | 1.6 × 1.6 cm | 200 × 200 m |
| **T4** | 1.6 × 0.8 cm | 200 × 100 m |
| **T5** | 1.2 × 3.4 cm | 150 × 425 m |

### 2.3. Distâncias do MC (T2) aos Terminais
As distâncias totais consideram o percurso do MC até à vala, a distância horizontal entre terminais e a subida da vala até ao piso de entrada de cada terminal.

| Percurso | Distância Horizontal | Distância Vertical (descida/subida) | **Distância Total** |
|:---------|:--------------------:|:-----------------------------------:|:-------------------:|
| **MC (T2) ↔ T3** | 537,5 m | 10m + 10m | **557,5 m** |
| **MC (T2) ↔ T4** | 550 m | 10m + 3m | **563 m** |
| **MC (T2) ↔ T5** | 1.625 m | 10m + 3m | **1.638 m** |

### 2.4. Dimensionamento da Cablagem (Fibra Ótica)

| Percurso | Distância | Nº Cabos (24 fibras) | Total Fibra (m) |
|:---------|:---------:|:---------------------:|:---------------:|
| MC (T2) ↔ T3 | 557,5 m | 2 | **1.115 m** |
| MC (T2) ↔ T4 | 563 m | 2 | **1.126 m** |
| MC (T2) ↔ T5 | 1.638 m | 2 | **3.276 m** |
| **TOTAL FIBRA OM4 (CAMPUS)** | | | **5.517 m** |

### 2.5. Hardware - Campus Backbone

| Item | Especificação | Quantidade | Localização |
| :--- | :--- | :--- | :--- |
| **Patch Panel Fibra** | OM4, 24 portas (LC duplex), 1U | 6 | MC (T2) |
| **Patch Panel Fibra** | OM4, 24 portas (LC duplex), 1U | 6 | Terminais (2 por terminal) |
| **Patch Cord Fibra** | OM4, LC-LC duplex, 1m | 288 | MC e Terminais |
| **Organizador de Cabos** | 1U horizontal | 12 | MC e Terminais |

---

## 3. Terminal 2 (T2) - Nível 1 (Arrivals)

### 3.1. Infraestrutura Existente
- **Dimensões:** 200m × 200m.
- **Salas Técnicas:** 6, 11, 12 (com shafts para ligação vertical e à vala).
- **Underfloor Raceway:** 11 → 12 → 6 → 5 → 4 → 3 → 2 → 1.
- **Distância vertical à vala externa:** 10 metros.

### 3.2. Cálculo de Tomadas por Sala

| Divisão | Área Real (m²) | Tomadas Interior (2/10m²) | Tomadas Exteriores (5m) | Tomadas AP | **Total Tomadas** | Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| Sala 1 | 441 | 88 | 0 | 0 | **88** | Com tomadas |
| Sala 2 | 338 | 68 | 0 | 0 | **68** | Com tomadas |
| Sala 3 | 520 | 104 | 0 | 0 | **104** | Com tomadas |
| Sala 4 | 597 | 119 | 0 | 0 | **119** | Com tomadas |
| Sala 5 | 520 | 104 | 0 | 0 | **104** | Com tomadas |
| **Sala 6** | 92 | 0 | 0 | 1 | **1** | **Técnica (HC-C)** |
| Sala 7 | 441 | 88 | 0 | 0 | **88** | Com tomadas |
| Sala 8 | 441 | 88 | 0 | 0 | **88** | Com tomadas |
| **Sala 9** | 469 | 0 | 0 | 0 | **0** | Sem tomadas |
| **Sala 10**| 469 | 0 | 0 | 0 | **0** | Sem tomadas |
| **Sala 11**| 128 | 0 | 0 | 1 | **1** | **Técnica (HC-A)** |
| **Sala 12**| 128 | 0 | 0 | 1 | **1** | **Técnica (MC)** |
| **Parede Direita** | 200m lineares | - | 40 | 0 | **40** | 1 tomada/5m |
| **Parede Inferior**| 200m lineares | - | 40 | 0 | **40** | 1 tomada/5m |
| **Teto Falso (APs)** | - | - | - | 22 | **(22)** | Distribuição uniforme |
| **TOTAL NÍVEL 1** | | **659** | **80** | **25** | **739** | ✓ |

### 3.3. Solução Proposta - HCs e CPs

| Designação | Localização | Função | Alimentado por |
|:-----------|:------------|:-------|:---------------|
| **HC-A** | Sala 11 | HC Zona Oeste + IC | MC |
| **HC-C** | Sala 6 | HC Zona Este + IC | MC |
| **MC** | Sala 12 | Main Cross-connect | - |
| **CP-5** | Sala 5 | Distribuir para S1, S2, S3, S4, S5 | HC-C |
| **CP-7** | Sala 7 | Distribuir para S7 | HC-C |
| **CP-8** | Junto à Sala 8 | Distribuir para S8 | HC-A |

### 3.4. Distâncias dos Backbones de Fibra

| Percurso | Medida no Desenho (cm) | Distância Real (m) | Nº Cabos | Total Fibra (m) |
|:---------|:-----------------------:|:------------------:|:--------:|:---------------:|
| HC-A (S11) ↔ MC (S12) | 10.8 cm | 174 m | 2 | 348 m |
| HC-C (S6) ↔ MC (S12) | 10.8 cm | 174 m | 2 | 348 m |
| HC-C (S6) ↔ CP-5 (S5) | 2.2 cm | 35 m | 2 | 70 m |
| HC-C (S6) ↔ CP-7 (S7) | 12.2 cm | 197 m | 2 | 394 m |
| HC-A (S11) ↔ CP-8 | 6.5 cm | 105 m | 2 | 210 m |
| Ligações verticais à vala | - | 10 m (cada) | 2 (cada) | 60 m |
| **TOTAL FIBRA OM4** | | | | **1.430 m** |

### 3.5. Dimensionamento da Cablagem Horizontal

| Zona | Nº Tomadas Utilizador | Nº APs | Dist. Média Horizontal | Total por Tomada | Total Cabo CAT7 |
|:-----|:---------------------:|:------:|:----------------------:|:----------------:|:---------------:|
| HC-A (direto) | 21 | 8 | 30 m | 40 m | 29 × 40 = **1.160 m** |
| HC-C (direto) | 41 | 9 | 40 m | 50 m | 50 × 50 = **2.500 m** |
| MC (direto) | 41 | 8 | 30 m | 40 m | 49 × 40 = **1.960 m** |
| CP-5 | 483 | 0 | 45 m | 55 m | 483 × 55 = **26.565 m** |
| CP-7 | 88 | 0 | 30 m | 40 m | 88 × 40 = **3.520 m** |
| CP-8 | 88 | 0 | 30 m | 40 m | 88 × 40 = **3.520 m** |
| **TOTAL NÍVEL 1** | **762** | **25** | | | **39.225 m** |

### 3.6. Hardware - Nível 1

| Item | HC-A (S11) | HC-C (S6) | MC (S12) | CP-5 (S5) | CP-7 (S7) | CP-8 | **TOTAL** |
|:-----|:----------:|:---------:|:--------:|:---------:|:---------:|:----:|:---------:|
| Tomadas Utilizador | 21 | 41 | 41 | 483 | 88 | 88 | **762** |
| Tomadas AP | 8 | 9 | 8 | 0 | 0 | 0 | **25** |
| Patch Panel Cobre (48p) | 1 | 2 | 1 | 11 | 2 | 2 | **19** |
| Patch Panel Fibra (24p) | 4 | 8 | 8 | 2 | 2 | 2 | **26** |
| Patch Cord Cobre (0.5m) | 29 | 50 | 49 | 483 | 88 | 88 | **787** |
| Patch Cord Cobre (3m) | 29 | 50 | 49 | 483 | 88 | 88 | **787** |
| Patch Cord Fibra (1m) | 96 | 192 | 192 | 48 | 48 | 48 | **624** |
| Armário | 9U | 24U | 24U | 42U | 18U | 18U | - |

---

## 4. Terminal 2 (T2) - Nível 4 (Departures)

### 4.1. Infraestrutura Existente
- **Dimensões:** 200m × 200m.
- **Salas Técnicas:** 8, 17, 18.
- **Underfloor Raceway:** 17 → 18 → 8 → 7 → 6 → 5 → 4 → 3 → 2 → 1.
- **Distância vertical à vala externa:** 25 metros.

### 4.2. Cálculo de Tomadas por Sala

| Divisão | Área Real (m²) | Tomadas Interior (2/10m²) | Tomadas Exteriores | Tomadas AP | **Total** | Status |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| Sala 1 | 255 | 51 | 0 | 0 | **51** | Com tomadas |
| Sala 2 | 255 | 51 | 0 | 0 | **51** | Com tomadas |
| Sala 3 | 219 | 44 | 0 | 0 | **44** | Com tomadas |
| Sala 4 | 219 | 44 | 0 | 0 | **44** | Com tomadas |
| Sala 5 | 561 | 112 | 0 | 0 | **112** | Com tomadas |
| Sala 6 | 468 | 94 | 0 | 0 | **94** | Com tomadas |
| Sala 7 | 515 | 103 | 0 | 0 | **103** | Com tomadas |
| **Sala 8** | 117 | 0 | 0 | 1 | **1** | **Técnica (HC-B)** |
| Sala 9 | 250 | 50 | 0 | 0 | **50** | Com tomadas |
| Sala 10 | 250 | 50 | 0 | 0 | **50** | Com tomadas |
| Sala 11 | 250 | 50 | 0 | 0 | **50** | Com tomadas |
| Sala 12 | 250 | 50 | 0 | 0 | **50** | Com tomadas |
| Sala 13 | 250 | 50 | 0 | 0 | **50** | Com tomadas |
| Sala 14 | 166 | 33 | 0 | 0 | **33** | Com tomadas |
| **Sala 15**| 532 | 0 | 0 | 0 | **0** | Sem tomadas |
| **Sala 16**| 532 | 0 | 0 | 0 | **0** | Sem tomadas |
| **Sala 17**| 128 | 0 | 0 | 1 | **1** | **Técnica (HC-A)** |
| **Sala 18**| 128 | 0 | 0 | 1 | **1** | **Técnica (MC)** |
| **Parede Direita** | 200m | - | 40 | 0 | **40** | 1 tomada/5m |
| **Parede Inferior**| 200m | - | 40 | 0 | **40** | 1 tomada/5m |
| **Teto Falso (APs)** | - | - | - | 22 | **(22)** | APs |
| **TOTAL** | | **732** | **80** | **25** | **812** | ✓ |

### 4.3. Solução Proposta - HCs e CPs

| Designação | Localização | Função | Alimentado por |
|:-----------|:------------|:-------|:---------------|
| **HC-A** | Sala 17 | HC Zona Oeste | MC |
| **HC-B** | Sala 8 | HC Zona Central/Leste | MC |
| **MC** | Sala 18 | Main Cross-connect | - |
| **CP-5** | Sala 5 | Distribuir para S5, S9-14 | HC-B |
| **CP-3** | Sala 3 | Distribuir para S1, S2, S3 | HC-B |

### 4.4. Distâncias dos Backbones de Fibra

| Percurso | Medida no Desenho (cm) | Distância Real (m) | Nº Cabos | Total Fibra (m) |
|:---------|:-----------------------:|:------------------:|:--------:|:---------------:|
| HC-A (S17) ↔ MC (S18) | 10.8 cm | 174 m | 2 | 348 m |
| MC (S18) ↔ HC-B (S8) | 10.8 cm | 174 m | 2 | 348 m |
| HC-B (S8) ↔ CP-5 (S5) | 4.2 cm | 68 m | 2 | 136 m |
| HC-B (S8) ↔ CP-3 (S3) | 9.0 cm | 145 m | 2 | 290 m |
| Ligações verticais à vala | - | 25 m (cada) | 2 (cada) | 150 m |
| **TOTAL FIBRA OM4** | | | | **1.272 m** |

### 4.5. Dimensionamento da Cablagem Horizontal

| Zona | Tomadas Utilizador | Nº APs | Dist. Média | Total por Tomada | Total Cabo CAT7 |
|:-----|:------------------:|:------:|:-----------:|:----------------:|:---------------:|
| HC-A (direto) | 2 | 8 | 30 m | 40 m | 10 × 40 = **400 m** |
| HC-B (direto S7,S6,S4) | 242 | 9 | 45 m | 55 m | 251 × 55 = **13.805 m** |
| MC (direto + S9-14) | 324 | 8 | 40 m | 50 m | 332 × 50 = **16.600 m** |
| CP-5 (S5 + S9-14) | 395 | 0 | 45 m | 55 m | 395 × 55 = **21.725 m** |
| CP-3 (S1-3) | 146 | 0 | 40 m | 50 m | 146 × 50 = **7.300 m** |
| **TOTAL** | **1.109** | **25** | | | **59.830 m** |

### 4.6. Hardware - Nível 4

| Item | HC-A (S17) | HC-B (S8) | MC (S18) | CP-5 (S5) | CP-3 (S3) | **TOTAL** |
|:-----|:----------:|:---------:|:--------:|:---------:|:---------:|:---------:|
| Tomadas Utilizador | 2 | 242 | 324 | 395 | 146 | **1.109** |
| Tomadas AP | 8 | 9 | 8 | 0 | 0 | **25** |
| Patch Panel Cobre (48p) | 1 | 6 | 7 | 9 | 4 | **27** |
| Patch Panel Fibra (24p) | 4 | 8 | 8 | 2 | 2 | **24** |
| Patch Cord Cobre (0.5m) | 10 | 251 | 332 | 395 | 146 | **1.134** |
| Patch Cord Cobre (3m) | 10 | 251 | 332 | 395 | 146 | **1.134** |
| Patch Cord Fibra (1m) | 96 | 192 | 192 | 48 | 48 | **576** |
| Armário | 12U | 42U | 42U | 42U | 24U | - |

---

## 5. Resumo Consolidado - Terminal 2

### 5.1. Totais por Piso

| Piso | Tomadas Utilizador | Tomadas AP | Tomadas Parede | **Total Tomadas** | CAT7 (m) | Fibra Building (m) | Fibra Campus (alocada) |
|:-----|:------------------:|:----------:|:--------------:|:------------------:|:--------:|:------------------:|:----------------------:|
| Nível 1 | 659 | 25 | 80 | **739** | 39.225 | 1.430 | - |
| Nível 4 | 732 | 25 | 80 | **812** | 59.830 | 1.272 | - |
| **Subtotal T2** | **1.391** | **50** | **160** | **1.551** | **99.055 m** | **2.702 m** | - |
| Campus | - | - | - | - | - | - | **5.517 m** |
| **TOTAL GERAL** | **1.391** | **50** | **160** | **1.551** | **~99 km** | **~2.7 km** | **~5.5 km** |

### 5.2. Resumo de Hardware T2

| Categoria | Quantidade |
|:----------|-----------:|
| Tomadas Keystone CAT7 (utilizador + APs + paredes) | 1.601 |
| Access Points | 50 |
| Patch Panels Cobre (CAT7 48 portas) | 46 |
| Patch Panels Fibra (OM4 24 portas) | 50 |
| Patch Cords Cobre (0.5m) | 1.921 |
| Patch Cords Cobre (3m) | 1.921 |
| Patch Cords Fibra (1m) | 1.200 |
| Armários 42U | 3 (HC-B N4, CP-5 N4, CP-5 N1) |
| Armários 24U | 3 (HC-C N1, MC N1, CP-3 N4) |
| Armários 18U | 2 (CP-7 N1, CP-8 N1) |
| Armários 12U | 1 (HC-A N4) |
| Armários 9U | 1 (HC-A N1) |

---

## 6. Conformidade e Conclusão

### 6.1. Conformidade com as Normas

| Requisito | Verificação |
|:----------|:------------|
| ✅ **ISO/IEC 11801 e EN 50173** | Cablagem estruturada conforme normas |
| ✅ **Distância HC → tomada < 90m** | Verificado para todos os pontos (máx 70m) |
| ✅ **Redundância de backbone** | 2 cabos de 24 fibras por percurso, caminhos separados |
| ✅ **Padrão T568B** | Todas as terminações |
| ✅ **Cabo S/FTP blindado** | Proteção EMI para ambiente aeroportuário |
| ✅ **Cabo LSZH** | Segurança em espaços públicos |
| ✅ **Sobredimensionamento** | 24 fibras por cabo (acima do mínimo de 8) |
| ✅ **Aproveitamento underfloor** | 100% utilizado para backbones |

### 6.2. Notas para os Responsáveis Individuais

- **Responsável T3 (T.1.2):** 2×24 fibras OM4 chegam ao T3. Distância do MC: 557,5m. Prever 2 patch panels de fibra.
- **Responsável T4 (T.1.4):** 2×24 fibras OM4 chegam ao T4. Distância do MC: 563m. Ponto de entrada obrigatório: Sala 4.
- **Responsável T5 (T.1.3):** 2×24 fibras OM4 chegam ao T5. Distância do MC: 1.638m (a mais longa). **Atenção:** Esta distância excede o alcance recomendado para OM4 a 100 Gbps. Recomendar transceivers de longo alcance ou considerar futura atualização para fibra monomodo (OS2).

### 6.3. Conclusão
O projeto de cablagem estruturada para o Terminal 2 e para o campus backbone foi dimensionado com base nas medidas reais, respeitando todas as normas técnicas e decisões de planning. A infraestrutura proposta garante **tolerância a falhas** (redundância de caminhos), **escalabilidade** (sobredimensionamento de fibras) e **segurança** (cabos LSZH), estando preparada para suportar as necessidades atuais e futuras do ambiente aeroportuário.

---

**Ficheiros Anexos (referência):**
- Plantas esquemáticas: `/doc/sprint1/1221514/img/schematic_T2_level1.pdf`
- Plantas esquemáticas: `/doc/sprint1/1221514/img/schematic_T2_level4.pdf`
- Plantas esquemáticas: `/doc/sprint1/1221514/img/schematic_campus_backbone.pdf`
- Medições originais: `/doc/sprint1/1221514/medidasT2L1L4.txt`