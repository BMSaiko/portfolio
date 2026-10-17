# Tarefa T.1.3 - Projeto de Cablagem Estruturada - Terminal 5 (T5)

**Número de Aluno:** 1241165

**Sprint:** 1

**Data:** 2026-03-12

**Edifício:** Terminal 5 (T5)

**Pisos Abrangidos:** Nível 0 (Arrivals) e Nível 2 (Departures)

---

## 1. Introdução

Este documento apresenta o projeto de cablagem estruturada para o Terminal 5 do aeroporto, abrangendo os pisos 0 e 2. O projeto segue as normas de cablagem estruturada (ISO/IEC 11801 e EN 50173), as decisões técnicas globais definidas no `planning.md` (padrão T568B, fibra OM4 para backbones, cobre CAT7 para horizontal) e as regras de dimensionamento aprendidas na aula PL01 (distância máxima de 90m para cablagem horizontal, sobredimensionamento de 100% para armários).

Em coordenação com a equipa, o Main Cross-connect (MC) do campus foi estabelecido no Terminal 2. Sendo assim, o Terminal 5 disporá de um Intermediate Cross-connect (IC) principal no Nível 0, que receberá o Campus Backbone proveniente do T2 e fará a distribuição vertical para os Horizontal Cross-connects (HC) dos Níveis 0 e 2. O acesso ao backbone externo faz-se via passagem subterrânea que dista verticalmente 3 metros do Nível 0 e 15 metros do Nível 2.

---

## 2. Dimensionamento de Tomadas de Rede

### **Critérios utilizados:**
1. **Número Standard:** 2 tomadas por cada 10 m² (ou fração) como base, garantindo flexibilidade.
2. **Access Points (Wi-Fi):** Ambos os pisos exigem cobertura wireless LAN total. Calculado com base em 1 AP por cada 250 m² de área, com mínimo de 1 AP por sala não técnica. O teto falso a quatro metros do chão será o local primário de instalação dos APs.
3. **Requisitos Especiais:**
    - Salas técnicas/alojamento de rede: 0 tomadas de utilizador (Salas 1, 2, 18 e 22 para Nível 0 / Salas 1, 2, 22 e 26 para Nível 2).
    - Salas sem tomadas: Conforme explicitamente especificado no enunciado.

### 2.1. Nível 0 (Arrivals)

| Divisão | Área (m²) | Cálculo Base (2/10m²) | Tomadas AP | **Total Tomadas** | Observações |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **5.0.1** | 97.7 | 0 | 0 | **0** | **Sala técnica** (HC primário) |
| **5.0.2** | 97.7 | 0 | 0 | **0** | **Sala técnica** (HC secundário) |
| 5.0.3 | 369.8 | (369.8/10)*2 = 74 | 2 | **76** | 2 APs |
| 5.0.4 | 260.3 | (260.3/10)*2 = 54 | 2 | **56** | 2 APs |
| 5.0.5 | 267.7 | (267.7/10)*2 = 54 | 2 | **56** | 2 APs |
| 5.0.6 | 308.6 | (308.6/10)*2 = 62 | 2 | **64** | 2 APs |
| 5.0.7 | 338.3 | (338.3/10)*2 = 68 | 2 | **70** | 2 APs |
| 5.0.8 | 301.2 | (301.2/10)*2 = 62 | 2 | **64** | 2 APs |
| 5.0.9 | 267.7 | (267.7/10)*2 = 54 | 2 | **56** | 2 APs |
| 5.0.10 | 374.2 | (374.2/10)*2 = 76 | 2 | **78** | 2 APs |
| 5.0.11 | 364.4 | (364.4/10)*2 = 74 | 2 | **76** | 2 APs |
| **5.0.12** | 364.4 | 0 | 0 | **0** | Sem tomadas |
| **5.0.13** | 364.4 | 0 | 0 | **0** | Sem tomadas |
| 5.0.14 | 330.2 | (330.2/10)*2 = 68 | 2 | **70** | 2 APs |
| 5.0.15 | 344.9 | (344.9/10)*2 = 70 | 2 | **72** | 2 APs |
| **5.0.16** | 378.6 | 0 | 0 | **0** | Sem tomadas |
| **5.0.17** | 372.7 | 0 | 0 | **0** | Sem tomadas |
| **5.0.18** | 97.7 | 0 | 0 | **0** | **Sala técnica** |
| 5.0.19 | 416.4 | (416.4/10)*2 = 84 | 2 | **86** | 2 APs |
| 5.0.20 | 490.8 | (490.8/10)*2 = 100 | 2 | **102** | 2 APs |
| 5.0.21 | 334.6 | (334.6/10)*2 = 68 | 2 | **70** | 2 APs |
| **5.0.22** | 97.7 | 0 | 0 | **0** | **Sala técnica** |
| **Corredores** | ~1000 | (1000/10)*2 = 200 | 4 | **204** | Distribuídas nas áreas de circulação |
| **TOTAL NÍVEL 0** | | | | **1200** | **Soma total de tomadas (N0)** |

### 2.2. Nível 2 (Departures)

| Divisão | Área (m²) | Cálculo Base (2/10m²) | Tomadas AP | **Total Tomadas** | Observações |
| :--- | :---: | :---: | :---: | :---: | :--- |
| **5.2.1** | 92.2 | 0 | 0 | **0** | **Sala técnica** |
| **5.2.2** | 92.2 | 0 | 0 | **0** | **Sala técnica** |
| 5.2.3 | 213.8 | (213.8/10)*2 = 44 | 1 | **45** | 1 AP |
| 5.2.4 | 213.8 | (213.8/10)*2 = 44 | 1 | **45** | 1 AP |
| 5.2.5 | 281.3 | (281.3/10)*2 = 58 | 2 | **60** | 2 APs |
| 5.2.6 | 348.3 | (348.3/10)*2 = 70 | 2 | **72** | 2 APs |
| 5.2.7 | 355.3 | (355.3/10)*2 = 72 | 2 | **74** | 2 APs |
| 5.2.8 | 382.6 | (382.6/10)*2 = 78 | 2 | **80** | 2 APs |
| 5.2.9 | 176.3 | (176.3/10)*2 = 36 | 1 | **37** | 1 AP |
| 5.2.10 | 397.6 | (397.6/10)*2 = 80 | 2 | **82** | 2 APs |
| 5.2.11 | 543.8 | (543.8/10)*2 = 110 | 3 | **113** | 3 APs |
| 5.2.12 | 431.3 | (431.3/10)*2 = 88 | 2 | **90** | 2 APs |
| 5.2.13 | 513.8 | (513.8/10)*2 = 104 | 3 | **107** | 3 APs |
| 5.2.14 | 371.3 | (371.3/10)*2 = 76 | 2 | **78** | 2 APs |
| 5.2.15 | 465.1 | (465.1/10)*2 = 94 | 2 | **96** | 2 APs |
| 5.2.16 | 344.1 | (344.1/10)*2 = 70 | 2 | **72** | 2 APs |
| 5.2.17 | 348.6 | (348.6/10)*2 = 70 | 2 | **72** | 2 APs |
| **5.2.18** | 292.5 | 0 | 0 | **0** | Sem tomadas |
| **5.2.19** | 298.1 | 0 | 0 | **0** | Sem tomadas |
| **5.2.20** | 362.7 | 0 | 0 | **0** | Sem tomadas |
| **5.2.21** | 362.7 | 0 | 0 | **0** | Sem tomadas |
| **5.2.22** | 92.2 | 0 | 0 | **0** | **Sala técnica** |
| 5.2.23 | 407.3 | (407.3/10)*2 = 82 | 2 | **84** | 2 APs |
| 5.2.24 | 481.0 | (481.0/10)*2 = 98 | 2 | **100** | 2 APs |
| 5.2.25 | 326.5 | (326.5/10)*2 = 66 | 2 | **68** | 2 APs |
| **5.2.26** | 92.2 | 0 | 0 | **0** | **Sala técnica** |
| **Corredores** | ~1000 | (1000/10)*2 = 200 | 4 | **204** | Distribuídas nas áreas de circulação |
| **TOTAL NÍVEL 2** | | | | **1579** | **Soma total de tomadas (N2)** |